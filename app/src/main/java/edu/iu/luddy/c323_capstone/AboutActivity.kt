package edu.iu.luddy.c323_capstone

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val spnPokemonFacts = findViewById<Spinner>(R.id.spnPokemonFacts)
        val pokemonFacts = resources.getStringArray(R.array.pokemon_facts)

        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, pokemonFacts)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spnPokemonFacts.adapter = adapter

        // randomize
        if (pokemonFacts.isNotEmpty()) {
            val randomIndex = (pokemonFacts.indices).random()
            spnPokemonFacts.setSelection(randomIndex)
        }
    }
}