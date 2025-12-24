package com.josecaballero.rickandmortyapp.presentation

import androidx.lifecycle.SavedStateHandle
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.model.UCResult
import com.josecaballero.rickandmortyapp.domain.usecase.getCharacter.GetCharacterUC
import com.josecaballero.rickandmortyapp.domain.util.DomainFailure
import com.josecaballero.rickandmortyapp.presentation.navigation.CHARACTER_ID
import com.josecaballero.rickandmortyapp.presentation.screen.characterDetail.CharacterDetailScreenState.CharacterDetailStatus
import com.josecaballero.rickandmortyapp.presentation.screen.characterDetail.CharacterDetailScreenVM
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class CharacterDetailScreenVMTest {

    private val getCharacterDetailsUC: GetCharacterUC = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    private val MOCK_CHARACTER_ID = 42

    private val mockCharacterModel = CharacterModel(
        id = MOCK_CHARACTER_ID,
        name = "Morty",
        status = "Alive",
        species = "Human",
        origin = "Earth",
        imageUrl = "morty_url",
        type = "",
        created = "2017-11-04T18:50:21.651Z"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(CHARACTER_ID to MOCK_CHARACTER_ID))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        coEvery { getCharacterDetailsUC(MOCK_CHARACTER_ID) } returns UCResult.Success(mockCharacterModel)

        val viewModel = CharacterDetailScreenVM(savedStateHandle, getCharacterDetailsUC)

        assertEquals(CharacterDetailStatus.Loading, viewModel.uiState.value.status)
    }

    @Test
    fun `fetchCharacterDetails success should update state to Success`() = runTest {
        coEvery { getCharacterDetailsUC(MOCK_CHARACTER_ID) } returns UCResult.Success(mockCharacterModel)
        val expectedCharacterName = mockCharacterModel.copy().name

        val viewModel = CharacterDetailScreenVM(savedStateHandle, getCharacterDetailsUC)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is CharacterDetailStatus.Success)

        val successStatus = state.status as CharacterDetailStatus.Success


        assertEquals(expectedCharacterName, successStatus.character.name)
        assertEquals("", state.displayMessage)
    }

    @Test
    fun `fetchCharacterDetails failure should update state to Error`() = runTest {
        coEvery { getCharacterDetailsUC(MOCK_CHARACTER_ID) } returns UCResult.Failure(DomainFailure.Error)

        val viewModel = CharacterDetailScreenVM(savedStateHandle, getCharacterDetailsUC)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is CharacterDetailStatus.Error)

        val errorStatus = state.status as CharacterDetailStatus.Error
        val expectedMessage = "Failed to load character details."

        assertEquals(expectedMessage, errorStatus.message)
        assertEquals(expectedMessage, state.displayMessage)
    }
}