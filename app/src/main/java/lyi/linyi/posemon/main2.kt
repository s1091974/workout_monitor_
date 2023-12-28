/**
 * 此APP主要對 林亦Pose monitor master 專案的 MainActivity.kt 與
 * 對 Tensorflow Lite Pose Estimation 示例項目的 activity_main.xml 文件進行改寫，
 * 示例項目中其餘文件除了包名調整，除新增同類檔案外，基本無改動，
 * 原版全歸 林亦 及 The Tensorflow Authors 所有 */
package lyi.linyi.posemon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import lyi.linyi.posemon.Editprofile
import lyi.linyi.posemon.MainActivity2

import lyi.linyi.posemon.crouch.vedio_crouch
import lyi.linyi.posemon.history.History

import lyi.linyi.posemon.leftarm.Down_test

import lyi.linyi.posemon.press.vedio_press
import lyi.linyi.posemon.rightarm.Down
import org.tensorflow.lite.examples.poseestimation.R

class main2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_act)

        val rightArmActbutton = findViewById<ImageView>(R.id.rightBtn)
        rightArmActbutton.setOnClickListener {
            val intent = Intent(this, Down::class.java)
            startActivity(intent)
        }
        val leftArmActbutton = findViewById<ImageView>(R.id.leftBtn)
        leftArmActbutton.setOnClickListener {
            val intent = Intent(this, Down_test::class.java)

            startActivity(intent)
        }
        val pressActbutton = findViewById<ImageView>(R.id.pressBtn)
        pressActbutton.setOnClickListener {
            val intent = Intent(this, vedio_press::class.java)
            startActivity(intent)
        }
        val crouchActbutton = findViewById<ImageView>(R.id.crouchBtn)
        crouchActbutton.setOnClickListener {
            val intent = Intent(this, vedio_crouch::class.java)
            startActivity(intent)
        }

//        val settingActbutton = findViewById<ImageButton>(R.id.setBtn)
//        settingActbutton.setOnClickListener {
//            val intent = Intent(this, MainActivity2::class.java)
//            startActivity(intent)
//        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHome -> {
                    // 跳转到 HomeActivity
                    val intent = Intent(this, main2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuSetting -> {
                    // 跳转到 SettingActivity
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuHistory -> {
                    // 跳转到 HistoryActivity
                    val intent = Intent(this, History::class.java)
                    startActivity(intent)
                    true

                }
                else -> false
            }
        }
    }
}