package com.lucasliberatore.pokedex

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.lucasliberatore.pokedex.databinding.ActivityPokemonDetailBinding

class PokemonDetailActivity : AppCompatActivity() {

    val colorMap = mapOf(
        "Grass" to "#93D776",
        "Water" to "#59AEFF",
        "Fire" to "#FF6849",
        "Poison" to "#BC76AE",
        "Flying" to "#85AEFF",
        "Bug" to "#BCC949",
        "Normal" to "#C9C9BD",
        "Electric" to "#FFD759",
        "Ground" to "#E5C976",
        "Fairy" to "#FFBDFF",
        "Fighting" to "#C97668",
        "Psychic" to "#FF76AE",
        "Rock" to "#C9BD85",
        "Steel" to "#BCBDC9",
        "Ice" to "#93E5FF",
        "Ghost" to "#8585C9",
        "Dragon" to "#9385F2",
        "Dark" to "#624D4E"
    )

    lateinit var binding: ActivityPokemonDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize binding
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent extras
        val pokemonID = intent.getIntExtra("POKEMON_ID", 0)
        val pokemonName = intent.getStringExtra("POKEMON_NAME") ?: "Unknown"
        val pokemonSprite = intent.getStringExtra("POKEMON_SPRITE")
        val pokemonTypes = intent.getStringArrayExtra("POKEMON_TYPES")
        val pokemonWeight = intent.getStringExtra("POKEMON_WEIGHT")
        val pokemonHeight = intent.getStringExtra("POKEMON_HEIGHT")
        val pokemonStats = intent.getIntArrayExtra("POKEMON_STATS")
        val pokemonAbilities = intent.getStringArrayExtra("POKEMON_ABILITIES")

        // Set the views with the data
        binding.pokemonName.text = pokemonName
        binding.pokemonID.text = "N° $pokemonID"
        binding.pokemonWeight.text = "$pokemonWeight"
        binding.pokemonHeight.text = "$pokemonHeight"
        binding.pokemonType.text = "${pokemonTypes?.joinToString(", ")}"
//        binding.pokemonStats.text = "Stats: ${pokemonStats?.joinToString(", ")}"
//        binding.pokemonAbilities.text = "Abilities: ${pokemonAbilities?.joinToString(", ")}"

        // Load the Pokémon sprite using Glide
        if (!pokemonSprite.isNullOrEmpty()) {
            Glide.with(this)
                .load(pokemonSprite)
                .into(binding.pokemonSprite)
        }

        // Set the top background color based on type
        val pokemonTypeColor = colorMap[pokemonTypes?.get(0)] ?: "#FF6849" // Default color
        binding.topBackground.setBackgroundColor(Color.parseColor(pokemonTypeColor))
        binding.main.setBackgroundColor(Color.parseColor(pokemonTypeColor))
    }

    // Close button click handler
    fun onCloseClick(view: View) {
        finish()  // Close the activity and return to the main screen
    }
}