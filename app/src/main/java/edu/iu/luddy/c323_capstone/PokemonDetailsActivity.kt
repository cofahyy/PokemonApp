package edu.iu.luddy.c323_capstone

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// My Team

class PokemonDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}