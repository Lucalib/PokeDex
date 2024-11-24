package com.lucasliberatore.pokedex

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.lucasliberatore.pokedex.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.buttonRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.username.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.password.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val email:String = binding.username.text.toString().trim {it <= ' '}
                    val password:String = binding.password.text.toString().trim {it <= ' '}
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val intent = Intent(this,LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("userid",auth.currentUser?.uid)
                                intent.putExtra("email",user)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }

                }
            }
        }
    }
}