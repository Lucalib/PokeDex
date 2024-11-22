package com.lucasliberatore.pokedex.model

data class EvolutionAPIFormat(
    var chain: Chain
)

data class Chain(
    var evolves_to: List<EvolvesTo>,
    var species: Species
)

data class EvolvesTo(
    var evolves_to: List<EvolvesTo>,
    val evolution_details: List<EvolutionDetail>,
    val species: Species,
)

data class EvolutionDetail(
    val min_level: Int?,
    val trigger: Trigger?
)

data class Trigger(
    val name: String,
)

data class Species(
    val name: String,
    val url: String
)

data class EvoInfo(
    val sprite: String,
    val min_level: Int?,
    val baseSprite: String
)
