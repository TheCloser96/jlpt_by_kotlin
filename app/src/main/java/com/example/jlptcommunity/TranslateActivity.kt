/**
 *  TranslateActivity
 *  한국어 혹은 일본어를 입력하는 경우 해당 언어의 문장을 실시간으로 변역해서 보여준다
 */
package com.example.jlptcommunity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.jlptcommunity.api.Papago
import com.example.jlptcommunity.databinding.ActivityTranslateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TranslateActivity : AppCompatActivity() {

    /*변수 모음*/
    val binding by lazy { ActivityTranslateBinding.inflate(layoutInflater) }    //바인딩 변수
    var language = "Korea" //사용자가 입력하는 언어(초기값: Korean)
    val scope = CoroutineScope(Dispatchers.Default)    //코루틴 설정변수

    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        toggleBtn() //번역언어 변경 버튼 설정
        translateStr()  //실시간 언어변역 실행
    }



    /*메서드 모음*/
    
    //변역언어 변경시켜주는 메서드
    fun toggleBtn() {
        binding.toggle.setOnClickListener {
            //번역하려는 언어에 맞게 변수값 및 화면이 변경
            if (language == "Korea") {
                language = "Japan"
                binding.beTranslate.text = "일본어"
                binding.translated.text = "한국어"
            } else {
                language = "Korea"
                binding.beTranslate.text = "한국어"
                binding.translated.text = "일본어"
            }
        }
    }

    //사용자가 단어 혹은 문장 입력후 바로 번역본을 보여주는 메서드
    fun translateStr() {
        //사용자의 입력이 끝난 이후에 번역이 시작됨
        binding.beTranslateStr.addTextChangedListener(afterTextChanged = {
            //번역 및 번역본 화면에 표현하는 코루틴(Dispatchers: Default)
            scope.launch {
                //파파고API를 사용하여 번역
                val translatedStr = if (language == "Korea") Papago.translate(binding.beTranslateStr.text.toString(), "ko", "ja")
                                    else Papago.translate(binding.beTranslateStr.text.toString(), "ja", "ko")

                //번역본을 화면에 표현하는 내부코루틴(Dispatchers: Main)
                launch(Dispatchers.Main) { binding.translatedStr.text = translatedStr }
            }
        })
    }

}