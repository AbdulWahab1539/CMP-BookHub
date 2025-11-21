package com.plcoding.bookpedia.book.data.repository

import com.plcoding.bookpedia.book.data.dto.SearchedBookDto
import com.plcoding.bookpedia.book.data.dto.SearchResponseDto
import com.plcoding.bookpedia.book.data.network.RemoteBookDataSource
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class DefaultBookRepositoryTest {

    @Test
    fun searchBooks_mapsDtosToDomain_onSuccess() = runTest {
        val remote = object : RemoteBookDataSource {
            override suspend fun searchBooks(query: String, resultLimit: Int?): Result<SearchResponseDto, DataError.Remote> {
                return Result.Success(
                    SearchResponseDto(
                        results = listOf(
                            SearchedBookDto(
                                id = "/works/OL1W",
                                title = "Title 1",
                                languages = listOf("eng"),
                                coverAlternativeKey = 1,
                                authorKeys = null,
                                authorNames = listOf("Author 1"),
                                coverKey = null,
                                firstPublishYear = 2000,
                                ratingsAverage = 3.5,
                                ratingsCount = 10,
                                numPagesMedian = 111,
                                numEditions = 2,
                            ),
                            SearchedBookDto(
                                id = "/works/OL2W",
                                title = "Title 2",
                                languages = null,
                                coverAlternativeKey = 2,
                                authorKeys = null,
                                authorNames = null,
                                coverKey = "COVER2",
                                firstPublishYear = null,
                                ratingsAverage = null,
                                ratingsCount = null,
                                numPagesMedian = null,
                                numEditions = null,
                            )
                        )
                    )
                )
            }
        }

        val repo = DefaultBookRepository(remote)
        val result = repo.searchBooks("kotlin")

        when (result) {
            is Result.Success -> {
                val list = result.data
                assertEquals(2, list.size)
                assertEquals("OL1W", list[0].id)
                assertEquals("Title 1", list[0].title)
                assertTrue(list[0].imageUrl.contains("/b/id/1-"))
                assertEquals(listOf("Author 1"), list[0].authors)

                assertEquals("OL2W", list[1].id)
                assertEquals("Title 2", list[1].title)
                assertTrue(list[1].imageUrl.contains("/b/olid/COVER2-"))
            }
            is Result.Error -> error("Expected success, got error: ${result.error}")
        }
    }

    @Test
    fun searchBooks_propagatesError() = runTest {
        val remote = object : RemoteBookDataSource {
            override suspend fun searchBooks(query: String, resultLimit: Int?): Result<SearchResponseDto, DataError.Remote> {
                return Result.Error(DataError.Remote.NO_INTERNET)
            }
        }

        val repo = DefaultBookRepository(remote)
        val result = repo.searchBooks("kotlin")

        when (result) {
            is Result.Error -> assertEquals(DataError.Remote.NO_INTERNET, result.error)
            is Result.Success -> error("Expected error, got success: ${result.data}")
        }
    }
}



