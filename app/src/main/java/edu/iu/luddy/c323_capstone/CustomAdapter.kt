package edu.iu.luddy.c323_capstone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class CustomAdapter(
    private val fullDataSet: MutableList<Results>,
    private val isTeamView: Boolean = false
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

        val pokemonId = pokemonResult.url.split("/").filter { it.isNotEmpty() }.last()
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.imageView)

        if (!isTeamView) {
            // --- ADD LOGIC ---
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
                    showCustomToast(context, "Your team is full! (Max 6 Pokémon)", imageUrl)
                } else if (!currentTeam.any { it.name == pokemonResult.name }) {
                    currentTeam.add(pokemonResult)
                    editor.putString("team_list", gson.toJson(currentTeam))
                    editor.apply()
                    showCustomToast(context, "${holder.titleView.text} added to team!", imageUrl)
                } else {
                    showCustomToast(context, "${holder.titleView.text} is already on your team!", imageUrl)
                }
            }
        } else {
            // --- CORRECTED REMOVE LOGIC ---
            holder.addButton.setImageResource(android.R.drawable.ic_menu_delete)
            holder.addButton.setOnClickListener {
                val context = holder.itemView.context
                // Get the Pokémon to be removed before modifying any lists
                val pokemonToRemove = filteredDataSet[holder.adapterPosition]

                // 1. Update SharedPreferences
                val sharedPreferences = context.getSharedPreferences("MyTeam", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val gson = Gson()
                val currentTeamJson = sharedPreferences.getString("team_list", "[]")
                val type = object : TypeToken<MutableList<Results>>() {}.type
                val currentTeam: MutableList<Results> = gson.fromJson(currentTeamJson, type)
                currentTeam.removeIf { it.name == pokemonToRemove.name }
                editor.putString("team_list", gson.toJson(currentTeam))
                editor.apply()

                // 2. Remove the item from the adapter's lists
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    filteredDataSet.removeAt(currentPosition)
                    fullDataSet.remove(pokemonToRemove)
                    notifyItemRemoved(currentPosition)
                }

                showCustomToast(context, "${pokemonToRemove.name.replaceFirstChar { it.titlecase(Locale.ROOT) }} removed from team!", imageUrl)
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

    private fun showCustomToast(context: Context, message: String, imageUrl: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)
        val imageView = layout.findViewById<ImageView>(R.id.ivToastImage)
        val textView = layout.findViewById<TextView>(R.id.tvToastMessage)
        textView.text = message
        Glide.with(context).load(imageUrl).into(imageView)
        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.textView)
        val imageView: ImageView = view.findViewById(R.id.ivPokemonImage)
        val addButton: ImageButton = view.findViewById(R.id.btnAddButton)
    }
}