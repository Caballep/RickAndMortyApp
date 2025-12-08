package com.josecaballero.characters.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.josecaballero.rickandmortyapp.presentation.screen.CharacterState
import com.josecaballero.rickandmortyapp.presentation.screen.CharacterVM

@Composable
fun CharactersScreen(
    viewModel: CharacterVM = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            println("UI EVENT TRIGGERED: $message")
        }
    }

    Scaffold(
        topBar = {
            SearchInput(
                searchTerm = state.searchTerm,
                onSearchTermChange = { newTerm ->
                    viewModel.onSearchClicked(newTerm)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                state.characters.isEmpty() && !state.isLoading -> {
                    Text(text = "No characters found for '${state.searchTerm}'")
                }

                else -> {
                    CharacterGrid(characters = state.characters)
                }
            }
        }
    }
}

@Composable
fun SearchInput(
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchTerm,
        onValueChange = onSearchTermChange,
        label = { Text("Search Character Name") },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun CharacterGrid(characters: List<CharacterState.Character>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(characters, key = { it.id }) { character ->
            CharacterCard(character = character)
        }
    }
}

@Composable
fun CharacterCard(character: CharacterState.Character) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.3f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.2f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = "${character.name} avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = character.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = "Status: ${character.status}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Origin: ${character.origin}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}