package com.example.android.camera2.basic.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.SIFT
import kotlin.Exception

object SIFTUtils {
    // SIFT detector
    private val siftDetector by lazy { SIFT.create() }

    fun similarity(bitmap1: Bitmap, bitmap2: Bitmap): Double {
        // 计算每张图片的特征点
        val descriptors1 = computeDescriptors(bitmap1)
        val descriptors2 = computeDescriptors(bitmap2)
        // 比较两张图片的特征点
        val descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
        val matches: List<MatOfDMatch> = ArrayList()
        // 计算大图中包含多少小图的特征点。
        // 如果计算小图中包含多少大图的特征点，结果会不准确。
        // 比如：若小图中的 50 个点都包含在大图中的 100 个特征点中，则计算出的相似度为 100%，显然不符合我们的预期
        if (bitmap1.byteCount > bitmap2.byteCount) {
            descriptorMatcher.knnMatch(descriptors1, descriptors2, matches, 2)
        } else {
            descriptorMatcher.knnMatch(descriptors2, descriptors1, matches, 2)
        }
        Log.i("~~~", "matches.size: ${matches.size}")
        if (matches.isEmpty()) return 0.00
        // 获取匹配的特征点数量
        var matchCount = 0
        // 邻近距离阀值，这里设置为 0.7，该值可自行调整
        val nndrRatio = 0.7f
        matches.forEach { match ->
            val array = match.toArray()
            // 用邻近距离比值法(NNDR)计算匹配点数
            if (array[0].distance <= array[1].distance * nndrRatio) {
                matchCount++
            }
        }
        Log.i("~~~", "matchCount: $matchCount")
        return String.format("%.6f", matchCount.toDouble() / matches.size).toDouble()
    }
    @SuppressLint("SuspiciousIndentation")
    fun similarity(descriptors1: MatOfKeyPoint, descriptors2: MatOfKeyPoint): Double {
        try {
            // 比较两张图片的特征点
            val descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
            val matches: List<MatOfDMatch> = ArrayList()
            // 计算大图中包含多少小图的特征点。
            // 如果计算小图中包含多少大图的特征点，结果会不准确。
            // 比如：若小图中的 50 个点都包含在大图中的 100 个特征点中，则计算出的相似度为 100%，显然不符合我们的预期
    //        if (bitmap1.byteCount > bitmap2.byteCount) {

            Log.d("TAG", "${descriptors2.size().height},${descriptors2.size().width}")
            if(descriptors2.size().height==0.0||descriptors2.size().width==0.0){
                return -1.0
            }
            descriptorMatcher.knnMatch(descriptors1, descriptors2, matches, 2)
    //        } else {
    //            descriptorMatcher.knnMatch(descriptors2, descriptors1, matches, 2)
    //        }
            Log.i("TAG", "matches.size: ${matches.size}")
            if (matches.isEmpty()) return 0.00
            // 获取匹配的特征点数量
            var matchCount = 0
            // 邻近距离阀值，这里设置为 0.7，该值可自行调整
            val nndrRatio = 0.7f
            matches.forEach { match ->
                val array = match.toArray()
                // 用邻近距离比值法(NNDR)计算匹配点数
                if (array[0].distance <= array[1].distance * nndrRatio) {
                    matchCount++
                }
            }
            Log.i("TAG", "matchCount: $matchCount")
            return String.format("%.6f", matchCount.toDouble() / matches.size).toDouble()
        }catch(e: Exception){
            return -1.0
        }
    }

    fun computeDescriptors(bitmap: Bitmap): MatOfKeyPoint {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        val keyPoints = MatOfKeyPoint()
        siftDetector.detect(mat, keyPoints)
        val descriptors = MatOfKeyPoint()
        // 计算图片的特征点
        siftDetector.compute(mat, keyPoints, descriptors)
        return descriptors
    }
}