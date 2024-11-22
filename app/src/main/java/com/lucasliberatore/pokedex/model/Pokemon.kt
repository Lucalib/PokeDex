package com.lucasliberatore.pokedex.model


class Pokemon(
    var name: String,
    var id: Int,
    var types: List<Types>,
    var sprites: String,
    var description: String? = null,
    var height: String,
    var weight: String,
    var abilities: List<Abilities>,
    var stats: List<Stats>,
    var evolutions: Chain? = null
)