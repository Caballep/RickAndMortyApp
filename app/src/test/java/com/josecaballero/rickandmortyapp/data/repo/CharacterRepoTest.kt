package com.josecaballero.rickandmortyapp.data.repo

import com.josecaballero.rickandmortyapp.data.source.local.sql.dao.CharacterDao
import com.josecaballero.rickandmortyapp.data.source.local.sql.entity.CharacterEntity
import com.josecaballero.rickandmortyapp.data.source.remote.rest.RickAndMortyAPI
import com.josecaballero.rickandmortyapp.data.source.remote.rest.response.CharactersResponse
import com.josecaballero.rickandmortyapp.data.repo.data.CharacterData
import io.mockk.coEvery
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
    private val dao: CharacterDao = mockk()
    private lateinit var repo: CharacterRepoImpl

    private val dispatcher = StandardTestDispatcher()

    private val name = "Rick"
    private val MOCK_TYPE = "Scientist"
    private val MOCK_CREATED = "2023-10-27T10:00:00.000Z"

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = CharacterRepoImpl(api, dao)
    }

    private val location =
        CharactersResponse.Character.LocationInfo("Earth", "url/earth")

    private val apiResults = listOf(
        CharactersResponse.Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = MOCK_TYPE,
            gender = "Male",
            origin = location,
            location = location,
            image = "url1",
            episode = emptyList(),
            url = "",
            created = MOCK_CREATED
        )
    )

    private val apiResponse = CharactersResponse(
        CharactersResponse.Info(1, 1), apiResults
    )

    private val entity = CharacterEntity(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        origin = "Earth",
        imageUrl = "url1",
        type = MOCK_TYPE,
        created = MOCK_CREATED
    )

    private val data = CharacterData(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        origin = "Earth",
        imageUrl = "url1",
        type = MOCK_TYPE,
        created = MOCK_CREATED
    )

    @Test
    fun getByName_apiSuccess() = runTest {
        coEvery { api.getCharactersByName(name) } returns apiResponse
        coEvery { dao.insertCharacters(any()) } returns Unit

        val result = repo.getCharactersByName(name)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals(data, result.getOrNull()!!.first())
    }

    @Test
    fun getByName_apiFails_localSuccess() = runTest {
        coEvery { api.getCharactersByName(name) } throws IOException()
        coEvery { dao.getCharactersByName(name) } returns listOf(entity)

        val result = repo.getCharactersByName(name)

        assertTrue(result.isSuccess)
        assertEquals(listOf(data), result.getOrNull())
    }

    @Test
    fun getByName_apiFails_localEmpty() = runTest {
        coEvery { api.getCharactersByName(name) } throws IOException("x")
        coEvery { dao.getCharactersByName(name) } returns emptyList()

        val result = repo.getCharactersByName(name)

        assertTrue(result.isFailure)
    }

    @Test
    fun getById_success() = runTest {
        coEvery { dao.getCharacterById(1) } returns entity

        val result = repo.getCharactersById(1)

        assertTrue(result.isSuccess)
        assertEquals(data, result.getOrNull())
    }

    @Test
    fun getById_notFound() = runTest {
        coEvery { dao.getCharacterById(1) } returns null

        val result = repo.getCharactersById(1)

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun getById_failure() = runTest {
        coEvery { dao.getCharacterById(1) } throws RuntimeException("x")

        val result = repo.getCharactersById(1)

        assertTrue(result.isFailure)
    }
}