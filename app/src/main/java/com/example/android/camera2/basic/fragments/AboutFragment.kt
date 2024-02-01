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
import android.hardware.camera2.*
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.example.android.camera2.basic.CameraActivity
import com.example.android.camera2.basic.R
import com.example.android.camera2.basic.databinding.FragmentInfoBinding
import com.example.android.camera2.basic.databinding.FragmentIntroBinding


class AboutFragment : Fragment() {
    /** Android ViewBinding */
    private var _fragmentInfoBinding: FragmentInfoBinding? = null

    private val fragmentInfoBinding get() = _fragmentInfoBinding!!
    private lateinit var from:String
    private var curId:Int = 0
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _fragmentInfoBinding = FragmentInfoBinding.inflate(inflater, container, false)

        return fragmentInfoBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInfoBinding.imageViewr1.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/ras-1-f.png")))
        fragmentInfoBinding.imageViewr2.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/ras-2-f.png")))
        fragmentInfoBinding.imageViewr3.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/ras-3-f.png")))
        fragmentInfoBinding.imageViews1.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/shifon-1-f.png")))
        fragmentInfoBinding.imageViews2.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/shifon-2-f.png")))
        fragmentInfoBinding.imageViews3.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/shifon-3-f.png")))
        fragmentInfoBinding.imageViewsa1.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/salt-1-f.png")))
        fragmentInfoBinding.imageViewsa2.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/salt-2-f.png")))
        fragmentInfoBinding.imageViewsa3.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao/salt-3-f.png")))
        fragmentInfoBinding.imageViewTaobao.setImageBitmap(BitmapFactory.decodeStream(requireContext().resources.assets.open("taobao_store.png")))

    }


    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    override fun onStop() {
        super.onStop()

    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
