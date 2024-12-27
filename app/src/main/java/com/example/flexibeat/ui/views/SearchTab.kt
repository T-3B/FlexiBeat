package com.example.flexibeat.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flexibeat.controllers.PlayerController
import com.example.flexibeat.data.searchableColumns
import com.example.flexibeat.ui.viewmodels.SearchTabModel
import com.example.flexibeat.ui.viewmodels.tagMapping


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(playerController: PlayerController, scrollToPage: (Int) -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchTabModel = viewModel { SearchTabModel(context) }

    DisposableEffect(Unit) { onDispose { focusManager.clearFocus() } }

    WithStoragePermission {
        DockedSearchBar({
            TextField(
                searchTabModel.searchKey,
                { searchTabModel.searchKey = it },
                Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(if (searchTabModel.chips.isEmpty()) "Search for local music!" else "Add a filter or press enter to search!") },
                trailingIcon = { IconButton({ searchTabModel.search() })  { Icon(Icons.Default.Search, "Search", tint = colorScheme.primary) } },
                isError = searchTabModel.errorSearchFilters,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus(); searchTabModel.search() }),
                singleLine = true
            )
        }, true, { }, Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }, shape = RectangleShape) {
            LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(searchTabModel.chips) { idx, chip ->
                    val iconToColor = if (chip.first) Icons.Default.RemoveCircleOutline to Color.Red else Icons.Default.AddCircleOutline to Color.Green
                    InputChip(
                        selected = chip.first,
                        label = { Text("${chip.second}: ${chip.third}") },
                        onClick = { },
                        leadingIcon = { Icon(iconToColor.first, "Positive or negative filter") },
                        trailingIcon = { IconButton({ searchTabModel.chipRemoveAt(idx) }) { Icon(Icons.Default.Close, "Remove filter from search") } },
                        colors = InputChipDefaults.inputChipColors(containerColor = iconToColor.second.copy(.3f), labelColor = iconToColor.second)
                    )
                }
            }
            Text("Search syntax is the following:\ttitle=love !artist=\"my father\"\nSupported tags: ${tagMapping.keys.joinToString()}\nA search without any tag will search inside: ${searchableColumns.joinToString()}", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            HorizontalDivider(Modifier.padding(vertical = 16.dp), Dp.Hairline, colorScheme.onSurface)
            AnimatedContent(searchTabModel.isSearching, Modifier.fillMaxSize().align(Alignment.CenterHorizontally)) { isSearching ->
                if (isSearching)
                    CircularProgressIndicator()
                else {
                    if(searchTabModel.searchResults.isEmpty())
                        Text("No result found.", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    LazyColumn(Modifier.fillMaxHeight()) {
                        itemsIndexed(searchTabModel.searchResults) { idx, it ->
                            AudioFileItem(it) { playerController.replaceQueue(searchTabModel.searchResults, idx); scrollToPage(1) }
                        }
                    }
                }
            }
        }
    }
}