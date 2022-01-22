/**
 *  Papago
 *  여기서 papago 번역을 진행 후, 결과값을 제공한다
 */
package com.example.jlptcommunity.api

import com.example.jlptcommunity.api.keys.APIKeys.NAVER_CLIENT_ID
import com.example.jlptcommunity.api.keys.APIKeys.NAVER_CLIENT_SECRET
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap

object Papago {
    fun translate(text: String, beforeLan: String, afterLan: String): String {
        var text = text
        val clientId = NAVER_CLIENT_ID
        val clientSecret = NAVER_CLIENT_SECRET
        val apiURL = "https://openapi.naver.com/v1/papago/n2mt"

        text = try {
            URLEncoder.encode(text, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("인코딩 실패", e)
        }

        val requestHeaders: MutableMap<String, String> = HashMap()
        requestHeaders["X-Naver-Client-Id"] = clientId
        requestHeaders["X-Naver-Client-Secret"] = clientSecret

        return try {
            val responseBody = post(apiURL, requestHeaders, text, beforeLan, afterLan)
            responseBody.split("\"").toTypedArray()[27]
        } catch (e: Exception) {
            "단어 및 문장을 입력하세요"
        }
    }

    private fun post(apiUrl: String, requestHeaders: Map<String, String>, text: String, beforeLan: String, afterLan: String): String {
        val con = connect(apiUrl)
        val postParams = "source=$beforeLan&target=$afterLan&text=$text"
        return try {
            con.requestMethod = "POST"
            for ((key, value) in requestHeaders) {
                con.setRequestProperty(key, value)
            }
            con.doOutput = true
            DataOutputStream(con.outputStream).use { wr ->
                wr.write(postParams.toByteArray())
                wr.flush()
            }

            val responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                readBody(con.inputStream)
            } else {  // 에러 응답
                readBody(con.errorStream)
            }
        } catch (e: IOException) {
            throw RuntimeException("API 요청과 응답 실패", e)
        } finally {
            con.disconnect()
        }
    }

    private fun connect(apiUrl: String): HttpURLConnection {
        return try {
            val url = URL(apiUrl)
            url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("연결이 실패했습니다. : $apiUrl", e)
        }
    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)
        try {
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()
                var line: String?
                while (lineReader.readLine().also { line = it } != null) {
                    responseBody.append(line)
                }
                return responseBody.toString()
            }
        } catch (e: IOException) {
            throw RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }
    }
}