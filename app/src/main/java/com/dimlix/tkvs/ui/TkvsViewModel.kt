package com.dimlix.tkvs.ui

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.dimlix.tkvs.R
import com.dimlix.tkvs.domain.Action
import com.dimlix.tkvs.domain.KeyValueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TkvsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val keyValueRepository: KeyValueRepository,
) : ViewModel() {
    val historyState: StateFlow<SnapshotStateList<String>>
        get() = _historyState
    val omniboxState: StateFlow<OmniboxState>
        get() = _omniboxState

    private val _historyState = MutableStateFlow<SnapshotStateList<String>>(SnapshotStateList())

    private val _omniboxState = MutableStateFlow<OmniboxState>(
        OmniboxState.KeyValueAction(OmniboxType.SET)
    )

    fun selectOmniboxType(commandType: OmniboxType) = when (commandType) {
        OmniboxType.SET -> _omniboxState.value = OmniboxState.KeyValueAction(commandType)
        OmniboxType.COUNT -> _omniboxState.value = OmniboxState.ValueAction(commandType)

        OmniboxType.GET,
        OmniboxType.DELETE,
        -> _omniboxState.value = OmniboxState.KeyAction(commandType)

        OmniboxType.BEGIN,
        OmniboxType.COMMIT,
        OmniboxType.ROLLBACK,
        -> _omniboxState.value = OmniboxState.Action(commandType)
    }

    fun applyCommand(omniboxType: OmniboxType, key: String = "", value: String = "") {
        val data = _historyState.value
        val keyString = if (!key.isBlank()) " $key" else ""
        val valueString = if (!value.isBlank()) " $value" else ""
        data.add(">$omniboxType$keyString$valueString")
        when (omniboxType) {
            OmniboxType.GET -> keyValueRepository.proceed(Action.Get(key))
            OmniboxType.SET -> keyValueRepository.proceed(Action.Set(key, value))
            OmniboxType.DELETE -> keyValueRepository.proceed(Action.Delete(key))
            OmniboxType.COUNT -> keyValueRepository.proceed(Action.Count(value))
            OmniboxType.BEGIN -> keyValueRepository.proceed(Action.BeginTransaction)
            OmniboxType.COMMIT -> keyValueRepository.proceed(Action.Commit)
            OmniboxType.ROLLBACK -> keyValueRepository.proceed(Action.Rollback)
        }.onSuccess {
            if (it != null) data.add(it)
        }.onFailure {
            val errorText = getErrorText(omniboxType)
            if (!errorText.isNullOrBlank()) data.add(errorText)
        }
        _historyState.value = data
    }

    private fun getErrorText(omniboxType: OmniboxType): String? = when (omniboxType) {
        OmniboxType.GET -> appContext.getString(R.string.key_not_set)
        OmniboxType.DELETE -> appContext.getString(R.string.key_not_set)
        OmniboxType.COMMIT -> appContext.getString(R.string.no_trx)
        OmniboxType.ROLLBACK -> appContext.getString(R.string.no_trx)
        else -> null
    }

    // TODO: Omnibox state should have respective values (key and/or value) inside this UI model
    //  in order to make cleanup logic testable inside viewModel, e.g. when command is applied
    //  text inside omnibox should be cleared. Currently this logic is inside UI layer and won't be
    //  tested via ViewModel Unit Test.
    sealed class OmniboxState(val type: OmniboxType) {
        class Action(type: OmniboxType) : OmniboxState(type)
        class KeyAction(type: OmniboxType) : OmniboxState(type)
        class ValueAction(type: OmniboxType) : OmniboxState(type)
        class KeyValueAction(type: OmniboxType) : OmniboxState(type)
    }

    enum class OmniboxType {
        GET, SET, DELETE, COUNT, BEGIN, COMMIT, ROLLBACK
    }
}