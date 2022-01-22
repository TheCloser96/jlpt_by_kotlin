/**
 *  App.kt에 사용되는 sharedpreferences 관련 클래스
 *  사용자의 최근 공부내역의 레벨 및 Day 그리고 시험레벨 및 점수를 편집하는 용도로 사용됨
 */
package com.example.jlptcommunity.sharedpreferences

import android.content.Context

class LocalUser(context: Context) {
    val prefsFilename = "localUser" //sharedpreferences파일의 이름
    val prefs = context.getSharedPreferences(prefsFilename, 0)  //prefsFilename의 파일이름과 PRIVATE모드(0)로 sharedpreferences파일 사용을 선언
    
    //해당 key값과 관련한 getter/setter 설정 목록들
    var studyLevel: String?
        get() = prefs.getString("studyLevel", "-")
        set(value) = prefs.edit().putString("studyLevel", value).apply()
    var studyDay: String?
        get() = prefs.getString("studyDay", "-")
        set(value) = prefs.edit().putString("studyDay", value).apply()
    var testLevel: String?
        get() = prefs.getString("testLevel", "-")
        set(value) = prefs.edit().putString("testLevel", value).apply()
    var testScore: String?
        get() = prefs.getString("testScore", "-")
        set(value) = prefs.edit().putString("testScore", value).apply()
}