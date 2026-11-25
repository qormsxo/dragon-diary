package com.dragondiary.app.ui.screens.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.Mood
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.IconOrb
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.MoodPill
import com.dragondiary.app.ui.components.NotificationButton
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.SmallIconBadge
import com.dragondiary.app.ui.components.ThumbPlaceholder
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.components.monthDay
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TIME = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)

data class ArchiveActions(
  val onOpen: (Long) -> Unit,
  val onDelete: (Long) -> Unit,
  val onWrite: () -> Unit,
)

/**
 * 기록 보관소 (_8). 월 선택, 감정 분류, 목록/격자 보기, 검색, 기록 삭제가 동작한다.
 */
@Composable
fun ArchiveScreen(entries: List<DiaryEntry>, actions: ArchiveActions, contentPadding: PaddingValues) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current

  var month by remember { mutableStateOf<YearMonth?>(null) } // null = 전체 기간
  var mood by remember { mutableStateOf<Mood?>(null) }
  var grid by rememberSaveable { mutableStateOf(false) }
  var searching by rememberSaveable { mutableStateOf(false) }
  var query by rememberSaveable { mutableStateOf("") }
  var pendingDelete by remember { mutableStateOf<DiaryEntry?>(null) }

  val inMonth = entries.filter { month == null || YearMonth.from(it.date) == month }
  val searched = if (query.isBlank()) inMonth else inMonth.filter {
    it.title.contains(query, true) || it.body.contains(query, true) || it.memo.contains(query, true)
  }
  val shown = searched.filter { mood == null || it.mood == mood }

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame(height = 56) {
      Icon(Icons.Outlined.AutoStories, null, Modifier.size(20.dp), tint = if (c.isDark) c.gold else c.primary)
      Spacer(Modifier.width(8.dp))
      Text("별밤용 일기", style = t.titleLg.copy(fontSize = 17.sp), color = c.primary, fontWeight = FontWeight.Bold)
      Text("  |  ", style = t.labelMd, color = c.outlineVariant)
      Text("기록 보관소", style = t.bodySm.copy(fontSize = 13.sp), color = c.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
      Spacer(Modifier.width(4.dp))
      Text(
        "(Grimoire Archive)",
        style = t.labelSm.copy(fontFamily = t.headlineFamily, fontSize = 11.sp, letterSpacing = 0.sp),
        color = c.outline,
        fontStyle = FontStyle.Italic,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
      )
      NotificationButton({ notice(SOON) })
      SmallIconBadge(Icons.Outlined.AutoFixHigh, actions.onWrite, "새 기록")
    }

    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(top = 12.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
      // 머리글
      Row(verticalAlignment = Alignment.CenterVertically) {
        Pill(
          "별밤 아카이브 마법서",
          container = c.surfaceContainer,
          content = if (c.isDark) c.gold else c.primary,
          border = c.gold.copy(alpha = 0.3f),
          icon = Icons.Outlined.Star,
          iconTint = c.gold,
          style = t.labelMd.copy(fontSize = 11.sp, letterSpacing = 0.sp),
          horizontal = 12.dp,
          vertical = 4.dp,
        )
        Spacer(Modifier.weight(1f))
        Box(
          Modifier.size(32.dp).clip(CircleShape).background(c.surfaceContainer).clickable {
            searching = !searching
            if (!searching) query = ""
          },
          contentAlignment = Alignment.Center,
        ) {
          Icon(if (searching) Icons.Outlined.Close else Icons.Outlined.Search, "기록 검색", Modifier.size(18.dp), tint = c.onSurfaceVariant)
        }
      }
      Text(
        "기록 보관소",
        style = t.headlineLg.copy(fontSize = 25.sp, lineHeight = 34.sp),
        color = c.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 6.dp),
      )
      Text(
        "차곡차곡 모인 나의 별밤용과 달빛 이야기들 (총 ${entries.size}편)",
        style = t.bodyMd.copy(fontSize = 13.sp),
        color = c.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
      )

      if (searching) {
        val shape = RoundedCornerShape(16.dp)
        Row(
          Modifier.padding(top = 12.dp).fillMaxWidth().clip(shape).background(c.surfaceContainerLow)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.5f), shape).padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Outlined.Search, null, Modifier.size(18.dp), tint = c.outline)
          Spacer(Modifier.width(8.dp))
          val style = t.bodyMd.copy(color = c.onSurface)
          BasicTextField(
            query, { query = it }, singleLine = true, textStyle = style, cursorBrush = SolidColor(c.gold),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
              if (query.isEmpty()) Text("제목이나 내용으로 찾기", style = style, color = c.outline)
              inner()
            },
          )
        }
      }

      // 월 선택 + 보기 전환
      Row(Modifier.padding(top = 20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        MonthPicker(month, entries) { month = it }
        Spacer(Modifier.weight(1f))
        Row(
          Modifier.clip(CircleShape).background(c.surfaceContainer).border(1.dp, c.outlineVariant.copy(alpha = 0.3f), CircleShape).padding(2.dp),
        ) {
          ViewToggle(Icons.AutoMirrored.Outlined.FormatListBulleted, "목록 보기", !grid) { grid = false }
          ViewToggle(Icons.Outlined.GridView, "격자 보기", grid) { grid = true }
        }
      }

      // 감정 분류
      Row(
        Modifier.padding(top = 12.dp).fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FilterChip("전체 (${searched.size})", mood == null) { mood = null }
        Mood.entries.forEach { m ->
          val n = searched.count { it.mood == m }
          if (n > 0 || mood == m) FilterChip("${m.short} ($n) ${m.shortEmoji}", mood == m) { mood = if (mood == m) null else m }
        }
      }

      Spacer(Modifier.height(20.dp))
      when {
        shown.isEmpty() -> EmptyArchive(hasAny = entries.isNotEmpty(), onWrite = actions.onWrite)
        grid -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          shown.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              pair.forEach { GridCard(it, Modifier.weight(1f)) { actions.onOpen(it.id) } }
              if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
          }
        }
        else -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          shown.forEach { e -> ArchiveCard(e, { actions.onOpen(e.id) }, { pendingDelete = e }) }
        }
      }

      Spacer(Modifier.height(24.dp))
      TipCard()
    }
  }

  pendingDelete?.let { e ->
    AlertDialog(
      onDismissRequest = { pendingDelete = null },
      title = { Text("이 기록을 지울까요?", style = t.headlineSm, color = c.onSurface) },
      text = { Text("'${e.displayTitle}' 기록이 기기에서 지워지고 되돌릴 수 없어요.", style = t.bodyMd, color = c.onSurfaceVariant) },
      confirmButton = {
        TextButton(onClick = { actions.onDelete(e.id); pendingDelete = null }) { Text("지우기", color = c.error) }
      },
      dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("그대로 두기", color = c.onSurfaceVariant) } },
      containerColor = c.surfaceContainerLow,
    )
  }
}

