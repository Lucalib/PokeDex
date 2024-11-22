package com.lucasliberatore.pokedex

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.lucasliberatore.pokedex.model.Pokemon
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val dataSet: List<Pokemon>, private val context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
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

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
        var textViewID: TextView
        var textViewName: TextView
        var textViewType: TextView
        var textViewTypeSecondary: TextView
        var card_view: CardView

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.imageViewPokemon)
            textViewID = view.findViewById<TextView>(R.id.textViewID)
            textViewName = view.findViewById<TextView>(R.id.textViewName)
            textViewType = view.findViewById<TextView>(R.id.textViewType)
            textViewTypeSecondary = view.findViewById<TextView>(R.id.textViewTypeSecondary)
            card_view = view.findViewById<CardView>(R.id.card_view)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_pokemon, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val pokemon = dataSet[position]
        Picasso.get().load(pokemon.sprites).into(viewHolder.imageView)

        var pokemonID = pokemon.id
        var pokemonName = pokemon.name.replaceFirstChar { it.uppercaseChar() }
        var pokemonTypes = pokemon.types
        var pokemonWeight = (pokemon.weight.toFloat() / 10).toString() + "m"
        var pokemonHeight = (pokemon.height.toFloat() / 10).toString() + "kg"
        var pokemonStats = pokemon.stats
        var pokemonAbilities = pokemon.abilities

        // Set Pokémon ID and Name
        viewHolder.textViewID.text = "N° $pokemonID"
        viewHolder.textViewName.text = pokemonName

        // Set the primary type
        val typeName = pokemonTypes[0].type.name.replaceFirstChar { it.uppercaseChar() }
        viewHolder.textViewType.text = typeName
        val colorHex = colorMap[typeName]
        viewHolder.textViewType.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))

        // Reset secondary type visibility and text
        viewHolder.textViewTypeSecondary.visibility = View.GONE
        viewHolder.textViewTypeSecondary.text = ""

        // Set the secondary type if it exists
        if (pokemonTypes.size > 1) {
            val typeSecondaryName = pokemonTypes[1].type.name.replaceFirstChar { it.uppercaseChar() }
            viewHolder.textViewTypeSecondary.text = typeSecondaryName
            val colorHex2 = colorMap[typeSecondaryName]
            viewHolder.textViewTypeSecondary.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex2))
            viewHolder.textViewTypeSecondary.visibility = View.VISIBLE
        }


        viewHolder.card_view.setOnClickListener {
            val intent = Intent(context, PokemonDetailActivity::class.java)
            intent.putExtra("POKEMON_ID", pokemonID)
            intent.putExtra("POKEMON_NAME", pokemonName)
            intent.putExtra("POKEMON_SPRITE", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/$pokemonID.gif")
            intent.putExtra("POKEMON_TYPES", pokemonTypes.map { it.type.name.replaceFirstChar { it.uppercaseChar() } }.toTypedArray())
            intent.putExtra("POKEMON_WEIGHT", pokemonWeight)
            intent.putExtra("POKEMON_HEIGHT", pokemonHeight)
            intent.putExtra("POKEMON_STATS", pokemonStats.map { it.base_stat }.toIntArray())
            intent.putExtra("POKEMON_ABILITIES", pokemonAbilities.map { it.ability.name }.toTypedArray())
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
