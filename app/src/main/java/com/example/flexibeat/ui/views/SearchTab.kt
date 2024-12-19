package com.example.flexibeat.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.ui.viewmodels.SearchTabModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(playerController: PlayerController, scrollToPage: (Int) -> Unit) {
    val context = LocalContext.current
    val searchTabModel = viewModel { SearchTabModel(context) }

    WithStoragePermission {
        DockedSearchBar({
            TextField(
                searchTabModel.searchKey,
                { searchTabModel.searchKey = it },
                Modifier.fillMaxWidth(),
                placeholder = { Text("Search for local music!") },
                trailingIcon = { Icon(Icons.Default.Search, "Search icon", tint = colorScheme.primary) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    searchTabModel.isSearching = true
                    searchTabModel.searchResults = mutableStateListOf()
                    searchTabModel.search()
                    searchTabModel.isSearching = false
                }),
                singleLine = true
            )
        }, true, { }, Modifier.fillMaxSize()) {
            AnimatedContent(searchTabModel.isSearching, Modifier.align(Alignment.CenterHorizontally)) { isSearching ->
                if (isSearching)
                    CircularProgressIndicator()
                else if(searchTabModel.searchResults.isEmpty())
                    Text("No result found.")
                else
                    LazyColumn {
                        items(searchTabModel.searchResults) {
                            AudioFileItem(it) { playerController.replaceQueue(listOf(it), 0) }
                        }
                    }
            }
        }
    }
}