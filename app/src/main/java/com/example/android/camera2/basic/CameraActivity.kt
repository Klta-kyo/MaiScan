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

package com.example.android.camera2.basic

import MatDecode
import android.R
import android.R.id
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.example.android.camera2.basic.databinding.ActivityCameraBinding
import com.example.android.camera2.basic.fragments.AboutFragment
import com.example.android.camera2.basic.fragments.CameraFragment
import com.example.android.camera2.basic.fragments.GalleryFragment
import com.example.android.camera2.basic.fragments.IntroFragment
import com.example.android.camera2.basic.fragments.PermissionsFragment
import com.example.android.camera2.basic.fragments.TextFragment
import com.github.dhaval2404.imagepicker.ImagePicker
import org.opencv.android.OpenCVLoader
import org.opencv.core.MatOfKeyPoint
import java.io.BufferedReader
import java.io.InputStreamReader


class CameraActivity : AppCompatActivity() {


    var chartMap : MutableMap<Int, JSONArray> = LinkedHashMap()
    var infoMap : MutableMap<Int, JSONObject> = LinkedHashMap()
    var imgMap : MutableMap<Int, MatOfKeyPoint> = LinkedHashMap()
    var aliasMapSearch : MutableMap<String, Int> = LinkedHashMap()
    var aliasMap : MutableMap<Int, JSONArray> = LinkedHashMap()
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
//                Log.d("TAG","123123:"+context.filesDir.path+"/img${zeros}${id}.dat")
//                Log.d("TAG","id:::::::"+musicItem.get("id") as String)
            }
//            Log.d("TAG","size:${codeList.size},${infoMap.size}")
//            Log.d("TAG","maps:${infoMap.size},${imgMap.size}")

        } catch (e: Exception) {
            Log.d("TAG","---e: "+e.toString())
        }finally {
            br?.close()
        }
    }

    inline fun readAliasData(context: Context){
        var br: BufferedReader? = null
        var fileName = "maimaidxalias.json"
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
            val codeList: JSONObject = JSON.parseObject(json)
//            var infoMap : MutableMap<Int,JSONObject> = LinkedHashMap()
//            var imgMap : MutableMap<Int,MatOfKeyPoint> = LinkedHashMap()
//            for(i in 0 until 30){
            for((k,v) in codeList){
                if(k.toIntOrNull()==null){
                    continue
                }else{
                    var alias:JSONArray = (v as JSONObject)["Alias"] as JSONArray
                    aliasMap.set(k.toInt(),alias)
                    for(s in alias){
                        aliasMapSearch.set(s.toString(),k.toInt())
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("TAG","---e: "+e.toString())
        }finally {
            br?.close()
        }
    }

    inline fun readChartData(context: Context){
        var br: BufferedReader? = null
        var fileName = "chart_stats.json"
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
            val codeList: JSONObject = JSON.parseObject(json).getJSONObject("charts")
//            var infoMap : MutableMap<Int,JSONObject> = LinkedHashMap()
//            var imgMap : MutableMap<Int,MatOfKeyPoint> = LinkedHashMap()
//            for(i in 0 until 30){
            for((k,v) in codeList){
                val id : Int = k.toInt()
                chartMap[id] = v as JSONArray
                Log.d("TAG","chartMap::${chartMap[id]}")
            }
            Log.d("TAG","size:${codeList.size},${infoMap.size}")
            Log.d("TAG","maps:${infoMap.size},${imgMap.size}")

        } catch (e: Exception) {
            Log.d("TAG","---e: "+e.toString())
        }finally {
            br?.close()
        }
    }

    private lateinit var activityCameraBinding: ActivityCameraBinding
    private var fragmentManager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null
    private var fragment0: Fragment? = null
    private var fragment1: Fragment? = null
    private var fragment2: Fragment? = null
    private var fragment3: Fragment? = null
    private var fragment4: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(activityCameraBinding.root)
        val loaded = OpenCVLoader.initDebug()
        Log.d("TAG", "loaded: $loaded")

        fragmentManager = supportFragmentManager
        transaction = fragmentManager!!.beginTransaction()

        fragment0 = PermissionsFragment()
        transaction!!.replace(activityCameraBinding.fragmentContainer.id, fragment0!!)
        transaction!!.commit()
        readMusicData(this)
        readChartData(this)
        readAliasData(this)
    }
    fun changeFrag_cam(view:View){
        transaction = fragmentManager!!.beginTransaction()
        fragment1 = CameraFragment()
        transaction!!.replace(activityCameraBinding.fragmentContainer.id, fragment1 as CameraFragment)
        transaction!!.commit()
    }
    fun changeFrag_gallery(view:View){
        transaction = fragmentManager!!.beginTransaction()
        fragment2 = GalleryFragment()
        transaction!!.replace(activityCameraBinding.fragmentContainer.id, fragment2 as GalleryFragment)
        transaction!!.commit()
    }
    fun changeFrag_text(view:View){
        transaction = fragmentManager!!.beginTransaction()
        fragment3 = TextFragment()
        transaction!!.replace(activityCameraBinding.fragmentContainer.id, fragment3 as TextFragment)
        transaction!!.commit()
    }
    fun changeFrag_about(view:View){
        transaction = fragmentManager!!.beginTransaction()
        fragment4 = AboutFragment()
        transaction!!.replace(activityCameraBinding.fragmentContainer.id, fragment4 as AboutFragment)
        transaction!!.commit()
    }
    override fun onRestart() {
        super.onRestart()
        Log.d("TAG", "restart")
    }
    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        activityCameraBinding.fragmentContainer.postDelayed({
            activityCameraBinding.fragmentContainer.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    companion object {
        /** Combination of all flags required to put activity into immersive mode */
        const val FLAGS_FULLSCREEN =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L




        private val GITHUB_REPOSITORY = "https://github.com/Dhaval2404/ImagePicker"

        private val PROFILE_IMAGE_REQ_CODE = 101
        private val GALLERY_IMAGE_REQ_CODE = 102
        private val CAMERA_IMAGE_REQ_CODE = 103
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            when (requestCode) {

                GALLERY_IMAGE_REQ_CODE -> {
//                    mGalleryUri = uri
//                    imgGallery.setLocalImage(uri)
                    Log.d("TAG","AAAAA${uri}")
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


}
