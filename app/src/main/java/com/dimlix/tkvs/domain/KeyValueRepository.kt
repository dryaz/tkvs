package com.dimlix.tkvs.domain

interface KeyValueRepository {

    // TODO: Maybe worth to make contract suspendable and/or return flow by default to gain
    //  more flexibility in case some long running action couldbe held and we need progress of it.
    fun proceed(action: Action): Result<String?>

}

sealed class Action {
    object Commit: Action()
    object BeginTransaction: Action()
    object Rollback: Action()
    class Set(val key: String, val value: String): Action()
    class Get(val key: String): Action()
    class Delete(val key: String): Action()
    class Count(val value: String): Action()
}