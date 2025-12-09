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

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = CharacterRepoImpl(api, dao)
    }

    private val name = "Rick"

    private val location =
        CharactersResponse.Character.LocationInfo("Earth", "url/earth")

    private val apiResults = listOf(
        CharactersResponse.Character(
            1, "Rick Sanchez", "Alive", "Human", "", "Male",
            location, location, "url1", emptyList(), "", ""
        )
    )

    private val apiResponse = CharactersResponse(
        CharactersResponse.Info(1, 1), apiResults
    )

    private val entity = CharacterEntity(
        1, "Rick Sanchez", "Human", "Alive", "Earth", "url1"
    )

    private val data = CharacterData(
        1, "Rick Sanchez", "Human", "Alive", "Earth", "url1"
    )

    @Test
    fun getByName_apiSuccess() = runTest {
        coEvery { api.getCharactersByName(name) } returns apiResponse
        coEvery { dao.insertCharacters(any()) } returns Unit

        val result = repo.getCharactersByName(name)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
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
