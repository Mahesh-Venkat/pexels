package com.maheshvenkat.pexels.ui.photos

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.maheshvenkat.pexels.Injection
import com.maheshvenkat.pexels.MainActivity
import com.maheshvenkat.pexels.R
import com.maheshvenkat.pexels.databinding.FragmentPhotosBinding
import com.maheshvenkat.pexels.models.Photo
import com.maheshvenkat.pexels.ui.photos.adapter.loadstate.PhotosLoadStateAdapter
import com.maheshvenkat.pexels.ui.photos.adapter.photos.PhotosAdapter
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
        (requireActivity() as MainActivity).title = getString(R.string.page_title_search_photos)

        // get the view model
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                context = requireContext(),
                owner = this
            )
        ).get(PhotosViewModel::class.java)

        // bind the state
        val photosAdapter = PhotosAdapter(PhotosAdapter.OnClickListener {
            viewModel.displayPhotographerDetails(it)
        })
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept,
            photosAdapter = photosAdapter
        )

        // Navigate to Bottom sheet on click of the Photo from the adapter
        viewModel.navigateToSelectedPhoto.observe(
            viewLifecycleOwner
        ) {
            if (null != it) {
                this.findNavController()
                    .navigate(PhotosFragmentDirections.actionPhotosFragmentToPhotoInfo(it))
                viewModel.displayPhotographerDetailsComplete()
            }
        }

        return binding.root
    }

    /**
     * Binds the [UiState] provided  by the PhotosViewModel to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentPhotosBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        uiActions: (UiAction) -> Unit,
        photosAdapter: PhotosAdapter
    ) {
        val header = PhotosLoadStateAdapter { photosAdapter.retry() }
        list.adapter = photosAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = PhotosLoadStateAdapter { photosAdapter.retry() }
        )
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            photosAdapter = photosAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

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
        header: PhotosLoadStateAdapter,
        photosAdapter: PhotosAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Photo>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

        retryButton.setOnClickListener { photosAdapter.retry() }

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        val notLoading = photosAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(photosAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            photosAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && photosAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                list.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.shared_label_oops_with_error_message) + it.error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


//        lifecycleScope.launch {
//            photosAdapter.loadStateFlow.collect { loadState ->
//                // Show a retry header if there was an error refreshing, and items were previously
//                // cached OR default to the default prepend state
//                header.loadState = loadState.mediator
//                    ?.refresh
//                    ?.takeIf { it is LoadState.Error && photosAdapter.itemCount > 0 }
//                    ?: loadState.prepend
//
//                val isListEmpty =
//                    loadState.refresh is LoadState.NotLoading && photosAdapter.itemCount == 0
//                // show empty list
//                emptyList.isVisible = isListEmpty
//                // Only show the list if refresh succeeds, either from the the local db or the remote.
//                list.isVisible =
//                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
//                // Show loading spinner during initial load or refresh.
//                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
//                // Show the retry state if initial load or refresh fails.
//                retryButton.isVisible =
//                    loadState.mediator?.refresh is LoadState.Error && photosAdapter.itemCount == 0
//                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
//                val errorState = loadState.source.append as? LoadState.Error
//                    ?: loadState.source.prepend as? LoadState.Error
//                    ?: loadState.append as? LoadState.Error
//                    ?: loadState.prepend as? LoadState.Error
//                errorState?.let {
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.shared_label_oops_with_error_message) + it.error,
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
    }
}

