package lyi.linyi.posemon.history.PressRecord

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "press_round_records")
data class PressRoundRecord(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val pressRound:Int
)
