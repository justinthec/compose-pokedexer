package des.c5inco.pokedexer.ui.parties

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import des.c5inco.pokedexer.data.parties.PartiesRepository
import des.c5inco.pokedexer.data.pokemon.PokemonRepository
import des.c5inco.pokedexer.model.Pokemon
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreatePartyViewModel @AssistedInject constructor(
    private val partiesRepository: PartiesRepository,
    private val pokemonRepository: PokemonRepository,
    @Assisted private val partyId: Int?
) : ViewModel() {
    var name by mutableStateOf("")
        private set

    var searchText by mutableStateOf("")
        private set

    private val _selectedPokemon = MutableStateFlow<List<Pokemon>>(emptyList())
    val selectedPokemon: StateFlow<List<Pokemon>> = _selectedPokemon.asStateFlow()

    private val _searchTextFlow = MutableStateFlow("")

    val pokemonList: StateFlow<List<Pokemon>> = combine(
        pokemonRepository.pokemon(),
        _searchTextFlow
    ) { pokemon, query ->
        if (query.isBlank()) {
            pokemon
        } else {
            pokemon.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (partyId != null) {
            // Load existing party for editing if needed
        }
    }

    fun resetIfNew() {
        if (partyId == null) {
            name = ""
            searchText = ""
            _searchTextFlow.value = ""
            _selectedPokemon.value = emptyList()
        }
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun updateSearchText(newText: String) {
        searchText = newText
        _searchTextFlow.value = newText
    }

    fun addPokemon(pokemon: Pokemon) {
        if (_selectedPokemon.value.size < 6 && !_selectedPokemon.value.any { it.id == pokemon.id }) {
            _selectedPokemon.value = _selectedPokemon.value + pokemon
        }
    }

    fun removePokemon(pokemon: Pokemon) {
        _selectedPokemon.value = _selectedPokemon.value.filterNot { it.id == pokemon.id }
    }

    fun saveParty(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.isNotBlank() && _selectedPokemon.value.isNotEmpty()) {
                partiesRepository.createParty(name, _selectedPokemon.value.map { it.id })
                onSuccess()
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(partyId: Int?): CreatePartyViewModel
    }
}
