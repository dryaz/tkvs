package com.dimlix.tkvs.ui

import android.content.Context
import com.dimlix.tkvs.R
import com.dimlix.tkvs.domain.Action
import com.dimlix.tkvs.domain.KeyValueRepository
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.Test
import org.mockito.kotlin.*

internal class TkvsViewModelTest {

    @Test
    fun `Initial omnibox state is SET`() {
        val viewModel = createViewModel()
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.KeyValueAction>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.KeyValueAction) {
            type shouldBe TkvsViewModel.OmniboxType.SET
        }
    }

    @Test
    fun `Initial history state is empty list`() {
        val viewModel = createViewModel()
        viewModel.historyState.value.size shouldBe 0
    }

    @Test
    fun `SET omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.SET)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.KeyValueAction>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.KeyValueAction) {
            type shouldBe TkvsViewModel.OmniboxType.SET
        }
    }

    @Test
    fun `GET omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.GET)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.KeyAction>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.KeyAction) {
            type shouldBe TkvsViewModel.OmniboxType.GET
        }
    }

    @Test
    fun `COUNT omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.COUNT)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.ValueAction>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.ValueAction) {
            type shouldBe TkvsViewModel.OmniboxType.COUNT
        }
    }

    @Test
    fun `DELETE omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.DELETE)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.KeyAction>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.KeyAction) {
            type shouldBe TkvsViewModel.OmniboxType.DELETE
        }
    }

    @Test
    fun `BEGIN omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.BEGIN)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.Action>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.Action) {
            type shouldBe TkvsViewModel.OmniboxType.BEGIN
        }
    }

    @Test
    fun `COMMIT omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.COMMIT)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.Action>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.Action) {
            type shouldBe TkvsViewModel.OmniboxType.COMMIT
        }
    }

    @Test
    fun `ROLLBACK omnibox type should lead to proper UI`() {
        val viewModel = createViewModel()
        viewModel.selectOmniboxType(TkvsViewModel.OmniboxType.ROLLBACK)
        viewModel.omniboxState.value.shouldBeInstanceOf<TkvsViewModel.OmniboxState.Action>()
        with(viewModel.omniboxState.value as TkvsViewModel.OmniboxState.Action) {
            type shouldBe TkvsViewModel.OmniboxType.ROLLBACK
        }
    }

    @Test
    fun `Apply key value command should add input as history`() {
        val viewModel = createViewModel()
        viewModel.applyCommand(TkvsViewModel.OmniboxType.SET, "a", "b")
        viewModel.historyState.value.first shouldBe ">SET a b"
    }

    @Test
    fun `Apply key command should add input as history`() {
        val viewModel = createViewModel()
        viewModel.applyCommand(TkvsViewModel.OmniboxType.GET, key = "a")
        viewModel.historyState.value.first shouldBe ">GET a"
    }

    @Test
    fun `Apply value command should add input as history`() {
        val viewModel = createViewModel()
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COUNT, value = "a")
        viewModel.historyState.value.first shouldBe ">COUNT a"
    }

    @Test
    fun `Apply command should add input as history`() {
        val viewModel = createViewModel()
        viewModel.applyCommand(TkvsViewModel.OmniboxType.BEGIN)
        viewModel.historyState.value.first shouldBe ">BEGIN"
    }

    @Test
    fun `Apply GET command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.GET, "a")
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        with(captor.firstValue) {
            shouldBeInstanceOf<Action.Get>()
            (this as Action.Get).key shouldBe "a"
        }
    }

    @Test
    fun `Apply SET command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.SET, "a", "b")
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        with(captor.firstValue) {
            shouldBeInstanceOf<Action.Set>()
            (this as Action.Set).key shouldBe "a"
            this.value shouldBe "b"
        }
    }

    @Test
    fun `Apply DELETE command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.DELETE, "a")
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        with(captor.firstValue) {
            shouldBeInstanceOf<Action.Delete>()
            (this as Action.Delete).key shouldBe "a"
        }
    }

    @Test
    fun `Apply COUNT command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COUNT, value = "a")
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        with(captor.firstValue) {
            shouldBeInstanceOf<Action.Count>()
            (this as Action.Count).value shouldBe "a"
        }
    }

    @Test
    fun `Apply BEGIN command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.BEGIN)
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        captor.firstValue.shouldBeInstanceOf<Action.BeginTransaction>()
    }

    @Test
    fun `Apply COMMIT command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COMMIT)
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        captor.firstValue.shouldBeInstanceOf<Action.Commit>()
    }

    @Test
    fun `Apply ROLLBACK command should call proer keyValueRepo method`() {
        val keyValueRepository = mock<KeyValueRepository>()
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.ROLLBACK)
        val captor = argumentCaptor<Action>()
        verify(keyValueRepository).proceed(captor.capture())
        captor.firstValue.shouldBeInstanceOf<Action.Rollback>()
    }

    @Test
    fun `Success response with message should be added to history`() {
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(Action.Commit) } doReturn Result.success("abc")
        }
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COMMIT)
        val history = viewModel.historyState.value
        history.size shouldBe 2
        history.last shouldBe "abc"
    }

    @Test
    fun `Success response with empty message should not be added to history`() {
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(Action.Commit) } doReturn Result.success("")
        }
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COMMIT)
        val history = viewModel.historyState.value
        history.size shouldBe 1
    }

    @Test
    fun `Success response with null message should not be added to history`() {
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(Action.Commit) } doReturn Result.success(null)
        }
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COMMIT)
        val history = viewModel.historyState.value
        history.size shouldBe 1
    }

    @Test
    fun `Failed response with null error message should not be added to history`() {
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.failure(Exception())
        }
        val viewModel = createViewModel(keyValueRepository = keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COUNT, value = "a")
        val history = viewModel.historyState.value
        history.size shouldBe 1
    }

    @Test
    fun `Failed GET response should add proper error to the history`() {
        val context = mock<Context> {
            on { getString(R.string.key_not_set) } doReturn "abcd"
        }
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.failure(Exception())
        }
        val viewModel = createViewModel(context, keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.GET, key = "a")
        val history = viewModel.historyState.value
        history.size shouldBe 2
        history.last shouldBe "abcd"
    }

    @Test
    fun `Failed DELETE response should add proper error to the history`() {
        val context = mock<Context> {
            on { getString(R.string.key_not_set) } doReturn "abcd"
        }
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.failure(Exception())
        }
        val viewModel = createViewModel(context, keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.DELETE, key = "a")
        val history = viewModel.historyState.value
        history.size shouldBe 2
        history.last shouldBe "abcd"
    }

    @Test
    fun `Failed COMMIT response should add proper error to the history`() {
        val context = mock<Context> {
            on { getString(R.string.no_trx) } doReturn "abcd"
        }
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.failure(Exception())
        }
        val viewModel = createViewModel(context, keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.COMMIT)
        val history = viewModel.historyState.value
        history.size shouldBe 2
        history.last shouldBe "abcd"
    }

    @Test
    fun `Failed ROLLBACK response should add proper error to the history`() {
        val context = mock<Context> {
            on { getString(R.string.no_trx) } doReturn "abcd"
        }
        val keyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.failure(Exception())
        }
        val viewModel = createViewModel(context, keyValueRepository)
        viewModel.applyCommand(TkvsViewModel.OmniboxType.ROLLBACK)
        val history = viewModel.historyState.value
        history.size shouldBe 2
        history.last shouldBe "abcd"
    }

    private fun createViewModel(
        context: Context = mock<Context>(),
        keyValueRepository: KeyValueRepository = mock<KeyValueRepository>() {
            on { proceed(any()) } doReturn Result.success(null)
        },
    ) = TkvsViewModel(
        context,
        keyValueRepository
    )

}