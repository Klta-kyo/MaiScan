/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2.basic.fragments

import MatDecode
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.PorterDuff
import android.graphics.RectF
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.example.android.camera.utils.GenericListAdapter
import com.example.android.camera.utils.OrientationLiveData
import com.example.android.camera.utils.computeExifOrientation
import com.example.android.camera.utils.getPreviewOutputSize
import com.example.android.camera2.basic.CameraActivity
import com.example.android.camera2.basic.R
import com.example.android.camera2.basic.databinding.FragmentCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.opencv.core.MatOfKeyPoint
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class CameraFragment : Fragment() {
    var infoMap : MutableMap<Int,JSONObject> = LinkedHashMap()
    var imgMap : MutableMap<Int,MatOfKeyPoint> = LinkedHashMap()
    /** Android ViewBinding */
    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    /** AndroidX navigation arguments */
//    private val args: CameraFragmentArgs by navArgs()

    /** Host's navigation controller */
    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment_container)
    }

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
//    private var characteristics: CameraCharacteristics by lazy {
//        cameraManager.getCameraCharacteristics(args.cameraId)
//    }
    private lateinit var characteristics: CameraCharacteristics
    /** Readers used as buffers for camera still shots */
    private lateinit var imageReader: ImageReader


    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply {
        start()
    }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)
    private val imageQueue = ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE)
    private var sizeNumOfCam: Int = 0
    /** Performs recording animation of flashing screen */
    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            fragmentCameraBinding.overlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            fragmentCameraBinding.overlay.postDelayed({
                // Remove white flash animation
                fragmentCameraBinding.overlay.background = null
            }, CameraActivity.ANIMATION_FAST_MILLIS)
        }
    }
    /** [HandlerThread] where all buffer reading operations run */
    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }

    /** [Handler] corresponding to [imageReaderThread] */
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private lateinit var session: CameraCaptureSession
    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData
    private var curImgWidth: Int = 0
    private lateinit var cameraList: List<FormatItem>
    private var curCam: Int = 0
    private data class FormatItem(val title: String, val cameraId: String, val format: Int)
    private lateinit var size: Size
    private fun lensOrientationString(value: Int) = when(value) {
        CameraCharacteristics.LENS_FACING_BACK -> "Back"
        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
        CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
        else -> "Unknown"
    }
    private fun enumerateCameras(cameraManager: CameraManager): List<FormatItem> {
        val availableCameras: MutableList<FormatItem> = mutableListOf()

        // Get list of all compatible cameras
        val cameraIds = cameraManager.cameraIdList.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            val capabilities = characteristics.get(
                    CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
            capabilities?.contains(
                    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
        }


        // Iterate over the list of cameras and return all the compatible ones
        cameraIds.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val orientation = lensOrientationString(
                    characteristics.get(CameraCharacteristics.LENS_FACING)!!)

            // Query the available capabilities and output formats
            val capabilities = characteristics.get(
                    CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
            val outputFormats = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.outputFormats

            // All cameras *must* support JPEG output so we don't need to check characteristics
            availableCameras.add(FormatItem("$orientation ($id)", id, ImageFormat.JPEG))


            // Return cameras that support RAW capability
//                if (capabilities.contains(
//                                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) &&
//                        outputFormats.contains(ImageFormat.RAW_SENSOR)) {
//                    availableCameras.add(FormatItem(
//                            "$orientation RAW ($id)", id, ImageFormat.RAW_SENSOR))
//                }



            // Return cameras that support JPEG DEPTH capability
//                if (capabilities.contains(
//                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT) &&
//                        outputFormats.contains(ImageFormat.DEPTH_JPEG)) {
//                    availableCameras.add(FormatItem(
//                            "$orientation DEPTH ($id)", id, ImageFormat.DEPTH_JPEG))
//                }
        }

        return availableCameras
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val cameraManager =
                requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraList = enumerateCameras(cameraManager)
        curCam = 0
        characteristics = cameraManager.getCameraCharacteristics(cameraList.get(curCam).cameraId)
        val layoutId = android.R.layout.simple_list_item_1


        val lines = object {}.javaClass.getResourceAsStream("music_data.json")?.bufferedReader()?.readLines()

        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
//        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.img1)
//        descriptor1 = SIFTUtils.computeDescriptors(bitmap1)
//        val bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.img2)
//        descriptor3 = SIFTUtils.computeDescriptors(bitmap3)
//        val bitmap4 = BitmapFactory.decodeResource(resources, R.drawable.img3)
//        descriptor4 = SIFTUtils.computeDescriptors(bitmap4)
        context?.let { readMusicData(it) }
        return fragmentCameraBinding.root
    }
    inline fun readMusicData(context: Context){
        var br: BufferedReader? = null
        var fileName = "music_data.json"
        try {
            br = BufferedReader(InputStreamReader(context.resources.assets.open(fileName)))
            var line: String? = null
            val sb = StringBuilder()
            while (br.readLine().also { line = it } != null){
                sb.append(line)
            }
            br.close()
            var json = sb.toString()
            Log.d("TAG","---json: $json")
            val codeList: JSONArray = JSON.parseArray(json)
//            var infoMap : MutableMap<Int,JSONObject> = LinkedHashMap()
//            var imgMap : MutableMap<Int,MatOfKeyPoint> = LinkedHashMap()
//            for(i in 0 until 30){
            for(i in 0 until codeList.size){
                val musicItem: JSONObject = codeList.getJSONObject(i)
                val id : Int = (musicItem.get("id") as String).toInt()
                var zeros = ""
                var x = id
                while(x<=9999){
                    x *= 10
                    zeros += "0"
                }
//                val bitmap = BitmapFactory.decodeStream(context.resources.assets.open("source_resize50/img${zeros}${id}.png"))
//                if(bitmap==null){
//                    Log.d("TAG","error:id:::::::"+musicItem.get("id") as String)
//                    continue
//                }

//                var descriptor: MatOfKeyPoint = SIFTUtils.computeDescriptors(bitmap)
//                MatDecode().save("img${zeros}${id}.dat",descriptor)
//                MatDecode().save(context.filesDir.path+"/img${zeros}${id}.dat",descriptor)
                val descriptor: MatOfKeyPoint? = MatDecode().load(context.resources.assets.open("matSource/img${zeros}${id}.dat"))
                infoMap[id] = musicItem
                imgMap[id] = descriptor?:MatOfKeyPoint()
                Log.d("TAG","123123:"+context.filesDir.path+"/img${zeros}${id}.dat")
                Log.d("TAG","id:::::::"+musicItem.get("id") as String)
            }
            Log.d("TAG","size:${codeList.size},${infoMap.size}")
            Log.d("TAG","maps:${infoMap.size},${imgMap.size}")

        } catch (e: Exception) {
            Log.d("TAG","---e: "+e.toString())
        }finally {
            br?.close()
        }
    }
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCameraBinding.captureButton.setOnApplyWindowInsetsListener { v, insets ->
            v.translationX = (-insets.systemWindowInsetRight).toFloat()
            v.translationY = (-insets.systemWindowInsetBottom).toFloat()
            insets.consumeSystemWindowInsets()
        }

        fragmentCameraBinding.viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder)=Unit

            override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int) {
                Log.d("TAG", "Changed")
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                    fragmentCameraBinding.viewFinder.display,
                    characteristics,
                    SurfaceHolder::class.java
                )

                Log.d("TAG", "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}")
                Log.d("TAG", "Selected preview size: $previewSize")

                Log.d("TAG", "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}")

                // To ensure that size is set, initialize camera in the view's thread
                view.post {
//                    size = characteristics.get(
//                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
//                            .getOutputSizes(cameraList[curCam].format).maxByOrNull { it.height * it.width }!!
                    size = characteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                            .getOutputSizes(cameraList[curCam].format)[2]!!
                    initializeCamera()
                    fragmentCameraBinding.btnChangeSize.setOnClickListener {
                        fragmentCameraBinding.seekBarFrame.progress = 0
                        imageReader.close()
                        session.close()
                        camera.close()
                        sizeNumOfCam += 1
                        var s = characteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                                .getOutputSizes(cameraList[curCam].format)
                        if(sizeNumOfCam>=s.size){
                            sizeNumOfCam = 0
                        }
                        fragmentCameraBinding.viewFinder.holder.setFixedSize(s.get(sizeNumOfCam).width,s.get(sizeNumOfCam).height)
                        size = characteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                                .getOutputSizes(cameraList[curCam].format).get(sizeNumOfCam)
                        initializeCamera()
                    }
                    fragmentCameraBinding.btnChangeCam.setOnClickListener {
                        fragmentCameraBinding.seekBarFrame.progress = 0
                        imageReader.close()
                        session.close()
                        camera.close()
                        curCam += 1
                        sizeNumOfCam = 0
                        if(curCam>=cameraList.size){
                            curCam = 0
                        }
                        var s = characteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                                .getOutputSizes(cameraList[curCam].format)

                        characteristics = cameraManager.getCameraCharacteristics(cameraList.get(curCam).cameraId)
                        fragmentCameraBinding.viewFinder.holder.setFixedSize(s.get(sizeNumOfCam).width,s.get(sizeNumOfCam).height)
                        size = characteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                                .getOutputSizes(cameraList[curCam].format).maxByOrNull { it.width*it.height }!!
                        initializeCamera()
                    }
                }
            }
        })
        fragmentCameraBinding.seekBarFrame.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val params: ViewGroup.LayoutParams = fragmentCameraBinding.imgFrame.layoutParams

                if(context!=null){
                    params.width = curImgWidth*(50+progress)/150
                    fragmentCameraBinding.imgFrame.layoutParams = params
                    Log.d("TAG","current:$progress")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
            observe(viewLifecycleOwner, Observer { orientation ->
                Log.d(TAG, "Orientation changed: $orientation")
            })
        }
    }

    fun updateImage(id: Int, target: ImageView, width: Int){
        if(id==0) return
        var zeros = ""
        var x = id
        var n = 0
        while(x<=9999){
            n +=1
            if(n>=6) return
            x *= 10
            zeros += "0"
        }
        val bitmap1 = BitmapFactory.decodeStream(requireContext().resources.assets.open("source_resize100/img${zeros}${id}.png"))
        val show1 = Bitmap.createBitmap(width,width,Bitmap.Config.ARGB_8888)
        val canvas1 = Canvas(show1)
        val rect1 = RectF(0f,0f,width.toFloat(),width.toFloat())
        canvas1.drawBitmap(bitmap1,null,rect1,null)
        target.setImageBitmap(show1)
    }
    fun updateInfo(id: Int, json: JSONObject){
//        fragmentCameraBinding.textView?.text =
//                "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"

        fragmentCameraBinding.textViewTitle?.text = json.get("title") as String
        fragmentCameraBinding.textViewDs?.text = "定数："+json.get("ds").toString()
        fragmentCameraBinding.textViewVersion?.text = json.get("type") as String
        fragmentCameraBinding.selectedImg?.let { updateImage(id, it,100) }

    }
    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating capture request
     * - Sets up the still image capture listeners
     */
    @SuppressLint("SetTextI18n")
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        // Open the selected camera

        camera = openCamera(cameraManager, cameraList[curCam].cameraId, cameraHandler)

        // Initialize an image reader which will be used to capture still photos


        //解决华为或vivo手机出现的图像变扁的情况

        Log.d("TAG","---xxa::${size.width}, ${size.height}")

        imageReader = ImageReader.newInstance(
                size.width, size.height, cameraList[curCam].format, IMAGE_BUFFER_SIZE)

        val params: ViewGroup.LayoutParams = fragmentCameraBinding.viewFinder.layoutParams
        if(requireContext().resources.displayMetrics.widthPixels*size.width/size.height>requireContext().resources.displayMetrics.heightPixels*0.6){
            Log.d("TAG","------a::${requireContext().resources.displayMetrics.heightPixels.times(0.6).toInt()}, ${params.height*size.height/size.width}")

            params.height = requireContext().resources.displayMetrics.heightPixels.times(0.6).toInt()
            params.width = params.height*size.height/size.width
        }else{
            params.width = requireContext().resources.displayMetrics.widthPixels
            params.height = params.width*size.width/size.height
        }
        curImgWidth = params.width
        fragmentCameraBinding.viewFinder.layoutParams = params
        val params2: ViewGroup.LayoutParams = fragmentCameraBinding.imgFrame.layoutParams
        params2.width = params.width*(50)/150
        params2.height = params.height
        fragmentCameraBinding.imgFrame.layoutParams = params2

        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()
            Log.d(TAG, "Image available in queue: ${image.timestamp}")
            imageQueue.add(image)
        }, imageReaderHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(fragmentCameraBinding.viewFinder.holder.surface, imageReader.surface)
        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)


        val captureRequest = camera.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW).apply { addTarget(fragmentCameraBinding.viewFinder.holder.surface) }

        // This will keep sending the dafcapture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called

        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)


        fragmentCameraBinding.captureButton.setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO) {

                takePhoto2().use { result ->
                    Log.d("TAG", "Result received: $result.")
                    val buffer = result.image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }

                    var bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    var seek  = fragmentCameraBinding.imgFrame.layoutParams.width
                    Log.d("TAG", "Result receivedfffff: ${bitmap.height}.")
                    var width = (bitmap.height*seek/params.width).toInt()

                    var bitmap2 = Bitmap.createBitmap(bitmap,((bitmap.width-width)/2).toInt(),((bitmap.height-width)/2).toInt(),width,width)

                    val result = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)

                    val canvas = Canvas(result)
                    val rect = RectF(0f,0f,50f,50f)
                    canvas.drawBitmap(bitmap2,null,rect,null)
                    Log.d("TAG", "Plane Size: ${result.byteCount}")

                    withContext(Dispatchers.Main) {
//                        val l = requireContext().resources.displayMetrics.widthPixels.times(0.1).toInt()
                        val l = 50
                        val show = Bitmap.createBitmap(l,l,Bitmap.Config.ARGB_8888)

                        val canvas = Canvas(show)
                        val rect = RectF(0f,0f,l.toFloat(),l.toFloat())
                        canvas.drawBitmap(bitmap2,null,rect,null)
                        fragmentCameraBinding.imageView?.setImageBitmap(show)
                    }
                    val descriptor = SIFTUtils.computeDescriptors(result)
                    var max1 = 0.0
                    var maxId1 = 0
                    var max2 = 0.0
                    var maxId2 = 0
                    var max3 = 0.0
                    var maxId3 = 0
                    Log.d("TAG", "Start Comparing")
                    var i =0
                    var cur = 0.0
                    for ((key, value) in imgMap) {
                        Log.d("TAG", "Comparing: ${key}")
                        cur = SIFTUtils.similarity(value, descriptor)
                        Log.d("TAG", "Res: ${cur}")
                        if(cur==-1.0){
                            break
                        }
                        if(cur>max1){
                            max1 = cur
                            maxId1 = key
                        }else if(cur>max2){
                            max2 = cur
                            maxId2 = key
                        }else if(cur>max3){
                            max3 = cur
                            maxId3 = key
                        }
                        withContext(Dispatchers.Main){
                            fragmentCameraBinding.progressBar.progress = i*100/imgMap.size
                            i += 1
                        }
                    }
                    if(cur==-1.0){
                        withContext(Dispatchers.Main) {
                            fragmentCameraBinding.textViewTitle?.text = "没有找到结果！"
                            fragmentCameraBinding.textViewDs?.text = "nya~"
                            fragmentCameraBinding.textViewVersion?.text = ""
                        }
                    }else{
                    val maxJson1: JSONObject? = infoMap[maxId1]
                    val maxJson2: JSONObject? = infoMap[maxId2]
                    val maxJson3: JSONObject? = infoMap[maxId3]
//                    Log.d("TAG", "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}")

                    val padding = 6
                    withContext(Dispatchers.Main){
                        fragmentCameraBinding.imageShow1!!.setPadding(padding,padding,padding,padding)
                        fragmentCameraBinding.imageShow2!!.setPadding(0,0,0,0)
                        fragmentCameraBinding.imageShow3!!.setPadding(0,0,0,0)
                        if (maxJson1 != null) {
                            Log.d("TAG", maxJson1.toString())
                            updateInfo(maxId1,maxJson1)
                        }
                        fragmentCameraBinding.imageShow1?.let { it1 -> updateImage(maxId1, it1,50) }
                        fragmentCameraBinding.imageShow1?.setOnClickListener {
                            fragmentCameraBinding.imageShow1!!.setPadding(padding,padding,padding,padding)
                            fragmentCameraBinding.imageShow2!!.setPadding(0,0,0,0)
                            fragmentCameraBinding.imageShow3!!.setPadding(0,0,0,0)
//                            fragmentCameraBinding.textView?.text =
//                                    "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"
                            if (maxJson1 != null) {
                                updateInfo(maxId1,maxJson1)
                            }

                        }
                        fragmentCameraBinding.imageShow2?.let { it2 -> updateImage(maxId2, it2,50) }
                        fragmentCameraBinding.imageShow2?.setOnClickListener {
                            fragmentCameraBinding.imageShow1!!.setPadding(0,0,0,0)
                            fragmentCameraBinding.imageShow2!!.setPadding(padding,padding,padding,padding)
                            fragmentCameraBinding.imageShow3!!.setPadding(0,0,0,0)
//                            fragmentCameraBinding.textView?.text =
//                                            "result2: ${maxId2},Name: ${maxJson2?.get("title")},Acc:${max2},Ds: ${maxJson2?.get("ds")}\n"
                            if (maxJson2 != null) {
                                updateInfo(maxId2,maxJson2)
                            }

                        }
                        fragmentCameraBinding.imageShow3?.let { it3 -> updateImage(maxId3, it3,50) }
                        fragmentCameraBinding.imageShow3?.setOnClickListener {
                            fragmentCameraBinding.imageShow1!!.setPadding(0,0,0,0)
                            fragmentCameraBinding.imageShow2!!.setPadding(0,0,0,0)
                            fragmentCameraBinding.imageShow3!!.setPadding(padding,padding,padding,padding)
//                            fragmentCameraBinding.textView?.text =
//                                            "result3: ${maxId3},Name: ${maxJson3?.get("title")},Acc:${max3},Ds: ${maxJson3?.get("ds")}\n"
                            if (maxJson3 != null) {
                                updateInfo(maxId3,maxJson3)
                            }

                        }

                    }
                    }
                    it.post { it.isEnabled = true }
                }
            }
        }

        // Listen to the capture button
