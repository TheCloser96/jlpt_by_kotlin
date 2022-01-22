/**
 *  LogoActivity
 *  어플리케이션 처음 실행 시 나타나는 화면
 *  특정 시간동안 대기 후 화면이 이동된다(후에 자세한 설정은 개발방향에 따라서 작성)
 */
package com.example.jlptcommunity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.jlptcommunity.databinding.ActivityLogoBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoActivity : AppCompatActivity() {

    /*변수모음*/
    val binding by lazy { ActivityLogoBinding.inflate(layoutInflater) } //바인딩 변수
    
    //구글 로그인 구성시 필요한 변수 목록들
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val startForResult =    //onActivityResult가 deprecated되어서 대체제로 사용됨
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

                try {
                    //구글 로그인 성공
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    //구글 로그인 실패
                }
            }
        }




    /*Activity 생명주기 관련 메서드 모음*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //구글 로그인 구성 단계
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //Firebase 인증 초기화
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        
        //사용자가 이미 로그인을 한 상태일 경우 설정된 해당 엑티비티로 진행하게 한다
        val currentUser = auth.currentUser
        updateUI(currentUser)

        //구글 로그인 버튼 설정
        binding.signInBtn.setOnClickListener { signIn() }
    }






    /*메서드 모음*/

    //구글 로그인 메서드
    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }
    
    //구글 토큰 권한검사 메서드
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //구글 로그인 성공시 사용자의 계정을 메서드 입력값으로 전달
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    //구글 로그인 성공시 사용자의 계정을 메서드 입력값으로 전달
                    updateUI(null)
                }
            }
    }

    //구글 로그인 사용자값이 null과 아닌 경우로 나뉘어 화면 변화를 주는 메서드
    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            toMain()    //다음 화면으로 넘어가기
        } else {
            Toast.makeText(this, "사용자로 부터 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    //구글 로그인에 성공하여 다음 화면으로 넘어가게 되는 메서드
    fun toMain() {
        Toast.makeText(this@LogoActivity, "환영합니다", Toast.LENGTH_SHORT).show()

        val toMain = Intent(this@LogoActivity, MainActivity::class.java)
        startActivity(toMain)
    }
}