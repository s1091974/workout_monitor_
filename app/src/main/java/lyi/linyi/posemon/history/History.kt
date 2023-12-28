package lyi.linyi.posemon.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lyi.linyi.posemon.MainActivity2
import lyi.linyi.posemon.main2
import org.tensorflow.lite.examples.poseestimation.R
import org.tensorflow.lite.examples.poseestimation.databinding.NewActBinding

class History : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        // 檢查 data_source 標誌的值
        //右手
        val dataSource = intent.getStringExtra("data_source")
        //左手
        val datasource = intent.getStringExtra("left_data_source")
        //深蹲
        val Datasource = intent.getStringExtra("crouch_data_source")
        //肩舉
        val Datasource1 = intent.getStringExtra("press_data_source")
        if (dataSource == "RightArm") {
            GlobalScope.launch(Dispatchers.IO) {
                val database = RoundDatabase.getDatabase(applicationContext)
                val latestRightArmData = database.roundRecordDao().getLatestRightArmData()
                // 選取 rightRound 欄位並轉換為 List<Int>
                val formattedData = latestRightArmData.joinToString("")
                val textViewRightArm = findViewById<TextView>(R.id.roundtimes)
                textViewRightArm.text = formattedData
            }
        } else {
            // 其他操作
        }

        if(datasource =="LeftArm"){
            GlobalScope.launch(Dispatchers.IO) {
                val database = RoundDatabase.getDatabase(applicationContext)
                val latestLeftArmData = database.leftRoundRecordDao().getLatestLeftArmData()
                // 選取 leftRound 欄位並轉換為 List<Int>
                val formattedData = latestLeftArmData.joinToString(",  ")
                val textViewLeftArm = findViewById<TextView>(R.id.leftroundtimes)
                textViewLeftArm.text = formattedData
            }
        } else {
            // 其他操作
        }

        if(Datasource =="Crouch"){
            GlobalScope.launch(Dispatchers.IO) {
                val database = RoundDatabase.getDatabase(applicationContext)
                val latestPressData = database.pressRoundRecordDao().getLatestPressData()
                // 選取 pressRound 欄位並轉換為 List<Int>
                val formattedData = latestPressData.joinToString(",  ")
                val textViewLeftArm = findViewById<TextView>(R.id.presstimes)
                textViewLeftArm.text = formattedData
            }
        } else {
            // 其他操作
        }

        if(Datasource1 =="Press"){
            GlobalScope.launch(Dispatchers.IO) {
                val database = RoundDatabase.getDatabase(applicationContext)
                val latestLeftArmData = database.crouchRoundRecordDao().getLatestCrouchData()
                // 選取 crouchRound 欄位並轉換為 List<Int>
                val formattedData = latestLeftArmData.joinToString(",  ")
                val textViewLeftArm = findViewById<TextView>(R.id.crouchtimes)
                textViewLeftArm.text = formattedData
            }
        } else {
            // 其他操作
        }

        //xml中的返回按鈕
        val backButton = findViewById<Button>(R.id.backbutton)
        backButton.setOnClickListener {

            val intent2 = Intent(this, main2::class.java)
            startActivity(intent2)
        }
    }
}
