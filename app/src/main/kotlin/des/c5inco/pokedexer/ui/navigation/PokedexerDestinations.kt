package des.c5inco.pokedexer.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Pokedex : Screen

    @Serializable
    data class PokemonDetails(val id: Int) : Screen

    @Serializable
    data object Moves : Screen

    @Serializable
    data object Items : Screen

    @Serializable
    data object TypeCharts : Screen

    @Serializable
    data object Parties : Screen

    @Serializable
    data class PartyDetails(val id: Int) : Screen

    @Serializable
    data class CreateParty(val id: Int? = null) : Screen
}
