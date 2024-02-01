package com.example.android.camera2.basic.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.example.android.camera2.basic.CameraActivity
import com.example.android.camera2.basic.R
import com.example.android.camera2.basic.databinding.FragmentTextBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TextFragment : Fragment() {


    private var mUri: Uri? = null

    private var _fragmentTextBinding: FragmentTextBinding? = null

    private val fragmentTextBinding get() = _fragmentTextBinding!!

//    private val startForProfileImageResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            val resultCode = result.resultCode
//            val data = result.data
//
//            if (resultCode == Activity.RESULT_OK) {
//                //Image Uri will not be null for RESULT_OK
//                val fileUri = data?.data!!
//
//                mUri = fileUri
//                lifecycleScope.launch(Dispatchers.IO) {
//
//                    Log.d("TAG", "Result received: $result.")
//
//                    var bitmap = BitmapFactory.decodeFile(mUri!!.path)
//                    Log.d("TAG", "Result receivedfffff: ${bitmap.height}.")
//
//
//                    val result = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888)
//
//                    val canvas = Canvas(result)
//                    val rect = RectF(0f,0f,50f,50f)
//                    canvas.drawBitmap(bitmap,null,rect,null)
//                    Log.d("TAG", "Plane Size: ${result.byteCount}")
//                    withContext(Dispatchers.Main) {
////                        val l = requireContext().resources.displayMetrics.widthPixels.times(0.1).toInt()
//                        val l = 50
//                        val show = Bitmap.createBitmap(l,l,Bitmap.Config.ARGB_8888)
//
//                        val canvas = Canvas(show)
//                        val rect = RectF(0f,0f,l.toFloat(),l.toFloat())
//                        canvas.drawBitmap(bitmap,null,rect,null)
//                        fragmentTextBinding.imageView.setImageBitmap(show)
//                    }
//                    val descriptor = SIFTUtils.computeDescriptors(result)
//                    var max1 = 0.0
//                    var maxId1 = 0
//                    var max2 = 0.0
//                    var maxId2 = 0
//                    var max3 = 0.0
//                    var maxId3 = 0
//                    Log.d("TAG", "Start Comparing")
//                    var i =0
//                    var cur = 0.0
//                    for ((key, value) in (activity as CameraActivity).imgMap) {
//                        Log.d("TAG", "Comparing: ${key}")
//                        cur = SIFTUtils.similarity(value, descriptor)
//                        Log.d("TAG", "Res: ${cur}")
//                        if(cur==-1.0){
//                            break
//                        }
//                        if(cur>max1){
//                            max1 = cur
//                            maxId1 = key
//                        }else if(cur>max2){
//                            max2 = cur
//                            maxId2 = key
//                        }else if(cur>max3){
//                            max3 = cur
//                            maxId3 = key
//                        }
//                        withContext(Dispatchers.Main){
//                            fragmentTextBinding.progressBar.progress = i*100/(activity as CameraActivity).imgMap.size
//                            i += 1
//                        }
//                    }
//                    var test = mutableListOf<Int>()
//                    if(maxId1!=0){
//                        test.add(maxId1)
//                    }
//                    if(maxId2!=0){
//                        test.add(maxId2)
//                    }
//                    if(maxId3!=0){
//                        test.add(maxId3)
//                    }
//                    showResList(test,fragmentTextBinding.resLayout)
////                    if(cur==-1.0){
////                        withContext(Dispatchers.Main) {
////                            fragmentGalleryBinding.textViewTitle.text = "没有找到结果！"
////                            fragmentGalleryBinding.textViewDs.text = "nya~"
////                            fragmentGalleryBinding.textViewVersion.text = ""
////                            val drawable = ContextCompat.getDrawable(
////                                activity as CameraActivity,
////                                com.example.android.camera2.basic.R.drawable.def)
////                            val drawable50 = ContextCompat.getDrawable(
////                                activity as CameraActivity,
////                                com.example.android.camera2.basic.R.drawable.def_resize50)
////                            fragmentGalleryBinding.selectedImg.setImageDrawable(drawable)
////                            fragmentGalleryBinding.imageShow1.setImageDrawable(drawable50)
////                            fragmentGalleryBinding.imageShow2.setImageDrawable(drawable50)
////                            fragmentGalleryBinding.imageShow3.setImageDrawable(drawable50)
////                        }
////                    }else{
////                        val maxJson1: JSONObject? = (activity as CameraActivity).infoMap[maxId1]
////                        val maxJson2: JSONObject? = (activity as CameraActivity).infoMap[maxId2]
////                        val maxJson3: JSONObject? = (activity as CameraActivity).infoMap[maxId3]
//////                    Log.d("TAG", "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}")
////
////                        val padding = dpToPx(3,requireContext())
////                        withContext(Dispatchers.Main){
////                            fragmentGalleryBinding.imageShow1.setPadding(padding,padding,padding,padding)
////                            fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
////                            fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
////                            if (maxJson1 != null) {
////                                Log.d("TAG", maxJson1.toString())
////                                updateInfo(maxId1,maxJson1)
////                            }
////                            fragmentGalleryBinding.imageShow1.let { it1 -> updateImage(maxId1, it1,50) }
////                            fragmentGalleryBinding.imageShow1.setOnClickListener {
////                                fragmentGalleryBinding.imageShow1.setPadding(padding,padding,padding,padding)
////                                fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
////                                fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
//////                            fragmentCameraBinding.textView?.text =
//////                                    "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"
////                                if (maxJson1 != null) {
////                                    updateInfo(maxId1,maxJson1)
////                                }
////
////                            }
////                            fragmentGalleryBinding.imageShow2.let { it2 -> updateImage(maxId2, it2,50) }
////                            fragmentGalleryBinding.imageShow2.setOnClickListener {
////                                fragmentGalleryBinding.imageShow1.setPadding(0,0,0,0)
////                                fragmentGalleryBinding.imageShow2.setPadding(padding,padding,padding,padding)
////                                fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
//////                            fragmentCameraBinding.textView?.text =
//////                                            "result2: ${maxId2},Name: ${maxJson2?.get("title")},Acc:${max2},Ds: ${maxJson2?.get("ds")}\n"
////                                if (maxJson2 != null) {
////                                    updateInfo(maxId2,maxJson2)
////                                }
////
////                            }
////                            fragmentGalleryBinding.imageShow3.let { it3 -> updateImage(maxId3, it3,50) }
////                            fragmentGalleryBinding.imageShow3.setOnClickListener {
////                                fragmentGalleryBinding.imageShow1.setPadding(0,0,0,0)
////                                fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
////                                fragmentGalleryBinding.imageShow3.setPadding(padding,padding,padding,padding)
//////                            fragmentCameraBinding.textView?.text =
//////                                            "result3: ${maxId3},Name: ${maxJson3?.get("title")},Acc:${max3},Ds: ${maxJson3?.get("ds")}\n"
////                                if (maxJson3 != null) {
////                                    updateInfo(maxId3,maxJson3)
////                                }
////
////                            }
////
////                        }
////                    }
//                    withContext(Dispatchers.Main) {
//                        fragmentTextBinding.btnTestSelect.isEnabled = true
//                        fragmentTextBinding.btnSearch.isEnabled = true
//                        fragmentTextBinding.editTextText.isEnabled = true
//                        fragmentTextBinding.spinner.isEnabled = true
//                    }
//                }
//            } else if (resultCode == ImagePicker.RESULT_ERROR) {
//                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
////                fragmentTextBinding.btnTestSelect.isEnabled = true
//                fragmentTextBinding.btnSearch.isEnabled = true
//                fragmentTextBinding.editTextText.isEnabled = true
//                fragmentTextBinding.spinner.isEnabled = true
//            } else {
//                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
////                fragmentTextBinding.btnTestSelect.isEnabled = true
//                fragmentTextBinding.btnSearch.isEnabled = true
//                fragmentTextBinding.editTextText.isEnabled = true
//                fragmentTextBinding.spinner.isEnabled = true
//            }
//
//
//        }

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
    fun updateInfo(id: Int, json: JSONObject, chartJson: JSONArray?){
        var basic = json.get("basic_info") as JSONObject
//        fragmentCameraBinding.textView?.text =
//                "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"

        fragmentTextBinding.textViewTitle.text = json.get("title") as String
        var ds:String = "官方定数："
        for(j in (json.get("ds") as JSONArray)){
            ds+= String.format("%.2f",j.toString().toFloat())+"  "
        }
        ds+="\n"
        if(chartJson!=null){
            ds += "拟合定数："
            for(j in chartJson){
                if((j as JSONObject).contains("fit_diff")){
                    ds+= String.format("%.2f",(j as JSONObject).get("fit_diff").toString().toFloat())+"  "
                }else{
                    ds+="  "
                }
            }
        }else{
            ds += "未有拟合定数记录！"
        }
        fragmentTextBinding.textViewDs.text = ds
        fragmentTextBinding.textViewVersion.text = basic.get("from").toString()
        fragmentTextBinding.selectedImg.let { updateImage(id, it,100) }
        fragmentTextBinding.btnToDetail.setOnClickListener {
            var transaction: FragmentTransaction? = null
            transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment1 = IntroFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putString("from", "text")
            fragment1.arguments = bundle
            transaction.replace(R.id.fragment_container, fragment1)
            transaction.commit()
        }
        fragmentTextBinding.selectedImg.setOnClickListener {
            var transaction2: FragmentTransaction? = null
            transaction2 = requireActivity().supportFragmentManager.beginTransaction()
            val fragment1 = IntroFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putString("from", "text")
            fragment1.arguments = bundle
            transaction2.replace(R.id.fragment_container, fragment1)
            transaction2.commit()
        }
    }
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentTextBinding = FragmentTextBinding.inflate(inflater, container, false)

        val test = arrayListOf("曲名","别名","ID","定数","等级")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, test)
        fragmentTextBinding.spinner.adapter = adapter



