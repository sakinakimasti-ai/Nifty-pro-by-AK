package com.example.util

import com.example.data.model.OptionLeg
import com.example.data.model.OptionStrike
import com.example.data.model.SellingCandidate
import kotlin.math.*

object QuantitativeEngine {

    private const val RISK_FREE_RATE = 0.065 // RBI repo rate ~6.5%

    fun normalPdf(x: Double): Double {
        return (1.0 / sqrt(2 * PI)) * exp(-0.5 * x * x)
    }

    fun normalCdf(x: Double): Double {
        val sign = if (x < 0) -1 else 1
        val absX = abs(x)

        val p = 0.2316419
        val b1 = 0.319381530
        val b2 = -0.356563782
        val b3 = 1.781477937
        val b4 = -1.821255978
        val b5 = 1.330274429

        val t = 1.0 / (1.0 + p * absX)
        val poly = t * (b1 + t * (b2 + t * (b3 + t * (b4 + t * b5))))
        val normalDist = 1.0 - normalPdf(absX) * poly

        return if (sign >= 0) normalDist else 1.0 - normalDist
    }

    data class Greeks(
        val delta: Double,
        val gamma: Double,
        val theta: Double,
        val vega: Double
    )

    fun calculateGreeks(
        spot: Double,
        strike: Double,
        daysToExpiry: Double,
        volatilityPercent: Double,
        isCall: Boolean
    ): Greeks {
        val t = max(1.0, daysToExpiry) / 365.0
        val sigma = max(0.05, volatilityPercent / 100.0)
        val r = RISK_FREE_RATE

        val sqrtT = sqrt(t)
        val d1 = (ln(spot / strike) + (r + 0.5 * sigma * sigma) * t) / (sigma * sqrtT)
        val d2 = d1 - sigma * sqrtT

        val nd1 = normalCdf(d1)
        val nd2 = normalCdf(d2)
        val npdfD1 = normalPdf(d1)

        val delta = if (isCall) nd1 else nd1 - 1.0
        val gamma = npdfD1 / (spot * sigma * sqrtT)
        val vega = (spot * npdfD1 * sqrtT) / 100.0

        val term1 = -(spot * npdfD1 * sigma) / (2.0 * sqrtT)
        val theta = if (isCall) {
            (term1 - r * strike * exp(-r * t) * nd2) / 365.0
        } else {
            (term1 + r * strike * exp(-r * t) * normalCdf(-d2)) / 365.0
        }

        return Greeks(
            delta = (delta * 100).roundToInt() / 100.0,
            gamma = (gamma * 10000).roundToInt() / 10000.0,
            theta = (theta * 100).roundToInt() / 100.0,
            vega = (vega * 100).roundToInt() / 100.0
        )
    }

    /**
     * Max Pain Calculation:
     * Calculates strike where option sellers incur the minimum loss across all open contracts.
     */
    fun calculateMaxPain(strikes: List<OptionStrike>): Double {
        if (strikes.isEmpty()) return 24300.0
        var minLoss = Double.MAX_VALUE
        var maxPainStrike = strikes[strikes.size / 2].strikePrice

        for (candidate in strikes) {
            val candidatePrice = candidate.strikePrice
            var totalLoss = 0.0

            for (s in strikes) {
                // Call writer loss if spot settles at candidatePrice
                if (candidatePrice > s.strikePrice) {
                    totalLoss += (candidatePrice - s.strikePrice) * s.ce.openInterest
                }
                // Put writer loss if spot settles at candidatePrice
                if (candidatePrice < s.strikePrice) {
                    totalLoss += (s.strikePrice - candidatePrice) * s.pe.openInterest
                }
            }

            if (totalLoss < minLoss) {
                minLoss = totalLoss
                maxPainStrike = candidatePrice
            }
        }
        return maxPainStrike
    }

