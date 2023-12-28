package lyi.linyi.posemon

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.tensorflow.lite.examples.poseestimation.R

class signup : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var pword:EditText
    private lateinit var cpword:EditText
    private lateinit var name:EditText
    private lateinit var gender:EditText
    private lateinit var height:EditText
    private lateinit var weight:EditText
    private lateinit var signupbtn: Button
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.email)
        pword = findViewById(R.id.password1)
        cpword = findViewById(R.id.password2)
        name = findViewById(R.id.name)
        gender = findViewById(R.id.gender)
        height = findViewById(R.id.height)
        weight = findViewById(R.id.weight)
        signupbtn = findViewById(R.id.button3)
        db = DBHelper(this)

        signupbtn.setOnClickListener {
            val emailtext = email.text.toString()
            val pwordtext = pword.text.toString()
            val cpwordtext = cpword.text.toString()
            val nametext = name.text.toString()
            val gendertext = gender.text.toString()
            val heighttext = height.text.toString().toDoubleOrNull()
            val weighttext = weight.text.toString().toDoubleOrNull()

            val intent2 = Intent(this, Editprofile::class.java)
            intent2.putExtra("email", emailtext)
            intent2.putExtra("password", pwordtext)
            intent2.putExtra("name", nametext)
            intent2.putExtra("gender", gendertext)
            intent2.putExtra("height", heighttext)
            intent2.putExtra("weight", weighttext)

            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("userName", nametext) // 將用戶名稱添加到 Intent 中
            startActivity(intent)

            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("userEmail", emailtext)
            editor.putString("userPassword", pwordtext)
            editor.putString("userName", nametext)
            editor.putString("userGender", gendertext)
            editor.putString("userHeight", heighttext.toString())
            editor.putString("userWeight", weighttext.toString())
            editor.apply()


            if (TextUtils.isEmpty(emailtext) || TextUtils.isEmpty(pwordtext) || TextUtils.isEmpty(cpwordtext) || TextUtils.isEmpty(nametext) || TextUtils.isEmpty(gendertext) || heighttext == null || weighttext == null) {
                Toast.makeText(this, "請確認內容是否皆有輸入且正確", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, signup::class.java)
                startActivity(intent)
            } else if (pwordtext != cpwordtext) {
                Toast.makeText(this, "密碼不一樣", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, signup::class.java)
                startActivity(intent)
            } else {
                val savedata = db.insertdata(emailtext, pwordtext, nametext, gendertext, heighttext, weighttext)
                if (savedata) {
                    Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, login::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "使用者已創建過", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, signup::class.java)
                    startActivity(intent)
                }
            }

        }
    }
}