package lyi.linyi.posemon.leftarm
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
import org.tensorflow.lite.examples.poseestimation.R


class Down_test : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.down_test)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val packageName = "android.resource://" + getPackageName() + "/" + R.raw.arm
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
            val intent = Intent(this, LeftArm::class.java)
            startActivity(intent)
        }
    }
}