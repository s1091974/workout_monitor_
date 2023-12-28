package lyi.linyi.posemon.history.PressRecord

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PressRoundRecordDao {
    @Insert
    fun insert(pressRoundRecord: PressRoundRecord)
    @Query("SELECT pressRound FROM press_round_records WHERE pressRound >0 ORDER BY id DESC LIMIT 1")
    fun getLatestPressData():List<Int>

}