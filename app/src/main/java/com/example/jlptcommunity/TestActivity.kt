/**
 *  TestActivity
 *  난이도별 자신이 단어를 얼마나 암기하였는지 시험하는 엑티비티
 */
package com.example.jlptcommunity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.jlptcommunity.database.DBHelper
import com.example.jlptcommunity.database.WordsEntity
import com.example.jlptcommunity.databinding.ActivityTestBinding
import com.example.jlptcommunity.sharedpreferences.App
import kotlinx.coroutines.*

class TestActivity : AppCompatActivity() {

    /*변수 모음*/
    val binding by lazy { ActivityTestBinding.inflate(layoutInflater) } //뷰 바인딩 변수

    val level by lazy { intent.getIntExtra("level", 0) }  //SelectStudyOrTestActivity로 부터 넘어온 1~5 값

    val jlptLv by lazy {    //변수 level의 값에 의해 jlpt난이도를 정한다
        val levelList: List<String> = listOf("N1", "N2", "N3", "N4", "N5")
        levelList[level-1]
    }

    val jlptArrayList = ArrayList<WordsEntity>()    //해당 jlpt 난이도에 맞는 단어들이 저장되는 배열

    var jlptArrayListIndex = 0   //jlpt단어들이 들어간 배열의 index역할 및 퀴즈정답 누적 스코어

    val scope = CoroutineScope(Dispatchers.Main)    //코루틴 스코프 변수

    var timer = 10  //퀴즈 시간제한 변수




    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //level의 값이 0일 경우 이전화면으로 넘어간다
        if (level == 0) {
            Toast.makeText(this, "접근 오류", Toast.LENGTH_SHORT).show()
            finish()
        }

