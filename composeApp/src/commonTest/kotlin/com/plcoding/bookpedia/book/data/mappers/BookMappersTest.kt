package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.dto.SearchedBookDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookMappersTest {

    @Test
    fun toBook_usesCoverKey_whenAvailable() {
        val dto = SearchedBookDto(
            id = "/works/OL123W",
            title = "Effective Kotlin",
            languages = listOf("eng"),
            coverAlternativeKey = 999,
            authorKeys = listOf("A1"),
            authorNames = listOf("Marcin Moskala"),
            coverKey = "OLCOVER",
            firstPublishYear = 2019,
            ratingsAverage = 4.5,
            ratingsCount = 1200,
            numPagesMedian = 320,
            numEditions = 3,
        )

        val book = dto.toBook()

        assertEquals("OL123W", book.id)
        assertEquals("Effective Kotlin", book.title)
        assertEquals("https://covers.openlibrary.org/b/olid/OLCOVER-L.jpg", book.imageUrl)
        assertEquals(listOf("Marcin Moskala"), book.authors)
        assertEquals(listOf("eng"), book.languages)
        assertEquals("2019", book.firstPublishYear)
        assertEquals(4.5, book.averageRating)
        assertEquals(320, book.numPages)
        assertEquals(1200, book.ratingCount)
        assertEquals(3, book.numEditions)
    }

    @Test
    fun toBook_usesAlternativeCover_whenCoverKeyNull() {
        val dto = SearchedBookDto(
            id = "/works/OL999W",
            title = "Kotlin in Action",
            languages = null,
            coverAlternativeKey = 12345,
            authorKeys = null,
            authorNames = null,
            coverKey = null,
            firstPublishYear = null,
            ratingsAverage = null,
            ratingsCount = null,
            numPagesMedian = null,
            numEditions = null,
        )

        val book = dto.toBook()

        assertEquals("OL999W", book.id)
        assertEquals("Kotlin in Action", book.title)
        assertEquals("https://covers.openlibrary.org/b/id/12345-L.jpg", book.imageUrl)
        assertTrue(book.authors.isEmpty())
        assertTrue(book.languages.isEmpty())
        assertEquals("null", book.firstPublishYear)
        assertEquals(null, book.averageRating)
        assertEquals(null, book.numPages)
        assertEquals(null, book.ratingCount)
        assertEquals(0, book.numEditions)
    }
}



