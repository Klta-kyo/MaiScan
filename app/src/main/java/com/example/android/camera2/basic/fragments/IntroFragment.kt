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

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.hardware.camera2.*
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.example.android.camera2.basic.CameraActivity
import com.example.android.camera2.basic.R
import org.opencv.core.MatOfKeyPoint
import com.example.android.camera2.basic.databinding.FragmentIntroBinding
import java.io.BufferedReader
import java.io.InputStreamReader


class IntroFragment : Fragment() {
    /** Android ViewBinding */
    private var _fragmentIntroBinding: FragmentIntroBinding? = null

    private val fragmentIntroBinding get() = _fragmentIntroBinding!!
    private lateinit var from:String
    private var curId:Int = 0
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _fragmentIntroBinding = FragmentIntroBinding.inflate(inflater, container, false)

        if (arguments != null) {
            curId = requireArguments().getInt("id");
            from = requireArguments().getString("from").toString()
            val tv: TextView = fragmentIntroBinding.textId
            tv.text = "ID: "+curId.toString()
        }
        return fragmentIntroBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (from == "gallery") {
                        var transaction: FragmentTransaction? = null
                        transaction = requireActivity().supportFragmentManager.beginTransaction()
                        val fragment1 = GalleryFragment()
                        transaction.replace(R.id.fragment_container, fragment1)
                        transaction.commit()
                    }else if (from == "text") {
                        var transaction: FragmentTransaction? = null
                        transaction = requireActivity().supportFragmentManager.beginTransaction()
                        val fragment1 = TextFragment()
                        transaction.replace(R.id.fragment_container, fragment1)
                        transaction.commit()
                    } else {
                        var transaction: FragmentTransaction? = null
                        transaction = requireActivity().supportFragmentManager.beginTransaction()
                        val fragment1 = CameraFragment()
                        transaction.replace(R.id.fragment_container, fragment1)
                        transaction.commit()
                    }
                }
            })


        val json: JSONObject? = (activity as CameraActivity).infoMap[curId]
        var jsonArray: JSONArray? = null
        var aliasArray: JSONArray? = null
        if((activity as CameraActivity).chartMap.contains(curId)){
            jsonArray = (activity as CameraActivity).chartMap[curId]
        }
        if((activity as CameraActivity).aliasMap.contains(curId)){
            aliasArray = (activity as CameraActivity).aliasMap[curId]
        }
        if(json!=null){
            updateInfo(curId,json,jsonArray)
            updateComment(curId,json,jsonArray)
            if(aliasArray!=null){
                updateAlias(curId,json,aliasArray)
            }
        }
    }
    fun updateAlias(id: Int, json: JSONObject, aliasArray: JSONArray){
        var str = "别名："
        for(s in aliasArray){
            str+=s.toString()+";"
        }
        fragmentIntroBinding.textAlias.text = str
    }

    fun updateInfo(id: Int, json: JSONObject, chartJson: JSONArray?){
        var basic = json.get("basic_info") as JSONObject
        fragmentIntroBinding.textTitle.text = json.get("title") as String
        fragmentIntroBinding.textViewVersion.text = basic.get("from").toString()
        fragmentIntroBinding.btnCopyTitle.setOnClickListener {
            copyToClipboard(requireContext(),json.get("title") as String)
        }
        fragmentIntroBinding.textArtist.text = basic.get("artist") as String
        fragmentIntroBinding.imgCur.let { updateImage(id, it,100) }


        val table = fragmentIntroBinding.table

//        fragmentCameraBinding.textView?.text =
//                "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"
        var levelNum = (json.get("level") as JSONArray).size
        var levelList = mutableListOf<String>()
        levelList.add("等级")
        var i =0
        for(j in (json.get("level") as JSONArray)){
            levelList.add(j.toString())
            i = i+1
            if(i>=levelNum) break
        }
        addTableRow(table,levelList)
        var dsList = mutableListOf<String>()
        dsList.add("官方定数")
        i=0
        for(j in (json.get("ds") as JSONArray)){
            dsList.add(String.format("%.2f",j.toString().toFloat()))
            i = i+1
            if(i>=levelNum) break
        }
        addTableRow(table,dsList)
        if(chartJson!=null){
            var dsmodList = mutableListOf<String>()
            dsmodList.add("拟合定数")
            i=0
            for(j in chartJson){
                if((j as JSONObject).contains("fit_diff")){
                    dsmodList.add(String.format("%.2f", j.get("fit_diff").toString().toFloat()))
                }else{
                    dsmodList.add("  ")
                }
                i = i+1
                if(i>=levelNum) break
            }
            addTableRow(table,dsmodList)
        }
        var charts = json.get("charts") as JSONArray
        var note = (charts[0] as JSONObject).get("notes") as JSONArray
        fun addList(title:String,item:Int){
            var noteList = mutableListOf<String>()
            noteList.add(title)
            for(j in 0 until levelNum){
                note = (charts[j] as JSONObject).get("notes") as JSONArray
                noteList.add(note[item].toString())
            }
            addTableRow(table,noteList)
        }
        if(note.size==4){
            addList("Tap总数",0)
            addList("Hold总数",1)
            addList("Slide总数",2)
            addList("Break总数",3)
        }else if(note.size==5){
            addList("Tap总数",0)
            addList("Hold总数",1)
            addList("Touch总数",2)
            addList("Slide总数",3)
            addList("Break总数",4)
        }

//        fragmentIntroBinding.textViewDs.text = ds


    }
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
    fun updateComment(id: Int, json: JSONObject, chartJson: JSONArray?){
        var basic = json.get("basic_info") as JSONObject
        val fileName = "commentSource/comment_$id.json"
        var br = BufferedReader(InputStreamReader(requireContext().resources.assets.open(fileName)))
        var line: String? = null
        val sb = StringBuilder()
        while (br.readLine().also { line = it } != null){
            sb.append(line)
        }
        br.close()
        var json = sb.toString()
//        fragmentCameraBinding.textView?.text =
//                "result1: ${maxId1},Name: ${maxJson1?.get("title")},Acc:${max1},Ds: ${maxJson1?.get("ds")}\n"
        val comment: JSONObject = JSON.parseObject(json)
        var data = comment.get("data") as JSONObject
        var results = data.get("result") as JSONArray
        var videos: JSONObject? = null
        for (i in 0 until results.size){
            var result = results[i] as JSONObject
            if(result.get("result_type")=="video"){
                videos = result as JSONObject
                break
            }
        }
        if(videos!=null){
            for (x in videos["data"] as JSONArray){
                var i = x as JSONObject
                var author = i["author"].toString()
                var basicInfo = i["title"].toString().replace("<em class=\"keyword\">","").replace("</em>","")
                var description = i["description"].toString()
                val comView = fragmentIntroBinding.commentLayout
                comView.addView(getCommentTextView(author,Color.BLACK,16f,true,Typeface.DEFAULT_BOLD))
                comView.addView(getCommentTextView(basicInfo,Color.BLACK,16f,true,Typeface.DEFAULT))
                comView.addView(getCommentBtnView(i["id"].toString(),Color.BLACK,16f))
                comView.addView(getCommentTextView(description+"\n\n",Color.GRAY,16f,false,Typeface.DEFAULT))
             }
        }

//        fragmentIntroBinding.textViewDs.text = ds


    }
    fun getCommentTextView(str:String,color:Int,textSize: Float,isTitle:Boolean,typeface:Typeface): TextView {
        var margin:Int=dpToPx( 2,requireContext());
        var padding:Int=dpToPx( 2,requireContext());
        var textView1:TextView = TextView(requireContext())
        textView1.setTextSize(textSize)
        textView1.text=str
        textView1.setTypeface(typeface)
        textView1.setTextColor(color)
        var params1:LinearLayout.LayoutParams=LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 match_parent
            LinearLayout.LayoutParams.WRAP_CONTENT // 高度设置为自适应内容
        )
        textView1.setGravity(Gravity.LEFT);
        textView1.setPadding(padding,0,padding,0)
        if(isTitle){
            params1.setMargins(margin,margin,margin,0);
        }else{
            params1.setMargins(margin,0,margin,0);
        }
        textView1.setLayoutParams(params1);
        return textView1
    }
    fun getCommentBtnView(str:String,color:Int,textSize: Float): TextView {
        var margin:Int=dpToPx( 0,requireContext());
        var padding:Int=dpToPx( 0,requireContext());
        var textView1: Button = Button(requireContext())
        textView1.setTextSize(textSize)
        textView1.text="复制av号"
        textView1.setTextColor(color)
        var params1:LinearLayout.LayoutParams=LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // 宽度设置为 match_parent
            LinearLayout.LayoutParams.WRAP_CONTENT // 高度设置为自适应内容
        )
        textView1.setGravity(Gravity.CENTER);
        textView1.setPadding(padding,padding,padding,padding)
        params1.setMargins(margin,margin,margin,margin);
        textView1.setLayoutParams(params1);
        textView1.setOnClickListener {
            copyToClipboard(requireContext(),str)
        }
        return textView1
    }
    fun addTableRow(tableLayout: TableLayout, list:MutableList<String>) {

        var tableRow =TableRow(requireContext());

        //这里一定要用android.widget.TableRow.LayoutParams，不然TableRow不显示
        tableRow.setLayoutParams(LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tableRow.setBackgroundResource(R.drawable.table_frame_gray)
        if(list.size==5){
            tableRow.addView(getTextView(list[0],Color.BLACK))
            tableRow.addView(getTextView(list[1],Color.rgb(69,193,36)))
            tableRow.addView(getTextView(list[2],Color.rgb(248,183,9)))
            tableRow.addView(getTextView(list[3],Color.rgb(254,90,102)))
            tableRow.addView(getTextView(list[4],Color.rgb(159,81,220)))
        }else if(list.size==6){
            tableRow.addView(getTextView(list[0],Color.BLACK))
            tableRow.addView(getTextView(list[1],Color.rgb(69,193,36)))
            tableRow.addView(getTextView(list[2],Color.rgb(248,183,9)))
            tableRow.addView(getTextView(list[3],Color.rgb(254,90,102)))
            tableRow.addView(getTextView(list[4],Color.rgb(159,81,220)))
            tableRow.addView(getTextView(list[5],Color.rgb(204,137,255)))
        }else{
            for(str in list){
                tableRow.addView(getTextView(list[2],Color.BLACK))
            }
        }
        tableLayout.addView(tableRow);
    }
    fun getTextView(str:String,color:Int): TextView {
        var margin:Int=dpToPx( 2,requireContext());
        var padding:Int=dpToPx( 5,requireContext());
        var textView1:TextView = TextView(requireContext())
        textView1.setTextSize(16f)
        textView1.text=str
        textView1.setTextColor(color)
        var params1:TableRow.LayoutParams=TableRow.LayoutParams();
        params1.weight= 1F;
        textView1.setGravity(Gravity.CENTER);
        textView1.setPadding(padding,padding,padding,padding)
        params1.setMargins(margin,margin,margin,margin);
        textView1.setLayoutParams(params1);
        return textView1
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
        val show1 = Bitmap.createBitmap(width,width, Bitmap.Config.ARGB_8888)
        val canvas1 = Canvas(show1)
        val rect1 = RectF(0f,0f,width.toFloat(),width.toFloat())
        canvas1.drawBitmap(bitmap1,null,rect1,null)
        target.setImageBitmap(show1)
    }
    override fun onStop() {
        super.onStop()

    }
    fun copyToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "已复制！", Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
