package com.dimlix.tkvs.domain

interface KeyValueRepository {

    fun set(key: String, value: String)

    fun get(key: String): String?

    fun delete(key: String)

    fun count(value: String): Int

    fun beginTransaction()

    fun commit(): Boolean

    fun rollback(): Boolean

}