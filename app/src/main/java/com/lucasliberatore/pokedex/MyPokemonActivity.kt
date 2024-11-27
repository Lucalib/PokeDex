package com.lucasliberatore.pokedex

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.lucasliberatore.pokedex.databinding.ActivityMainBinding
import com.lucasliberatore.pokedex.model.Abilities
import com.lucasliberatore.pokedex.model.Ability
import com.lucasliberatore.pokedex.model.Pokemon
import com.lucasliberatore.pokedex.model.Stats
import com.lucasliberatore.pokedex.model.Type
import com.lucasliberatore.pokedex.model.Types

class MyPokemonActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var pokemons: MutableList<Pokemon>
    lateinit var filteredPokemons: MutableList<Pokemon>
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.setHasFixedSize(true)

        pokemons = mutableListOf<Pokemon>()
        filteredPokemons = mutableListOf<Pokemon>()
    }

    override fun onResume() {
        loadPokemonData()
        super.onResume()
    }

    private fun loadPokemonData() {
        if (auth.currentUser != null) {
            pokemons.clear()
            filteredPokemons.clear()
            val email = auth.currentUser!!.email!!
            db.collection(email)
                .orderBy("pokemonId")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        val pokemonTypes = document.get("pokemonTypes") as? List<String> ?: listOf()
                        val pokemonTypeNames: List<Types> = pokemonTypes.mapIndexed { index, typeName ->
                            Types(
                                slot = null,
                                type = Type(name = typeName)
                            )
                        }
                        val pokemonStats = document.get("pokemonStats") as? List<Int> ?: listOf()
                        val pokemonStatValues: List<Stats> = pokemonStats.mapIndexed { index, statValue ->
                            Stats(
                                base_stat = statValue,
                                stat = null
                            )
                        }

                        val pokemonAbilities = document.get("pokemonAbilities") as? List<String> ?: listOf()
                        val pokemonAbilityNames: List<Abilities> = pokemonAbilities.map { abilityName ->
                            Abilities(
                                ability = Ability(name = abilityName)
                            )
                        }

                        val pokemon = Pokemon(
                            id = document.getLong("pokemonId")?.toInt() ?: 0,
                            name = document.getString("pokemonName") ?: "Unknown",
                            types = pokemonTypeNames,
                            sprites = document.getString("pokemonSprite") ?: "Unknown",
                            height = document.getString("pokemonHeight") ?: "Unknown",
                            weight = document.getString("pokemonWeight") ?: "Unknown",
                            stats = pokemonStats.map { stat -> Stats(base_stat = stat.toInt(), stat = null) },
                            abilities = pokemonAbilities.map { ability -> Abilities(ability = Ability(name = ability)) }
                        )
                        pokemons.add(pokemon)
                    }
                    filteredPokemons.addAll(pokemons)
                    binding.recyclerView.adapter = RecyclerAdapter(filteredPokemons, this, this )
                }
        }
    }

    @SuppressLint("ResourceType")
    private fun showLoadingSpinner()
    {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.animator.spin)
        binding.imageViewPokeball.visibility = ImageView.VISIBLE
        binding.imageViewPokeball.startAnimation(animation)
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