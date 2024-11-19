package com.lucasliberatore.pokedex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lucasliberatore.pokedex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up the layout binding (optional but recommended)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}








//package com.lucasliberatore.pokedex
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.ViewModelProvider
//import com.google.firebase.auth.FirebaseAuth
//import com.lucasliberatore.pokedex.databinding.ActivityMainBinding
//import com.lucasliberatore.pokedex.viewModel.MainViewModel
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//
//    lateinit var binding: ActivityMainBinding
//    lateinit var viewModel: MainViewModel
//    val db = Firebase.firestore
//    val userKey: String = "USER"
//    val todoKey: String = "TODO"
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance()
//
//        // Setting up edge-to-edge display
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Add NavFragment to the FragmentContainerView dynamically
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainerView, NavFragment())
//                .commit()
//        }
//
//        // Set up the ViewModel
////        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//
//        // Load Pokémon data (using coroutine in ViewModel)
////        runCoroutineFromViewModel()
//
//        // Setup the NavFragment to display the navigation bar
////        supportFragmentManager.beginTransaction()
////            .replace(R.id.fragment_container, NavFragment())  // Add the NavFragment
////            .commit()
//
////        // Cloud Firestore, add data
////        binding.buttonAdd.setOnClickListener {
////            val userToSave: MutableMap<String, String> = mutableMapOf()
////            userToSave[userKey] = binding.textViewEmail.text.toString()
////            userToSave[todoKey] = binding.editTextAdd.text.toString()
////            db.collection(binding.textViewEmail.text.toString()) // Use email as collection
////                .add(userToSave)
////                .addOnSuccessListener {
////                    Log.d("Firebase", "${binding.textViewEmail.text} added successfully")
////                }
////                .addOnFailureListener {
////                    Log.d("Firebase", "${binding.textViewEmail.text} not added")
////                }
////        }
////
////        // Cloud Firestore, read data
////        binding.buttonRead.setOnClickListener {
////            db.collection(binding.textViewEmail.text.toString()) // Use email as collection
////                .get()
////                .addOnSuccessListener { result ->
////                    for (document in result) {
////                        Log.d("Firestore", "${document.id} => ${document.data}")
////                        binding.editTextAdd.setText(document.data[todoKey].toString())
////                    }
////                }
////                .addOnFailureListener { exception ->
////                    Log.w("Firestore", "Error getting documents.", exception)
////                }
////        }
////
////        // Logout button functionality
////        binding.buttonLogout.setOnClickListener {
////            FirebaseAuth.getInstance().signOut()
////            Toast.makeText(this, R.string.logged_out, Toast.LENGTH_SHORT).show()
////            startActivity(Intent(this, LoginActivity::class.java))
////            finish()
////        }
//    }
//
////    private fun runCoroutineFromViewModel() {
////        CoroutineScope(Dispatchers.Main).launch {
////            val request = viewModel.getPokemon()
////            if (request != null) {
////                Log.i("TAG", request.toString())
////            }
////        }
////    }
//}
