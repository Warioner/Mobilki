package com.example.myapplication.widget

import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import retrofit2.http.GET
import java.nio.charset.Charset

interface CbrApi {
    // ВАЖНО: http:// часто обходит блокировку Cloudflare, которая стоит на https:// для этих скриптов
    @GET("http://www.cbr.ru/scripts/xml_metall.asp")
    suspend fun getMetalsRates(): retrofit2.Response<ResponseBody>
}

class GoldRateRepository {
    suspend fun loadRate(): GoldRate? {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://www.cbr.ru/") // Тоже http
                .build()
            val api = retrofit.create(CbrApi::class.java)
            val response = api.getMetalsRates()

            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!.string()
                val price = parseGold(xml)
                if (price != null) {
                    return GoldRate(price)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseGold(xmlData: String): Double? {
        val parser = org.xmlpull.v1.Xml.newPullParser()
        // Пробуем UTF-8, так как при http-запросе ЦБ иногда отдает в ней,
        // а для цифр с запятой это не имеет значения.
        parser.setInput(xmlData.byteInputStream(Charsets.UTF_8), "UTF-8")

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