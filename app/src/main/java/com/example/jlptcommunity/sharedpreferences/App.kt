/**
 *  소속 혹은 관련된 Activity: MainActivity, StudyWordsActivity, TestActivity
 *  application name: 항목에 본 클래스를 기재하여 어플리케이션 실행시 가장 먼저 sharedpreference기능이 실행되게 설계
 */
package com.example.jlptcommunity.sharedpreferences

import android.app.Application

class App : Application() {

    //사용하고 싶은 sharedpreference와 관련한 클래스들을 컴페니언 오브젝트 안에 입력한다
    companion object {
        lateinit var localUser: LocalUser
    }

    //어플리케이션 실행시 바로 실행하는 메서드
    override fun onCreate() {
        localUser = LocalUser(applicationContext)   //LocalUser와 관련한 sharedpreference파일을 사용한다
        super.onCreate()
    }
}