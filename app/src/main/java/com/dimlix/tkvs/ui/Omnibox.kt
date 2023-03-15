package com.dimlix.tkvs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dimlix.tkvs.R
import com.dimlix.tkvs.ui.theme.TkvsTheme

@Composable
fun Omnibox(
    modifier: Modifier = Modifier,
    state: TkvsViewModel.OmniboxState,
    onTypeChanged: (TkvsViewModel.OmniboxType) -> Unit = {},
    onCommand: (TkvsViewModel.OmniboxType, String, String) -> Unit = { _, _, _ -> },
) {
    var confirmationInfo by remember { mutableStateOf<ConfirmationInfo?>(null) }

    OmniboxView(
        modifier,
        state,
        onTypeChanged,
        onCommand = { type, key, value ->
            if (isConfirmationNeeded(type)) {
                confirmationInfo = ConfirmationInfo(type, key, value)
            } else {
                onCommand(type, key, value)
            }
        }
    )

    confirmationInfo?.let {
        ConfirmOperation(it.type, it.key, it.value,
            onConfirm = {
                onCommand(it.type, it.key, it.value)
                confirmationInfo = null
            }, onDismiss = {
                confirmationInfo = null
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun OmniboxView(
    modifier: Modifier = Modifier,
    state: TkvsViewModel.OmniboxState,
    onTypeChanged: (TkvsViewModel.OmniboxType) -> Unit = {},
    onCommand: (TkvsViewModel.OmniboxType, String, String) -> Unit = { _, _, _ -> },
) {
    var key by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var value by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var expanded by remember { mutableStateOf(false) }

    val items = TkvsViewModel.OmniboxType.values()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = modifier.weight(1f),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = state.type.toString(),
                singleLine = true,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                modifier = modifier.width(150.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { selectedOption ->
                    DropdownMenuItem(onClick = {
                        onTypeChanged(selectedOption)
                        expanded = false
                    }) {
                        Text(text = selectedOption.toString())
                    }
                }
            }
        }
        if (isKeyVisible(state)) {
            Spacer(modifier = modifier.width(4.dp))
            TextField(
                modifier = modifier.weight(1f),
                value = key,
                label = { Text(stringResource(id = R.string.key)) },
                singleLine = true,
                onValueChange = { key = it }
            )
        }
        if (isValueVisible(state)) {
            Spacer(modifier = modifier.width(4.dp))
            TextField(
                modifier = modifier.weight(1f),
                value = value,
                label = { Text(stringResource(id = R.string.value)) },
                singleLine = true,
                onValueChange = { value = it }
            )
        }
        if (!isValueVisible(state) && !isKeyVisible(state)) {
            Spacer(modifier = modifier.weight(1f))
        } else {
            Spacer(modifier = modifier.width(8.dp))
        }
        val isButtonEnabled =
            (value.text.isNotBlank() || !isValueVisible(state))
                    && (key.text.isNotBlank() || !isKeyVisible(state))
        Button(
            modifier = modifier.size(48.dp),
            shape = CircleShape,
            enabled = isButtonEnabled,
            onClick = {
                onCommand(state.type, key.text.trim(), value.text.trim())
                key = TextFieldValue("")
                value = TextFieldValue("")
            }
        ) {
            Icon(
                tint = Color.White,
                imageVector = Icons.Filled.Send,
                contentDescription = null
            )
        }
        Spacer(modifier = modifier.width(8.dp))
    }
}

@Composable
private fun ConfirmOperation(
    operation: TkvsViewModel.OmniboxType,
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = { Text(stringResource(R.string.confirm_action)) },
        text = {
            Text(stringResource(R.string.action_to_confirm,
                operation.toString(),
                key,
                value))
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.change_mind))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
}

private data class ConfirmationInfo(
    val type: TkvsViewModel.OmniboxType,
    val key: String,
    val value: String,
)

private fun isConfirmationNeeded(operation: TkvsViewModel.OmniboxType): Boolean = when (operation) {
    TkvsViewModel.OmniboxType.DELETE,
    TkvsViewModel.OmniboxType.COMMIT,
    TkvsViewModel.OmniboxType.ROLLBACK,
    -> true
    TkvsViewModel.OmniboxType.GET,
    TkvsViewModel.OmniboxType.SET,
    TkvsViewModel.OmniboxType.COUNT,
    TkvsViewModel.OmniboxType.BEGIN,
    -> false
}

private fun isKeyVisible(state: TkvsViewModel.OmniboxState): Boolean = when (state) {
    is TkvsViewModel.OmniboxState.KeyAction,
    is TkvsViewModel.OmniboxState.KeyValueAction,
    -> true
    is TkvsViewModel.OmniboxState.Action,
    is TkvsViewModel.OmniboxState.ValueAction,
    -> false
}

private fun isValueVisible(state: TkvsViewModel.OmniboxState): Boolean = when (state) {
    is TkvsViewModel.OmniboxState.ValueAction,
    is TkvsViewModel.OmniboxState.KeyValueAction,
    -> true
    is TkvsViewModel.OmniboxState.KeyAction,
    is TkvsViewModel.OmniboxState.Action,
    -> false
}

@Preview(showBackground = true)
@Composable
private fun KeyValuePreview() {
    TkvsTheme {
        Omnibox(state = TkvsViewModel.OmniboxState.KeyValueAction(TkvsViewModel.OmniboxType.SET))
    }
}

@Preview(showBackground = true)
@Composable
private fun ValuePreview() {
    TkvsTheme {
        Omnibox(state = TkvsViewModel.OmniboxState.ValueAction(TkvsViewModel.OmniboxType.COUNT))
    }
}

@Preview(showBackground = true)
@Composable
private fun KeyPreview() {
    TkvsTheme {
        Omnibox(state = TkvsViewModel.OmniboxState.KeyAction(TkvsViewModel.OmniboxType.GET))
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionPreview() {
    TkvsTheme {
        Omnibox(state = TkvsViewModel.OmniboxState.Action(TkvsViewModel.OmniboxType.ROLLBACK))
    }
}

