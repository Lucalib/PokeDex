package com.lucasliberatore.pokedex.model

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

data class Types(
    var slot: Int?,
    var type: Type
)

data class Type(
    var name: String
)

data class Stats(
    var base_stat:Int,
    var stat: Stat?
)

data class Stat(
    var name:String,
)

data class Sprites(
    var front_default: String,
)

data class Abilities(
    var ability: Ability
)

data class Ability(
    var name:String
)