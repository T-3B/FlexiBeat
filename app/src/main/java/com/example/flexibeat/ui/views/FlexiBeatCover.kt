package com.example.flexibeat.ui.views

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.flexibeat.R

@Composable
fun FlexiBeatCover(modifier: Modifier) {
    Icon(
        painterResource(R.drawable.ic_notification),
        "Cover Art",
        modifier,
        colorScheme.primary
    )
}