package com.dimlix.tkvs.di

import com.dimlix.tkvs.data.InMemoryKeyValueRepository
import com.dimlix.tkvs.domain.KeyValueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
final class StorageModule {

    @Provides
    fun provideKeyValueStorage(
        inMemoryKeyValueRepository: InMemoryKeyValueRepository,
    ): KeyValueRepository = inMemoryKeyValueRepository

}