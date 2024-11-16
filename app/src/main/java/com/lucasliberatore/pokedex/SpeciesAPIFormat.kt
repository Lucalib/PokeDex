package com.lucasliberatore.pokedex

data class SpeciesAPIFormat(
    var evolution_chain: EvolutionChain,
    var flavor_text_entries: List<FlavorTextEntries>,
)
