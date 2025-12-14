package edu.iu.luddy.c323_capstone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PokemonListActivity : AppCompatActivity() {

    private var customAdapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val searchView: SearchView = findViewById(R.id.svPokemonSearch)
        val recyclerView: RecyclerView = findViewById(R.id.rvPokemonList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val myBuilder = CronetEngine.Builder(this)
        val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        val gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()
        val networkClient = NetworkClient(cronetEngine, executor)

        networkClient.get("https://pokeapi.co/api/v2/pokemon/?limit=1025") { response ->
            runOnUiThread {
                val pokemon = try {
                    gson.fromJson(response, Pokemon::class.java)
                } catch (e: Exception) {
                    null
                }

                if (pokemon != null && pokemon.pkmn != null) {
                    customAdapter = CustomAdapter(pokemon.pkmn.toMutableList())
                    recyclerView.adapter = customAdapter
                } else {
                    Log.e("PokemonListActivity", "Failed to parse Pokémon list or the list is null.")
                }
            }
        }

        // search logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                customAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                customAdapter?.filter?.filter(newText)
                return true
            }
        })
    }
}