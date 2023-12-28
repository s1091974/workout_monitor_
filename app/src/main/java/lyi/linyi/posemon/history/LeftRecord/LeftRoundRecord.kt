package lyi.linyi.posemon.history.LeftRecord

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "left_round_records")
data class LeftRoundRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val leftRound: Int
)
