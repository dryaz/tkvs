package com.dimlix.tkvs.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimlix.tkvs.ui.theme.TkvsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TkvsActivity : ComponentActivity() {

    private val viewModel: TkvsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TkvsTheme {
                val historyState by viewModel.historyState.collectAsState()
                val omniboxState by viewModel.omniboxState.collectAsState()

                Screen(historyState, omniboxState,
                    onTypeChanged = {
                        viewModel.selectOmniboxType((it))
                    }, onCommand = { type, key, value ->
                        viewModel.applyCommand(type, key, value)
                    }
                )
            }
        }
    }
}

@Composable
private fun Screen(
    history: SnapshotStateList<String>,
    omnibox: TkvsViewModel.OmniboxState,
    onTypeChanged: (TkvsViewModel.OmniboxType) -> Unit = {},
    onCommand: (TkvsViewModel.OmniboxType, String, String) -> Unit = { _, _, _ -> },
) {
    Column(modifier = Modifier.fillMaxSize()) {
        History(Modifier
            .weight(1f)
            .padding(horizontal = 16.dp), history)
        Omnibox(
            state = omnibox,
            onTypeChanged = onTypeChanged,
            onCommand = onCommand,
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ScreenPreview() {
    TkvsTheme {
        Screen(
            SnapshotStateList<String>().apply {
                add(">SET foo 1")
                add(">GET foo")
                add("1")
                add("BEGIN")
            },
            TkvsViewModel.OmniboxState.KeyValueAction(TkvsViewModel.OmniboxType.SET),
        )
    }
}