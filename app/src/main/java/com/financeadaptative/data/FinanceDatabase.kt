package com.financeadaptative.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.financeadaptative.data.dao.AccountDao
import com.financeadaptative.data.dao.CategoryDao
import com.financeadaptative.data.dao.ExpenseDao
import com.financeadaptative.data.entity.*
import java.time.LocalDateTime

@Database(
    entities = [Account::class, Category::class, Expense::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, MovementTypeConverter::class)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: build(context).also { INSTANCE = it }
        }

        private fun build(context: Context): FinanceDatabase = Room.databaseBuilder(
            context.applicationContext,
            FinanceDatabase::class.java,
            "finance.db"
        ).build()
    }
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromEpoch(value: Long?): LocalDateTime? = value?.let { LocalDateTime.ofEpochSecond(it, 0, java.time.ZoneOffset.UTC) }

    @TypeConverter
    fun toEpoch(date: LocalDateTime?): Long? = date?.toEpochSecond(java.time.ZoneOffset.UTC)
}

class MovementTypeConverter {
    @TypeConverter
    fun fromString(value: String?): MovementType? = value?.let { MovementType.valueOf(it) }

    @TypeConverter
    fun toString(type: MovementType?): String? = type?.name
}
