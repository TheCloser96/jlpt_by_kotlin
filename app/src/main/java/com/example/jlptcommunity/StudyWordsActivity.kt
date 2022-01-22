/**
 *  StudyWordsActivity
 *  SelectDayActivity의 리사이클러 뷰 에 생성된 Day들 중 하나를 클릭할 경우 현재의 엑티비티에 도달한다
 *  클릭한 Day에 저장된 정보들을 가지고 현재의 엑티비티에 적용하여 알맞은 단어들을 사용자에게 제공한다
 */
package com.example.jlptcommunity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jlptcommunity.adapter.SelectDayItem
import com.example.jlptcommunity.adapter.StudyWordsAdapter
import com.example.jlptcommunity.database.DBHelper
import com.example.jlptcommunity.database.WordsEntity
import com.example.jlptcommunity.databinding.ActivityStudyWordsBinding
import com.example.jlptcommunity.sharedpreferences.App
import java.util.*
import kotlin.collections.ArrayList

class StudyWordsActivity : AppCompatActivity() {

    /*변수 모음*/
    val binding by lazy { ActivityStudyWordsBinding.inflate(layoutInflater) }   //뷰 바인딩 변수
    val key by lazy { intent.getParcelableExtra<SelectDayItem>("key")!! } //SelectDayActivity의 특정 Day로 부터 넘어올때 보유하고 있는 고유의 정보 객체

    val wordsArray = ArrayList<WordsEntity>()   //해당 난이도와 Day범위 그리고 난이도별 단어의 개수 만큼 단어들이 저장되는 배열

    lateinit var tts: TextToSpeech  //TTS객체 변수



    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getWordsFromDB()    //해당 난이도와 Day에 맞춰서 단어들을 추출하는 메서드
        
        makingTTSInstance() //TTS객체를 생성하는 메서드

        makingRecyclerView()    //해당 난이도와 Day에 맞춰서 단어 카드뷰를 만드는 메서드
    }

    override fun onDestroy() {
        //종료할 경우 남아있는 TTS객체 중지 후 제거(권장사항)
        tts.stop()
        tts.shutdown()

        //최근 조회한 내역을 해당 sharedpreferences에 갱신
        updateInfo()

        super.onDestroy()
    }


    
    
    
    /*메서드 모음*/

    //key를 이용하여 시스템 내부 DB로 부터 단어들을 추출하는 메서드
    @SuppressLint("Recycle")
    fun getWordsFromDB() {
        val DBInstance = DBHelper(this, "JLPT.db", null, 1).readableDatabase    //(시스템 내부)JLPT.db 인스턴스


        val levelByString = getLevelByString()   //실행할 쿼리문에 필요한 level값을 가져온다
        val query = "SELECT * FROM Words WHERE Level = '${levelByString}'"   //해당 레벨과 관련한 단어들만 찾는 쿼리문
        val cursor = DBInstance.rawQuery(query, null)   //쿼리문 실행 후 결과값을 저장


        var startPoint = (key.day-1) * key.wordsPerDays + 1   //이 변수를 기준으로 단어가 들어간다
        var endPoint: Int
        //끝의 지점은 해당 Day*레벨별 하나의 Day당 단어의 개수가 본 난이도의 총 단어량을 넘었을 경우 총 단어량으로 지정
        if (key.day * key.wordsPerDays > key.numberOfRow) {
            endPoint = key.numberOfRow
        } else {
            endPoint = key.day * key.wordsPerDays
        }


        var cnt = 1 //while문 시작점
        while (cursor.moveToNext()) {   //해당 범위만큼 단어들을 추출하고 배열에 저장
            if (cnt in startPoint..endPoint) {
                wordsArray.add(WordsEntity(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                ))
            }
            cnt++
        }
    }


    //key 에 의해 해당하는 JLPT난이도 레벨을 문자열 형태로 결과값을 내놓는 메서드
    fun getLevelByString(): String {
        when (key.level) {
            1 -> {return "N1"}
            2 -> {return "N2"}
            3 -> {return "N3"}
            4 -> {return "N4"}
            5 -> {return "N5"}
            else -> {return ""}
        }
    }


    //TTS객체를 생성하는 역할의 메서드
    fun makingTTSInstance() {
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.JAPAN)

                if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(this, "본 언어는 TTS를 지원하지 않습니다1", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TTS 설정 초기화 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    
    //설정값에 따라서 단어 카드뷰를 만드는 메서드
    fun makingRecyclerView() {
        val adapter = StudyWordsAdapter(wordsArray, this, tts)   //데이터와 현재 엑티비티의 context도 같이 첨부

        binding.recyclerWords.adapter = adapter //설정된 어댑터 변수를 해당 리사이클러 뷰의 어뎁터 속성에 주입
        binding.recyclerWords.layoutManager = LinearLayoutManager(this) //일반적인 세로형 레이아웃으로 설정
    }


    //해당 엑티비티를 종료할 경우 본인이 공부한 JLPT레벨, day를 해당sharedpreferences 갱신시키는 메서드
    fun updateInfo() {
        val strLevel = getLevelByString()   //현재 사용자가 조회하는 jlpt레벨
        val strDay = key.day.toString() //현재 사용자가 조회하는 jlpt Day

        //해당 sharedPreference파일에 기록
        App.localUser.studyLevel = strLevel
        App.localUser.studyDay = strDay
    }
}