/**
 *  SelectDayActivity
 *  사용자가 선택한 JLPT 난이도에 따라 Day가 해당 난이도에 맞게 생성되고 색상도 변경되는 엑티비티
 *  해당 Day를 클릭시 각 Day마다 저장된 데이터가 StudyWordsActivity로 이동한다
 */
package com.example.jlptcommunity

import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jlptcommunity.adapter.SelectDayAdapter
import com.example.jlptcommunity.adapter.SelectDayItem
import com.example.jlptcommunity.database.DBHelper
import com.example.jlptcommunity.databinding.ActivitySelectDayBinding

class SelectDayActivity : AppCompatActivity() {

    /*변수 모음*/
    val binding by lazy { ActivitySelectDayBinding.inflate(layoutInflater) }    //뷰 바인딩 변수
    val level by lazy { intent.getIntExtra("level", 0) }  //SelectStudyOrTestActivity로 부터 넘어온 1~5 값
    
    val wordsPerDays by lazy {  //JLPT 난이도에 따라서 하나의 Day에 저장되는 단어의 수
        when(level) {
            in 1..2 -> {40} //1과 2의 경우 Day당 (최대)40개가 할당된다
            in 3..4 -> {30} //3과 4의 경우 Day당 (최대)30개가 할당된다
            5 -> {20}   //5의 경우 Day당 (최대)20개가 할당된다
            else -> {0} //문법적으로 문제가 되기 때문에 작성된 명령구문(애초에 이 항목에 걸리기 전에 현재 엑티비티에 진입할 수 없음)
        }
    }

    val jlptLv by lazy {    //변수 level의 값에 의해 jlpt난이도(문자열 형태)를 정한다
        val levelList: List<String> = listOf("N1", "N2", "N3", "N4", "N5")
        levelList[level-1]
    }

    val numberOfRow by lazy {   //JLPT 각 난이도별 단어의 개수
        val instance = DBHelper(this, "JLPT.db", null, 1).readableDatabase  //(시스템 내부)JLPT.db 인스턴스
        val query = "SELECT COUNT(*) FROM Words WHERE Level = '${jlptLv}'"  //해당 레벨에 맞는 단어들의 개수를 가져오는 쿼리문
        val count = DatabaseUtils.longForQuery(instance, query, null)
        count.toInt()
    }

    val days by lazy {  //Day 1, Day2... 와 같이 사용자에게 보여지는 총 개수
        if (numberOfRow % wordsPerDays > 0) {
            numberOfRow/wordsPerDays+1
        } else {
            numberOfRow/wordsPerDays
        }
    }




    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //level의 값이 0일 경우 이전화면으로 넘어간다
        if (level == 0) {
            Toast.makeText(this, "접근 오류", Toast.LENGTH_SHORT).show()
            finish()
        }

        makingRecyclerView()    //리사이클러 뷰를 생성하는 메서드
    }




    /*메서드 모음*/

    //설정값에 따라 Day 카드뷰를 만드는 메서드
    fun makingRecyclerView() {
        val arraylist = ArrayList<SelectDayItem>()

        //JLPT 난이도에 따라 반복횟수가 정해지고, 정해진 만큼 데이터를 채워넣는 작업을 진행
        for (day in 1..days) {
            arraylist.add(SelectDayItem(level, day, numberOfRow, wordsPerDays))
        }
        
        val adapter = SelectDayAdapter(arraylist, this) //채워진 데이터와 현재 엑티비티의 context도 같이 첨부

        binding.recyclerDays.adapter = adapter  //설정된 어댑터 변수를 해당 리사이클러 뷰의 어뎁터 속성에 주입
        binding.recyclerDays.layoutManager = LinearLayoutManager(this)  //일반적인 세로형 레이아웃으로 설정
    }
}