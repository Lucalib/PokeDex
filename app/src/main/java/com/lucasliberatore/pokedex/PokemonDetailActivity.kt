package com.lucasliberatore.pokedex

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout.Spec
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.lucasliberatore.pokedex.databinding.ActivityPokemonDetailBinding
import com.lucasliberatore.pokedex.model.Chain
import com.lucasliberatore.pokedex.model.EvoInfo
import com.lucasliberatore.pokedex.model.Pokemon
import com.lucasliberatore.pokedex.model.Species
import com.lucasliberatore.pokedex.viewModel.EvolutionsViewModel
import com.lucasliberatore.pokedex.viewModel.MainViewModel
import com.lucasliberatore.pokedex.viewModel.SpeciesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    lateinit var viewModel: SpeciesViewModel
    lateinit var viewModelEVO: EvolutionsViewModel
    lateinit var evos: MutableList<EvoInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize binding
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SpeciesViewModel::class.java)
        viewModelEVO = ViewModelProvider(this).get(EvolutionsViewModel::class.java)

        evos = mutableListOf<EvoInfo>()
// Get data from Intent extras
        val pokemonID = intent.getIntExtra("POKEMON_ID", 0)
        val pokemonName = intent.getStringExtra("POKEMON_NAME") ?: "Unknown"
        val pokemonSprite = intent.getStringExtra("POKEMON_SPRITE")
        val pokemonTypes = intent.getStringArrayExtra("POKEMON_TYPES") ?: arrayOf("Unknown")
        val pokemonWeight = intent.getStringExtra("POKEMON_WEIGHT") ?: "Unknown"
        val pokemonHeight = intent.getStringExtra("POKEMON_HEIGHT") ?: "Unknown"
        val pokemonStats = intent.getIntArrayExtra("POKEMON_STATS") ?: intArrayOf(0, 0, 0, 0, 0, 0)
        val pokemonAbilities = intent.getStringArrayExtra("POKEMON_ABILITIES") ?: arrayOf("Unknown")

// Set the views with the data
        binding.pokemonName.text = pokemonName
        binding.pokemonID.text = "N° $pokemonID"
        binding.pokemonWeight.text = "$pokemonWeight"
        binding.pokemonHeight.text = "$pokemonHeight"

// Set Pokemon types
        val typeName = pokemonTypes.getOrNull(0) ?: ""

        binding.pokemonType1.text = typeName
        val colorHex = colorMap[typeName]
        binding.pokemonType1.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))

        // Reset secondary type visibility and text
        binding.pokemonType2.visibility = View.GONE
        binding.pokemonType2.text = ""
        if (pokemonTypes.size > 1) {
            val typeName2 = pokemonTypes.getOrNull(1) ?: ""
            binding.pokemonType2.text = typeName2
            val colorHex2 = colorMap[typeName2]
            binding.pokemonType2.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex2))
            binding.pokemonType2.visibility = View.VISIBLE
        }

        binding.ability1.text = pokemonAbilities.getOrNull(0) ?: ""
        binding.abilitylayout2.visibility = View.GONE

        // Set Pokemon abilities (assuming TextViews for each ability)
        if (pokemonAbilities.size > 1) {
            binding.ability2.text = pokemonAbilities.getOrNull(1) ?: ""
            binding.abilitylayout2.visibility = View.VISIBLE
        }

        // Set Pokemon stats (assuming IDs like HPValue, ATKValue, etc.)
        binding.HPValue.text = pokemonStats.getOrNull(0)?.toString() ?: "0"   // HP
        binding.ATKValue.text = pokemonStats.getOrNull(1)?.toString() ?: "0"  // ATK
        binding.DEFValue.text = pokemonStats.getOrNull(2)?.toString() ?: "0"  // DEF
        binding.SpAValue.text = pokemonStats.getOrNull(3)?.toString() ?: "0"  // SpA
        binding.SpDValue.text = pokemonStats.getOrNull(4)?.toString() ?: "0"  // SpD
        binding.SPDValue.text = pokemonStats.getOrNull(5)?.toString() ?: "0"  // SPD

        // Handle case where Total (TOT) is calculated dynamically
        binding.TOTValue.text = pokemonStats.sum().toString()


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

        runCoroutineFromViewModel(pokemonID)
    }

    private fun runCoroutineFromViewModel(pokemonID: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val request = viewModel.getPokemon(pokemonID)
            if (request != null) {
                // Replace new line characters with an empty string
                val flavorText = request.flavor_text_entries[0].flavor_text.replace("\n", " ")
                binding.pokedexEntry.text = flavorText

                    val evoURL = request.evolution_chain.url
                    val request2 = viewModelEVO.getPokemon(evoURL)
                if (request2 != null) {
                    extractEvolutionData(request2.chain)
                    addEvolutionData(evos)
                }
            }
        }
    }

    fun extractEvolutionData(chain: Chain) {
        // Iterate over all evolutions at this level
        val baseSpecies = chain.species.url.split("/")
        val baseSpeciesId = baseSpecies.get(baseSpecies.size - 2)
        val baseSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$baseSpeciesId.png"

        chain.evolves_to.forEach { evolvesTo ->
            val speciesURL = evolvesTo.species.url
            val speciesArray = speciesURL.split("/")
            val speciesId = speciesArray.get(speciesArray.size - 2)
            val minLevel = evolvesTo.evolution_details.firstOrNull()?.min_level

            val evo = EvoInfo(
                sprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$speciesId.png",
                min_level = minLevel,
                baseSprite = baseSprite
            )
            evos.add(evo)

            if (evolvesTo.evolves_to.isNotEmpty()) {
                extractEvolutionData(Chain(evolvesTo.evolves_to, species = Species("", baseSprite)))
            }
        }
    }

    fun addEvolutionData(evos: List<EvoInfo>) {
        if(evos.size > 0 )
        {
            binding.evolutionsLabel.visibility = View.VISIBLE
            binding.evoView.visibility = View.VISIBLE
            val baseImageView = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(320, 320)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Picasso.get()
                .load(evos[0].baseSprite)
                .into(baseImageView)

            binding.evolutionsContainer.addView(baseImageView)
        }
        evos.forEach { evo ->
            // Create an ImageView for the sprite
            val imageView = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(320, 320)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Picasso.get()
                .load(evo.sprite)
                .into(imageView)

            // Create a TextView for the min level
            val minLevelTextView = TextView(this)
            minLevelTextView.text = "${evo.min_level ?: "?"}"
            minLevelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            minLevelTextView.setTextColor(Color.BLACK)
            minLevelTextView.background = ContextCompat.getDrawable(this, R.drawable.curve)
            minLevelTextView.gravity = Gravity.CENTER
            ViewCompat.setBackgroundTintList(
                minLevelTextView,
                ColorStateList.valueOf(Color.parseColor("#E2E2E7"))
            )
            minLevelTextView.setPadding(42, 15, 42, 15)


            // Add the sprite and min level to the LinearLayout
            binding.evolutionsContainer.addView(minLevelTextView)
            binding.evolutionsContainer.addView(imageView)
        }
    }

    // Close button click handler
    fun onCloseClick(view: View) {
        finish()  // Close the activity and return to the main screen
    }
}