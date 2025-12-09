package com.josecaballero.rickandmortyapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.josecaballero.rickandmortyapp.presentation.screen.characterDetail.CharacterDetailScreen
import com.josecaballero.rickandmortyapp.presentation.screen.characters.CharactersScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "characters"
    ) {
        composable(route = AppRoutes.CHARACTERS) {
            CharactersScreen(
                onCharacterId = { characterId ->
                    navController.navigate("characterDetail/$characterId")
                }
            )
        }

        composable(
            route = AppRoutes.CHARACTER_DETAIL,
            arguments = listOf(
                navArgument(CHARACTER_ID) {
                    type = NavType.IntType
                }
            )

        ) {
            CharacterDetailScreen()
        }
    }
}

object AppRoutes {
    const val CHARACTERS = "characters"
    const val CHARACTER_DETAIL = "characterDetail/{$CHARACTER_ID}"
}

const val CHARACTER_ID = "characterId"