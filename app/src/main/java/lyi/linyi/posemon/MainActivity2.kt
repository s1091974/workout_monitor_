package lyi.linyi.posemon

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import lyi.linyi.posemon.history.History
//import com.example.settings.Editprofile
//import com.example.settings.loginpage
import org.tensorflow.lite.examples.poseestimation.R


class MainActivity2 : AppCompatActivity() {

    private lateinit var mainImageView: ImageView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        mainImageView = findViewById(R.id.userImage)
        textView = findViewById(R.id.username)

        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userImageUri = sharedPrefs.getString("userImageUri", null)

        if (userImageUri != null) {
            val uri = Uri.parse(userImageUri)
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(mainImageView)
        } else {
            mainImageView.setImageResource(R.drawable.profile)
        }

        var openActivityBtn = findViewById<Button>(R.id.openActivityBtn)
        openActivityBtn.setOnClickListener {
            val intent = Intent(this, Editprofile::class.java)
            startActivity(intent)
        }

        var logOutBtn = findViewById<ImageView>(R.id.logOutbtn)
        logOutBtn.setOnClickListener {
            val intent = Intent(this, loginpage::class.java)
            startActivity(intent)
        }
        var backBtn = findViewById<ImageView>(R.id.backbtn2)
        backBtn.setOnClickListener {
            val intent = Intent(this, main2::class.java)
            startActivity(intent)
        }

//        val userName = intent.getStringExtra("userName")
//
//        if (userName != null) {
//            textView.text = userName
//        } else {
//            textView.text = ""
//        }


        val userName = sharedPrefs.getString("userName", null)
        val nameTextView = findViewById<TextView>(R.id.username)

        if (userName != null) {
            nameTextView.setText(userName)
        }

        val emailTextView = findViewById<TextView>(R.id.email)
        val userEmail = sharedPrefs.getString("emailedtx", "")

        if (!userEmail.isNullOrBlank()) {
            emailTextView.text = userEmail
        } else {
            emailTextView.text = " "
        }
        val historyActbutton = findViewById<ImageView>(R.id.historyBtn)
        historyActbutton.setOnClickListener {
            val intent = Intent(this, History::class.java)
            intent.putExtra("data_source", "RightArm")
            intent.putExtra("left_data_source", "LeftArm")
            intent.putExtra("crouch_data_source", "Crouch")
            intent.putExtra("press_data_source", "Press")
            startActivity(intent)

            val intent2 = Intent(this, History::class.java)
            startActivity(intent2)

        }

    }
    override fun onResume() {
        super.onResume()
        val userName = intent.getStringExtra("userName")
        if (userName != null) {
            textView.text = userName
        }
    }
}