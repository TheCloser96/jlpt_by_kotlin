/**
 *  LoadingDialog
 *  해당 화면을 구성하는 데이터들이 보여지기 이전 로딩 화면연출이 필요한 경우 사용되는 클래스
 */
package com.example.jlptcommunity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class LoadingDialog (context: Context) : Dialog(context){

    init {
        setCanceledOnTouchOutside(false)    //다이얼로그 외부 화면 터치시 종료되는것을 방지

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))    //다이얼로그의 배경 투명하게 만들기

        setContentView(R.layout.dialog_loading)
    }
}