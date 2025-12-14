package edu.iu.luddy.c323_capstone

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("results") val pkmn : List<Results>
)

data class Results (
    @SerializedName("name") val name : String
)
