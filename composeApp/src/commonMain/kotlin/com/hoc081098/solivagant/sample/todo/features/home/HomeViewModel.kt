package com.hoc081098.solivagant.sample.todo.features.home

import androidx.compose.runtime.Immutable
import com.hoc081098.flowext.startWith
import com.hoc081098.kmp.viewmodel.ViewModel
import com.hoc081098.solivagant.sample.todo.domain.TodoItem
import com.hoc081098.solivagant.sample.todo.features.home.domain.ObserveAllTodoItems
import com.hoc081098.solivagant.sample.todo.features.home.domain.RemoveItemById
import com.hoc081098.solivagant.sample.todo.features.home.domain.ToggleItemById
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
internal sealed interface HomeUiState {
  data object Loading : HomeUiState
  data class Error(val message: String) : HomeUiState
  data class Content(val items: ImmutableList<TodoItem>) : HomeUiState

  @Immutable
  data class TodoItem(
    val id: String,
    val text: String,
    val isDone: Boolean,
  )
}

internal class HomeViewModel(
  private val removeItemById: RemoveItemById,
  private val toggleItemById: ToggleItemById,
  observeAllTodoItems: ObserveAllTodoItems,
) : ViewModel() {
  internal val uiStateFlow: StateFlow<HomeUiState> =
    observeAllTodoItems()
      .map { items ->
        HomeUiState.Content(
          items = items
            .map { it.toTodoItemUi() }
            .toImmutableList(),
        )
      }
      .startWith(HomeUiState.Loading)
      .catch {
        emit(
          HomeUiState.Error(
            message = it.message ?: "Unknown error",
          ),
        )
      }
      .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        HomeUiState.Loading,
      )

  internal fun toggle(item: HomeUiState.TodoItem) {
    viewModelScope.launch { toggleItemById(TodoItem.Id(item.id)) }
  }

  internal fun remove(item: HomeUiState.TodoItem) {
    viewModelScope.launch { removeItemById(TodoItem.Id(item.id)) }
  }
}

private fun TodoItem.toTodoItemUi(): HomeUiState.TodoItem = HomeUiState.TodoItem(
  id = id.value,
  text = text.value,
  isDone = isDone,
)
