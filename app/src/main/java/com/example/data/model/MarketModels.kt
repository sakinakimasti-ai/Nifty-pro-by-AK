package com.example.data.model

enum class DataProviderType(val displayName: String, val badgeLabel: String) {
    YAHOO_LIVE("Yahoo Finance (FREE - LIVE - No API Key)", "🟢 LIVE via Yahoo"),
    NSE_PUBLIC("NSE Public Option Chain (Rate-Limited)", "🟡 NSE Session"),
    DEMO("Demo Data (Realistic Market Simulation)", "🔵 DEMO DATA – NOT LIVE")
}

enum class MarketStatus(val label: String) {
    OPEN("MARKET OPEN"),
    CLOSED("MARKET CLOSED"),
    PRE_OPEN("PRE-OPEN")
}

data class SpotData(
    val symbol: String = "NIFTY 50",
    val spotPrice: Double = 24350.25,
    val prevClose: Double = 24280.00,
    val openPrice: Double = 24295.50,
    val dayHigh: Double = 24410.80,
    val dayLow: Double = 24260.40,
    val change: Double = 70.25,
    val changePercent: Double = 0.29,
    val vwap: Double = 24335.60,
    val indiaVix: Double = 12.85,
    val vixChange: Double = -0.42,
    val marketStatus: MarketStatus = MarketStatus.OPEN,
    val timestamp: String = "10 Nov 2026, 15:30:00 IST",
    val isLive: Boolean = true,
    val providerStatus: String = "🟢 LIVE via Yahoo",
    val isRefreshing: Boolean = false,
    val candleHistory: List<SpotCandle> = emptyList()
)

data class SpotCandle(
    val timestamp: Long,
    val timeLabel: String,
    val price: Double
)

data class OptionLeg(
    val strikePrice: Double,
    val ltp: Double,
    val change: Double,
    val pChange: Double,
    val bid: Double,
    val ask: Double,
    val volume: Long,
    val openInterest: Long,
    val changeInOI: Long,
    val iv: Double,
    val delta: Double,
    val theta: Double,
    val gamma: Double,
    val vega: Double
)

data class OptionStrike(
    val strikePrice: Double,
    val atmDistance: Double,
    val expiryDate: String,
    val ce: OptionLeg,
    val pe: OptionLeg,
    val isAtm: Boolean = false,
    val isHighestCeOi: Boolean = false,
    val isHighestPeOi: Boolean = false,
    val isHighestCeChgOi: Boolean = false,
    val isHighestPeChgOi: Boolean = false
)

data class OptionChainData(
    val underlyingValue: Double = 24350.25,
    val expiryDates: List<String> = emptyList(),
    val selectedExpiry: String = "",
    val strikes: List<OptionStrike> = emptyList(),
    val totalCeOi: Long = 0L,
    val totalPeOi: Long = 0L,
    val totalCeVolume: Long = 0L,
    val totalPeVolume: Long = 0L,
    val pcrOi: Double = 1.05,
    val pcrVolume: Double = 0.98,
    val maxPain: Double = 24300.0,
    val atmIv: Double = 13.2,
    val ivChange: Double = -0.35,
    val timestamp: String = ""
)

data class SellingCandidate(
    val type: String, // "CE" or "PE"
    val strike: Double,
    val premium: Double,
    val bid: Double,
    val ask: Double,
    val oi: Long,
    val changeInOI: Long,
    val iv: Double,
    val delta: Double,
    val suggestedHedgeStrike: Double,
    val hedgePremium: Double,
    val netCredit: Double,
    val suitabilityScore: Int, // 0 - 100
    val rating: String, // STRONG CANDIDATE, GOOD CANDIDATE, MODERATE, AVOID
    val entryCondition: String,
    val invalidationLevel: Double,
    val stopLossMethod: String,
    val profitBooking: String,
    val selectionReason: String,
    val scoreBreakdown: Map<String, Int>
)

data class MarketDirection(
    val status: String = "BULLISH", // STRONG BULLISH, BULLISH, SIDEWAYS, BEARISH, STRONG BEARISH, HIGH VOLATILITY, NO TRADE
    val strengthScore: Int = 74,
    val immediateSupport: Double = 24200.0,
    val strongSupport: Double = 24000.0,
    val immediateResistance: Double = 24500.0,
    val strongResistance: Double = 24600.0,
    val momentum: String = "Mild Positive (+0.32%)",
    val volatilityCondition: String = "Normal (VIX 12.85)",
    val marketStructure: String = "Higher Lows on 15m intraday, VWAP support holding",
    val reason: String = "Spot trading above VWAP (24,335) with robust PE put writing at 24,200/24,300 strikes and healthy PCR of 1.12 indicating solid cushion on dips."
)

data class NewsEvent(
    val id: String,
    val title: String,
    val date: String,
    val timeIst: String,
    val category: String, // "INDIAN" or "GLOBAL"
    val importance: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val previous: String,
    val forecast: String,
    val actual: String,
    val sourceLink: String,
    val marketImpact: String,
    val increasesVolatility: Boolean
)

data class RiskAnalysis(
    val capital: Double = 500000.0,
    val riskPercent: Double = 2.0,
    val lots: Int = 2,
    val lotSize: Int = 75, // NIFTY Current lot size (75 / official master)
    val shortPremium: Double = 85.0,
    val hedgePremium: Double = 22.0,
    val stopLossPercent: Double = 50.0,
    val targetPercent: Double = 70.0,
    val estimatedMargin: Double = 185000.0,
    val netCreditPoints: Double = 63.0,
    val maxPotentialProfit: Double = 9450.0,
    val maxPotentialLoss: Double = 20550.0,
    val riskRewardRatio: String = "1 : 2.17",
    val estimatedBrokerageTaxes: Double = 145.0,
    val netEstimatedPnl: Double = 9305.0,
    val breakEvenSpot: Double = 24563.0,
    val isOverRiskLimit: Boolean = false
)

data class TradeSetup(
    val title: String,
    val strategy: String,
    val shortStrike: String,
    val hedgeStrike: String,
    val entryPriceRange: String,
    val stopLoss: String,
    val target: String,
    val maxRisk: String,
    val maxReward: String,
    val rrRatio: String,
    val reasoning: String,
    val invalidation: String,
    val isNoTrade: Boolean = false,
    val noTradeReason: String = ""
)
