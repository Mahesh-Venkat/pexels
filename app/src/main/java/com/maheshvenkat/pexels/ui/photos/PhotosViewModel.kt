package com.maheshvenkat.pexels.ui.photos

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maheshvenkat.pexels.data.PhotosRepository
import com.maheshvenkat.pexels.models.Photo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the PhotosFragment screen.
 * The ViewModel works with the PhotoRepository to get the data.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PhotosViewModel(
    private val repository: PhotosRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<Photo>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    /**
     * Helps us to set the Photographer info to the bottom sheet from Fragment
     */
    private val _navigateToSelectedPhotographer = MutableLiveData<Photo>()
    val navigateToSelectedPhoto: LiveData<Photo>
        get() = _navigateToSelectedPhotographer

    init {
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchPhoto(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = UiState()
        )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    /**
     * Intentionally commented this code, as there is a bug with network -> Cache  and Cache -> Network
     * I will be enabling the initial just Network fetch for now and leaving the database access in this file and everywhere
     */
//    private fun searchPhoto(queryString: String): Flow<PagingData<Photo>> =
//        repository.getSearchResultStream(queryString)
//            .map { pagingData -> pagingData.map { dbPhoto -> dbPhoto.asDomainModel() } }

    private fun searchPhoto(queryString: String): Flow<PagingData<Photo>> =
        repository.getSearchResultStream(queryString)

    fun displayPhotographerDetails(photoInfo: Photo) {
        _navigateToSelectedPhotographer.value = photoInfo
    }

    fun displayPhotographerDetailsComplete() {
        _navigateToSelectedPhotographer.value = null
    }
}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = ""