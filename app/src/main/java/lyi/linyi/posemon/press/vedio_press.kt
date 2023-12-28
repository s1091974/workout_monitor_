package lyi.linyi.posemon.press

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import lyi.linyi.posemon.leftarm.LeftArm
import org.tensorflow.lite.examples.poseestimation.R


class vedio_press : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vedio_press)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val packageName = "android.resource://" + getPackageName() + "/" + R.raw.press
        val uri = Uri.parse(packageName)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)

        val imageView = findViewById<ImageView>(R.id.rightPreviewImage)
        imageView.setOnClickListener {
            videoView.start()
            imageView.visibility = View.GONE
        }


        val rightArmActbutton = findViewById<ImageView>(R.id.goright_Btn)
        rightArmActbutton.setOnClickListener {
            val intent = Intent(this, Press::class.java)
            startActivity(intent)
        }
    }
}