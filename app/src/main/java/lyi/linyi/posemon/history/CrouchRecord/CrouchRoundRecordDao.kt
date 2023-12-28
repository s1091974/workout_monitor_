package lyi.linyi.posemon.history.CrouchRecord

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CrouchRoundRecordDao {
    @Insert
    fun insert(crouchRoundRecord: CrouchRoundRecord)

    @Query("SELECT crouchRound FROM crouch_round_records WHERE crouchRound >0 ORDER BY id DESC LIMIT 1")
    fun getLatestCrouchData():List<Int>
}