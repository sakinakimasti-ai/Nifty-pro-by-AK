package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "trade_journal")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tradeDate: String,
    val strategy: String,
    val optionType: String, // "CE" or "PE" or "CONDOR"
    val strike: Double,
    val entryPrice: Double,
    val exitPrice: Double,
    val quantity: Int,
    val brokerage: Double = 40.0,
    val pnl: Double,
    val reason: String,
    val notes: String
)

@Dao
interface TradeDao {
    @Query("SELECT * FROM trade_journal ORDER BY id DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity)

    @Delete
    suspend fun deleteTrade(trade: TradeEntity)

    @Query("DELETE FROM trade_journal")
    suspend fun clearAll()
}

@Database(entities = [TradeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
}
