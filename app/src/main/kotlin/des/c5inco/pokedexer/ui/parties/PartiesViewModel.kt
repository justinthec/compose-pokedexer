package des.c5inco.pokedexer.ui.parties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import des.c5inco.pokedexer.data.parties.PartiesRepository
import des.c5inco.pokedexer.data.pokemon.PokemonRepository
import des.c5inco.pokedexer.model.Party
import des.c5inco.pokedexer.model.Pokemon
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PartyWithPokemon(
    val party: Party,
    val pokemon: List<Pokemon>
)

sealed interface PartiesUiState {
    object Loading : PartiesUiState
    data class Ready(
        val parties: List<PartyWithPokemon>
    ) : PartiesUiState
}

class PartiesViewModel @Inject constructor(
    private val partiesRepository: PartiesRepository,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {
    val uiState: StateFlow<PartiesUiState> = combine(
        partiesRepository.getAllPartiesWithMembersFlow(),
        pokemonRepository.pokemon()
    ) { partiesWithMembers, allPokemon ->
        val pokemonMap = allPokemon.associateBy { it.id }
        val parties = partiesWithMembers.map { pwm ->
            PartyWithPokemon(
                party = pwm.party,
                pokemon = pwm.members.sortedBy { it.position }.mapNotNull { pokemonMap[it.pokemonId] }
            )
        }
        PartiesUiState.Ready(parties)
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PartiesUiState.Loading
    )

    fun deleteParty(party: Party) {
        viewModelScope.launch {
            partiesRepository.deleteParty(party)
        }
    }
}
