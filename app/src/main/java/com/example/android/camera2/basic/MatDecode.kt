
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.SIFT
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MatDecode {

    val siftDetector by lazy { SIFT.create() }

    fun save(path: String, mat: MatOfKeyPoint) {
        val file = File(path).absoluteFile
        file.parentFile.mkdirs()
        try {
            val cols = mat.cols()
            val rows = mat.rows()
            val type = mat.type()
            val data = FloatArray(mat.total().toInt() * mat.channels())
            mat[0, 0, data]
            ObjectOutputStream(FileOutputStream(path)).use { oos ->
                oos.writeInt(cols)
                oos.writeInt(rows)
                oos.writeInt(type)
                oos.writeObject(data)
                oos.close()
            }
        } catch (ex: IOException) {
            Log.d("TAG","ERROR: Could not save mat to file: $path")
        } catch (ex: ClassCastException) {
            Log.d("TAG","ERROR: Could not save mat to file: $path")
        }
    }

    fun load(source: InputStream): MatOfKeyPoint? {
        try {
            var cols: Int
            var rows: Int
            var type: Int
            var data: FloatArray
            ObjectInputStream(source).use { ois ->
                cols = ois.readInt() as Int
                rows = ois.readInt() as Int
                type = ois.readInt() as Int
                data = ois.readObject() as FloatArray
            }
            val keyPoints = MatOfKeyPoint()
            keyPoints.create(rows, cols, type)
            keyPoints.put(0, 0, data)
            return keyPoints
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("TAG","ERROR: Could not load mat from file: $source")
        } catch (ex: ClassNotFoundException) {
            ex.printStackTrace()
            Log.d("TAG","ERROR: Could not load mat from file: $source")
        } catch (ex: ClassCastException) {
            ex.printStackTrace()
            Log.d("TAG","ERROR: Could not load mat from file: $source")
        }
        return null
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

fun main() {
    var br: BufferedReader? = null

    var fileName = "C:\\Users\\11252\\Desktop\\Camera2Basic\\app\\src\\main\\assets\\music_data.json"
    br = BufferedReader(InputStreamReader(FileInputStream(fileName)))
    var line: String? = null
    val sb = StringBuilder()
    while (br.readLine().also { line = it } != null){
        sb.append(line)
    }
    br.close()
    var json = sb.toString()
    print("---json: $json")
    for(i in 0 until 30){
        val codeList: JSONArray = JSON.parseArray(json)
//            for(i in 0 until codeList.size){
        val musicItem: JSONObject = codeList.getJSONObject(i)
        val id : Int = (musicItem.get("id") as String).toInt()
        var zeros = ""
        var x = id
        while(x<=9999){
            x = x*10
            zeros = zeros+"0"
        }
        val bitmap = BitmapFactory.decodeStream(FileInputStream("C:\\Users\\11252\\Desktop\\Camera2Basic\\app\\src\\main\\assets\\source/img${zeros}${id}.png"))
        if(bitmap==null){
            continue
        }
        val descriptor: MatOfKeyPoint = MatDecode().computeDescriptors(bitmap)



    }

}