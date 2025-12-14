package edu.iu.luddy.c323_capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class CustomAdapter (private val dataSet : List<Results>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.titleView.text = dataSet[position].name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        val pokeNumber = dataSet[position].url.replace(Regex("[^0-9]"), "")
        val rpokeN = pokeNumber.drop(1)
        val imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-vii/icons/$rpokeN.png"
        holder.sprite.load(imgUrl)

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = itemView.findViewById(R.id.textView)
        val sprite: ImageView = itemView.findViewById(R.id.imageView2)
    }

}