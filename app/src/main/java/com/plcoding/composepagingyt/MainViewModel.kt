package com.plcoding.composepagingyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val repository = Repository()

    var state by mutableStateOf(ScreenState())

    private val paginator = DefaultPaginator(
        // initial key is by default zero as the first page
        initialKey = state.page,
        // updating load state whether it is or isnt loading
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        // onRequest gives us the next key/page
        onRequest = { nextPage ->
            // get next items from the current page/key and get 20 of them
            repository.getItems(nextPage, 20)
        },
        getNextKey = {
            // load the next page
            state.page + 1
            // This is a easy example
            // some APIs give the key in their response
        },
        // get the error and extract the message
        onError = {
            state = state.copy(error = it?.localizedMessage)
        },
        // we get the new items and the new key
        onSuccess = { items, newKey ->
            // update the state
            state = state.copy(
                // items should contain the old data + the new data as well
                items = state.items + items,
                page = newKey,
                // the end will be when we will get an empty list
                endReached = items.isEmpty()
            )
        }
    )

    init {
        // we want to call the data at start
        loadNextItems()
    }

    // and we also want to call more data when we scroll to the bottom
    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }
}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<ListItem> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)