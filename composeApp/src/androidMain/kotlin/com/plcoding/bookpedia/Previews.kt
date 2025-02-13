package com.plcoding.bookpedia

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.presentation.book_list.BookListScreen
import com.plcoding.bookpedia.book.presentation.book_list.BookListState
import com.plcoding.bookpedia.book.presentation.book_list.components.BookSearchBar


@Preview(showBackground = true)
@Composable
private fun BookSearchPreview() {
    MaterialTheme {
        BookSearchBar(
            searchQuery = "Kotlin",
            onSearchQueryChanged = {},
            onImeSearch = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val books = (1..10).map {
    Book(
        id = it.toString(),
        title = "Book $it",
        authors = listOf("Author $it"),
        description = "Description $it",
        averageRating = 1.34 + it,
        languages = emptyList(),
        imageUrl = "https://test.com",
        numEditions = 1,
        numPages = 2,
        firstPublishYear = "202$it"
    )
}

@Preview
@Composable
private fun BookListPreview() {
    BookListScreen(
        state = BookListState(
            searchResults = books
        ),
        onAction = {}
    )
}