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
    @SerializedName("name") val name2 : String,
    @SerializedName("id") val id : String,
    @SerializedName("types") val types : List<PokemonTypeWrapper>,
    @SerializedName("sprites") val sprites : Sprites,
    @SerializedName("stats") val stats : List<StatsWrapper>
)

data class StatsWrapper (
    @SerializedName("base_stat") val baseStat : Int,
    @SerializedName("stat") val stat : Stat
)

data class Stat (
    @SerializedName("name") val name : String
)

data class Sprites (
    @SerializedName("other") val other : Other
)

data class Other (
    @SerializedName("official-artwork") val officialArtwork : Artworks
)

data class Artworks (
    @SerializedName("front_default") val front : String?,
    @SerializedName("front_shiny") val shiny : String?
)

data class PokemonTypeWrapper (
    @SerializedName("type") val type : PokemonType
)

data class PokemonType (
    @SerializedName("name") val name : String
)

