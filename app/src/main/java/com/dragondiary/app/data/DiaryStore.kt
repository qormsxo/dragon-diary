package com.dragondiary.app.data

import android.content.Context
import com.dragondiary.app.ui.theme.ThemeMode
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 기기 안에만 저장하는 임시 저장소 (SharedPreferences + JSON).
 * 이번 단계는 UI 구현이 목적이라 DB 없이 최소한으로 둔다.
 * 나중에 Room 등으로 바꿀 때는 이 클래스만 교체하면 된다.
 */
class DiaryStore(context: Context) {
  private val prefs = context.getSharedPreferences("dragon_diary", Context.MODE_PRIVATE)

  fun loadEntries(): List<DiaryEntry> {
    val raw = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
    return runCatching {
      val arr = JSONArray(raw)
      (0 until arr.length()).map { entryFromJson(arr.getJSONObject(it)) }
    }.getOrDefault(emptyList())
  }

  fun saveEntries(entries: List<DiaryEntry>) {
    val arr = JSONArray()
    entries.forEach { arr.put(entryToJson(it)) }
    prefs.edit().putString(KEY_ENTRIES, arr.toString()).apply()
  }

  fun loadDraft(): DiaryDraft? {
    val raw = prefs.getString(KEY_DRAFT, null) ?: return null
    return runCatching {
      val o = JSONObject(raw)
      DiaryDraft(
        editingId = if (o.has("editingId")) o.getLong("editingId") else null,
        date = LocalDate.parse(o.getString("date")),
        mood = o.optString("mood").takeIf { it.isNotEmpty() }?.let { runCatching { Mood.valueOf(it) }.getOrNull() },
        tags = o.optJSONArray("tags")?.let { a -> (0 until a.length()).map { a.getString(it) }.toSet() } ?: emptySet(),
        memo = o.optString("memo"),
        title = o.optString("title"),
        body = o.optString("body"),
        savedAt = o.optString("savedAt").takeIf { it.isNotEmpty() }?.let(LocalDateTime::parse),
      )
    }.getOrNull()
  }

  fun saveDraft(draft: DiaryDraft?) {
    if (draft == null) {
      prefs.edit().remove(KEY_DRAFT).apply()
      return
    }
    val o = JSONObject()
    draft.editingId?.let { o.put("editingId", it) }
    o.put("date", draft.date.toString())
    o.put("mood", draft.mood?.name ?: "")
    o.put("tags", JSONArray(draft.tags.toList()))
    o.put("memo", draft.memo)
    o.put("title", draft.title)
    o.put("body", draft.body)
    o.put("savedAt", draft.savedAt?.toString() ?: "")
    prefs.edit().putString(KEY_DRAFT, o.toString()).apply()
  }

  var themeMode: ThemeMode
    get() = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, null) ?: "") }.getOrDefault(ThemeMode.Light)
    set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

  var textSize: TextSize
    get() = runCatching { TextSize.valueOf(prefs.getString(KEY_TEXT, null) ?: "") }.getOrDefault(TextSize.Normal)
    set(value) = prefs.edit().putString(KEY_TEXT, value.name).apply()

  var dragonColor: DragonColor
    get() = runCatching { DragonColor.valueOf(prefs.getString(KEY_COLOR, null) ?: "") }.getOrDefault(DragonColor.Amber)
    set(value) = prefs.edit().putString(KEY_COLOR, value.name).apply()

  var dragonHorn: DragonHorn
    get() = runCatching { DragonHorn.valueOf(prefs.getString(KEY_HORN, null) ?: "") }.getOrDefault(DragonHorn.Constellation)
    set(value) = prefs.edit().putString(KEY_HORN, value.name).apply()

  private fun entryToJson(e: DiaryEntry) = JSONObject().apply {
    put("id", e.id)
    put("date", e.date.toString())
    put("createdAt", e.createdAt.toString())
    put("mood", e.mood?.name ?: "")
    put("tags", JSONArray(e.tags))
    put("memo", e.memo)
    put("title", e.title)
    put("body", e.body)
  }

  private fun entryFromJson(o: JSONObject) = DiaryEntry(
    id = o.getLong("id"),
    date = LocalDate.parse(o.getString("date")),
    createdAt = LocalDateTime.parse(o.getString("createdAt")),
    mood = o.optString("mood").takeIf { it.isNotEmpty() }?.let { runCatching { Mood.valueOf(it) }.getOrNull() },
    tags = o.optJSONArray("tags")?.let { a -> (0 until a.length()).map { a.getString(it) } } ?: emptyList(),
    memo = o.optString("memo"),
    title = o.optString("title"),
    body = o.optString("body"),
  )

  private companion object {
    const val KEY_ENTRIES = "entries"
    const val KEY_DRAFT = "draft"
    const val KEY_THEME = "theme"
    const val KEY_TEXT = "text_size"
    const val KEY_COLOR = "dragon_color"
    const val KEY_HORN = "dragon_horn"
  }
}
