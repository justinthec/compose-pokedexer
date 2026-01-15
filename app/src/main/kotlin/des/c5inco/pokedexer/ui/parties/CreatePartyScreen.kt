package des.c5inco.pokedexer.ui.parties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import des.c5inco.pokedexer.R
import des.c5inco.pokedexer.ui.common.PokemonImage
import des.c5inco.pokedexer.ui.pokedex.PokedexCard

@Composable
fun CreatePartyScreenRoute(
    viewModel: CreatePartyViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.resetIfNew()
    }

    CreatePartyScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        onSuccess = onSuccess
    )
}

@Composable
fun CreatePartyScreen(
    viewModel: CreatePartyViewModel,
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val selectedPokemon by viewModel.selectedPokemon.collectAsStateWithLifecycle()
    val pokemonList by viewModel.pokemonList.collectAsStateWithLifecycle()
    val name = viewModel.name

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.createPartyLabel)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backActionContentDescription),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveParty(onSuccess) },
                        enabled = name.isNotBlank() && selectedPokemon.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.savePartyLabel))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(R.string.partyNamePlaceholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            Text(
                text = "Selected (${selectedPokemon.size}/6)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until 6) {
                    val p = selectedPokemon.getOrNull(i)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clickable { p?.let { viewModel.removePokemon(it) } },
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 2.dp,
                        border = if (p == null) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (p != null) {
                                PokemonImage(
                                    image = p.image,
                                    description = p.name,
                                    modifier = Modifier.size(48.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(pokemonList, key = { it.id }) { pokemon ->
                    val isSelected = selectedPokemon.any { it.id == pokemon.id }
                    PokedexCard(
                        pokemon = pokemon,
                        onPokemonSelected = {
                            if (isSelected) viewModel.removePokemon(pokemon)
                            else viewModel.addPokemon(pokemon)
                        },
                        modifier = Modifier.alpha(if (isSelected) 0.5f else 1f)
                    )
                }
            }
        }
    }
}
