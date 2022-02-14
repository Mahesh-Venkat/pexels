package com.maheshvenkat.pexels.ui.photos

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.maheshvenkat.pexels.Injection
import com.maheshvenkat.pexels.databinding.FragmentPhotosBinding
import com.maheshvenkat.pexels.models.Photo
import com.maheshvenkat.pexels.ui.photos.adapter.PhotosAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        val binding = FragmentPhotosBinding.inflate(layoutInflater)

        // get the view model
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                owner = this
            )
        ).get(PhotosViewModel::class.java)

        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    /**
     * Binds the [UiState] provided  by the PhotosViewModel to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentPhotosBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        uiActions: (UiAction) -> Unit
    ) {
        val photosAdapter = PhotosAdapter()
        list.adapter = photosAdapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            photosAdapter = photosAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun FragmentPhotosBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchPhoto.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchPhoto.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchPhoto::setText)
        }
    }

    private fun FragmentPhotosBinding.updatePhotoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchPhoto.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentPhotosBinding.bindList(
        photosAdapter: PhotosAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = photosAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(photosAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }
    }

}