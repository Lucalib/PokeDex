package com.lucasliberatore.pokedex.model

data class SpeciesAPIFormat(
    var evolution_chain: EvolutionChain,
    var flavor_text_entries: List<FlavorTextEntries>,
)

data class EvolutionChain(
    var url:String
)

data class FlavorTextEntries(
    var flavor_text:String
)