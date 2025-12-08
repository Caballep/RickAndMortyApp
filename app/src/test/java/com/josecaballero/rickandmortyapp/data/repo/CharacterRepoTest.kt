package com.josecaballero.rickandmortyapp.data.repo

import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity
import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import com.josecaballero.rickandmortyapp.data.source.remote.rest.response.CharactersResponse
import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepoImplTest {

    private val api: RickAndMortyAPI = mockk()
    private val characterDao: CharacterDao = mockk()

    private lateinit var characterRepoImpl: CharacterRepoImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        characterRepoImpl = CharacterRepoImpl(api, characterDao)
    }

    private val testName = "Rick"

    private val testLocation =
        CharactersResponse.Character.LocationInfo(name = "Earth", url = "url/earth")

    private val apiCharacterList = listOf(
        CharactersResponse.Character(
            id = 1, name = "Rick Sanchez", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = testLocation, location = testLocation,
            image = "url1", episode = emptyList(), url = "url/char1", created = ""
        ),
        CharactersResponse.Character(
            id = 2, name = "Morty Smith", status = "Alive", species = "Human", type = "",
            gender = "Male", origin = testLocation, location = testLocation,
            image = "url2", episode = emptyList(), url = "url/char2", created = ""
        )
    )

    private val apiResponse = CharactersResponse(
        info = CharactersResponse.Info(count = 2, pages = 1),
        results = apiCharacterList
    )

    private val apiEmptyResponse = CharactersResponse(
        info = CharactersResponse.Info(count = 0, pages = 0),
        results = emptyList()
    )

    private val entityList = listOf(
        CharacterEntity(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            status = "Alive",
            origin = "Earth",
            imageUrl = "url1"
        ),
        CharacterEntity(
            id = 2,
            name = "Morty Smith",
            species = "Human",
            status = "Alive",
            origin = "Earth",
            imageUrl = "url2"
        )
    )

    private val dataList = listOf(
        CharacterData(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            status = "Alive",
            origin = "Earth",
            imageUrl = "url1"
        ),
        CharacterData(
            id = 2,
            name = "Morty Smith",
            species = "Human",
            status = "Alive",
            origin = "Earth",
            imageUrl = "url2"
        )
    )

    @Test
    fun `getCharactersByName success should fetch remote, save local, and return data`() = runTest {
        coEvery { api.getCharactersByName(testName) } returns apiResponse
        coEvery { characterDao.insertCharacters(any()) } returns Unit
        coEvery { characterDao.getCharactersByName(testName) } returns entityList

        val result = characterRepoImpl.getCharactersByName(testName)

        coVerify(exactly = 1) { api.getCharactersByName(testName) }
        coVerify(exactly = 1) { characterDao.insertCharacters(any()) }
        coVerify(exactly = 1) { characterDao.getCharactersByName(testName) }

        assertTrue(result.isSuccess)
        assertEquals(dataList, result.getOrNull())
    }

    @Test
    fun `getCharactersByName success should return empty list when API returns no results`() = runTest {
        coEvery { api.getCharactersByName(testName) } returns apiEmptyResponse
        coEvery { characterDao.insertCharacters(emptyList()) } returns Unit
        coEvery { characterDao.getCharactersByName(testName) } returns emptyList()

        val result = characterRepoImpl.getCharactersByName(testName)

        coVerify(exactly = 1) { api.getCharactersByName(testName) }
        coVerify(exactly = 1) { characterDao.insertCharacters(emptyList()) }
        coVerify(exactly = 1) { characterDao.getCharactersByName(testName) }

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `getCharactersByName failure if database insertion fails should return failure result`() = runTest {
        val dbException = RuntimeException("DB Constraint Violation")

        coEvery { api.getCharactersByName(testName) } returns apiResponse
        coEvery { characterDao.insertCharacters(any()) } throws dbException
        coEvery { characterDao.getCharactersByName(any()) } returns emptyList()

        val result = characterRepoImpl.getCharactersByName(testName)

        coVerify(exactly = 1) { api.getCharactersByName(testName) }
        coVerify(exactly = 1) { characterDao.insertCharacters(any()) }
        coVerify(exactly = 0) { characterDao.getCharactersByName(any()) }

        assertTrue(result.isFailure)
        assertEquals(dbException.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getCharactersByName failure when API call fails should return failure result`() = runTest {
        val exception = IOException("Network error")
        coEvery { api.getCharactersByName(testName) } throws exception

        val result = characterRepoImpl.getCharactersByName(testName)

        coVerify(exactly = 1) { api.getCharactersByName(testName) }
        coVerify(exactly = 0) { characterDao.insertCharacters(any()) }
        coVerify(exactly = 0) { characterDao.getCharactersByName(any()) }

        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }
}