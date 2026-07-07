package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY date DESC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Insert
    suspend fun insertBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)
}
