package edu.iu.luddy.c323_capstone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.Strictness
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// Pokemon List

class PokemonListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val myBuilder = CronetEngine.Builder(this)
        val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        val gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()
        val recyclerView : RecyclerView = findViewById(R.id.rvPokemonList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val networkClient = NetworkClient(cronetEngine, executor)
        networkClient.get("https://pokeapi.co/api/v2/pokemon/?limit=1025") { response ->
            runOnUiThread {
                if (response != null) {
                    try {
                        val pokemon = gson.fromJson(response, Pokemon::class.java)
                        if (pokemon?.pkmn != null) {
                            val adapter = CustomAdapter(pokemon.pkmn)
                            recyclerView.adapter = adapter
                        }
                    }
                    catch (e : JsonSyntaxException) {
                        Log.e("MainActivity", "JSON parsing error: ", e)
                    }
                }
            }
        }
    }
}