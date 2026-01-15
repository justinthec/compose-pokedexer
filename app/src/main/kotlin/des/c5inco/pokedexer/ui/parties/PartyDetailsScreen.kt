package des.c5inco.pokedexer.ui.parties

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import des.c5inco.pokedexer.R
import des.c5inco.pokedexer.model.Pokemon
import des.c5inco.pokedexer.ui.common.LoadingIndicator
import des.c5inco.pokedexer.ui.common.PokemonImage

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
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent
                ),
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
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = "file:///android_asset/backgrounds/background.webp")
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize()
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
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        uiState.pokemon.forEachIndexed { index, pokemon ->
                            PokemonImage(
                                image = pokemon.id,
                                modifier = Modifier
                                    .size(120.dp)
                                    .offset(x = (-48 * index).dp),
                                onClick = { onPokemonSelected(pokemon) }
                            )
                        }
                    }
                }
            }
        }
    }
}
