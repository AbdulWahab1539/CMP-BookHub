package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.Route
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookDetailViewModel(
    val bookRepository: BookRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state.onStart {
        fetchBookDescription()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )


    fun onAction(action: BookDetailAction) {
        when (action) {

            BookDetailAction.OnFavoriteClick -> {

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
