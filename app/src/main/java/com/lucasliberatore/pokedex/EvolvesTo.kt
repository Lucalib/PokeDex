package com.lucasliberatore.pokedex

data class EvolvesTo(
    var evolves_to: List<EvolvesTo>,
    val evolution_details: List<EvolutionDetail>,
    val species: Species,
)
