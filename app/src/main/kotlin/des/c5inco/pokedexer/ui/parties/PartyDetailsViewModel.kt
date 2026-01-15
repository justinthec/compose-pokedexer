package des.c5inco.pokedexer.ui.parties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import des.c5inco.pokedexer.data.parties.PartiesRepository
import des.c5inco.pokedexer.data.pokemon.PokemonRepository
import des.c5inco.pokedexer.model.Party
import des.c5inco.pokedexer.model.Pokemon
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PartyDetailsUiState {
    object Loading : PartyDetailsUiState
    data class Ready(
        val party: Party,
        val pokemon: List<Pokemon>
    ) : PartyDetailsUiState
    object Error : PartyDetailsUiState
}

class PartyDetailsViewModel @AssistedInject constructor(
    private val partiesRepository: PartiesRepository,
    private val pokemonRepository: PokemonRepository,
    @Assisted private val partyId: Int
) : ViewModel() {
    val uiState: StateFlow<PartyDetailsUiState> = partiesRepository.getPartyWithMembersFlow(partyId)
        .flatMapLatest { pwm ->
            if (pwm == null) {
                flowOf(PartyDetailsUiState.Error)
            } else {
                val pokemonIds = pwm.members.sortedBy { it.position }.map { it.pokemonId }
                pokemonRepository.getPokemonByIds(pokemonIds).map { pokemonList ->
                    val sortedPokemon = pokemonIds.mapNotNull { id -> pokemonList.find { it.id == id } }
                    PartyDetailsUiState.Ready(pwm.party, sortedPokemon)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PartyDetailsUiState.Loading
        )

    fun deleteParty() {
        val state = uiState.value
        if (state is PartyDetailsUiState.Ready) {
            viewModelScope.launch {
                partiesRepository.deleteParty(state.party)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(partyId: Int): PartyDetailsViewModel
    }
}
