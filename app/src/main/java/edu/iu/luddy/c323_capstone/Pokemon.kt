package edu.iu.luddy.c323_capstone

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("results") val pkmn : List<Results>
)

data class Results (
    @SerializedName("name") val name : String,
    @SerializedName("url") val url : String
)

data class Info (
    @SerializedName("sprites") val sprite : Sprites
)

data class Sprites (
    @SerializedName("Versions") val versions : Versions
)

data class Versions (
    @SerializedName("generation-vii") val sprites : String
)
