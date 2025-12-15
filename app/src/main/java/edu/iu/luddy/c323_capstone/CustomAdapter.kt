package edu.iu.luddy.c323_capstone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class CustomAdapter(
    private val fullDataSet: MutableList<Results>,
    private val isTeamView: Boolean = false,
    private val onItemClicked: (Results) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(), Filterable {

    private var filteredDataSet: MutableList<Results> = fullDataSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemonResult = filteredDataSet[position]

        holder.titleView.text = pokemonResult.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        val pokeNumber = pokemonResult.url.replace(Regex("[^0-9]"), "")
        val rpokeN = pokeNumber.drop(1)
        val imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-viii/icons/$rpokeN.png"
        holder.sprite.load(imgUrl)

        if (!isTeamView) {
            holder.addButton.setImageResource(android.R.drawable.ic_menu_add)
            holder.addButton.setOnClickListener {
                val context = holder.itemView.context
                val sharedPreferences = context.getSharedPreferences("MyTeam", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val gson = Gson()

                val currentTeamJson = sharedPreferences.getString("team_list", "[]")
                val type = object : TypeToken<MutableList<Results>>() {}.type
                val currentTeam: MutableList<Results> = gson.fromJson(currentTeamJson, type)

                if (currentTeam.size >= 6) {
                    Toast.makeText(context, "Your team is full! (Max 6 Pokémon)", Toast.LENGTH_SHORT).show()
                } else if (!currentTeam.any { it.name == pokemonResult.name }) {
                    currentTeam.add(pokemonResult)
                    editor.putString("team_list", gson.toJson(currentTeam))
                    editor.apply()
                    Toast.makeText(context, "${holder.titleView.text} added to team!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "${holder.titleView.text} is already on your team!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.addButton.setImageResource(android.R.drawable.ic_menu_delete)
            holder.addButton.setOnClickListener {
                val context = holder.itemView.context
                val pokemonToRemove = filteredDataSet[holder.adapterPosition]

                val sharedPreferences = context.getSharedPreferences("MyTeam", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val gson = Gson()
                val currentTeamJson = sharedPreferences.getString("team_list", "[]")
                val type = object : TypeToken<MutableList<Results>>() {}.type
                val currentTeam: MutableList<Results> = gson.fromJson(currentTeamJson, type)
                currentTeam.removeIf { it.name == pokemonToRemove.name }
                editor.putString("team_list", gson.toJson(currentTeam))
                editor.apply()
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    filteredDataSet.removeAt(currentPosition)
                    fullDataSet.remove(pokemonToRemove)
                    notifyItemRemoved(currentPosition)
                }

                Toast.makeText(context, "${pokemonToRemove.name.replaceFirstChar { it.titlecase(Locale.ROOT) }} removed from team!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.lowercase(Locale.ROOT) ?: ""
                val results = FilterResults()
                results.values = if (charString.isEmpty()) {
                    fullDataSet
                } else {
                    fullDataSet.filter { it.name.lowercase(Locale.ROOT).contains(charString) }.toMutableList()
                }
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataSet = results?.values as? MutableList<Results> ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = itemView.findViewById(R.id.textView)
        val sprite: ImageView = itemView.findViewById(R.id.imageView2)
        val addButton: ImageButton = view.findViewById(R.id.btnAddButton)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(filteredDataSet[position])
                }
            }
        }
    }
}