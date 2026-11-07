package com.example.myapplication.widget

import android.util.Xml
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface CbrApi {
    @GET("scripts/xml_metall.asp")
    suspend fun getMetals(
        @Query("date_req1") dateFrom: String,
        @Query("date_req2") dateTo: String
    ): retrofit2.Response<ResponseBody>
}

class GoldRateRepository {
    private val api: CbrApi = Retrofit.Builder()
        .baseUrl("http://www.cbr.ru/")
        .build()
        .create(CbrApi::class.java)

    suspend fun loadRate(): Double {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val calendar = Calendar.getInstance()

            val dateTo = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -7) // Берем неделю на случай выходных
            val dateFrom = dateFormat.format(calendar.time)

            val response = api.getMetals(dateFrom, dateTo)

            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!.string()
                parseGold(xml) ?: 0.0
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun parseGold(xmlData: String): Double? {
        val parser = Xml.newPullParser()
        parser.setInput(xmlData.byteInputStream(java.nio.charset.Charset.forName("windows-1251")), "windows-1251")

        var eventType = parser.eventType
        var isGold = false
        var priceString = "0"

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name == "Record") {
                    isGold = parser.getAttributeValue(null, "Code") == "1"
                }
                if (parser.name == "Buy" && isGold) {
                    priceString = parser.nextText()
                }
            }
            eventType = parser.next()
        }
        return priceString.replace(",", ".").toDoubleOrNull()
    }
}