@Composable
private fun MonthPicker(month: YearMonth?, entries: List<DiaryEntry>, onPick: (YearMonth?) -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  var open by remember { mutableStateOf(false) }
  val months = (entries.map { YearMonth.from(it.date) } + YearMonth.now()).distinct().sortedDescending()
  Box {
    Row(
      Modifier.glow(CircleShape, c.shadow.copy(alpha = 0.08f), 2.dp, offsetY = 1.dp)
        .clip(CircleShape).background(c.surfaceContainerLow).border(1.dp, c.outlineVariant.copy(alpha = 0.4f), CircleShape)
        .clickable { open = true }.padding(horizontal = 14.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(18.dp), tint = c.gold)
      Spacer(Modifier.width(6.dp))
      Text(
        month?.let { "${it.year}년 ${it.monthValue}월" } ?: "전체 기간",
        style = t.labelLg.copy(fontFamily = t.headlineFamily, fontSize = 13.sp),
        color = c.onSurface,
      )
      Icon(Icons.Outlined.ArrowDropDown, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
    }
    DropdownMenu(open, { open = false }, containerColor = c.surfaceContainerLow) {
      DropdownMenuItem({ Text("전체 기간", color = c.onSurface) }, { onPick(null); open = false })
      months.forEach { m ->
        DropdownMenuItem({ Text("${m.year}년 ${m.monthValue}월", color = c.onSurface) }, { onPick(m); open = false })
      }
    }
  }
}

@Composable
private fun ViewToggle(icon: ImageVector, desc: String, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  Box(
    Modifier.size(28.dp).clip(CircleShape)
      .background(if (selected) c.surfaceContainerLowest else c.surfaceContainer)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) { Icon(icon, desc, Modifier.size(16.dp), tint = if (selected) c.primary.let { if (c.isDark) c.gold else it } else c.onSurfaceVariant) }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Box(
    Modifier.clip(CircleShape)
      .background(if (selected) c.primaryContainer else c.surfaceContainerLowest.let { if (c.isDark) c.surfaceContainer else it })
      .border(1.dp, if (selected) c.primaryContainer else c.outlineVariant.copy(alpha = 0.4f), CircleShape)
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 5.dp),
  ) {
    Text(
      text,
      style = t.labelMd.copy(fontSize = 12.sp, letterSpacing = 0.sp),
      color = if (selected) c.onPrimaryContainer.let { if (c.isDark) c.onPrimaryFixed else it } else c.onSurfaceVariant,
      fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
    )
  }
}

