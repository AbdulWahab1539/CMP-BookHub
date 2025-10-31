package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.dto.SearchedBookDto
import com.plcoding.bookpedia.book.domain.Book


fun SearchedBookDto.toBook(): Book {
    return Book(
        id = id.substringAfterLast("/"),
        title = title,
        imageUrl = if (coverKey != null)
            "https://covers.openlibrary.org/b/olid/${coverKey}-L.jpg"
        else "https://covers.openlibrary.org/b/id/${coverAlternativeKey}-L.jpg",
        authors = authorNames ?: emptyList(),
        description = null,
        languages = languages ?: emptyList(),
        averageRating = ratingsAverage,
        numEditions = numEditions ?: 0,
        numPages = numPagesMedian,
        firstPublishYear = firstPublishYear.toString(),
        ratingCount = ratingsCount
    )
}