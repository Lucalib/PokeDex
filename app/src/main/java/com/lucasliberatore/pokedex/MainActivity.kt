package com.lucasliberatore.pokedex

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lucasliberatore.pokedex.databinding.ActivityMainBinding
import com.lucasliberatore.pokedex.model.Pokemon
import com.lucasliberatore.pokedex.model.PokemonAPIFormat
import com.lucasliberatore.pokedex.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var pokemons: MutableList<Pokemon>
    lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "pokemon_cache"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up the layout binding (optional but recommended)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val gridLayoutManager = GridLayoutManager(applicationContext, 2)  // 2 items per row
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.setHasFixedSize(true)

        // initialize the list of books
        pokemons = mutableListOf<Pokemon>()

        // launch URL call on thread
        loadPokemonData()
    }

    private fun loadPokemonData() {
        val cachedData = sharedPreferences.getString("pokemons", null)

        if (cachedData != null) {
            // If cached data exists, load it into the list
            val gson = Gson()
            val pokemonArray = gson.fromJson(cachedData, Array<Pokemon>::class.java)
            pokemons.addAll(pokemonArray)
            binding.recyclerView.adapter = RecyclerAdapter(pokemons, this)
        } else {
            showLoadingSpinner()
            runCoroutineFromViewModel()
        }
    }

    @SuppressLint("ResourceType")
    private fun showLoadingSpinner() {
        // Make the Pokeball ImageView visible
        binding.imageViewPokeball.visibility = ImageView.VISIBLE
        val animation: Animation = AnimationUtils.loadAnimation(this, R.animator.spin)
        binding.imageViewPokeball.startAnimation(animation)
    }

    private fun runCoroutineFromViewModel() {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until 493) {
                val request = viewModel.getPokemon(i + 1)
                if (request != null) {
                    val pokemon = Pokemon(
                        id = i + 1,
                        name = request.name,
                        types = request.types,
                        sprites = request.sprites.front_default,
                        height = request.height,
                        weight = request.weight,
                        stats = request.stats,
                        abilities = request.abilities
                    )
                    pokemons.add(pokemon)
                }
            }

            // Cache the fetched data in SharedPreferences
            cachePokemonData()

            // Update the UI on the main thread
            binding.imageViewPokeball.clearAnimation()  // Stop the animation
            binding.imageViewPokeball.visibility = ImageView.INVISIBLE  // Hide the spinner
            binding.recyclerView.adapter = RecyclerAdapter(pokemons, this@MainActivity)
        }
    }

    private fun cachePokemonData() {
        val gson = Gson()
        val json = gson.toJson(pokemons) // Convert the Pokémon list to a JSON string

        // Save the JSON string in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("pokemons", json)
        editor.apply() // Commit changes asynchronously
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
//}