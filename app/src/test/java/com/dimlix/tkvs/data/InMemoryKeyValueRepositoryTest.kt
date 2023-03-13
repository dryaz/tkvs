package com.dimlix.tkvs.data

import com.dimlix.tkvs.domain.Action
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.Test

internal class InMemoryKeyValueRepositoryTest {

    @Test
    fun `Set variable should be then get properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
    }

    @Test
    fun `Non set variable should be then get properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Get("b")).getOrThrow() shouldBe null
    }

    @Test
    fun `Remove variable should not be exists`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Delete("a"))
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe null
    }

    @Test
    fun `Count entries by key should return value`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Set("b", "1"))
        repository.proceed(Action.Set("c", "3"))
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Count("1")).getOrThrow() shouldBe "2"
    }

    @Test
    fun `Removed value should not be count`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Set("b", "1"))
        repository.proceed(Action.Delete("a"))
        repository.proceed(Action.Count("1")).getOrThrow() shouldBe "1"
    }

    @Test
    fun `Non existed value should return 0 as count`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Count("a")).getOrThrow() shouldBe "0"
    }

    @Test
    fun `Removed variable in transaction should not be exists before rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Delete("a"))
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe null
    }

    @Test
    fun `Removed variable in transaction should exist after rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Delete("a"))
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
    }

    @Test
    fun `Rollback after commit should not affect values`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Delete("a"))
        repository.proceed(Action.Commit)
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
    }

    @Test
    fun `Commit without transaction should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Commit).exceptionOrNull()
            .shouldBeInstanceOf<ArrayIndexOutOfBoundsException>()
    }

    @Test
    fun `Rollback without transaction should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Rollback).exceptionOrNull()
            .shouldBeInstanceOf<ArrayIndexOutOfBoundsException>()
    }

    @Test
    fun `Commit after rollback should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Commit).exceptionOrNull()
            .shouldBeInstanceOf<ArrayIndexOutOfBoundsException>()
    }

    @Test
    fun `Rollback after commit should return false`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Commit)
        repository.proceed(Action.Rollback).exceptionOrNull()
            .shouldBeInstanceOf<ArrayIndexOutOfBoundsException>()
    }

    @Test
    fun `Commit with transaction should return true`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Commit).isSuccess shouldBe true
    }

    @Test
    fun `Rollback with transaction should return true`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Rollback).isSuccess shouldBe true
    }

    @Test
    fun `Get should return value even before commit`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
    }

    @Test
    fun `Set in transaction should take affect before rollback`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
        repository.proceed(Action.Set("a", "2"))
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "2"
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
    }


    @Test
    fun `Nested transactions should work properly`() {
        val repository = InMemoryKeyValueRepository()
        repository.proceed(Action.Set("a", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "2"))
        repository.proceed(Action.Set("b", "1"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "3"))
        repository.proceed(Action.Set("b", "2"))
        repository.proceed(Action.BeginTransaction)
        repository.proceed(Action.Set("a", "4"))
        repository.proceed(Action.Set("c", "1"))
        repository.proceed(Action.Commit)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "4"
        repository.proceed(Action.Get("b")).getOrThrow() shouldBe "2"
        repository.proceed(Action.Get("c")).getOrThrow() shouldBe "1"
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "2"
        repository.proceed(Action.Get("b")).getOrThrow() shouldBe "1"
        repository.proceed(Action.Get("c")).getOrThrow() shouldBe null
        repository.proceed(Action.Rollback)
        repository.proceed(Action.Get("a")).getOrThrow() shouldBe "1"
        repository.proceed(Action.Get("b")).getOrThrow() shouldBe null
        repository.proceed(Action.Get("c")).getOrThrow() shouldBe null
    }

}