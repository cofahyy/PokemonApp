package edu.iu.luddy.c323_capstone

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
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
                if (pokemon != null) {
                    customAdapter = CustomAdapter(pokemon.pkmn.toMutableList()) {
                        clickedItem -> showDetailsDialog(clickedItem)
                    }
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
        }
        )
    }

    fun showDetailsDialog(item: Results) {
        val customView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val imageView: ImageView = customView.findViewById(R.id.imageView)
        val checkBox : CheckBox = customView.findViewById(R.id.checkBox)
        val myBuilder = CronetEngine.Builder(this)
        val cronetEngine: CronetEngine = myBuilder.build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        val gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()
        val networkClient = NetworkClient(cronetEngine, executor)
        networkClient.get(item.url) { response ->
            runOnUiThread {
                val pkInfo = gson.fromJson(response, Info::class.java)
                var typing = ""
                var baseStats = ""
                val pkSprite = pkInfo.sprites.other.officialArtwork.front
                val shinyPkSprite = pkInfo.sprites.other.officialArtwork.shiny
                pkInfo.types.forEach { typeWrapper -> typing += typeWrapper.type.name + " " }
                pkInfo.stats.forEach { statWrapper -> baseStats += statWrapper.stat.name + ": " + statWrapper.baseStat.toString() + "\n" }
                imageView.load(pkSprite)
                val titleString = pkInfo.name2
                val capitalTitle = titleString.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                    .setView(customView)
                    .setMessage("Pokédex #${pkInfo.id}\nType: $typing\nBase Stats:\n$baseStats")
                    .setTitle(Html.fromHtml("<b>$capitalTitle</b>", Html.FROM_HTML_MODE_LEGACY))
                    .setPositiveButton("Close") { dialog, which -> dialog.dismiss() }
                val dialog: AlertDialog = builder.create()
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) { imageView.load(shinyPkSprite) }
                    else { imageView.load(pkSprite) }
                }
                dialog.show()
                dialog.window?.setLayout(700, 1400)
            }
        }
    }
}