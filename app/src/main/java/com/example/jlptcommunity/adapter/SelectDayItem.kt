/**
 *  소속 혹은 관련된 Activity: SelectDayActivity
 *  SelectDayActivity의 각 Day와 관련한 내용을 담거나 전달하는 용도의(리사이클러 뷰의 아이템 항목) 데이터 클래스
 */
package com.example.jlptcommunity.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectDayItem(
    val level: Int, //레벨을 알려주는 변수(버튼 색 조정에 사용되기도 함)
    val day: Int,   //버튼의 DAY1, DAY2... 식으로 작성되는 변수
    val numberOfRow: Int,   //해당 레벨의 jlpt 단어의 개수
    val wordsPerDays: Int   //레벨별 하나의 Day당 단어의 개수
    ) : Parcelable