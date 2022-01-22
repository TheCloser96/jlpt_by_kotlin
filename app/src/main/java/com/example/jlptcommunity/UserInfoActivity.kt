/**
 *  UserInfoActivity
 *  사용자의 정보를 변경및 조회하는 엑티비티
 */
package com.example.jlptcommunity

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.jlptcommunity.api.FirebaseCustom
import com.example.jlptcommunity.databinding.ActivityUserInfoBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class UserInfoActivity : AppCompatActivity() {
    
    /*변수 모음*/
    val binding by lazy { ActivityUserInfoBinding.inflate(layoutInflater) } //바인딩 변수
    val scope = CoroutineScope(Dispatchers.Main)    //코루틴 스코프 변수
    val firebaseUserIdPath = FirebaseCustom.database.child("users").child(FirebaseCustom.uid)   //실시간 데이터베이스 해당 사용자 uid 경로
    val firebaseUserProfilePath = FirebaseCustom.storageRef.child("users").child(FirebaseCustom.uid).child("profile")  //스토리지 해당 사용자의 프로필 사진 경로

    val beforeChangedNick by lazy {
        binding.editNickName.text.toString()
    }   //변화된 닉네임과 비교하기 위해 존재하는 변수
    var imgUri: Uri? = null //이미지 변경시 해당 Uri가 저장되는 변수(기본값은 null)

    var getImgFromUser = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding.userProfileImg.setImageURI(it)  //사용자 프로필 사진 변경
            imgUri = it   //1번이라도 교체가 될 경우 해당 변수에 Uri대입
        }
    }   //권한이 승인된 상태일 경우 갤러리로 부터 이미지를 가져와 사용자 프로필 란을 변경하는 변수



    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkRequestPermission()    //권한 검사하는 메서드
        showLoadingUntilDone()  //사용자의 정보를 불러오는 역할과 화면에 비치되기 이전까지 로딩창을 계속 띄워주는 메서드
    }

    override fun onResume() {
        super.onResume()

        //프로필 사진 클릭 시 사용자의 이미지가 저장된 내부 저장소로 이동
        binding.userProfileImg.setOnClickListener {
            getImgFromUser.launch("image/*")
        }

        //이미지 초기화 버튼 클릭 시 이미지를 기본값, 실시간 데이터베이스에도 값을 변경한다
        binding.setDefaultImgBtn.setOnClickListener {
            binding.userProfileImg.setImageResource(R.drawable.ic_person)   //사용자 프로필 항목을 기본값으로 전환
            imgUri = null  //이전에 저장된 Uri를 null로 전환
            firebaseUserIdPath.child("profile").setValue(null)    //실시간 데이터베이스에서 사용자의 프로필 사진 파일 정보를 삭제한다
        }
    }






    /*메서드 모음*/

    //갤러리접근이 허용되었는지 검사하는 메서드
    fun checkRequestPermission() {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            //권한 설정이 안되어 있는 경우 실행됨
            if (!it) {
                Toast.makeText(this, "사용자 정보 조회 및 수정하려면 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //사용자 정보를 가져올때까지 로딩을 걸어두고, 가져오면 해제하는 메서드
    fun showLoadingUntilDone() {
            scope.launch {
                val loadingDialog = LoadingDialog(this@UserInfoActivity) //로딩 창 객체
                loadingDialog.show()    //로딩 창 나타남

                //사용자 정보를 불러오는 작업
                val getUserInfo = launch {
                    //실시간 데이터베이스로부터 값을 추출하는 작업
                    firebaseUserIdPath.get().addOnSuccessListener { dataSnapshot ->

                        //사용자의 정보를 추출하는 작업
                        val email = FirebaseCustom.email    //해당 사용자의 이메일
                        val uid = FirebaseCustom.uid    //해당 사용자의 고유 아이디값(uid)
                        val nick = dataSnapshot.child("nick").value.toString()    //해당 사용자의 닉네임
                        val profile = if (dataSnapshot.child("profile").value?.toString() != null) {  //해당 사용자의 프로필 사진 파일명
                            dataSnapshot.child("profile").value.toString()
                        } else {
                            null
                        }
                        
                        //사용자 정보 화면에 비치하는 작업
                        if (profile != null) {  //사용자 프로필 이미지 파일 조회작업(프로필 사진 파일명이 존재하는 경우만 실행)
                            firebaseUserProfilePath.child(profile).downloadUrl.addOnSuccessListener { uri ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    Glide.with(this@UserInfoActivity).load(uri).into(binding.userProfileImg)    //이미지 로드하는 속도 문제로 미리 배치 시킴
                                }
                            }
                        } else {    //사용자 프로필 이미지 파일명이 존재하지 않을 경우 실행됨
                            binding.userProfileImg.setImageResource(R.drawable.ic_person)   //해당 화면의 이미지를 기본값으로 정해둔다
                        }
                        binding.userUid.text = uid  //사용자 고유 아이디 화면배치
                        binding.editNickName.setText(nick)  //사용자 닉네임 화면배치
                        binding.userEmail.text = email  //사용자 이메일 화면배치



                        //닉네임과 프로필 사진 변경 이전 비교군으로 미리 저장하는 작업
                        beforeChangedNick   //사용자 별명 변경 이전

                    }.addOnFailureListener {
                        Toast.makeText(this@UserInfoActivity, "오류: 사용자 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }   //어떠한 오류로 의하여 사용자의 정보를 불러오지 못할경우 실행됨
                }
                getUserInfo.join()  //해당 코루틴이 수행 된 이후에 밑의 작업이 실행된다

                //모든 작업 마칠 경우 로딩화면 제거
                delay(3000)
                loadingDialog.dismiss()
            }
    }

    //설정을 마치고 해당 정보들을 저장 후 종료하는 메서드
    @SuppressLint("SimpleDateFormat")
    override fun onBackPressed() {
        var isChangeFlag = false    //이미지 혹은 별명중 하나라도 수정되어 저장될 경우 true

        //사용자가 사용자 프로필 사진을 변경할 경우 작동한다
        if (imgUri != null) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = "IMAGE_"+timeStamp+"_.png"
            firebaseUserIdPath.child("profile").setValue(fileName)  //정해놓은 이미지 파일명을 실시간 데이터베이스의 해당 경로에 저장

            val storageRef = firebaseUserProfilePath.child(fileName)    //스토리지 경로 설정
            storageRef.putFile(imgUri!!)    //설정된 경로에 Uri저장
            isChangeFlag = true //참값으로 전환
        }

        val changedNick = binding.editNickName.text.toString()  //닉네임란에 있는 텍스트를 받는 변수

        //닉네임란에 있는 텍스트가 변경되었을 경우 작동한다
        if (beforeChangedNick != changedNick) {
            firebaseUserIdPath.child("nick").setValue(changedNick)  //수정된 닉네임을 실시간 데이터베이스의 해당 경로에 저장
            isChangeFlag = true //참값으로 전환
        }

        //사용자의 정보가 수정되었을 경우 수정여부 공지
        if (isChangeFlag) {
            Toast.makeText(this, "정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
        }

        finish()    //해당 엑티비티 종료
    }
}