package edu.iu.luddy.c323_capstone

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        val sharedPreferences = getSharedPreferences("MyTeam", Context.MODE_PRIVATE)
        val gson = Gson()
        val currentTeamJson = sharedPreferences.getString("team_list", "[]")
        val type = object : TypeToken<MutableList<Results>>() {}.type
        val myTeam: MutableList<Results> = gson.fromJson(currentTeamJson, type)

        // hide the add button
        val adapter = CustomAdapter(myTeam, true)
        recyclerView.adapter = adapter
    }
}