        //정해진 jlptLv과 관련한 단어들을 해당 배열에 저장한다
        getWords(jlptLv, jlptArrayList)
    }

    override fun onResume() {
        super.onResume()

        setQuiz()   //초반 퀴즈 셋팅

        //타이머기능과 버튼 상호작용기능을 실행하는 코루틴(Dispatchers: Main)
        scope.launch {
            
            //타이머 동작 및 타임오버시 메인화면 처리관련 담당
            val timerJob = launch {

                while (timer > 0) {
                    binding.timerOrScore.text = timer.toString()
                    delay(1000L)
                    timer--
                }

                prohibitBtn()   //시간초과(timer가 0이 될경우) 작동하는 메서드
            }

            
            //버튼 상호작용 모음
            binding.quizBtn1.setOnClickListener {
                btnInteraction(binding.quizBtn1, timerJob)
            }
            binding.quizBtn2.setOnClickListener {
                btnInteraction(binding.quizBtn2, timerJob)
            }
            binding.quizBtn3.setOnClickListener {
                btnInteraction(binding.quizBtn3, timerJob)
            }
            binding.quizBtn4.setOnClickListener {
                btnInteraction(binding.quizBtn4, timerJob)
            }
        }

    }

    override fun onDestroy() {
        updateInfo()    //엑티비티 종료 전 본인의 JLPT레벨과 점수를 해당sharedpreferences 갱신 작업
        super.onDestroy()
    }




    /*메서드 모음*/

    //해당 레벨에 맞는 JLPT단어들을 배열에 저장하는 메서드
    @SuppressLint("Recycle")
    fun getWords(jlptLv: String, jlptArrayList: ArrayList<WordsEntity>) {
        val INSTANCE = DBHelper(this, "JLPT.db", null, 1).readableDatabase  //(시스템 내부)JLPT.db 인스턴스
        val query = "SELECT * FROM Words WHERE Level = '${jlptLv}'"  //해당 레벨에 맞는 단어들을 가져오는 쿼리문
        val cursor = INSTANCE.rawQuery(query, null)

        //해당 레벨과 관련한 단어들만 추출하여 배열에 저장
        while (cursor.moveToNext()) {
            jlptArrayList.add(WordsEntity(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
        }
    }

    //단어퀴즈의 제시어와 보기 4개를 설정하는 메서드
    fun setQuiz() {
        val integerArray = arrayOfNulls<Int>(4)   //jlptArrayListIndex와 0~jlptArrayList.size-1 사이의 랜덤한 숫자가 들어가는 배열
        val answerIndex = (0..3).random()  //정답이 들어갈 인덱스
        var cnt = 0 //랜덤하고 중복없는 숫자 채우기 카운트
        var ranNum: Int  //랜덤 숫자

        //integerArray 안에 jlptArrayListIndex와 랜덤한 숫자들(중복없이)을 랜덤한 순서로 기입하는 과정
        retry@while (cnt < integerArray.size) {
            if (cnt == answerIndex) {
                integerArray[cnt] = jlptArrayListIndex
                cnt++
            } else {
                ranNum = (0 until jlptArrayList.size).random()  //0과 jlpt단어들이 담긴 배열의 크기사이의 랜덤 숫자
                for (i in 0..cnt) {
                    if (ranNum == integerArray[i] || ranNum == jlptArrayListIndex) {
                        continue@retry
                    }
                }
                integerArray[cnt] = ranNum
                cnt++
            }
        }

        binding.quiz.text = jlptArrayList[jlptArrayListIndex].word  //제시어 설정

        binding.quizBtn1.text = jlptArrayList[integerArray[0]!!].mean   //제시어에 해당하는 보기1 설정
        binding.quizBtn2.text = jlptArrayList[integerArray[1]!!].mean   //제시어에 해당하는 보기2 설정
        binding.quizBtn3.text = jlptArrayList[integerArray[2]!!].mean   //제시어에 해당하는 보기3 설정
        binding.quizBtn4.text = jlptArrayList[integerArray[3]!!].mean   //제시어에 해당하는 보기4 설정
    }

    //버튼 클릭시 처리되는 메서드
    fun btnInteraction(button: Button, job: Job) {
        //해당 버튼의 보기가 정답인지 판단한다
        if (button.text != jlptArrayList[jlptArrayListIndex].mean) {    //오답일 경우
            prohibitBtn()   //버튼 조작 금지
            job.cancel()    //타이머 정지
            
            binding.answer.setTextColor(resources.getColor(R.color.wrong, resources.newTheme()))    //정답 텍스트 색 설정
            binding.answer.text = jlptArrayList[jlptArrayListIndex].mean    //정답을 띄워준다
            binding.timerOrScore.text = jlptArrayListIndex.toString()   //타이머 기록하는 대신 점수를 보여준다

        } else {    //정답일 경우
            jlptArrayListIndex++    //스코어 누적 및 jlptArrayList의 다음 인덱스번호

            //다음 문제가 남아있는지 확인하고 작업을 수행한다
            if (jlptArrayListIndex == jlptArrayList.size) { //모두 맞춰서 더이상 문제가 없을 경우
                job.cancel()    //타이머 정지
                prohibitBtn()   //버튼 조작 금지

                binding.quiz.text = "CLEAR" //(테스트)제시어 텍스트 변경
                binding.answer.text = "CLEAR"   //(테스트)정답 텍스트 변경
                binding.timerOrScore.text = jlptArrayListIndex.toString()   //타이머 기록하는 대신 점수를 보여준다
            } else {    //아직 문제가 남아있는 경우
                setQuiz()   //(테스트)퀴즈를 다시 셋팅
                timer = 10
            }
        }
    }
    
    //4개 버튼 조작 방지 메서드
    fun prohibitBtn() {
        binding.quizBtn1.isEnabled = false
        binding.quizBtn2.isEnabled = false
        binding.quizBtn3.isEnabled = false
        binding.quizBtn4.isEnabled = false
    }

    //해당 엑티비티를 종료할 경우 본인의 JLPT레벨과 점수를 해당sharedpreferences 갱신시키는 메서드
    fun updateInfo() {
        val strLevel = jlptLv   //현재 사용자가 도전하는 jlpt레벨
        val strScore = jlptArrayListIndex.toString()   //현재 사용자의 점수

        //해당 sharedPreference파일에 기록
        App.localUser.testLevel = strLevel
        App.localUser.testScore = strScore
    }
}