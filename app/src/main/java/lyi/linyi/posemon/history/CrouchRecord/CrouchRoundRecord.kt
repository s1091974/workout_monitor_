package lyi.linyi.posemon.history.CrouchRecord

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crouch_round_records")
data class CrouchRoundRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val crouchRound: Int
)
