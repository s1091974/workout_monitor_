package lyi.linyi.posemon
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import lyi.linyi.posemon.MainActivity2
import org.tensorflow.lite.examples.poseestimation.R


class login : AppCompatActivity() {

    private lateinit var loginbtn: Button
    private lateinit var editemail: EditText
    private lateinit var editpword: EditText
    private lateinit var dbh: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginbtn = findViewById(R.id.button4)
        editemail = findViewById(R.id.editTextText2)
        editpword = findViewById(R.id.editTextTextPassword3)
        dbh = DBHelper(this)

        loginbtn.setOnClickListener {

            val emailedtx = editemail.text.toString()
            val passedtx = editpword.text.toString()
            val sharedPrefs = getSharedPreferences ("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("emailedtx", emailedtx)
            editor.apply()

            if (TextUtils.isEmpty(emailedtx) || TextUtils.isEmpty(passedtx)){
                Toast.makeText(this,"輸入使用者名稱和密碼",Toast.LENGTH_SHORT).show()
            }
            else{
                val checkemail = dbh.checkuserpass(emailedtx, passedtx)
                if (checkemail==true){
                    Toast.makeText(this,"登入成功!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, main2::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"使用者名稱或密碼錯誤",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}