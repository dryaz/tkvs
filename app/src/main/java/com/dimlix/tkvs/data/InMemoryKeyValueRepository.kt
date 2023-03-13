package com.dimlix.tkvs.data

import com.dimlix.tkvs.domain.Action
import com.dimlix.tkvs.domain.KeyValueRepository
import java.util.*
import javax.inject.Inject

class InMemoryKeyValueRepository @Inject constructor() : KeyValueRepository {

    private val storage = LinkedList<HashMap<String, String>>().apply { add(hashMapOf()) }

    override fun proceed(action: Action): Result<String?> = when (action) {
        Action.BeginTransaction -> beginTransaction()
        Action.Commit -> commit()
        Action.Rollback -> rollback()
        is Action.Count -> count(action.value)
        is Action.Delete -> delete(action.key)
        is Action.Get -> get(action.key)
        is Action.Set -> set(action.key, action.value)
    }

    private fun set(key: String, value: String): Result<String?> {
        storage.last.put(key, value)
        return Result.success(null)
    }

    private fun get(key: String): Result<String?> = Result.success(storage.last.get(key))

    private fun delete(key: String): Result<String?> {
        storage.last.remove(key)
        return Result.success(null)
    }

    private fun count(value: String): Result<String?> =
        Result.success(storage.last.count { it.value == value }.toString())

    private fun beginTransaction(): Result<String?> {
        val lastNode = storage.last
        storage.add(hashMapOf<String, String>().apply { putAll(lastNode) })
        return Result.success(null)
    }

    private fun commit(): Result<String?> {
        if (storage.size == 1) return Result.failure(ArrayIndexOutOfBoundsException())
        val lastNode = storage.removeLast()
        storage.last.putAll(lastNode)
        return Result.success(null)
    }

    private fun rollback(): Result<String?> {
        if (storage.size == 1) return Result.failure(ArrayIndexOutOfBoundsException())
        storage.removeLast()
        return Result.success(null)
    }
}