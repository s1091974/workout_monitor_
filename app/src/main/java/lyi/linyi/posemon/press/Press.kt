/* Copyright 2023 POJUI HUANG. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/


package lyi.linyi.posemon.press

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Process
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import lyi.linyi.posemon.camera.CameraSource_press
import lyi.linyi.posemon.data.Device
import lyi.linyi.posemon.data.Camera
import lyi.linyi.posemon.ml.ModelType
import lyi.linyi.posemon.ml.MoveNet
import lyi.linyi.posemon.ml.PoseClassifier_press
import android.widget.Button
import lyi.linyi.posemon.history.PressRecord.PressRoundRecord
import lyi.linyi.posemon.history.RoundDatabase
import lyi.linyi.posemon.main2
import org.tensorflow.lite.examples.poseestimation.R

class Press : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    /** 为视频画面创建一个 SurfaceView */
    private lateinit var surfaceView: SurfaceView

    /** 修改默认计算设备：CPU、GPU、NNAPI（AI加速器） */
    private var device = Device.CPU

    /** 修改默认摄像头：FRONT、BACK */
    private var selectedCamera = Camera.BACK

    /** 定义几个计数器 */
    private var forwardCounter = 0 //forwardhead = forward
    private var backwardCounter = 0 //crossleg = backward
    private var standardCounter = 0
    private var missingCounter = 0
    private var forwardTimes = 0
    private var standardTimes = 0
    private var backwardTimes = 0
    private var backTimer = 0
    private var timeCounter = 0.0
    private var getstart = true
    private var getonce = false
    private var press_round = 0

    val charAry = charArrayOf('9')


    /** 定义一个历史姿态寄存器 */
    private var poseRegister = "standard"

    /** 设置一个用来显示 Debug 信息的 TextView */
    private lateinit var tvDebug: TextView

    /** 设置一个用来显示当前坐姿状态的 ImageView */
    private lateinit var ivStatus: ImageView

    private lateinit var ivTime: ImageView

    private lateinit var tvFPS: TextView
    private lateinit var tvScore: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnCamera: Spinner

    private var cameraSource: CameraSource_press? = null
    private var isClassifyPose = true

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                /** 得到用户相机授权后，程序开始运行 */
                openCamera()
            } else {
                /** 提示用户“未获得相机权限，应用无法运行” */
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }

    private fun showDiaLogBox(message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)

        tvMessage.text = message

        btnYes.setOnClickListener {
            Toast.makeText(this, "確認", Toast.LENGTH_LONG).show()
            val intent = Intent(this, main2::class.java)
            startActivity(intent)
        }

        dialog.show()
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            /** 如果用户未选择运算设备，使用默认设备进行计算 */
        }
    }

    private var changeCameraListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, view: View?, direction: Int, id: Long) {
            changeCamera(direction)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            /** 如果用户未选择摄像头，使用默认摄像头进行拍摄 */
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.right_arm)

        /** 程序运行时保持屏幕常亮 */
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = findViewById(R.id.tvScore)

        /** 用来显示 Debug 信息 */
        tvDebug = findViewById(R.id.tvDebug)

        /** 用来显示当前坐姿状态 */
        ivStatus = findViewById(R.id.ivStatus)

        ivTime = findViewById(R.id.ivTime)

        tvFPS = findViewById(R.id.tvFps)
        spnDevice = findViewById(R.id.spnDevice)
        spnCamera = findViewById(R.id.spnCamera)
        surfaceView = findViewById(R.id.surfaceView)
        initSpinner()
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    /** 检查相机权限是否有授权 */
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera() {
        /** 音频播放 */
        val crosslegPlayer = MediaPlayer.create(this, R.raw.backward)
        val forwardheadPlayer = MediaPlayer.create(this, R.raw.forward)
        val standardPlayer = MediaPlayer.create(this, R.raw.greatjob)
        val restPlayer = MediaPlayer.create(this, R.raw.rest)
        val finishPlayer = MediaPlayer.create(this, R.raw.seeu)
        val onelastPlayer = MediaPlayer.create(this, R.raw.onelast)
        val twolastPlayer = MediaPlayer.create(this, R.raw.twolast)
        val threelastPlayer = MediaPlayer.create(this, R.raw.threelast)


        //休息音檔
        var crosslegPlayerFlag = true
        var forwardheadPlayerFlag = true
        var standardPlayerFlag = true

        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource_press(
                        surfaceView,
                        selectedCamera,
                        object : CameraSource_press.CameraSourceListener {
                            override fun onFPSListener(fps: Int) {

                                /** 解释一下，tfe_pe_tv 的意思：tensorflow example、pose estimation、text view */
                                tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                            }

                            /** 对检测结果进行处理 */
                            override fun onDetectedInfo(
                                personScore: Float?,
                                poseLabels: List<Pair<String, Float>>?
                            )  {
                                tvScore.text =
                                    getString(R.string.tfe_pe_tv_score, personScore ?: 0f)

                                /** 分析目标姿态，给出提示 */
                                if (poseLabels != null && personScore != null && personScore > 0.3) {
                                    missingCounter = 0
                                    val sortedLabels = poseLabels.sortedByDescending { it.second }

                                    when (sortedLabels[0].first) {
                                        "start" -> {
                                            backwardCounter = 0
                                            forwardCounter = 0
                                            if (getonce) {
                                                timeCounter += 0.5
                                                getstart = true
                                                getonce = false
                                            }
                                        }

                                        "once" -> {
                                            backwardCounter = 0
                                            forwardCounter = 0
                                            if (getstart) {
                                                timeCounter += 0.5
                                                getonce = true
                                                getstart = false
                                            }
                                        }

                                        "forward" -> {
                                            backwardCounter = 0
                                            standardCounter = 0
                                            if (poseRegister == "forward") {
                                                forwardCounter++
                                            }
                                            poseRegister = "forward"
                                            /** 目前姿勢：手臂位置太前 */
                                            if (forwardCounter > 10) {
                                                forwardTimes += 1
                                                /** 播放提示音 */
                                                if (forwardheadPlayerFlag) {
                                                    forwardheadPlayer.start()

                                                }
                                                standardPlayerFlag = true
                                                crosslegPlayerFlag = true
                                                forwardheadPlayerFlag = false

//                                                ivStatus.setImageResource(R.drawable.forward)
                                            }
                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $forwardCounter"
                                            )

                                        }

                                        "backward" -> {
                                            forwardCounter = 0
                                            standardCounter = 0
                                            if (poseRegister == "backward") {
                                                backwardCounter++
                                            }
                                            poseRegister = "backward"

                                            /** 目前姿勢 : 手臂向後抬起 */
                                            if (backwardCounter > 10) {
                                                backwardTimes += 1
                                                /** 播放提示音 */
                                                if (crosslegPlayerFlag) {
                                                    crosslegPlayer.start()
                                                }
                                                standardPlayerFlag = true
                                                crosslegPlayerFlag = false
                                                forwardheadPlayerFlag = true
//                                                ivStatus.setImageResource(R.drawable.backward)
                                            }

                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $backwardCounter"
                                            )
                                        }

                                        else -> {
                                            forwardCounter = 0
                                            backwardCounter = 0
                                            if (poseRegister == "standard") {
                                                standardCounter++
                                            }
                                            poseRegister = "standard"

                                            /** 目前姿勢 : 標準 */
                                            if (standardCounter > 20) {
                                                standardTimes += 1

                                                /** 播放提示音：坐姿标准 */
                                                if (standardPlayerFlag) {
                                                    standardPlayer.start()
                                                }
                                                standardPlayerFlag = false
                                                crosslegPlayerFlag = true
                                                forwardheadPlayerFlag = true

//                                                ivStatus.setImageResource(R.drawable.goodjob)
                                            }

                                            /** 显示 Debug 信息 */
                                            tvDebug.text = getString(
                                                R.string.tfe_pe_tv_debug,
                                                "${sortedLabels[0].first} $standardCounter"
                                            )
                                        }
                                    }

                                    println("time:" + timeCounter)
                                    println("back:" + backTimer)


                                } else {
                                    missingCounter++
//                                    if (missingCounter > 30) {
//                                        ivStatus.setImageResource(R.drawable.whereru)
//                                    }

                                    /** 显示 Debug 信息 */
                                    tvDebug.text = getString(
                                        R.string.tfe_pe_tv_debug,
                                        "missing $missingCounter"
                                    )
                                }

                                if (timeCounter >= 12) {
                                    press_round += 1
                                    if (press_round == 4) {
                                        //播放完成音檔
                                        finishPlayer.start()
                                        //資料庫回傳直放這
                                        val pressRoundRecord = PressRoundRecord(pressRound = press_round)
                                        val database = RoundDatabase.getDatabase(applicationContext)
                                        database.pressRoundRecordDao().insert(pressRoundRecord)

                                        val message: String? = "恭喜完成!"
                                        showDiaLogBox(message)
                                        cameraSource?.close()
                                        cameraSource = null
                                    }
                                    timeCounter = 0.0
                                    getstart = true
                                    getonce = false
                                    if (press_round < 4) {
                                        restPlayer.start()
                                    }
                                    Thread.sleep(5000)
                                    if (press_round == 1) {
                                        //播開始1音檔
                                        threelastPlayer.start()
                                    }
                                    if (press_round == 2) {
                                        //播開始2音檔
                                        twolastPlayer.start()
                                    }
                                    if (press_round == 3) {
                                        //播開始3音檔
                                        onelastPlayer.start()
                                    }

                                }
                                when (timeCounter) {
                                    0.0 -> {
                                        ivTime.setImageResource(R.drawable.time0)
                                    }
                                    0.5 -> {
                                        ivTime.setImageResource(R.drawable.time0)
                                    }

                                    1.0 -> {
                                        ivTime.setImageResource(R.drawable.time1)
                                    }

                                    1.5 -> {
                                        ivTime.setImageResource(R.drawable.time1)
                                    }

                                    2.0 -> {
                                        ivTime.setImageResource(R.drawable.time2)
                                    }

                                    2.5 -> {
                                        ivTime.setImageResource(R.drawable.time2)
                                    }

                                    3.0 -> {
                                        ivTime.setImageResource(R.drawable.time3)
                                    }

                                    3.5 -> {
                                        ivTime.setImageResource(R.drawable.time3)
                                    }

                                    4.0 -> {
                                        ivTime.setImageResource(R.drawable.time4)
                                    }

                                    4.5 -> {
                                        ivTime.setImageResource(R.drawable.time4)
                                    }

                                    5.0 -> {
                                        ivTime.setImageResource(R.drawable.time5)
                                    }

                                    5.5 -> {
                                        ivTime.setImageResource(R.drawable.time5)
                                    }

                                    6.0 -> {
                                        ivTime.setImageResource(R.drawable.time6)
                                    }

                                    6.5 -> {
                                        ivTime.setImageResource(R.drawable.time6)
                                    }

                                    7.0 -> {
                                        ivTime.setImageResource(R.drawable.time7)
                                    }

                                    7.5 -> {
                                        ivTime.setImageResource(R.drawable.time7)
                                    }


                                    8.0 -> {
                                        ivTime.setImageResource(R.drawable.time8)
                                    }

                                    8.5 -> {
                                        ivTime.setImageResource(R.drawable.time8)
                                    }

                                    9.0 -> {
                                        ivTime.setImageResource(R.drawable.time9)
                                    }

                                    9.5 -> {
                                        ivTime.setImageResource(R.drawable.time9)
                                    }

                                    10.0 -> {
                                        ivTime.setImageResource(R.drawable.time10)
                                    }

                                    10.5 -> {
                                        ivTime.setImageResource(R.drawable.time10)
                                    }

                                    11.0 -> {
                                        ivTime.setImageResource(R.drawable.time11)
                                    }

                                    11.5 -> {
                                        ivTime.setImageResource(R.drawable.time11)
                                    }

                                    12.0 -> {
                                        ivTime.setImageResource(R.drawable.time12)
                                    }
                                }
                                when (press_round) {
                                    1 -> {
                                        ivStatus.setImageResource(R.drawable.group1)
                                    }

                                    2 -> {
                                        ivStatus.setImageResource(R.drawable.group2)
                                    }

                                    3 -> {
                                        ivStatus.setImageResource(R.drawable.group3)
                                    }

                                    4 -> {
                                        ivStatus.setImageResource(R.drawable.group4)
                                    }


                                }


                            }
                        }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }

    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier_press.create(this) else null)
    }

    /** 初始化运算设备选项菜单（CPU、GPU、NNAPI） */
    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnDevice.adapter = adapter
            spnDevice.onItemSelectedListener = changeDeviceListener
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_camera_name, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCamera.adapter = adapter
            spnCamera.onItemSelectedListener = changeCameraListener
        }
    }

    /** 在程序运行过程中切换运算设备 */
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    /** 在程序运行过程中切换摄像头 */
    private fun changeCamera(direaction: Int) {
        val targetCamera = when (direaction) {
            0 -> Camera.BACK
            else -> Camera.FRONT
        }
        if (selectedCamera == targetCamera) return
        selectedCamera = targetCamera

        cameraSource?.close()
        cameraSource = null
        openCamera()
    }

    private fun createPoseEstimator() {
        val poseDetector = MoveNet.create(this, device, ModelType.Thunder)
        poseDetector.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                openCamera()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    /** 显示报错信息 */
    class ErrorDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // pass
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
