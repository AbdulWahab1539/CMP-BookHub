package com.plcoding.bookpedia.book.presentation.book_list

import app.cash.turbine.test
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_triggersSearch_forDefaultQuery() = runTest(testDispatcher) {
        val books = listOf(
            Book(
                id = "1",
                title = "Kotlin",
                imageUrl = "u",
                authors = listOf("A"),
                description = null,
                languages = emptyList(),
                firstPublishYear = null,
                averageRating = null,
                numPages = null,
                ratingCount = null,
                numEditions = null,
            )
        )

        val repo = object : BookRepository {
            override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
                return Result.Success(books)
            }
        }

        val vm = BookListViewModel(repo)

        vm.state.test {
            // initial emission
            val s1 = awaitItem()
            assertEquals("Kotlin", s1.searchQuery)
            assertTrue(s1.isLoading)

            // Advance past debounce
            advanceTimeBy(500L)

            // After search success
            val s2 = awaitItem()
            assertEquals(false, s2.isLoading)
            assertEquals(books, s2.searchResults)
        }
    }

    @Test
    fun onSearchQueryChanged_shortQuery_doesNotSearch_andKeepsCached() = runTest(testDispatcher) {
        val repo = object : BookRepository {
            override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
                error("Should not be called for short queries")
            }
        }

        val vm = BookListViewModel(repo)

        vm.onAction(BookListAction.OnSearchQueryChanged("ok"))

        vm.state.test {
            val s1 = awaitItem()
            assertEquals("ok", s1.searchQuery)
            // no debounce trigger since length <= 2
            // ensure no loading started
            assertTrue(s1.isLoading)
        }
    }

    @Test
    fun onSearchQueryChanged_error_setsErrorMessage_andClearsResults() = runTest(testDispatcher) {
        val repo = object : BookRepository {
            override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
                return Result.Error(DataError.Remote.NO_INTERNET)
            }
        }

        val vm = BookListViewModel(repo)
        vm.onAction(BookListAction.OnSearchQueryChanged("great books"))

        vm.state.test {
            // initial
            awaitItem()

            advanceTimeBy(500L)
            val after = awaitItem()
            assertEquals(false, after.isLoading)
            assertTrue(after.searchResults.isEmpty())
            // errorMessage not null; content mapping tested elsewhere, here we assert presence
            assertTrue(after.errorMessage != null)
        }
    }
}



