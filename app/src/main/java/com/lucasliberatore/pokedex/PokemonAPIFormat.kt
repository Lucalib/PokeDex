package com.lucasliberatore.pokedex

data class PokemonAPIFormat(
    var name:String,
    var id: Int,
    var height:String,
    var weight:String,
    var types: List<Types>,
    var stats: List<Stats>,
    var sprites: Sprites,
    var abilities: List<Abilities>
)
