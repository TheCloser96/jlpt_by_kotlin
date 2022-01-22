/**
 *  소속 혹은 관련된 Activity: SelectDayActivity
 *  SelectDayActivity의 각 Day를 만들어내고, 각 Day의 특징에 맞게 커스터마이징하는 리사이클러 뷰의 어댑터용 클래스
 */
package com.example.jlptcommunity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jlptcommunity.StudyWordsActivity
import com.example.jlptcommunity.databinding.SelectDayItemBinding

class SelectDayAdapter(val listOfDaysBtn: ArrayList<SelectDayItem>, val context: Context) : RecyclerView.Adapter<SelectDayAdapter.CustomHolder>() {

    //한 화면에 그려지는 아이템 개수만큼 레이아웃 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomHolder {
        val binding = SelectDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomHolder(binding)
    }

    //생성된 아이템 레이아웃에 값 입력 후 목록에 출력
    override fun onBindViewHolder(holder: CustomHolder, position: Int) {
        val selectDayItem = listOfDaysBtn[position]
        holder.setCustomHolder(selectDayItem, context)
    }

    //목록에 보여질 아이템의 개수
    override fun getItemCount() = listOfDaysBtn.size




    //뷰홀더를 상속받는 커스텀 뷰홀더 클래스
    @SuppressLint("ResourceAsColor")
    inner class CustomHolder(val binding: SelectDayItemBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var item: SelectDayItem    //해당 Day와 관련한 정보가 담겨있는 객체

        init {
            //해당 Day를 클릭시 특정 정보들을 가지고 StudyWordsActivity로 이동한다
            binding.daysBtn.setOnClickListener {
                val toStudyWords = Intent(context, StudyWordsActivity::class.java)
                toStudyWords.putExtra("key", item)
                context.startActivity(toStudyWords)
            }
        }


        //주로 화면에 보여지는 것들 위주로 설정하는 메서드
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun setCustomHolder(selectDayItem: SelectDayItem, context: Context) {
            item = selectDayItem    //현재 생성된 뷰홀더와 관련한 데이터를 가져온다

            val dayToString = item.day.toString()   //day(Int형)을 문자열화
            binding.text.text = "Day $dayToString"  //해당 카트뷰의 텍스트를 "Day 01" 과 같은 형태로 바꾼다

            //현재 생성된 뷰홀더와 관련한 난이도에 따라서 뷰홀더의 색이 바뀐다
            when (item.level) {
                1 -> {binding.daysBtn.setCardBackgroundColor(Color.parseColor("#E71A41"))}
                2 -> {binding.daysBtn.setCardBackgroundColor(Color.parseColor("#0094DC"))}
                3 -> {binding.daysBtn.setCardBackgroundColor(Color.parseColor("#7FBE25"))}
                4 -> {binding.daysBtn.setCardBackgroundColor(Color.parseColor("#F0820F"))}
                5 -> {binding.daysBtn.setCardBackgroundColor(Color.parseColor("#A66BAB"))}
            }
        }
        
    }
}