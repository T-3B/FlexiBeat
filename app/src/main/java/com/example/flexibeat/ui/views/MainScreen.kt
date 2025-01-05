package com.example.flexibeat.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.R
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.ui.viewmodels.GlobalModel

internal val TABS = arrayOf(  // Icon() can only be created inside the Composable TabRow... so this is a List of Pairs
    Icons.AutoMirrored.Filled.QueueMusic to "Queues list",
    Icons.Default.PlayCircleOutline to "Currently playing song",
    Icons.AutoMirrored.Filled.ManageSearch to "Search",
    Icons.Default.Folder to "Local files",
    Icons.Default.Settings to "Settings"
)

@Composable
fun MusicPlayerMainScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playerController = remember { PlayerController(context) }
    val globalModel = viewModel { GlobalModel(coroutineScope) }
    val isReady by remember { derivedStateOf { globalModel.pagerState != null && playerController.isInitialized } }

    DisposableEffect(Unit) {
        onDispose(playerController::destroyPlayer)
    }

    AnimatedContent(isReady, Modifier.statusBarsPadding()) {
        if (it)
            Scaffold(
                topBar = {
                    TabRow(selectedTabIndex = globalModel.pagerState!!.currentPage) {
                        TABS.forEachIndexed { index, icon ->
                            Tab(
                                selected = globalModel.pagerState!!.currentPage == index,
                                onClick = { globalModel.scrollToPage(index) },
                                icon = { Icon(icon.first, contentDescription = icon.second) }
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    HorizontalPager(
                        state = globalModel.pagerState!!,
                        modifier = Modifier.padding(paddingValues)
                    ) { page ->
                        when (page) {
                            0 -> QueueTab(playerController, globalModel::scrollToPage)
                            1 -> PlayingSongTab(playerController)
                            2 -> SearchTab(playerController, globalModel::scrollToPage)
                            3 -> LocalStorageTab(playerController, globalModel::scrollToPage)
                            4 -> PreferencesTab()
                        }
                    }
                }
            )
        else
            Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher),
                contentDescription = "Cover Art",
                modifier = Modifier.aspectRatio(1f),
                tint = Color.Unspecified
            )
            CircularProgressIndicator()
        }

    }
}