package com.example.flexibeat.ui.viewmodels

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flexibeat.data.datasave.GlobalRepository
import com.example.flexibeat.ui.views.TABS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val pagerIdxSaveKey = "pager_idx"

class GlobalModel(private val coroutineScope: CoroutineScope) : ViewModel() {
    var pagerState: PagerState? by mutableStateOf(null)
        private set

    init {
        GlobalRepository.getValueBackground(pagerIdxSaveKey, 1) { pagerState = PagerState(it) { TABS.size } }
        viewModelScope.launch { snapshotFlow { pagerState?.targetPage } .collect { pageIndex -> pageIndex?.let { GlobalRepository.saveValue(pagerIdxSaveKey, it) } } }
    }
    fun scrollToPage(idx: Int) {
        coroutineScope.launch { pagerState?.animateScrollToPage(idx) } }
}