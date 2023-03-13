package com.dimlix.tkvs.ui

import androidx.lifecycle.ViewModel
import com.dimlix.tkvs.domain.KeyValueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TkvsViewModel @Inject constructor(
    private val keyValueRepository: KeyValueRepository,
) : ViewModel() {

}