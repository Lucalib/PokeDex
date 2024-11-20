package com.lucasliberatore.pokedex.model


class Pokemon(
    var name: String,
    var id: Int,
    var types: List<Types>,
    var sprites: String,
    var description: String? = null,
    var height: String? = null,
    var weight: String? = null,
    var abilities: List<Abilities>? = null,
    var stats: List<Stats>? = null,
    var evolutions: Chain? = null
)