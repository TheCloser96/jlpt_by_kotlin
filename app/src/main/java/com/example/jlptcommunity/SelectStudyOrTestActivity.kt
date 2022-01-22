/**
 *  SelectStudyOrTestActivity
 *  MainActivity에서 단어학습, 단어체크와 공유하는 엑티비티
 *  시스템 내부의 JLPT.db가 존재하지 않을경우 생성한다
 *  단어학습일 경우 mode값은 1 단어체크일 경우 0으로 설정된다
 *  mode값에 의해서 SelectDayActivity 혹은 TestActivity로 이동한다
 */
package com.example.jlptcommunity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.jlptcommunity.databinding.ActivitySelectStudyOrTestBinding
import java.io.*

class SelectStudyOrTestActivity : AppCompatActivity() {

    /*변수 모음*/
    val binding by lazy { ActivitySelectStudyOrTestBinding.inflate(layoutInflater) } //뷰 바인딩 변수
    val mode by lazy { intent.getIntExtra("mode", -1) }  //MainActivity로 부터 넘어올 때 0혹은 1을 부여받는 변수


    
    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //mode의 값이 -1일 경우 이전화면으로 넘어간다
        if (mode == -1) {
            Toast.makeText(this, "접근 오류", Toast.LENGTH_SHORT).show()
            finish()
        }

        checkInAppDatabase(this)    //JLPT.db가 시스템 내부(data/data/com.example.jlptcommunity/databases)에 존재하는지 아닌지 파악후, 없을 경우 assets폴더에 저장된 JLPT.db를 복사한다

        setOnClickJLPTLevelBtn()    //각 레벨별 버튼 리스너 설정 메서드
    }



    /*메서드 모음*/

    //시스템 내부에서 JLPT.db가 존재하는지 판단하는 메서드
    fun checkInAppDatabase(context: Context) {
        val dbPath = context.getDatabasePath("JLPT.db") //시스템 내부의 해당 DB파일을 불러온다

        //만약 시스템 내부에서 존재하지 않을경우 실행된다
        if (!dbPath.exists()) {
            dbPath.parentFile.mkdir()
            copyDatabaseFromAssets(context, dbPath)
        }
    }

    //checkInAppDatabase()에서 시스템 내부의 해당 DB파일이 발견되지 않을 경우 실행되는 메서드, (assets의 폴더에 존재하는 해당 파일을 복사하는 메서드)
    fun copyDatabaseFromAssets(context: Context, dbPath: File) {
        try {
            val inputStream = context.assets.open("database/JLPT.db")
            val output = FileOutputStream(dbPath)
            val buffer = ByteArray(8192)
            var length: Int

            while (true){
                length = inputStream.read(buffer, 0, 8192)
                if(length <= 0)
                    break
                output.write(buffer, 0, length)
            }

            output.flush()
            output.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    //activity_select_study_or_test.xml에 작성된 JLPT레벨별 버튼들의 클릭 리스너들을 모아둔 메서드
    fun setOnClickJLPTLevelBtn() {
        //변수 mode는 단어학습으로 진입한 경우 1, 단어체크는 0 으로 설정됨 (1의 경우 SelectDayActivity로 이동, 0은 TestActivity로 이동)
        binding.jlptN1.setOnClickListener {
            toStudyOrTest(mode, 1)
        }
        binding.jlptN2.setOnClickListener {
            toStudyOrTest(mode, 2)
        }
        binding.jlptN3.setOnClickListener {
            toStudyOrTest(mode, 3)
        }
        binding.jlptN4.setOnClickListener {
            toStudyOrTest(mode, 4)
        }
        binding.jlptN5.setOnClickListener {
            toStudyOrTest(mode, 5)
        }
    }

    //setOnClickJLPTLevelBtn()에서 사용하는 메서드, mode에 따라서 SelectDayActivity 혹은 TestActivity 로 이동하는 메서드
    fun toStudyOrTest(mode: Int, level: Int) {
        //mode에 따라서 SelectDayActivity, TestActivity 둘로 나뉘어진다
        when (mode) {
            1 -> {
                val toStudy = Intent(this, SelectDayActivity::class.java)
                toStudy.putExtra("level", level) //toStudyOrTest메서드에 입력한 level값
                startActivity(toStudy)
            }
            0 -> {
                val toTest = Intent(this, TestActivity::class.java)
                toTest.putExtra("level", level)  //toStudyOrTest메서드에 입력한 level값
                startActivity(toTest)
            }
        }

    }

}