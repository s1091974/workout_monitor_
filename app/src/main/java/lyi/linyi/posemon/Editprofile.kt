package lyi.linyi.posemon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import coil.load
import coil.transform.CircleCropTransformation
import org.tensorflow.lite.examples.poseestimation.R
import org.tensorflow.lite.examples.poseestimation.databinding.ActivityEditprofileBinding


class Editprofile : AppCompatActivity() {
    private lateinit var binding: ActivityEditprofileBinding
    private val galleryRequestCode = 2
    private lateinit var mainImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        galleryCheckPermission()
        mainImageView = binding.userImage

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userImageUri = sharedPrefs.getString("userImageUri", null)

        if (userImageUri != null) {
            val uri = Uri.parse(userImageUri)
            mainImageView.load(uri) {
                crossfade(true)
                crossfade(1000)
                transformations(CircleCropTransformation())
            }
        } else {
            mainImageView.setImageResource(R.drawable.profile)
        }

        val changeImageBtn = findViewById<ImageView>(R.id.userImage)
        changeImageBtn.setOnClickListener {
            gallery()
        }

        val backBtn = findViewById<ImageView>(R.id.backbtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        val usermail = sharedPrefs.getString("userEmail", null)
        val userPassword = sharedPrefs.getString("userPassword", null)
        val userName = sharedPrefs.getString("userName", null)
        val userGender = sharedPrefs.getString("userGender", null)
        val userHeightStr = sharedPrefs.getString("userHeight", null)
        val userWeightStr = sharedPrefs.getString("userWeight", null)


//        val userPassword = intent.getStringExtra("password")
//        val userName = intent.getStringExtra("name")
//        val userGender = intent.getStringExtra("gender")
//        val userHeight = intent.getDoubleExtra("height", 0.0)
//        val userWeight = intent.getDoubleExtra("weight", 0.0)

        val username = findViewById<TextView>(R.id.username1)
        val userEmail = findViewById<TextView>(R.id.email)
        val editNewPassword = findViewById<EditText>(R.id.editpassword)
        val editNewName = findViewById<EditText>(R.id.editname)
        val editNewGender = findViewById<EditText>(R.id.editgender)
        val editNewHeight = findViewById<EditText>(R.id.editheight)
        val editNewWeight = findViewById<EditText>(R.id.editweight)
        val btnSaveData = findViewById<Button>(R.id.btnSave)

        if (userName != null) {
            username.setText(userName)
        }

        if (userEmail != null) {
            userEmail.setText(usermail)
        }

        if (userPassword != null) {
            editNewPassword.setText(userPassword)
        }

        if (userName != null) {
            editNewName.setText(userName)
        }

        if (userGender != null) {
            editNewGender.setText(userGender)
        }

        if (userHeightStr != null) {
            editNewHeight.setText(userHeightStr)
        }

        if (userWeightStr != null) {
            editNewWeight.setText(userWeightStr)
        }

//        editNewPassword.setText(userPassword)
//        editNewName.setText(userName)
//        editNewGender.setText(userGender)
//        editNewHeight.setText(userHeight.toString())
//        editNewWeight.setText(userWeight.toString())

        btnSaveData.setOnClickListener {
            val newPassword = editNewPassword.text.toString()
            val newName = editNewName.text.toString()
            val newGender = editNewGender.text.toString()
            val newHeightStr = editNewHeight.text.toString()
            val newWeightStr = editNewWeight.text.toString()

            // 添加數字過濾器以確保輸入的是數字
            editNewHeight.filters = arrayOf(NumericInputFilter())
            editNewWeight.filters = arrayOf(NumericInputFilter())

            if (newPassword.isNotEmpty() && newName.isNotEmpty() && newGender.isNotEmpty() && newHeightStr.isNotEmpty() && newWeightStr.isNotEmpty()) {
                val newHeight = newHeightStr.toDoubleOrNull()
                val newWeight = newWeightStr.toDoubleOrNull()

                if (newHeight != null && newWeight != null) {
                    val dbHelper = DBHelper(this)
                    val userEmail = sharedPrefs.getString("emailedtx", "")

                    if (userEmail != null) {
                        if (dbHelper.updateUserData(
                                userEmail,
                                newPassword,
                                newName,
                                newGender,
                                newHeight,
                                newWeight
                            )
                        ) {
                            Toast.makeText(
                                this,
                                "使用者資料更新成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            // 清除已輸入的資料
                            clearEditTexts(editNewPassword, editNewName, editNewGender, editNewHeight, editNewWeight)

                            val editor = sharedPrefs.edit()
                            editor.putString("userPassword", newPassword)
                            editor.putString("userName", newName)
                            editor.putString("userGender", newGender)
                            editor.putString("userHeight", newHeightStr)
                            editor.putString("userWeight", newWeightStr)
                            editor.apply()

                            val intent = Intent(applicationContext, MainActivity2::class.java)
                            intent.putExtra("userName", newName)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "使用者資料更新失敗", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "身高或體重無效", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "請輸入所有必填資料", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearEditTexts(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.text.clear()
        }
    }

    class NumericInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            for (i in start until end) {
                if (!Character.isDigit(source!![i])) {
                    return ""
                }
            }
            return null
        }
    }

//    private fun galleryCheckPermission() {
//        Dexter.withContext(this).withPermission(
//            android.Manifest.permission.READ_EXTERNAL_STORAGE
//        ).withListener(object : PermissionListener {
//            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
//                gallery()
//            }
//
//            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
//                Toast.makeText(
//                    this@Editprofile,
//                    "您已拒絕選擇圖片的儲存權限",
//                    Toast.LENGTH_SHORT
//                ).show()
//
////                showRotationalDialogForPermission()
//            }
//
//            override fun onPermissionRationaleShouldBeShown(
//                p0: PermissionDeniedResponse?,
//                p1: PermissionToken?
//            ) {
////                showRotationalDialogForPermission()
//            }
//        }).onSameThread().check()
//    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, galleryRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                galleryRequestCode -> {
                    val selectedImageUri = data?.data
                    val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("userImageUri", selectedImageUri.toString())
                    editor.apply()

                    // 載入並設置圖片
                    mainImageView.load(selectedImageUri) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }
    }

//    private fun showRotationalDialogForPermission() {
//        AlertDialog.Builder(this)
//            .setMessage(
//                "It looks like you have turned off permissions "
//                        + "required for this feature. You can enable them in App settings!!!"
//            )
//
//            .setPositiveButton("Go To Settings") { _, _ ->
//                try {
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivity(intent)
//
//                } catch (e: ActivityNotFoundException) {
//                    e.printStackTrace()
//                }
//            }
//            .setNegativeButton("CANCEL") { dialog, _ ->
//                dialog.dismiss()
//            }.show()
//    }
}