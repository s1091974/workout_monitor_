package lyi.linyi.posemon.history.RightRecord

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import lyi.linyi.posemon.history.RightRecord.RoundRecord

@Dao
interface RoundRecordDao {
    @Insert
    fun insert(roundRecord: RoundRecord)


    @Query("SELECT rightRound FROM round_records WHERE rightRound > 0 ORDER BY id DESC LIMIT 1")
        fun getLatestRightArmData(): List<Int>


}
