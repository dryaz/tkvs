package com.dimlix.tkvs.data

import com.dimlix.tkvs.domain.KeyValueRepository
import java.util.*
import javax.inject.Inject

class InMemoryKeyValueRepository @Inject constructor() : KeyValueRepository {

    private val storage = LinkedList<HashMap<String, String>>().apply { add(hashMapOf()) }

    override fun set(key: String, value: String) {
        storage.last.put(key, value)
    }

    override fun get(key: String): String? = storage.last.get(key)

    override fun delete(key: String) {
        storage.last.remove(key)
    }

    override fun count(value: String): Int = storage.last.count { it.value == value }

    override fun beginTransaction() {
        val lastNode = storage.last
        storage.add(hashMapOf<String, String>().apply { putAll(lastNode) })
    }

    override fun commit(): Boolean {
        if (storage.size == 1) return false
        val lastNode = storage.removeLast()
        storage.last.putAll(lastNode)
        return true
    }

    override fun rollback(): Boolean {
        if (storage.size == 1) return false
        storage.removeLast()
        return true
    }
}