@Composable
private fun ArchiveCard(e: DiaryEntry, onClick: () -> Unit, onDelete: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  var menu by remember { mutableStateOf(false) }
  Row(
    Modifier.fillMaxWidth()
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.4f else 0.05f), 10.dp, offsetY = 2.dp)
      .clip(shape)
      .background(c.card)
      .border(1.dp, c.cardBorder, shape)
      .clickable(onClick = onClick)
      .padding(14.dp),
  ) {
    ArtFrame(DiaryArt.storyFor(e.id), Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))) {
      ThumbPlaceholder(iconTint = c.outline, shape = RoundedCornerShape(12.dp), dashed = true)
    }
    Spacer(Modifier.width(14.dp))
    Column(Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(e.monthDay(), style = t.labelLg.copy(fontFamily = t.headlineFamily, fontSize = 12.sp, letterSpacing = 0.sp), color = c.onSurface)
        Spacer(Modifier.width(6.dp))
        e.mood?.let { MoodPill("${it.short} ${it.shortEmoji}", it.tone) }
        Spacer(Modifier.weight(1f))
        Box {
          Icon(
            Icons.Outlined.MoreHoriz, "기록 옵션",
            Modifier.clip(CircleShape).clickable { menu = true }.padding(2.dp).size(16.dp),
            tint = c.outlineVariant,
          )
          DropdownMenu(menu, { menu = false }, containerColor = c.surfaceContainerLow) {
            DropdownMenuItem({ Text("열어보기", color = c.onSurface) }, { menu = false; onClick() })
            DropdownMenuItem({ Text("기록 지우기", color = c.error) }, { menu = false; onDelete() })
          }
        }
      }
      Text(
        e.displayTitle,
        style = t.headlineSm.copy(fontSize = 15.sp, lineHeight = 21.sp),
        color = c.onSurface,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 4.dp),
      )
      if (e.preview.isNotBlank()) {
        Text(
          e.preview,
          style = t.bodySm.copy(lineHeight = 19.sp),
          color = c.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(top = 2.dp),
        )
      }
      Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Draw, null, Modifier.size(13.dp), tint = c.gold)
        Spacer(Modifier.width(2.dp))
        Text("${e.charCount}자", style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = c.outline, fontWeight = FontWeight.Normal)
        Text("  •  ", style = t.labelSm.copy(fontSize = 10.sp), color = c.outlineVariant)
        Text(e.createdAt.format(TIME), style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = c.outline, fontWeight = FontWeight.Normal)
      }
    }
  }
}

