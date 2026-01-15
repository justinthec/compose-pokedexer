package des.c5inco.pokedexer.ui.parties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import des.c5inco.pokedexer.R
import des.c5inco.pokedexer.model.Pokemon
import des.c5inco.pokedexer.ui.common.LoadingIndicator
import des.c5inco.pokedexer.ui.common.Pokeball
import des.c5inco.pokedexer.ui.common.PokemonImage
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
                    PartyDetailsScene(
                        pokemon = uiState.pokemon,
                        onPokemonSelected = onPokemonSelected
                    )
                }
            }
        }
    }
}

@Composable
fun SceneBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = Color(0xFF87CEEB)) // Sky blue

        drawOval(
            color = Color.White,
            topLeft = Offset(x = size.width * 0.1f, y = size.height * 0.2f),
            size = Size(width = size.width * 0.4f, height = size.height * 0.2f)
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(x = size.width * 0.5f, y = size.height * 0.3f),
            size = Size(width = size.width * 0.5f, height = size.height * 0.25f)
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(x = size.width * 0.8f, y = size.height * 0.15f),
            size = Size(width = size.width * 0.45f, height = size.height * 0.22f)
        )
    }
}

@Composable
fun PartyDetailsScene(
    modifier: Modifier = Modifier,
    pokemon: List<Pokemon>,
    onPokemonSelected: (Pokemon) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        SceneBackground()
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 128.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                pokemon.forEach {
                    PokemonImage(
                        image = it.image,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onPokemonSelected(it) }
                    )
                }
            }
        }
    }
}
