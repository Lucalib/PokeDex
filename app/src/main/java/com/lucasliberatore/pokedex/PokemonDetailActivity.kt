package com.lucasliberatore.pokedex

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import com.lucasliberatore.pokedex.databinding.ActivityPokemonDetailBinding
import com.lucasliberatore.pokedex.model.Abilities
import com.lucasliberatore.pokedex.model.Ability
import com.lucasliberatore.pokedex.model.Chain
import com.lucasliberatore.pokedex.model.EvoInfo
import com.lucasliberatore.pokedex.model.Pokemon
import com.lucasliberatore.pokedex.model.Species
import com.lucasliberatore.pokedex.model.Stats
import com.lucasliberatore.pokedex.model.Type
import com.lucasliberatore.pokedex.model.Types
import com.lucasliberatore.pokedex.viewModel.EvolutionsViewModel
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
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var pokemonJsonData: String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_pokemon_detail)
        viewModel = ViewModelProvider(this).get(SpeciesViewModel::class.java)
        viewModelEVO = ViewModelProvider(this).get(EvolutionsViewModel::class.java)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        evos = mutableListOf<EvoInfo>()

        auth = FirebaseAuth.getInstance()

        intent.getStringArrayExtra("POKEMON_TYPES") ?: arrayOf("Unknown")

        val pokemonTypeNames = intent.getStringArrayExtra("POKEMON_TYPES") ?: arrayOf("Unknown")
        val pokemonTypes: List<Types> = pokemonTypeNames.mapIndexed { index, typeName ->
            Types(
                slot = null,
                type = Type(name = typeName)
            )
        }

        val pokemonStatValues = intent.getIntArrayExtra("POKEMON_STATS") ?: intArrayOf(0, 0, 0, 0, 0, 0)
        val pokemonStats: List<Stats> = pokemonStatValues.mapIndexed { index, statValue ->
            Stats(
                base_stat = statValue,
                stat = null
            )
        }

        val pokemonAbilityNames = intent.getStringArrayExtra("POKEMON_ABILITIES") ?: arrayOf("Unknown")
        val pokemonAbilities: List<Abilities> = pokemonAbilityNames.map { abilityName ->
            Abilities(
                ability = Ability(name = abilityName)
            )
        }

        val pokemon = Pokemon(
            id = intent.getIntExtra("POKEMON_ID", 0),
            name = intent.getStringExtra("POKEMON_NAME") ?: "Unknown",
            sprites = intent.getStringExtra("POKEMON_SPRITE")!!,
            types = pokemonTypes,
            weight = intent.getStringExtra("POKEMON_WEIGHT") ?: "Unknown",
            height = intent.getStringExtra("POKEMON_HEIGHT") ?: "Unknown",
            stats = pokemonStats,
            abilities = pokemonAbilities
        )
        binding.pokemon = pokemon

        val typeName = pokemon.types.getOrNull(0)!!.type.name ?: ""
        binding.pokemonType1.text = typeName
        val colorHex = colorMap[typeName]
        binding.pokemonType1.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))

        binding.pokemonType2.visibility = View.GONE
        binding.pokemonType2.text = ""
        if (pokemon.types.size > 1) {
            val typeName2 = pokemon.types.getOrNull(1)!!.type.name ?: ""
            binding.pokemonType2.text = typeName2
            val colorHex2 = colorMap[typeName2]
            binding.pokemonType2.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex2))
            binding.pokemonType2.visibility = View.VISIBLE
        }

        binding.ability1.text = pokemon.abilities.getOrNull(0)!!.ability.name ?: ""
        binding.abilitylayout2.visibility = View.GONE

        if (pokemonAbilities.size > 1) {
            binding.ability2.text = pokemon.abilities.getOrNull(1)!!.ability.name ?: ""
            binding.abilitylayout2.visibility = View.VISIBLE
        }

        binding.TOTValue.text = pokemonStatValues.sum().toString()

        if (!pokemon.sprites.isNullOrEmpty()) {
            Glide.with(this)
                .load(pokemon.sprites)
                .into(binding.pokemonSprite)
        }

        val pokemonTypeColor = colorMap[pokemon.types?.get(0)!!.type.name] ?: "#FF6849"
        binding.topBackground.setBackgroundColor(Color.parseColor(pokemonTypeColor))
        binding.main.setBackgroundColor(Color.parseColor(pokemonTypeColor))

        if (auth.currentUser != null) {
            db.collection(auth.currentUser!!.email!!)
                .whereEqualTo("pokemonId", binding.pokemon?.id)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        binding.topLeftImageButtonOff.setImageResource(android.R.drawable.btn_star_big_on)
                    }
                }
        }
        runCoroutineFromViewModel(pokemon.id)
    }

    private fun runCoroutineFromViewModel(pokemonID: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val request = viewModel.getPokemon(pokemonID)
            if (request != null) {
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
            val imageView = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(320, 320)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Picasso.get()
                .load(evo.sprite)
                .into(imageView)

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

            binding.evolutionsContainer.addView(minLevelTextView)
            binding.evolutionsContainer.addView(imageView)
        }
    }

    fun catchPokemon(view: View) {
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val userEmail = auth.currentUser?.email ?: ""
            val currentDrawable = binding.topLeftImageButtonOff.drawable
            val offDrawable = resources.getDrawable(android.R.drawable.btn_star_big_off, null)
            if (currentDrawable.constantState == offDrawable.constantState) {
                binding.topLeftImageButtonOff.setImageResource(android.R.drawable.btn_star_big_on)
                addPokemonToFirestore(userEmail)
            } else {
                binding.topLeftImageButtonOff.setImageResource(android.R.drawable.btn_star_big_off)
                removePokemonFromFirestore(userEmail)
            }
        }
    }

    private fun addPokemonToFirestore(userEmail: String) {
        val pokemonTypes = binding.pokemon?.types?.map { it.type.name } ?: listOf()
        val pokemonStats = binding.pokemon?.stats?.map { it.base_stat } ?: listOf()
        val pokemonAbilities = binding.pokemon?.abilities?.map { it.ability.name } ?: listOf()

        val pokemonData = hashMapOf(
            "pokemonName" to binding.pokemon?.name,
            "pokemonId" to binding.pokemon?.id,
            "pokemonSprite" to "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${binding.pokemon?.id}.png",
            "pokemonHeight" to binding.pokemon?.height,
            "pokemonWeight" to binding.pokemon?.weight,
            "pokemonTypes" to pokemonTypes,
            "pokemonStats" to pokemonStats,
            "pokemonAbilities" to pokemonAbilities,
        )
        db.collection(userEmail)
            .add(pokemonData)
    }

    private fun removePokemonFromFirestore(userEmail: String) {
        db.collection(userEmail)
            .whereEqualTo("pokemonId", binding.pokemon?.id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                }
            }
    }

    fun onCloseClick(view: View) {
        finishAfterTransition()
    }
}