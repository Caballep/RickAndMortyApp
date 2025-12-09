package com.josecaballero.rickandmortyapp.presentation.screen.characterDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun CharacterDetailScreen(
    characterDetailScreenVM: CharacterDetailScreenVM = hiltViewModel()
) {
    val uiState by characterDetailScreenVM.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val currentStatus = uiState.status) {

                CharacterDetailScreenState.CharacterDetailStatus.Loading -> {
                    CircularProgressIndicator()
                }

                is CharacterDetailScreenState.CharacterDetailStatus.Error -> {
                    Text(
                        text = currentStatus.message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is CharacterDetailScreenState.CharacterDetailStatus.Success -> {
                    CharacterDetailContent(character = currentStatus.character)
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailContent(character: CharacterDetailScreenState.DetailCharacter) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = character.imageUrl,
            contentDescription = "${character.name} detail image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status: ${character.status}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Species: ${character.species}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Origin: ${character.origin}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}