    /**
     * AI Selling Score Engine (0 - 100 Framework):
     * - Market trend alignment: 20 pts
     * - Delta suitability: 15 pts (Sweet spot |delta| between 0.12 and 0.25)
     * - IV conditions: 15 pts (Moderate to high IV is attractive for selling premium)
     * - OI & Chg in OI: 15 pts (High resistance for CE, High support for PE)
     * - Liquidity & Bid-Ask spread: 10 pts
     * - Support/Resistance distance: 10 pts
     * - Volatility & Expiry decay: 10 pts (High theta decay window)
     * - Event risk: 5 pts
     */
    fun evaluateSellingCandidate(
        type: String, // "CE" or "PE"
        leg: OptionLeg,
        strike: Double,
        spot: Double,
        vwap: Double,
        indiaVix: Double,
        trendScore: Int, // 0 to 100, >55 bullish, <45 bearish
        support: Double,
        resistance: Double,
        hedgeStrike: Double,
        hedgePremium: Double,
        daysToExpiry: Int = 3
    ): SellingCandidate {
        val absDelta = abs(leg.delta)
        val breakdown = mutableMapOf<String, Int>()

        // 1. Trend Alignment (20 pts)
        // If selling CE: we want Bearish or Sideways (trend <= 52)
        // If selling PE: we want Bullish or Sideways (trend >= 48)
        val trendPts = if (type == "CE") {
            when {
                trendScore < 45 -> 20 // Bearish market is great for CE selling
                trendScore in 45..55 -> 16 // Sideways
                else -> 8 // Bullish makes CE selling risky
            }
        } else {
            when {
                trendScore > 55 -> 20 // Bullish market is great for PE selling
                trendScore in 45..55 -> 16 // Sideways
                else -> 8 // Bearish makes PE selling risky
            }
        }
        breakdown["Trend Alignment"] = trendPts

        // 2. Delta Suitability (15 pts) - OTM delta 0.12 to 0.22 is gold standard for option sellers
        val deltaPts = when {
            absDelta in 0.12..0.22 -> 15
            absDelta in 0.08..0.28 -> 11
            absDelta in 0.04..0.35 -> 7
            else -> 3
        }
        breakdown["Delta Sweetspot"] = deltaPts

        // 3. IV Condition (15 pts) - Selling benefits from elevated IV (richer premiums)
        val ivPts = when {
            leg.iv in 12.0..18.0 -> 15
            leg.iv in 10.0..22.0 -> 11
            else -> 6
        }
        breakdown["IV Richness"] = ivPts

        // 4. OI and Change in OI (15 pts)
        val oiPts = when {
            leg.openInterest > 8000000 && leg.changeInOI > 500000 -> 15
            leg.openInterest > 5000000 -> 12
            leg.openInterest > 2500000 -> 9
            else -> 5
        }
        breakdown["OI Resistance/Support"] = oiPts

        // 5. Liquidity & Tight Bid-Ask Spread (10 pts)
        val spread = abs(leg.ask - leg.bid)
        val spreadPts = when {
            spread <= 0.30 -> 10
            spread <= 0.60 -> 8
            spread <= 1.20 -> 5
            else -> 2
        }
        breakdown["Bid-Ask Liquidity"] = spreadPts

        // 6. Support/Resistance Buffer (10 pts)
        val bufferPercent = abs(strike - spot) / spot * 100.0
        val bufferPts = when {
            bufferPercent >= 1.5 -> 10
            bufferPercent >= 1.0 -> 8
            bufferPercent >= 0.5 -> 5
            else -> 2
        }
        breakdown["S/R Buffer Distance"] = bufferPts

        // 7. Volatility & Theta Decay (10 pts)
        val decayPts = when (daysToExpiry) {
            in 1..3 -> 10 // Max theta burn phase
            in 4..6 -> 8
            else -> 5
        }
        breakdown["Theta Decay Window"] = decayPts

        // 8. Event Risk (5 pts)
        val eventPts = if (indiaVix < 16.0) 5 else 2
        breakdown["Event Risk Cushion"] = eventPts

        val totalScore = breakdown.values.sum().coerceIn(0, 100)

        val rating = when {
            totalScore >= 80 -> "STRONG CANDIDATE"
            totalScore >= 65 -> "GOOD CANDIDATE"
            totalScore >= 50 -> "MODERATE"
            else -> "AVOID"
        }

        val netCredit = (leg.ltp - hedgePremium).coerceAtLeast(0.0)

        val invalidation = if (type == "CE") {
            (strike - (strike * 0.0035)).roundToInt().toDouble()
        } else {
            (strike + (strike * 0.0035)).roundToInt().toDouble()
        }

        val selectionReason = if (type == "CE") {
            "Strike $strike CE offers attractive premium (₹${leg.ltp}) with delta of ${leg.delta}. Strong OI buildup (${leg.openInterest / 100000}L) at resistance zone $resistance creates a heavy supply wall above spot ₹${spot.roundToInt()}."
        } else {
            "Strike $strike PE offers ₹${leg.ltp} premium with conservative delta of ${leg.delta}. Massive put writing (${leg.openInterest / 100000}L OI) aligns with support floor $support, offering solid protection against downward pullbacks."
        }

        return SellingCandidate(
            type = type,
            strike = strike,
            premium = leg.ltp,
            bid = leg.bid,
            ask = leg.ask,
            oi = leg.openInterest,
            changeInOI = leg.changeInOI,
            iv = leg.iv,
            delta = leg.delta,
            suggestedHedgeStrike = hedgeStrike,
            hedgePremium = hedgePremium,
            netCredit = (netCredit * 100).roundToInt() / 100.0,
            suitabilityScore = totalScore,
            rating = rating,
            entryCondition = "Sell on intraday bounce toward VWAP ₹${vwap.roundToInt()} when RSI is cooling off",
            invalidationLevel = invalidation,
            stopLossMethod = "Exit short leg at 1.5x entry premium (₹${(leg.ltp * 1.5).roundToInt()}) or if spot breaches invalidation",
            profitBooking = "Book 70-80% of max credit or close position at 3:15 PM on expiry day",
            selectionReason = selectionReason,
            scoreBreakdown = breakdown
        )
    }
}
