package des.c5inco.pokedexer.ui.pokedex

import androidx.lifecycle.SavedStateHandle
import des.c5inco.pokedexer.data.pokemon.PokemonRepository
import des.c5inco.pokedexer.data.preferences.UserPreferences
import des.c5inco.pokedexer.data.preferences.UserPreferencesRepository
import des.c5inco.pokedexer.model.Generation
import des.c5inco.pokedexer.model.Pokemon
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexViewModelTest {
    private val pokemonRepository = mockk<PokemonRepository>()
    private val userPreferencesRepository = mockk<UserPreferencesRepository>()
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: PokedexViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { userPreferencesRepository.userPreferencesFlow } returns flowOf(UserPreferences(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model is initialized, state starts with loading`() = runTest(dispatcher) {
        // Given
        val pokemonList = listOf(
            Pokemon(
                id = 1,
                name = "Bulbasaur",
                description = "Bulbasaur description",
                typeOfPokemon = listOf("Grass", "Poison"),
                category = "Seed",
                image = 1,
                height = 0.7,
                weight = 6.9,
                hp = 45,
                attack = 49,
                defense = 49,
                specialAttack = 65,
                specialDefense = 65,
                speed = 45
            )
        )
        every { pokemonRepository.getPokemonByGeneration(Generation.I) } returns flowOf(pokemonList)

        // When
        viewModel = PokedexViewModel(
            pokemonRepository = pokemonRepository,
            userPreferencesRepository = userPreferencesRepository,
            savedStateHandle = savedStateHandle
        )

        // Then
        // Check initial value directly
        assertTrue("Initial state value should be Loading", viewModel.state.value is PokedexUiState.Loading)
    }

    @Test
    fun `when data is ready, state becomes Ready with pokemon list`() = runTest(dispatcher) {
        // Given
        val pokemonList = listOf(
            Pokemon(
                id = 1,
                name = "Bulbasaur",
                description = "Bulbasaur description",
                typeOfPokemon = listOf("Grass", "Poison"),
                category = "Seed",
                image = 1,
                height = 0.7,
                weight = 6.9,
                hp = 45,
                attack = 49,
                defense = 49,
                specialAttack = 65,
                specialDefense = 65,
                speed = 45
            )
        )
        every { pokemonRepository.getPokemonByGeneration(Generation.I) } returns flowOf(pokemonList)

        // When
        viewModel = PokedexViewModel(
            pokemonRepository = pokemonRepository,
            userPreferencesRepository = userPreferencesRepository,
            savedStateHandle = savedStateHandle
        )

        // Then
        val states = mutableListOf<PokedexUiState>()
        backgroundScope.launch {
            viewModel.state.collect { states.add(it) }
        }

        // Wait for Loading state and delay. Using advanceTimeBy to ensure delay is passed.
        // We know the delay is 500ms.
        advanceTimeBy(600)
        advanceUntilIdle()

        // Check states
        // Expected: Loading -> (delay) -> Ready
        // Or Loading(initial) -> Loading(from flow) -> Ready

        assertTrue("Should have at least 2 states", states.size >= 2)
        assertTrue("First state is Loading", states[0] is PokedexUiState.Loading)
        assertTrue("Last state is Ready", states.last() is PokedexUiState.Ready)

        val readyState = states.last() as PokedexUiState.Ready
        assertEquals(pokemonList, readyState.pokemon)
        assertEquals(emptySet<Int>(), readyState.favoriteIds)
    }
}
