package com.dragondiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.data.TagCatalog
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

fun DiaryEntry.monthDay(): String = "${date.monthValue}월 ${date.dayOfMonth}일"
fun DiaryEntry.monthDayWeek(): String =
  "${monthDay()} (${date.dayOfWeek.getDisplayName(JTextStyle.SHORT, Locale.KOREAN)})"

/** 썸네일 자리 아이콘 (라이트 _5 은 감정에 따라 다른 아이콘을 쓴다) */
private fun DiaryEntry.thumbIcon() = when (mood?.tone) {
  MoodTone.Gold -> Icons.Outlined.WbTwilight
  MoodTone.Peach -> Icons.Outlined.LocalCafe
  else -> Icons.Outlined.AutoStories
}

/**
 * 홈 '최근 별밤 동화들'(_1) / '펼쳐진 일기 조각들'(_5) 의 한 줄 카드.
 */
@Composable
fun RecentEntryCard(entry: DiaryEntry, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val s = DiaryTheme.shapes
  if (c.isDark) {
    Row(
      Modifier
        .fillMaxWidth()
        .glow(s.listCard, c.shadow.copy(alpha = 0.5f), 8.dp, offsetY = 4.dp)
        .clip(s.listCard)
        .background(c.surfaceContainer)
        .clickable(onClick = onClick)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      ArtFrame(DiaryArt.storyFor(entry.id), Modifier.size(80.dp).clip(s.thumb)) {
        ThumbPlaceholder(icon = entry.thumbIcon(), label = null)
      }
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(entry.monthDayWeek(), style = t.labelSm, color = c.onSurfaceVariant, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
          entry.mood?.let {
            Pill("${it.label} ${it.emoji}", c.secondaryContainer.copy(alpha = 0.6f), c.secondary, horizontal = 8.dp)
          }
        }
        Text(entry.displayTitle, style = t.labelLg, color = c.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (entry.preview.isNotBlank()) {
          Text(entry.preview, style = t.bodySm, color = c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        val tags = entry.tags.take(2).mapNotNull(TagCatalog::hashtag)
        if (tags.isNotEmpty()) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tags.forEachIndexed { i, tag ->
              if (i > 0) Text("•", style = t.labelSm, color = c.onSurfaceVariant.copy(alpha = 0.6f))
              Text(tag, style = t.labelSm, color = c.gold, maxLines = 1)
            }
          }
        }
      }
    }
  } else {
    Row(
      Modifier
        .fillMaxWidth()
        .glow(s.listCard, c.shadow.copy(alpha = 0.06f), 14.dp, offsetY = 3.dp)
        .clip(s.listCard)
        .background(c.card)
        .border(1.dp, c.cardBorder, s.listCard)
        .clickable(onClick = onClick)
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      ArtFrame(DiaryArt.storyFor(entry.id), Modifier.size(80.dp).clip(s.thumb)) {
        ThumbPlaceholder(
          icon = entry.thumbIcon(),
          iconTint = entry.mood?.tone?.let { toneDot(it) } ?: c.toneViolet,
          label = "1:1 삽화\n(80x80)",
        )
      }
      Spacer(Modifier.width(14.dp))
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(entry.monthDay(), style = t.bodySm.copy(fontSize = 11.sp, fontFamily = t.headlineFamily), color = c.outline)
          entry.mood?.let { MoodPill("${it.short} ${it.shortEmoji}", it.tone) }
        }
        Spacer(Modifier.height(4.dp))
        Text(
          entry.displayTitle,
          style = t.titleMd.copy(fontFamily = t.headlineFamily, fontSize = 15.sp),
          color = c.onSurface,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        if (entry.preview.isNotBlank()) {
          Text(entry.preview, style = t.bodySm, color = c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        }
      }
      Icon(Icons.Outlined.ChevronRight, null, Modifier.padding(start = 4.dp).size(18.dp), tint = c.outlineVariant)
    }
  }
}

/** 라이트 테마 감정 배지 (`px-2 py-0.5 rounded-full text-[10px] font-semibold border`) */
@Composable
fun MoodPill(text: String, tone: MoodTone) {
  val (bg, fg) = toneColors(tone)
  val c = DiaryTheme.colors
  Pill(
    text,
    container = bg,
    content = fg,
    border = if (c.isDark) null else toneDot(tone).copy(alpha = 0.4f),
    style = DiaryTheme.type.labelSm.copy(fontSize = 10.sp, letterSpacing = 0.sp),
    horizontal = 8.dp,
  )
}
