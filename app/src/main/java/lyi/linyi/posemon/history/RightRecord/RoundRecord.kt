package lyi.linyi.posemon.history.RightRecord

import androidx.room.Entity
import androidx.room.PrimaryKey

        @Entity(tableName = "round_records")
        data class RoundRecord(
            @PrimaryKey(autoGenerate = true)
            val id: Long = 0,
            val rightRound: Int,
    )

