package edu.iu.luddy.c323_capstone

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.google.gson.reflect.TypeToken
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PokemonDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rvMyTeam)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // display team
        val sharedPreferences = getSharedPreferences("MyTeam", MODE_PRIVATE)
        val gson = Gson()
        val currentTeamJson = sharedPreferences.getString("team_list", "[]")
        val type = object : TypeToken<MutableList<Results>>() {}.type
        val myTeam: MutableList<Results> = gson.fromJson(currentTeamJson, type)

        // hide the add button
        val adapter = CustomAdapter(myTeam, true) {
            clickedItem -> showDetailsDialog(clickedItem)
        }
        recyclerView.adapter = adapter
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