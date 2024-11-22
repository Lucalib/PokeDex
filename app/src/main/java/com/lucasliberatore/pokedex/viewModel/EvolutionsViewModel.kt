package com.lucasliberatore.pokedex.viewModel

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.lucasliberatore.pokedex.model.EvolutionAPIFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class EvolutionsViewModel: ViewModel() {

    suspend fun getPokemon(urlString: String): EvolutionAPIFormat? {
        val defer = CoroutineScope(Dispatchers.IO).async {
            val url =
                URL(urlString)
            val connection = url.openConnection() as HttpsURLConnection
            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                // if crashes on line below your classes are not correct
                val request = Gson().fromJson(inputStreamReader, EvolutionAPIFormat::class.java)
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