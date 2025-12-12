package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.Route
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    val bookRepository: BookRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state.onStart {
        fetchBookDescription()
        observeFavoriteStatus()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )


    fun onAction(action: BookDetailAction) {
        when (action) {

            BookDetailAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    if (state.value.isFavorite) {
                        bookRepository.deleteFavoriteBook(bookId)
                    } else {
                        state.value.book?.let { book ->
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }
            }

            is BookDetailAction.OnSelectedBookChange -> {
                _state.update {
                    it.copy(
                        book = action.book
                    )
                }
            }

            else -> Unit
        }
    }

    private fun observeFavoriteStatus() {
        bookRepository.isBookFavorite(bookId)
            .onEach { isFavorite ->
                _state.update { s ->
                    s.copy(isFavorite = isFavorite)
                }
            }
            .launchIn(viewModelScope)
    }

    val bookId: String
        get() = savedStateHandle.toRoute<Route.BookDetail>().id

    fun fetchBookDescription() {
        viewModelScope.launch {
            bookRepository.getBookDescription(
                bookId
            ).onSuccess { description ->
                _state.update {
                    it.copy(
                        book = it.book?.copy(
                            description = description
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }
}
