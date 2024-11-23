package com.lucasliberatore.pokedex

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.lucasliberatore.pokedex.databinding.ActivityMainBinding
import com.lucasliberatore.pokedex.model.Pokemon
import com.lucasliberatore.pokedex.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var pokemons: MutableList<Pokemon>
    lateinit var filteredPokemons: MutableList<Pokemon>
    lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "pokemon_cache"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.setHasFixedSize(true)

        pokemons = mutableListOf<Pokemon>()
        filteredPokemons = mutableListOf<Pokemon>()
        loadPokemonData()
    }

    private fun loadPokemonData()
    {
        val cachedData = sharedPreferences.getString("pokemons", null)
        if (cachedData != null)
        {
            val gson = Gson()
            val pokemonArray = gson.fromJson(cachedData, Array<Pokemon>::class.java)
            pokemons.addAll(pokemonArray)
            filteredPokemons.addAll(pokemons)
            binding.recyclerView.adapter = RecyclerAdapter(filteredPokemons, this)
        }
        else
        {
            showLoadingSpinner()
            runCoroutineFromViewModel()
        }
    }

    @SuppressLint("ResourceType")
    private fun showLoadingSpinner()
    {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.animator.spin)
        binding.imageViewPokeball.visibility = ImageView.VISIBLE
        binding.imageViewPokeball.startAnimation(animation)
    }

    private fun runCoroutineFromViewModel()
    {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until 493)
            {
                val request = viewModel.getPokemon(i + 1)
                if (request != null)
                {
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
            cachePokemonData()

            binding.imageViewPokeball.clearAnimation()
            binding.imageViewPokeball.visibility = ImageView.INVISIBLE
            binding.recyclerView.adapter = RecyclerAdapter(pokemons, this@MainActivity)
        }
    }

    private fun cachePokemonData()
    {
        val gson = Gson()
        val json = gson.toJson(pokemons)

        val editor = sharedPreferences.edit()
        editor.putString("pokemons", json)
        editor.apply()
    }

    fun performSearch(view: android.view.View)
    {
        val query = binding.pokemonSearch.text.toString().trim().lowercase()
        filterPokemonList(query)
    }

    private fun filterPokemonList(query: String)
    {
        filteredPokemons.clear()
        if (query.isEmpty()) {
            filteredPokemons.addAll(pokemons)
        } else {
            filteredPokemons.addAll(
                pokemons.filter { it.name.lowercase().startsWith(query.lowercase()) }
            )
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}

//    val db = Firebase.firestore
//    val userKey: String = "USER"
//    val todoKey: String = "TODO"
//    private lateinit var auth: FirebaseAuth
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance()

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