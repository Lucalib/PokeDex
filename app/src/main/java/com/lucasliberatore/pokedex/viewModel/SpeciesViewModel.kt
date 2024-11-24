package com.lucasliberatore.pokedex.viewModel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.lucasliberatore.pokedex.model.SpeciesAPIFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class SpeciesViewModel: ViewModel() {

    suspend fun getPokemon(int: Int): SpeciesAPIFormat? {
        val defer = CoroutineScope(Dispatchers.IO).async {
            val url =
                URL("https://pokeapi.co/api/v2/pokemon-species/$int")
            val connection = url.openConnection() as HttpsURLConnection
            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, SpeciesAPIFormat::class.java)
                inputStreamReader.close()
                inputSystem.close()
                return@async request
            }
            else {
                return@async null
            }
        }
        return defer.await()
    }
}