package com.lucasliberatore.pokedex

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucasliberatore.pokedex.model.Pokemon
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val dataSet: List<Pokemon>) :
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
        "Dragon" to "#9385F2"
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

        init {
            // Define click listener for the ViewHolder's View.
            imageView = view.findViewById(R.id.imageViewPokemon)
            textViewID = view.findViewById<TextView>(R.id.textViewID)
            textViewName = view.findViewById<TextView>(R.id.textViewName)
            textViewType = view.findViewById<TextView>(R.id.textViewType)
            textViewTypeSecondary = view.findViewById<TextView>(R.id.textViewTypeSecondary)
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
        Picasso.get().load(dataSet[position].sprites).into(viewHolder.imageView)
        viewHolder.textViewID.text = "N° " + dataSet[position].id.toString()
        viewHolder.textViewName.text = dataSet[position].name.replaceFirstChar { it.uppercaseChar() }
        var typeName = dataSet[position].types[0].type.name.replaceFirstChar { it.uppercaseChar() }
        viewHolder.textViewType.text = typeName
        val colorHex = colorMap[typeName]
        viewHolder.textViewType.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))

        if(dataSet[position].types.size > 1)
        {
            var typeSecondaryName = dataSet[position].types[1].type.name.replaceFirstChar { it.uppercaseChar() }
            viewHolder.textViewTypeSecondary.text = typeSecondaryName
            val colorHex2 = colorMap[typeSecondaryName]
            viewHolder.textViewTypeSecondary.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex2))
            viewHolder.textViewTypeSecondary.visibility = View.VISIBLE
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