//        fragmentTextBinding.btnTestSelect.setOnClickListener{
//            fragmentTextBinding.btnTestSelect.isEnabled = false
//            fragmentTextBinding.btnSearch.isEnabled = false
//            fragmentTextBinding.editTextText.isEnabled = false
//            fragmentTextBinding.spinner.isEnabled = false
//            ImagePicker.with(this)
//                .cropSquare()	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .createIntent { intent ->
//                    startForProfileImageResult.launch(intent)
//                }
//        }
        fragmentTextBinding.btnSearch.setOnClickListener {
//            fragmentTextBinding.btnTestSelect.isEnabled = false
            fragmentTextBinding.btnSearch.isEnabled = false
            fragmentTextBinding.editTextText.isEnabled = false
            fragmentTextBinding.spinner.isEnabled = false

            val input = fragmentTextBinding.editTextText.text.toString()
            val cate = fragmentTextBinding.spinner.selectedItem.toString()
            val resList = getResList(input,cate)
            //Image Uri will not be null for RESULT_OK
//            val drawable50 = ContextCompat.getDrawable(
//                activity as CameraActivity,
//                com.example.android.camera2.basic.R.drawable.def_resize50)
//            fragmentTextBinding.imageView.setImageDrawable(drawable50)
            showResList(resList,fragmentTextBinding.resLayout)
        }
        return fragmentTextBinding.root
    }
    fun showResList(resList:MutableSet<Int>,resLayout:LinearLayout){
        lifecycleScope.launch(Dispatchers.IO) {
            if(resList.isEmpty()){
                withContext(Dispatchers.Main) {
                    resLayout.removeAllViews()
                    fragmentTextBinding.textViewTitle.text = "没有找到结果！"
                    fragmentTextBinding.textViewDs.text = "nya~"
                    fragmentTextBinding.textViewVersion.text = ""
                    val drawable = ContextCompat.getDrawable(
                        activity as CameraActivity,
                        com.example.android.camera2.basic.R.drawable.def)
                    fragmentTextBinding.selectedImg.setImageDrawable(drawable)
                }
            }else{
                withContext(Dispatchers.Main) {
                    var imgList = mutableListOf<ImageView>()
                    resLayout.removeAllViews()
                    for (id in resList) {
                        val json: JSONObject? = (activity as CameraActivity).infoMap[id]
                        var jsonArray: JSONArray? = null
                        if((activity as CameraActivity).chartMap.contains(id)){
                            jsonArray = (activity as CameraActivity).chartMap[id]
                        }
                        var img: ImageView = ImageView(requireContext())
                        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(50,50)
                        with(params) {
                            height = dpToPx(50,requireContext())
                            width = dpToPx(50,requireContext())
                            leftMargin = dpToPx(5,requireContext())
                            rightMargin = dpToPx(5,requireContext())
                        }
                        img.layoutParams = params
                        val yellow = ContextCompat.getDrawable(
                            activity as CameraActivity,
                            com.example.android.camera2.basic.R.drawable.bg_border1
                        )
                        img.background = yellow
                        val padding = dpToPx(3,requireContext())
                        img.setPadding(padding,padding,padding,padding)
                        img.let { it1 -> updateImage(id, it1, 50) }
                        img.setOnClickListener {
                            for(v in imgList){
                                v.setPadding(0,0,0,0)
                            }
                            img.setPadding(padding,padding,padding,padding)
                            if (json != null) {
                                updateInfo(id,json,jsonArray)
                            }
                        }
                        imgList.add(img)
                        resLayout.addView(img)
                    }
                    for(v in imgList){
                        v.setPadding(0,0,0,0)
                    }
                    val padding = dpToPx(3,requireContext())
                    imgList[0].setPadding(padding,padding,padding,padding)
                    val json: JSONObject? = (activity as CameraActivity).infoMap[resList.first()]
                    var jsonArray: JSONArray? = null
                    if((activity as CameraActivity).chartMap.contains(resList.first())){
                        jsonArray = (activity as CameraActivity).chartMap[resList.first()]
                    }
                    if(json!=null){
                        updateInfo(resList.first(),json,jsonArray)
                    }
                }

//                    val maxJson2: JSONObject? = (activity as CameraActivity).infoMap[maxId2]
//                    val maxJson3: JSONObject? = (activity as CameraActivity).infoMap[maxId3]
////                    Log.d("TAG", "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}")
//
//                    val padding = 6
//                    withContext(Dispatchers.Main){
//                        fragmentGalleryBinding.imageShow1.setPadding(padding,padding,padding,padding)
//                        fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
//                        fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
//                        if (maxJson1 != null) {
//                            Log.d("TAG", maxJson1.toString())
//                            updateInfo(maxId1,maxJson1)
//                        }
//                        fragmentGalleryBinding.imageShow1.let { it1 -> updateImage(maxId1, it1,50) }
//                        fragmentGalleryBinding.imageShow1.setOnClickListener {
//                            fragmentGalleryBinding.imageShow1.setPadding(padding,padding,padding,padding)
//                            fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
//                            fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
////                            fragmentCameraBinding.textView?.text =
////                                    "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"
//                            if (maxJson1 != null) {
//                                updateInfo(maxId1,maxJson1)
//                            }
//
//                        }
//                        fragmentGalleryBinding.imageShow2.let { it2 -> updateImage(maxId2, it2,50) }
//                        fragmentGalleryBinding.imageShow2.setOnClickListener {
//                            fragmentGalleryBinding.imageShow1.setPadding(0,0,0,0)
//                            fragmentGalleryBinding.imageShow2.setPadding(padding,padding,padding,padding)
//                            fragmentGalleryBinding.imageShow3.setPadding(0,0,0,0)
////                            fragmentCameraBinding.textView?.text =
////                                            "result2: ${maxId2},Name: ${maxJson2?.get("title")},Acc:${max2},Ds: ${maxJson2?.get("ds")}\n"
//                            if (maxJson2 != null) {
//                                updateInfo(maxId2,maxJson2)
//                            }
//
//                        }
//                        fragmentGalleryBinding.imageShow3.let { it3 -> updateImage(maxId3, it3,50) }
//                        fragmentGalleryBinding.imageShow3.setOnClickListener {
//                            fragmentGalleryBinding.imageShow1.setPadding(0,0,0,0)
//                            fragmentGalleryBinding.imageShow2.setPadding(0,0,0,0)
//                            fragmentGalleryBinding.imageShow3.setPadding(padding,padding,padding,padding)
////                            fragmentCameraBinding.textView?.text =
////                                            "result3: ${maxId3},Name: ${maxJson3?.get("title")},Acc:${max3},Ds: ${maxJson3?.get("ds")}\n"
//                            if (maxJson3 != null) {
//                                updateInfo(maxId3,maxJson3)
//                            }
//
//                        }
//
//                    }
            }
            withContext(Dispatchers.Main) {
//                fragmentTextBinding.btnTestSelect.isEnabled = true
                fragmentTextBinding.btnSearch.isEnabled = true
                fragmentTextBinding.editTextText.isEnabled = true
                fragmentTextBinding.spinner.isEnabled = true
            }
        }
    }
    fun getResList(input:String,cate:String): MutableSet<Int> {
        var test = mutableSetOf<Int>()
        val infoMap = (activity as CameraActivity).infoMap
        val aliasSearch = (activity as CameraActivity).aliasMapSearch
        if(cate=="ID"){
            if(input.toIntOrNull()!=null&&infoMap.contains(input.toInt())){
                test.add(input.toInt())
            }
            return test
        }
        if(cate=="曲名"){
            if(input == ""||input==" "){
                return test
            }
            for((id, json) in infoMap){
                if((json.get("title") as String).contains(input, ignoreCase = true)){
                    test.add(id)
                }
            }
            return test
        }else if(cate=="定数"){
            if(input.toFloatOrNull()!=null){
                for((id, json) in infoMap){
                    val ds = json.get("ds") as JSONArray
                    for (d in ds){
                        if(d.toString().toFloat()==input.toFloat()){
                            test.add(id)
                        }
                    }
                }
            }
            return test
        }else if(cate=="等级"){
            for((id, json) in infoMap){
                val level = json.get("level") as JSONArray
                for (d in level){
                    if(d.toString().equals(input)){
                        test.add(id)
                    }
                }
            }
            return test
        }else if(cate=="别名"){
            for((str, id) in aliasSearch){
                if(str.toString().contains(input)){
                    test.add(id)
                }
            }
            return test
        }
        return test
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
//        imgProfile.setDrawableImage(R.drawable.ic_person, true)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_github -> {
//                IntentUtil.openURL(this, GITHUB_REPOSITORY)
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    /*private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                // Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                mProfileUri = fileUri
                imgProfile.setLocalImage(fileUri, true)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }*/

    /**
     * Ref: https://gist.github.com/granoeste/5574148
     */
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            // Uri object will not be null for RESULT_OK
//            val uri: Uri = data?.data!!
//            mGalleryUri = uri
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
//        }
//    }


}
