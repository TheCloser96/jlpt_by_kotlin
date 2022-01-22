/**
 *  MainActivity
 *  이용하고자 하는 기능들을 모두 모아놓은 허브와 같은 역할을 하는 엑티비티
 */
package com.example.jlptcommunity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.jlptcommunity.api.FirebaseCustom
import com.example.jlptcommunity.databinding.ActivityMainBinding
import com.example.jlptcommunity.sharedpreferences.App
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    /*변수 모음*/
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) } //뷰 바인딩 변수

    var backKeyPressedTime: Long = 0    //백버튼 누를경우 현재시간이 입력됨

    val firebaseUserIdPath = FirebaseCustom.database.child("users").child(FirebaseCustom.uid)   //실시간 데이터베이스 해당 사용자 uid 경로
    val firebaseUserProfilePath = FirebaseCustom.storageRef.child("users").child(FirebaseCustom.uid).child("profile")  //스토리지 해당 사용자의 프로필 사진 경로



    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        defaultSetup()    //초기 UI 설정하기
    }

    override fun onResume() {
        super.onResume()

        addPostEventListener()  //Nav의 사용자 정보를 배치한다(처음 뿐만 아니라 사용자 정보가 변경이 되어도 반영이 되는 메서드)
        setLocalUserStudyOrTestInfo()   //사용자의 최근 공부 및 테스트 내역을 화면에 설정(sharedpreferences 관련)
    }







    /*메서드 모음*/

    //onCreate단계에서 미리 UI를 설정하는 메서드
    fun defaultSetup() {
        setSupportActionBar(binding.appbarAndContents.toolbar.navToolbar)  //툴바를 액티비티 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //드로어를 꺼낼 홈 버튼을 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.nav_menu) //nav의 홈 버튼 이미지 설정
        supportActionBar?.setDisplayShowTitleEnabled(false) //툴바에 타이틀(프로젝트명)이 보이지 않게 설정

        setOnClickNavigationHead()  //nav의 header 리스너 설정하는 메서드
        binding.headerAndMenu.setNavigationItemSelectedListener(this)   //nav의 menu 리스너 설정하는 메서드 (이것이 없을 경우 nav의 메뉴들을 클릭해도 의미가 없다)
        setOnClickMainLayout()  //메인화면의 컨텐츠 및 주요내용들의 클릭 이벤트 리스너 설정하는 메서드
    }

    //nav의 홈버튼 클릭할 경우 실행되는 메서드
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  //nav의 홈 버튼
                binding.mainNav.openDrawer(GravityCompat.START) //네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //nav의 menu항목들 클릭시 처리되는 메서드
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                val toSetting = Intent(this, SettingActivity::class.java)
                startActivity(toSetting)
            }
        }
        binding.mainNav.closeDrawer(GravityCompat.START)   //nav 닫아주기
        return false
    }

    //nav의 head클릭시 처리되는 메서드
    fun setOnClickNavigationHead() {
        val navHeader = binding.headerAndMenu.getHeaderView(0).findViewById<View>(R.id.profileLayout)   //nav의 헤더 레이아웃 뷰의 값을 저장
        navHeader.setOnClickListener {
            //UserInfoActivity로 이동
            val toUserInfo = Intent(this, UserInfoActivity::class.java)
            startActivity(toUserInfo)

            binding.mainNav.closeDrawer(GravityCompat.START)    //해당 엑티비티로 넘어갈때 열려있는 nav를 닫아주기
        }
    }
    
    //main_layout.xml에 작성된 본 화면의 컨텐츠 및 주요내용들의 클릭 이벤트 리스너를 모아둔 메서드
    fun setOnClickMainLayout() {
        binding.appbarAndContents.JLPTWordsBtn.setOnClickListener { //단어 학습 버튼 리스너
            //SelectStudyOrTestActivity로 이동하되 단어 공부하는 목적으로 이동
            val toStudy = Intent(this, SelectStudyOrTestActivity::class.java)
            toStudy.putExtra("mode", 1)   //공부하는 목적일 경우 1
            startActivity(toStudy)
        }
        binding.appbarAndContents.JLPTTestBtn.setOnClickListener {  //단어 체크 버튼 리스너
            //SelectStudyOrTestActivity로 이동하되 단어 테스트하는 목적으로 이동
            val toTest = Intent(this, SelectStudyOrTestActivity::class.java)
            toTest.putExtra("mode", 0)   //테스트하는 목적일 경우 0
            startActivity(toTest)
        }
        binding.appbarAndContents.translationBtn.setOnClickListener {   //미니 번역기 버튼 리스너
            //TranslateActivity로 이동
            val toTranslate = Intent(this, TranslateActivity::class.java)
            startActivity(toTranslate)
        }
    }

    //Nav의 사용자 정보를 배치하는 메서드(처음 뿐만 아니라 사용자 정보가 변경이 되어도 반영됨)
    fun addPostEventListener() {
        firebaseUserIdPath.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nick = snapshot.child("nick").value.toString()
                val profile = snapshot.child("profile").value

                binding.headerAndMenu.getHeaderView(0).findViewById<TextView>(R.id.userNick).text = nick

                if (profile!=null) {
                    val strProfile = profile.toString()
                    firebaseUserProfilePath.child(strProfile).downloadUrl.addOnSuccessListener {
                        val profileView = binding.headerAndMenu.getHeaderView(0).findViewById<ImageView>(R.id.profileImg)
                        Glide.with(this@MainActivity).load(it).into(profileView)
                    }
                } else {
                    binding.headerAndMenu.getHeaderView(0).findViewById<ImageView>(R.id.profileImg).setImageResource(R.drawable.ic_person)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "사용자 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //사용자의 최근 공부 및 테스트한 내역을 불러와 해당 화면에 보여주는 메서드
    fun setLocalUserStudyOrTestInfo() {
        //최근 공부조회 정보
        val studyLevel = App.localUser.studyLevel
        val studyDay = App.localUser.studyDay

        //최근 시험조회 정보
        val testLevel = App.localUser.testLevel
        val testScore = App.localUser.testScore

        //해당 정보들을 모두 기재한다
        binding.appbarAndContents.recentStudyLevel.text = studyLevel
        binding.appbarAndContents.recentStudyDay.text = studyDay
        binding.appbarAndContents.recentTestLevel.text = testLevel
        binding.appbarAndContents.recentTestScore.text = testScore
    }

    //뒤로가기 버튼 클릭시 실행되는 메서드
    override fun onBackPressed() {
        if (binding.mainNav.isDrawerOpen(GravityCompat.START)) {    //nav가 열려있는 경우
            binding.mainNav.closeDrawer(Gravity.LEFT)   //좌측에 열려있는 nav를 닫는다
        } else {    //nav가 닫혀있을 경우
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {   //2연속 백버튼을 누르지 않은경우 및 한번만 눌렀을 경우
                backKeyPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show()
            } else {    //2연속으로 백버튼을 눌렀을 경우
                finishAffinity()
            }
        }
    }

}