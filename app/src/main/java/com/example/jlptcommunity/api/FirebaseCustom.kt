/**
 *  FirebaseCustom
 *  각 엑티비티에서 자주 사용 및 코드가 난잡할 우려가 있는 Firebase와 관련한 변수 메서드 등을 모아둠
 */
package com.example.jlptcommunity.api

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseCustom {
    /*변수모음*/
    //구글 로그인 관련
    val user = Firebase.auth.currentUser!!  //구글 로그인 접속할 경우 해당 사용자와 관련한 정보를 얻을 때 사용되는 변수
    val email = Firebase.auth.currentUser!!.email  //구글 로그인 접속할 경우 해당 사용자의 구글 이메일 주소
    val uid = Firebase.auth.currentUser!!.uid   //구글 로그인 접속시 부여받는 사용자의 고유 ID(구글 로그인으로 밖에 못얻음)

    //실시간 데이터베이스 관련
    val database = Firebase.database.reference  //Firebase에서 데이터 읽기 및 쓰기 동작시 필요한 변수

    //클라우드 스토리지 관련
    var storageRef = Firebase.storage.reference  //파일 업/다운/삭제, 메타데이터 가져오기/업데이트를 하기위한 참조변수
}