package lyi.linyi.posemon.history.LeftRecord

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import lyi.linyi.posemon.history.LeftRecord.LeftRoundRecord
@Dao
interface LeftRoundRecordDao {
    @Insert
    fun insert(leftRoundRecord: LeftRoundRecord)

    @Query("SELECT leftRound FROM left_round_records WHERE leftRound >0 ORDER BY id DESC LIMIT 1")
    fun getLatestLeftArmData(): List<Int>
}