//        fragmentCameraBinding.captureButton.setOnClickListener {
//
//            // Disable click listener to prevent multiple requests simultaneously in flight
//            it.isEnabled = false
//
//            // Perform I/O heavy operations in a different scope
//            lifecycleScope.launch(Dispatchers.IO) {
//                takePhoto2().use { result ->{
//                    Log.d(TAG, "Result received: $result")
//                    Log.d(TAG, "Plane Size: ${result.image.planes.size}")
//                }
//                    // Save the result to disk
////                    val output = saveResult(result)
////                    Log.d(TAG, "Image saved: ${output.absolutePath}")
////
////                    // If the result is a JPEG file, update EXIF metadata with orientation info
////                    if (output.extension == "jpg") {
////                        val exif = ExifInterface(output.absolutePath)
////                        exif.setAttribute(
////                                ExifInterface.TAG_ORIENTATION, result.orientation.toString())
////                        exif.saveAttributes()
////                        Log.d(TAG, "EXIF metadata saved: ${output.absolutePath}")
////                    }
////
////                    // Display the photo taken to user
////                    lifecycleScope.launch(Dispatchers.Main) {
////                        navController.navigate(CameraFragmentDirections
////                                .actionCameraToJpegViewer(output.absolutePath)
////                                .setOrientation(result.orientation)
////                                .setDepth(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
////                                        result.format == ImageFormat.DEPTH_JPEG))
////                    }
//                }
//
//                // Re-enable click listener after photo is taken
//                it.post { it.isEnabled = true }
//            }
//        }
    }

    private fun getHashList(result: Bitmap, width: Int): FloatArray {
        var hashList: FloatArray = FloatArray(width*width)
//        var max = Float.MIN_VALUE
//        var min = Float.MAX_VALUE
        var sum =0f
        for (i in 0..15) {
            for (j in 0..15) {
                hashList[i*width+j] = (result.getPixel(j,i).red+result.getPixel(j,i).green+result.getPixel(j,i).blue).toFloat()
//                hashList[i*width+j] = (result.getPixel(j,i).red).toFloat()
                sum += hashList[i*width+j]
//                if(hashList[i*width+j]>max){
//                    max = hashList[i*width+j]
//                }else if(hashList[i*width+j]<min){
//                    min = hashList[i*width+j]
//                }
            }
        }
        sum =sum/16/16
        for (i in 0..15) {
            for (j in 0..15) {
                if(hashList[i*width+j]>sum){
                    hashList[i*width+j]=1f
                }else{
                    hashList[i*width+j]=0f
                }
//                hashList[i*width+j] = ((hashList[i*width+j]-min).toFloat()/(max-min))
            }
        }
        return hashList
    }
    private fun openCamera(image: Image, widthPercent: Float, heightOffsetPercent: Float, mapHeight: Int){

        var imgHeightOffset = (heightOffsetPercent*image.height).toInt()
        var imgWidth = (widthPercent*image.width).toInt()
        if(imgWidth<mapHeight){
            throw Exception()
        }
        var widthOffset = ((image.width-imgWidth)/2).toInt()
        var width = image.width
        var height = image.height
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
            manager: CameraManager,
            cameraId: String,
            handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Starts a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine
     */
    private suspend fun createCaptureSession(
            device: CameraDevice,
            targets: List<Surface>,
            handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Create a capture session using the predefined targets; this also involves defining the
        // session state callback to be notified of when the session is ready
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Helper function used to capture a still image using the [CameraDevice.TEMPLATE_STILL_CAPTURE]
     * template. It performs synchronization between the [CaptureResult] and the [Image] resulting
     * from the single capture, and outputs a [CombinedCaptureResult] object.
     */


    private suspend fun takePhoto2():
            CombinedCaptureResult = suspendCoroutine { cont ->

        // Flush any images left in the image reader
        @Suppress("ControlFlowWithEmptyBody")
        while (imageReader.acquireNextImage() != null) {
        }

        // Start a new image queue
        val imageQueue = ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()
            Log.d(TAG, "Image available in queue: ${image.timestamp}")
            imageQueue.add(image)
        }, imageReaderHandler)

        val captureRequest = session.device.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE).apply { addTarget(imageReader.surface) }
        session.capture(captureRequest.build(), object : CameraCaptureSession.CaptureCallback() {

            override fun onCaptureStarted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    timestamp: Long,
                    frameNumber: Long) {
                super.onCaptureStarted(session, request, timestamp, frameNumber)
            }

            override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult) {
                super.onCaptureCompleted(session, request, result)
                val resultTimestamp = result.get(CaptureResult.SENSOR_TIMESTAMP)
                Log.d(TAG, "Capture result received: $resultTimestamp")

                // Set a timeout in case image captured is dropped from the pipeline
                val exc = TimeoutException("Image dequeuing took too long")
                val timeoutRunnable = Runnable { cont.resumeWithException(exc) }
                imageReaderHandler.postDelayed(timeoutRunnable, IMAGE_CAPTURE_TIMEOUT_MILLIS)

                // Loop in the coroutine's context until an image with matching timestamp comes
                // We need to launch the coroutine context again because the callback is done in
                //  the handler provided to the `capture` method, not in our coroutine context
                @Suppress("BlockingMethodInNonBlockingContext")
                lifecycleScope.launch(cont.context) {
                    while (true) {

                        // Dequeue images while timestamps don't match
                        val image = imageQueue.take()
                        // TODO(owahltinez): b/142011420
                        // if (image.timestamp != resultTimestamp) continue
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                image.format != ImageFormat.DEPTH_JPEG &&
                                image.timestamp != resultTimestamp) continue
                        Log.d(TAG, "Matching image dequeued: ${image.timestamp}")

                        // Unset the image reader listener
                        imageReaderHandler.removeCallbacks(timeoutRunnable)
                        imageReader.setOnImageAvailableListener(null, null)

                        // Clear the queue of images, if there are left
                        while (imageQueue.size > 0) {
                            imageQueue.take().close()
                        }

                        // Compute EXIF orientation metadata
//                        val rotation = relativeOrientation.value ?: 0
//                        val mirrored = characteristics.get(CameraCharacteristics.LENS_FACING) ==
//                                CameraCharacteristics.LENS_FACING_FRONT
//                        val exifOrientation = computeExifOrientation(rotation, mirrored)
                        val exifOrientation = computeExifOrientation(0,false)
                        // Build the result and resume progress
                        cont.resume(CombinedCaptureResult(
                                image, result, exifOrientation, imageReader.imageFormat))

                        // There is no need to break out of the loop, this coroutine will suspend
                    }
                }
            }
        }, cameraHandler)
    }


    /** Helper function used to save a [CombinedCaptureResult] into a [File] */
    private suspend fun saveResult(result: CombinedCaptureResult): File = suspendCoroutine { cont ->
        when (result.format) {

            // When the format is JPEG or DEPTH JPEG we can simply save the bytes as-is
            ImageFormat.JPEG, ImageFormat.DEPTH_JPEG -> {
                val buffer = result.image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                try {
                    val output = createFile(requireContext(), "jpg")
                    FileOutputStream(output).use { it.write(bytes) }
                    cont.resume(output)
                } catch (exc: IOException) {
                    Log.e(TAG, "Unable to write JPEG image to file", exc)
                    cont.resumeWithException(exc)
                }
            }

            // When the format is RAW we use the DngCreator utility library
            ImageFormat.RAW_SENSOR -> {
                val dngCreator = DngCreator(characteristics, result.metadata)
                try {
                    val output = createFile(requireContext(), "dng")
                    FileOutputStream(output).use { dngCreator.writeImage(it, result.image) }
                    cont.resume(output)
                } catch (exc: IOException) {
                    Log.e(TAG, "Unable to write DNG image to file", exc)
                    cont.resumeWithException(exc)
                }
            }

            // No other formats are supported by this sample
            else -> {
                val exc = RuntimeException("Unknown image format: ${result.image.format}")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()
    }
    companion object {
        private val TAG = CameraFragment::class.java.simpleName

        /** Maximum number of images that will be held in the reader's buffer */
        private const val IMAGE_BUFFER_SIZE: Int = 3

        /** Maximum time allowed to wait for the result of an image capture */
        private const val IMAGE_CAPTURE_TIMEOUT_MILLIS: Long = 5000

        /** Helper data class used to hold capture metadata with their associated image */
        data class CombinedCaptureResult(
                val image: Image,
                val metadata: CaptureResult,
                val orientation: Int,
                val format: Int
        ) : Closeable {
            override fun close() = image.close()
        }

        /**
         * Create a [File] named a using formatted timestamp with the current date and time.
         *
         * @return [File] created.
         */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.filesDir, "IMG_${sdf.format(Date())}.$extension")
        }
    }
}
