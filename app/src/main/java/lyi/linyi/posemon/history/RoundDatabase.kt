package lyi.linyi.posemon.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lyi.linyi.posemon.history.CrouchRecord.CrouchRoundRecord
import lyi.linyi.posemon.history.CrouchRecord.CrouchRoundRecordDao
import lyi.linyi.posemon.history.LeftRecord.LeftRoundRecord
import lyi.linyi.posemon.history.LeftRecord.LeftRoundRecordDao
import lyi.linyi.posemon.history.PressRecord.PressRoundRecord
import lyi.linyi.posemon.history.PressRecord.PressRoundRecordDao
import lyi.linyi.posemon.history.RightRecord.RoundRecord
import lyi.linyi.posemon.history.RightRecord.RoundRecordDao


@Database(entities = [RoundRecord::class, LeftRoundRecord::class, CrouchRoundRecord::class, PressRoundRecord::class], version = 4, exportSchema = true)

abstract class RoundDatabase : RoomDatabase() {
    abstract fun roundRecordDao(): RoundRecordDao
    abstract fun leftRoundRecordDao(): LeftRoundRecordDao
    abstract fun crouchRoundRecordDao():CrouchRoundRecordDao
    abstract fun pressRoundRecordDao():PressRoundRecordDao
    companion object {
        @Volatile
        private var INSTANCE: RoundDatabase? = null

        fun getDatabase(context: Context): RoundDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoundDatabase::class.java,
                    "round_records"
                )   .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()


                INSTANCE = instance
                instance

            }
        }
    }
}
