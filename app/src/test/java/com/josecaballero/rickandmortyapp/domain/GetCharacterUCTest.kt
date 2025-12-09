package com.josecaballero.rickandmortyapp.domain

import com.josecaballero.rickandmortyapp.domain.usecase.getCharacter.GetCharacterUC
import com.josecaballero.rickandmortyapp.data.repo.CharacterRepo
import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import com.josecaballero.rickandmortyapp.domain.model.CharacterModel
import com.josecaballero.rickandmortyapp.domain.usecase.getCharacter.GetCharacterResult
import com.josecaballero.rickandmortyapp.domain.util.FailureType
import com.josecaballero.rickandmortyapp.domain.util.ThrowableMapper
import com.josecaballero.rickandmortyapp.domain.util.TimeFormatter
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

@ExperimentalCoroutinesApi
class GetCharacterUCTest {

    private val repository: CharacterRepo = mockk()
    private lateinit var getCharacterUC: GetCharacterUC
    private val testDispatcher = StandardTestDispatcher()

    private val MOCK_CHARACTER_ID = 1
    private val MOCK_ISO_DATE = "2023-01-01T00:00:00.000Z"
    private val MOCK_FORMATTED_DATE = "01/01/2023"

    private val mockCharacterData = CharacterData(
        id = MOCK_CHARACTER_ID,
        name = "Rick",
        status = "Alive",
        species = "Human",
        origin = "Earth",
        imageUrl = "url",
        type = "Scientist",
        created = MOCK_ISO_DATE
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(TimeFormatter.Companion)
        coEvery { TimeFormatter.formatToStandardDate(MOCK_ISO_DATE) } returns MOCK_FORMATTED_DATE

        getCharacterUC = GetCharacterUC(repository)
    }

    @After
    fun tearDown() {
        unmockkObject(TimeFormatter.Companion)
    }


    @Test
    fun `invoke should return Success when repository returns valid data`() = runTest {
        coEvery { repository.getCharactersById(MOCK_CHARACTER_ID) } returns Result.success(mockCharacterData)

        val result = getCharacterUC(MOCK_CHARACTER_ID)

        assertTrue(result is GetCharacterResult.Success)
        val successResult = result as GetCharacterResult.Success

        assertEquals(MOCK_CHARACTER_ID, successResult.characterModel.id)
        assertEquals(MOCK_FORMATTED_DATE, successResult.characterModel.created)
        assertEquals("Scientist", successResult.characterModel.type)
    }

    @Test
    fun `invoke should return Failure NotFound when repository returns null data`() = runTest {
        coEvery { repository.getCharactersById(MOCK_CHARACTER_ID) } returns Result.success(null)

        val result = getCharacterUC(MOCK_CHARACTER_ID)

        assertTrue(result is GetCharacterResult.Failure)
        assertEquals(FailureType.NotFound, (result as GetCharacterResult.Failure).failureType)
    }

    @Test
    fun `invoke should return Failure when repository returns Throwable (e g IOException)`() = runTest {
        val exception = IOException("Network error")
        val expectedFailureType = FailureType.NetworkError

        coEvery { repository.getCharactersById(MOCK_CHARACTER_ID) } returns Result.failure(exception)

        mockkObject(ThrowableMapper)
        coEvery { ThrowableMapper.getFailureType(exception) } returns expectedFailureType

        val result = getCharacterUC(MOCK_CHARACTER_ID)

        assertTrue(result is GetCharacterResult.Failure)
        assertEquals(expectedFailureType, (result as GetCharacterResult.Failure).failureType)

        unmockkObject(ThrowableMapper)
    }
}