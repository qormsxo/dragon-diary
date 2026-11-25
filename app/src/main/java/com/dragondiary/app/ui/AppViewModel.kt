package com.dragondiary.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dragondiary.app.data.DiaryDraft
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.DiaryStore
import com.dragondiary.app.data.DragonColor
import com.dragondiary.app.data.DragonHorn
import com.dragondiary.app.data.Mood
import com.dragondiary.app.data.TextSize
import com.dragondiary.app.ui.theme.ThemeMode
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime

data class SettingsState(
  val themeMode: ThemeMode,
  val textSize: TextSize,
  val dragonColor: DragonColor,
  val dragonHorn: DragonHorn,
)

/**
 * 앱 전체 상태와 이벤트를 다루는 단일 ViewModel.
 * 화면 Composable 은 상태를 받아 그리고, 사용자 동작은 여기 함수로 넘긴다.
 */
class AppViewModel(app: Application) : AndroidViewModel(app) {
  private val store = DiaryStore(app)

  private val _entries = MutableStateFlow(store.loadEntries().sortedByDescending { it.createdAt })
  val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()

  /**
   * 작성 중 초안. 텍스트 입력이 한 글자도 빠지지 않도록 Flow 가 아닌 Compose 스냅샷 상태로 둔다
   * (Flow 를 거치면 다음 프레임에야 값이 돌아와 빠른 입력이 유실될 수 있다).
   */
  private val _draft = mutableStateOf(store.loadDraft() ?: DiaryDraft())
  val draft: DiaryDraft get() = _draft.value

  private val _settings = MutableStateFlow(
    SettingsState(store.themeMode, store.textSize, store.dragonColor, store.dragonHorn),
  )
  val settings: StateFlow<SettingsState> = _settings.asStateFlow()

  fun entry(id: Long): DiaryEntry? = _entries.value.firstOrNull { it.id == id }

  // ---- 작성 초안 ----

  /** 새 일기 쓰기. 임시 보관한 초안이 있으면 이어 쓴다. */
  fun startNewDraft() {
    val current = _draft.value
    if (current.editingId != null) _draft.value = DiaryDraft()
  }

  /** 기존 기록 고치기 */
  fun startEditing(id: Long) {
    val e = entry(id) ?: return
    _draft.value = DiaryDraft(
      editingId = e.id, date = e.date, mood = e.mood, tags = e.tags.toSet(),
      memo = e.memo, title = e.title, body = e.body,
    )
  }

  private inline fun editDraft(change: (DiaryDraft) -> DiaryDraft) {
    _draft.value = change(_draft.value)
  }

  fun selectMood(mood: Mood) = editDraft { it.copy(mood = if (it.mood == mood) null else mood) }
  fun toggleTag(id: String) = editDraft { it.copy(tags = if (id in it.tags) it.tags - id else it.tags + id) }
  fun updateMemo(text: String) = editDraft { it.copy(memo = text.take(MEMO_LIMIT)) }
  fun updateTitle(text: String) = editDraft { it.copy(title = text) }
  fun updateBody(text: String) = editDraft { it.copy(body = text) }

  /** '임시 보관' — 초안을 기기에 저장 */
  fun saveDraftForLater() {
    val saved = _draft.value.copy(savedAt = LocalDateTime.now())
    _draft.value = saved
    store.saveDraft(saved)
  }

  /** 초안을 일기 한 편으로 저장하고 그 id 를 돌려준다. */
  fun commitDraft(): Long {
    val d = _draft.value
    val now = LocalDateTime.now()
    val existing = d.editingId?.let(::entry)
    val saved = DiaryEntry(
      id = existing?.id ?: System.currentTimeMillis(),
      date = existing?.date ?: d.date,
      createdAt = existing?.createdAt ?: now,
      mood = d.mood,
      tags = d.tags.toList(),
      memo = d.memo.trim(),
      title = d.title.trim(),
      body = d.body.trim(),
    )
    _entries.update { list -> (list.filterNot { it.id == saved.id } + saved).sortedByDescending { it.createdAt } }
    store.saveEntries(_entries.value)
    _draft.value = DiaryDraft()
    store.saveDraft(null)
    return saved.id
  }

  fun deleteEntry(id: Long) {
    _entries.update { list -> list.filterNot { it.id == id } }
    store.saveEntries(_entries.value)
  }

  // ---- 설정 ----

  fun setTheme(mode: ThemeMode) {
    store.themeMode = mode
    _settings.update { it.copy(themeMode = mode) }
  }

  fun setTextSize(size: TextSize) {
    store.textSize = size
    _settings.update { it.copy(textSize = size) }
  }

  fun setDragonColor(color: DragonColor) {
    store.dragonColor = color
    _settings.update { it.copy(dragonColor = color) }
  }

  fun setDragonHorn(horn: DragonHorn) {
    store.dragonHorn = horn
    _settings.update { it.copy(dragonHorn = horn) }
  }

  fun entriesOn(date: LocalDate): List<DiaryEntry> = _entries.value.filter { it.date == date }

  companion object {
    const val MEMO_LIMIT = 150
  }
}
