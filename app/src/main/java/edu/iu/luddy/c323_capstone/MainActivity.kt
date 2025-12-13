package edu.iu.luddy.c323_capstone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPokemonList = findViewById<Button>(R.id.btnPokemonList)
        val btnMyTeam = findViewById<Button>(R.id.btnMyTeam)
        val btnAbout = findViewById<Button>(R.id.btnAbout)

        btnPokemonList.setOnClickListener {
            startActivity(Intent(this, PokemonListActivity::class.java))
        }

        btnMyTeam.setOnClickListener {
            startActivity(Intent(this, PokemonDetailsActivity::class.java))
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}