package des.c5inco.pokedexer.ui.parties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import des.c5inco.pokedexer.R
import des.c5inco.pokedexer.model.Pokemon
import des.c5inco.pokedexer.ui.common.LoadingIndicator
import des.c5inco.pokedexer.ui.common.Pokeball
import des.c5inco.pokedexer.ui.pokedex.PokedexCard

@Composable
fun PartyDetailsScreenRoute(
    viewModel: PartyDetailsViewModel,
    onPokemonSelected: (Pokemon) -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PartyDetailsScreen(
        uiState = uiState,
        onPokemonSelected = onPokemonSelected,
        onBackClick = onBackClick,
        onDeleteParty = {
            viewModel.deleteParty()
            onBackClick()
        }
    )
}

@Composable
fun PartyDetailsScreen(
    uiState: PartyDetailsUiState,
    onPokemonSelected: (Pokemon) -> Unit = {},
    onBackClick: () -> Unit = {},
    onDeleteParty: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        when (uiState) {
                            is PartyDetailsUiState.Ready -> uiState.party.name
                            else -> stringResource(R.string.partiesLabel)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backActionContentDescription),
                        )
                    }
                },
                actions = {
                    if (uiState is PartyDetailsUiState.Ready) {
                        IconButton(onClick = onDeleteParty) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.deleteActionContentDescription)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Pokeball(
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 90.dp, y = (-72).dp),
            )

            when (uiState) {
                is PartyDetailsUiState.Loading -> {
                    LoadingIndicator()
                }
                is PartyDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Party not found")
                    }
                }
                is PartyDetailsUiState.Ready -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val count = uiState.pokemon.size
                        val fanAngle = 15f
                        val radius = with(LocalDensity.current) { 120.dp.toPx() }

                        uiState.pokemon.forEachIndexed { index, pokemon ->
                            val rotation = if (count > 1) (index.toFloat() / (count - 1) * fanAngle * 2) - fanAngle else 0f
                            val translationX = if (count > 1) (index.toFloat() / (count - 1) * radius * 2) - radius else 0f

                            PokedexCard(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .scale(0.85f)
                                    .graphicsLayer {
                                        this.rotationZ = rotation
                                        this.translationX = translationX
                                    }
                                    .zIndex(index.toFloat()),
                                pokemon = pokemon,
                                onPokemonSelected = onPokemonSelected
                            )
                        }
                    }
                }
            }
        }
    }
}
