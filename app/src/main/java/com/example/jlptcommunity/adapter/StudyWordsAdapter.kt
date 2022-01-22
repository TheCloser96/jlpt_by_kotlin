/**
 *  소속 혹은 관련된 Activity: StudyWordsActivity
 *  StudyWordsActivity의 리사이클러 뷰 에 생성된 단어들과 각 단어에 해당하는 TTS버튼들이 있는 리사이클러 뷰의 어댑터용 클래스
 */
package com.example.jlptcommunity.adapter

import android.content.Context
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jlptcommunity.database.WordsEntity
import com.example.jlptcommunity.databinding.StudyWordsItemBinding
import kotlin.collections.ArrayList

class StudyWordsAdapter(val wordsList: ArrayList<WordsEntity>, val context: Context, val tts: TextToSpeech) : RecyclerView.Adapter<StudyWordsAdapter.Customholder>() {

    //한 화면에 그려지는 아이템 개수만큼 레이아웃 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Customholder {
        val binding = StudyWordsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Customholder(binding)
    }

    //생성된 아이템 레이아웃에 값 입력 후 목록에 출력
    override fun onBindViewHolder(holder: Customholder, position: Int) {
        val studyWordsItem = wordsList[position]
        holder.setCustomholder(studyWordsItem)
    }

    //목록에 보여질 아이템의 개수
    override fun getItemCount() = wordsList.size





    //뷰 홀더를 상속받는 커스텀 뷰 홀더
    inner class Customholder(val binding: StudyWordsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var item: WordsEntity  //해당 단어 카드뷰와 관련한 정보가 담겨있는 객체

        init {
            //재생 버튼 클릭 시 해당 단어의 발음을 재생
            binding.playBtn.setOnClickListener {
                speak(item)
            }
        }


        //화면에 보이는 것들을 위주로 설정 및 TTS언어 설정 메서드 실행 메서드
        fun setCustomholder(studyWordsItem: WordsEntity) {
            item = studyWordsItem   //현재 생성된 뷰 홀더와 관련한 데이터를 가져온다

            //해당 카드뷰의 텍스트들을 설정
            binding.word.text = studyWordsItem.word
            binding.mean.text = studyWordsItem.mean
            binding.pronunciation.text = studyWordsItem.pronunciation
            
            //해당 카드뷰의 색을 설정
            when (item.level) {
                "N1" -> {binding.wordCardView.setCardBackgroundColor(Color.parseColor("#E71A41"))}
                "N2" -> {binding.wordCardView.setCardBackgroundColor(Color.parseColor("#0094DC"))}
                "N3" -> {binding.wordCardView.setCardBackgroundColor(Color.parseColor("#7FBE25"))}
                "N4" -> {binding.wordCardView.setCardBackgroundColor(Color.parseColor("#F0820F"))}
                "N5" -> {binding.wordCardView.setCardBackgroundColor(Color.parseColor("#A66BAB"))}
            }
        }

        //해당 단어 카드뷰의 단어를 TTS 음성출력하는 메서드
        fun speak(item: WordsEntity) {
            val pronunciation = item.pronunciation
            tts.speak(pronunciation, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}