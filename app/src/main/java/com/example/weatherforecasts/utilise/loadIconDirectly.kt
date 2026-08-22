package com.example.weatherforecasts.utilise



import android.graphics.BitmapFactory
import com.example.weatherforecasts.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// دالة خفيفة لجلب الصورة شبكياً بدون الحاجة لمكتبات Glide أو Coil
fun loadIconDirectly(iconCode: String, imageView: android.widget.ImageView) {
    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val url = URL(iconUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connect()

            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)

            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(myBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                // في حالة وجود خطأ يتم وضع صورة افتراضية
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }
}







