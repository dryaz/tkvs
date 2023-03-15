package com.dimlix.tkvs.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dimlix.tkvs.ui.theme.TkvsTheme
import kotlinx.coroutines.launch

@Composable
internal fun History(
    modifier: Modifier = Modifier,
    history: SnapshotStateList<String>,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = scrollState,
    ) {
        items(items = history) {
            Text(text = "$it")
        }

        coroutineScope.launch {
            scrollState.animateScrollToItem(Math.max(0, history.size - 1))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    TkvsTheme {
        History(history = SnapshotStateList<String>().apply {
            add(">SET foo 1")
            add(">GET foo")
            add("1")
            add("BEGIN")
        })
    }
}

