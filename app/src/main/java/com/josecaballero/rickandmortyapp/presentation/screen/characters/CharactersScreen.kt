package com.josecaballero.rickandmortyapp.presentation.screen.characters

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.josecaballero.rickandmortyapp.presentation.shared.MessageDialog

@Composable
fun CharactersScreen(
    viewModel: CharactersScreenVM = hiltViewModel(),
    onCharacterId: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var showMessageDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            dialogMessage = message
            showMessageDialog = true
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

            Text(
                text = state.displayMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )

            when (val currentStatus = state.status) {
                CharactersScreenState.CharactersStatus.Initial -> {
                    Text(
                        text = "Start searching by typing a character's name above.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                CharactersScreenState.CharactersStatus.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                CharactersScreenState.CharactersStatus.Empty -> {
                    Text(
                        text = "No characters were found.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                is CharactersScreenState.CharactersStatus.Success -> {
                    CharacterGrid(
                        characters = currentStatus.characters,
                        onCharacterClick = onCharacterId
                    )
                }
            }
        }
    }

    if (showMessageDialog) {
        MessageDialog(
            message = dialogMessage,
            onDismissRequest = {
                showMessageDialog = false
            }
        )
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
fun CharacterGrid(
    characters: List<CharactersScreenState.CharactersStatus.Character>,
    onCharacterClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(characters, key = { it.id }) { character ->
            CharacterCard(
                character = character,
                onCharacterClick = {
                    val navData = character.id
                    onCharacterClick(navData)
                }
            )
        }
    }
}

@Composable
fun CharacterCard(
    character: CharactersScreenState.CharactersStatus.Character,
    onCharacterClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.3f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onCharacterClick
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
        }
    }
}