@Composable
private fun GridCard(e: DiaryEntry, modifier: Modifier, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  Column(
    modifier
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.4f else 0.05f), 10.dp, offsetY = 2.dp)
      .clip(shape).background(c.card).border(1.dp, c.cardBorder, shape)
      .clickable(onClick = onClick).padding(10.dp),
  ) {
    ArtFrame(DiaryArt.storyFor(e.id), Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp))) {
      ThumbPlaceholder(iconTint = c.outline, shape = RoundedCornerShape(12.dp), dashed = true)
    }
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
      Text(e.monthDay(), style = t.labelLg.copy(fontFamily = t.headlineFamily, fontSize = 12.sp), color = c.onSurface, modifier = Modifier.weight(1f))
      e.mood?.let { MoodPill(it.shortEmoji, it.tone) }
    }
    Text(
      e.displayTitle,
      style = t.headlineSm.copy(fontSize = 14.sp, lineHeight = 20.sp),
      color = c.onSurface,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.padding(top = 2.dp),
    )
  }
}

@Composable
private fun EmptyArchive(hasAny: Boolean, onWrite: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  Column(
    Modifier.fillMaxWidth().clip(shape).background(c.card).border(1.dp, c.cardBorder, shape).padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    IconOrb(Icons.AutoMirrored.Outlined.MenuBook, 48.dp, 24.dp, c.surfaceContainer, c.gold)
    Text(
      if (hasAny) "조건에 맞는 기록이 없어요" else "아직 보관된 이야기가 없어요",
      style = t.titleMd.copy(fontFamily = t.headlineFamily),
      color = c.onSurface,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(top = 12.dp),
    )
    Text(
      if (hasAny) "다른 달이나 감정을 골라보세요." else "오늘 밤 첫 기록을 남기면 이곳에 차곡차곡 모여요.",
      style = t.bodySm,
      color = c.onSurfaceVariant,
      modifier = Modifier.padding(top = 4.dp),
    )
    if (!hasAny) {
      Box(
        Modifier.padding(top = 14.dp).clip(CircleShape).background(c.primaryContainer).clickable(onClick = onWrite)
          .padding(horizontal = 16.dp, vertical = 8.dp),
      ) {
        Text("첫 기록 남기기", style = t.labelLg, color = if (c.isDark) c.onPrimaryFixed else c.onPrimaryContainer)
      }
    }
  }
}

@Composable
private fun TipCard() {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  Row(
    Modifier.fillMaxWidth()
      .glow(shape, c.glow.copy(alpha = 0.12f), 16.dp, offsetY = 4.dp)
      .clip(shape).background(c.surfaceContainerLow)
      .border(1.dp, c.cardBorder.let { if (c.isDark) it else c.primaryFixed.copy(alpha = 0.6f) }, shape)
      .padding(16.dp),
    verticalAlignment = Alignment.Top,
  ) {
    IconOrb(Icons.AutoMirrored.Outlined.MenuBook, 36.dp, 20.dp, c.gold.copy(alpha = 0.2f), if (c.isDark) c.gold else c.primary, border = c.gold.copy(alpha = 0.4f))
    Spacer(Modifier.width(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("동화책 만들기 팁", style = t.titleLg.copy(fontSize = 13.sp, lineHeight = 18.sp), color = c.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(6.dp))
        Box(Modifier.size(4.dp).clip(CircleShape).background(c.outlineVariant))
        Spacer(Modifier.width(6.dp))
        Text("별밤용의 전언", style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = c.onSurfaceVariant)
      }
      Text(
        "하루에 한 편씩 일기를 기록하면, 계절마다 나만의 별밤용 동화책으로 엮을 수 있어요 📖",
        style = t.bodySm.copy(lineHeight = 19.sp),
        color = c.onSurfaceVariant,
      )
    }
  }
}
