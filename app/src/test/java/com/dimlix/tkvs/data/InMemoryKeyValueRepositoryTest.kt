package com.dimlix.tkvs.data

import io.kotlintest.shouldBe
import org.junit.Test

internal class InMemoryKeyValueRepositoryTest {

    @Test
    fun `Set variable should be then get properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.get("a") shouldBe "1"
    }

    @Test
    fun `Non set variable should be then get properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.get("b") shouldBe null
    }

    @Test
    fun `Remove variable should not be exists`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.delete("a")
        repository.get("a") shouldBe null
    }

    @Test
    fun `Count entries by key should return value`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.set("b", "1")
        repository.set("c", "3")
        repository.set("a", "1")
        repository.count("1") shouldBe 2
    }

    @Test
    fun `Removed value should not be count`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.set("b", "1")
        repository.delete("a")
        repository.count("1") shouldBe 1
    }

    @Test
    fun `Non existed value should return 0 as count`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.count("a") shouldBe 0
    }

    @Test
    fun `Removed variable in transaction should not be exists before rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.beginTransaction()
        repository.delete("a")
        repository.get("a") shouldBe null
    }

    @Test
    fun `Removed variable in transaction should exist after rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.beginTransaction()
        repository.delete("a")
        repository.rollback()
        repository.get("a") shouldBe "1"
    }

    @Test
    fun `Rollback after commit should not affect values`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.beginTransaction()
        repository.delete("a")
        repository.commit()
        repository.rollback()
        repository.get("a") shouldBe "1"
    }

    @Test
    fun `Commit without transaction should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.commit() shouldBe false
    }

    @Test
    fun `Rollback without transaction should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.rollback() shouldBe false
    }

    @Test
    fun `Commit after rollback should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.beginTransaction()
        repository.set("a", "1")
        repository.rollback()
        repository.commit() shouldBe false
    }

    @Test
    fun `Rollback after commit should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.beginTransaction()
        repository.set("a", "1")
        repository.commit()
        repository.rollback() shouldBe false
    }

    @Test
    fun `Commit with transaction should return true`() {
        val repository = InMemoryKeyValueRepository()
        repository.beginTransaction()
        repository.set("a", "1")
        repository.commit() shouldBe true
    }

    @Test
    fun `Rollback with transaction should return true`() {
        val repository = InMemoryKeyValueRepository()
        repository.beginTransaction()
        repository.set("a", "1")
        repository.rollback() shouldBe true
    }

    @Test
    fun `Get should return value even before commit`() {
        val repository = InMemoryKeyValueRepository()
        repository.beginTransaction()
        repository.set("a", "1")
        repository.get("a") shouldBe "1"
    }

    @Test
    fun `Set in transaction should take affect before rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.beginTransaction()
        repository.get("a") shouldBe "1"
        repository.set("a", "2")
        repository.get("a") shouldBe "2"
        repository.rollback()
        repository.get("a") shouldBe "1"
    }


    @Test
    fun `Nested transactions should work properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.set("a", "1")
        repository.beginTransaction()
        repository.set("a", "2")
        repository.set("b", "1")
        repository.beginTransaction()
        repository.set("a", "3")
        repository.set("b", "2")
        repository.beginTransaction()
        repository.set("a", "4")
        repository.set("c", "1")
        repository.commit()
        repository.get("a") shouldBe "4"
        repository.get("b") shouldBe "2"
        repository.get("c") shouldBe "1"
        repository.rollback()
        repository.get("a") shouldBe "2"
        repository.get("b") shouldBe "1"
        repository.get("c") shouldBe null
        repository.rollback()
        repository.get("a") shouldBe "1"
        repository.get("b") shouldBe null
        repository.get("c") shouldBe null
    }

}