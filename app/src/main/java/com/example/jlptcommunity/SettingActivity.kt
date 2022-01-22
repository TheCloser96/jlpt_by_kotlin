/**
 *  SettingActivity
 *  앱 관련한 설정들을 모아둔 엑티비티
 */
package com.example.jlptcommunity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jlptcommunity.databinding.ActivitySettingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {

    /*변수모음*/
    val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }  //바인딩 변수


    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logout.setOnClickListener { logout() }  //로그아웃 레이아웃 클릭시 로그아웃
    }



    
    
    
    /*메서드 모음*/
    
    //로그아웃을 수행하는 메서드
    fun logout() {
        Firebase.auth.signOut() //구글 로그인에서 로그아웃
        
        //초기 로그인 화면으로 이동
        val toLogo = Intent(this, LogoActivity::class.java)
        toLogo.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //엑티비티 스택 모두 초기화 후 화면 띄우기 설정
        startActivity(toLogo)
    }
}