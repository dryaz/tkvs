package com.dimlix.tkvs.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Screen()
                }
            }
        }

    }
}

@Composable
private fun Screen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        History(Modifier
            .weight(1f)
            .padding(horizontal = 16.dp))
        Omnibox()
    }
}