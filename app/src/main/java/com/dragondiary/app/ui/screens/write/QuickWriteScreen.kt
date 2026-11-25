package com.dragondiary.app.ui.screens.write

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.AutoFixNormal
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.HotelClass
import androidx.compose.material.icons.outlined.Interests
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryDraft
import com.dragondiary.app.data.Mood
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.data.TagCatalog
import com.dragondiary.app.data.TagGroup
import com.dragondiary.app.ui.AppViewModel
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.ArtPlace
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.AmbientBlob
import com.dragondiary.app.ui.components.BackButton
import com.dragondiary.app.ui.components.IconOrb
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.PrimaryCta
import com.dragondiary.app.ui.components.ProfileBadge
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.SmallIconBadge
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** 작성 화면 동작 */
data class WriteActions(
  val onBack: () -> Unit,
  val onMood: (Mood) -> Unit,
  val onTag: (String) -> Unit,
  val onMemo: (String) -> Unit,
  val onTitle: (String) -> Unit,
  val onBody: (String) -> Unit,
  val onGenerate: () -> Unit,
  val onSaveForLater: () -> Unit,
  val onFreeMode: () -> Unit,
)

fun LocalDate.nightLabel(): String =
  "${year}년 ${monthValue}월 ${dayOfMonth}일 ${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)} 밤"

/** 고른 조각 수 */
private val DiaryDraft.pieceCount get() = (if (mood != null) 1 else 0) + tags.size

/** 속삭임에 넣을 조각 이름 (최대 3개) */
private fun DiaryDraft.piecePhrase(dark: Boolean): String = tags.take(3)
  .mapNotNull { id -> TagCatalog.find(id)?.let { if (dark) it.darkLabel ?: it.lightLabel else it.lightLabel }?.substringAfter(' ') }
  .ifEmpty { listOfNotNull(mood?.label) }
  .joinToString(", ")

/**
 * 간편 기록 화면 — 다크(_2) / 라이트(_4).
 * 마음 빛깔(단일 선택) + 조각(다중 선택) + 한 줄 메모를 받는다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickWriteScreen(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val dark = c.isDark
  val notice = LocalNotice.current

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BackButton(actions.onBack)
      Spacer(Modifier.width(4.dp))
      if (dark) {
        Text("Diary Entry", style = t.headlineSm, color = c.onSurface, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        ProfileBadge { notice(SOON) }
      } else {
        Text("별밤 일기장", style = t.headlineMd.copy(fontSize = 20.sp), color = c.ink, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(6.dp))
        Text("Grimoire", style = t.bodySm.copy(fontSize = 13.sp), color = c.secondary, modifier = Modifier.weight(1f))
        SmallIconBadge(Icons.Outlined.AutoStories, actions.onFreeMode, "자유 모드")
      }
    }

    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(horizontal = if (dark) 20.dp else 16.dp)
        .padding(top = 16.dp, bottom = 16.dp)
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.spacedBy(if (dark) 20.dp else 16.dp),
    ) {
      IntroCard(draft.date)
      MoodSection(draft, actions)
      TagSection(draft, actions)
      MemoSection(draft, actions)
      WhisperCard(draft)
      BannerSlot()
      Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = if (dark) 8.dp else 4.dp)) {
        PrimaryCta(
          if (dark) "오늘의 동화 장면 생성하기" else "오늘의 동화 장면 빚기",
          actions.onGenerate,
          icon = Icons.Outlined.AutoAwesome,
          enabled = !draft.isEmpty,
          shape = CircleShape,
          height = if (dark) 60.dp else 54.dp,
          textStyle = if (dark) t.headlineSm.copy(fontWeight = FontWeight.Bold) else t.headlineSm.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
          TextLink(
            if (dark) "긴 글로 작성하기 (자유 모드)" else "자유 긴 글 모드로 쓰기",
            if (dark) Icons.AutoMirrored.Outlined.MenuBook else Icons.Outlined.HistoryEdu,
            c.secondary,
            actions.onFreeMode,
          )
          Spacer(Modifier.weight(1f))
          TextLink("임시 보관", Icons.Outlined.BookmarkBorder, c.outline, actions.onSaveForLater)
        }
      }
    }
  }
}

@Composable
private fun TextLink(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
  Row(
    Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(vertical = 4.dp, horizontal = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(icon, null, Modifier.size(16.dp), tint = color)
    Spacer(Modifier.width(4.dp))
    Text(text, style = DiaryTheme.type.labelMd.copy(fontSize = if (DiaryTheme.isDark) 12.sp else 12.sp), color = color)
  }
}

@Composable
private fun IntroCard(date: LocalDate) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  if (c.isDark) {
    Box(
      Modifier.fillMaxWidth()
        .glow(DiaryTheme.shapes.section, c.shadow.copy(alpha = 0.6f), 24.dp, offsetY = 10.dp)
        .clip(DiaryTheme.shapes.section)
        .background(c.surfaceContainer),
    ) {
      AmbientBlob(c.gold.copy(alpha = 0.10f), 128.dp, 24.dp, (-24).dp, alignRight = true)
      AmbientBlob(c.secondaryContainer.copy(alpha = 0.35f), 112.dp, (-24).dp, 24.dp, alignBottom = true)
      Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Bedtime, null, Modifier.size(16.dp), tint = c.primary)
          Spacer(Modifier.width(6.dp))
          Text(date.nightLabel(), style = t.labelMd, color = c.onSurfaceVariant, modifier = Modifier.weight(1f))
          Pill("간편 기록 모드", c.secondaryContainer.copy(alpha = 0.6f), c.secondary, icon = Icons.Outlined.AutoAwesome, iconTint = c.primary, vertical = 4.dp)
        }
        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Top) {
          IconOrb(Icons.Outlined.AutoAwesome, 40.dp, 22.dp, c.gold.copy(alpha = 0.2f), c.primary, glowColor = c.glow.copy(alpha = 0.2f))
          Spacer(Modifier.width(12.dp))
          Column {
            Text("오늘 하루는 어땠나요?", style = t.headlineSm, color = c.primary, fontWeight = FontWeight.SemiBold)
            Text("가볍게 톡톡 선택하면, 아기 수호룡이 포근한 동화로 엮어줄게요 ✨", style = t.bodySm, color = c.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
          }
        }
      }
    }
  } else {
    val shape = RoundedCornerShape(24.dp)
    Box(
      Modifier.fillMaxWidth()
        .glow(shape, c.shadow.copy(alpha = 0.08f), 8.dp, offsetY = 2.dp)
        .clip(shape)
        .background(Brush.linearGradient(listOf(c.primaryFixed.copy(alpha = 0.35f), c.surfaceContainerLow, c.secondaryFixed.copy(alpha = 0.4f))))
        .border(1.dp, c.outlineVariant, shape),
    ) {
      AmbientBlob(c.gold.copy(alpha = 0.15f), 112.dp, 32.dp, (-32).dp, alignRight = true)
      AmbientBlob(c.secondaryContainer.copy(alpha = 0.3f), 96.dp, (-24).dp, 24.dp, alignBottom = true)
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.HotelClass, null, Modifier.size(16.dp), tint = c.primary)
          Spacer(Modifier.width(6.dp))
          Text(date.nightLabel(), style = t.labelMd.copy(fontSize = 12.sp), color = c.onSurfaceVariant, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
          Pill(
            "간편 기록 모드", c.secondaryContainer.copy(alpha = 0.4f), c.secondary,
            border = c.secondary.copy(alpha = 0.2f), icon = Icons.Outlined.AutoAwesome, iconTint = c.primary,
          )
        }
        Row(verticalAlignment = Alignment.Top) {
          IconOrb(
            Icons.AutoMirrored.Outlined.MenuBook, 40.dp, 22.dp, c.surfaceContainerLow, c.primary,
            border = c.gold.copy(alpha = 0.5f), glowColor = c.glow.copy(alpha = 0.18f),
          )
          Spacer(Modifier.width(12.dp))
          Column {
            Text("오늘 밤, 어떤 기억을 수놓을까요?", style = t.headlineSm, color = c.ink, fontWeight = FontWeight.SemiBold)
            Text(
              "마음에 드는 빛깔과 조각을 톡톡 얹으면, 아기 별지기 용이 따스한 동화로 빚어드려요 ✨",
              style = t.bodySm.copy(fontSize = 13.sp, lineHeight = 19.sp),
              color = c.onSurfaceVariant,
              modifier = Modifier.padding(top = 4.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, trailing: String, iconTint: Color) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, null, Modifier.size(20.dp), tint = iconTint)
    Spacer(Modifier.width(8.dp))
    Text(
      title,
      style = if (c.isDark) t.headlineSm else t.titleLg.copy(fontSize = 17.sp),
      color = if (c.isDark) c.onSurface else c.ink,
      modifier = Modifier.weight(1f),
    )
    Text(trailing, style = t.labelSm.copy(fontSize = 11.sp), color = c.outline, fontWeight = FontWeight.Medium)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoodSection(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  Column(verticalArrangement = Arrangement.spacedBy(if (c.isDark) 12.dp else 10.dp)) {
    SectionTitle(
      "오늘 나의 마음 빛깔",
      if (c.isDark) Icons.Outlined.AutoFixHigh else Icons.Outlined.AutoAwesome,
      if (draft.mood == null) "하나를 골라주세요" else "1개 선택",
      c.primary,
    )
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(if (c.isDark) 10.dp else 8.dp),
      verticalArrangement = Arrangement.spacedBy(if (c.isDark) 10.dp else 8.dp),
    ) {
      Mood.entries.forEach { mood -> MoodChip(mood, draft.mood == mood) { actions.onMood(mood) } }
    }
  }
}

@Composable
private fun MoodChip(mood: Mood, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = CircleShape
  val mod = if (c.isDark) {
    Modifier
      .then(if (selected) Modifier.glow(shape, c.glow.copy(alpha = 0.3f), 18.dp) else Modifier)
      .clip(shape)
      .background(if (selected) c.surfaceContainerHigh else c.surfaceContainer)
      .padding(horizontal = 16.dp, vertical = 10.dp)
  } else {
    Modifier
      .then(if (selected) Modifier.glow(shape, c.glow.copy(alpha = 0.22f), 12.dp, offsetY = 2.dp) else Modifier)
      .clip(shape)
      .background(if (selected) c.primaryFixed.copy(alpha = 0.3f).compositeOn(c.surfaceContainerLowest) else c.surfaceContainerLowest)
      .border(1.dp, if (selected) c.gold.copy(alpha = 0.8f) else c.outlineVariant.copy(alpha = 0.6f), shape)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  }
  Row(
    Modifier.clip(shape).clickable(onClick = onClick).then(mod),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(mood.emoji, fontSize = 16.sp)
    Text(
      mood.label,
      style = t.labelLg,
      color = when {
        !selected -> c.onSurfaceVariant
        c.isDark -> c.primary
        else -> c.onPrimaryContainer
      },
      fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
    )
    if (selected) Icon(Icons.Outlined.Star, null, Modifier.size(16.dp), tint = c.primary)
  }
}

/** 반투명 색을 바탕색 위에 미리 합성 (Tailwind 의 bg-x/30 위에 다른 bg 를 겹친 효과) */
private fun Color.compositeOn(base: Color): Color {
  val a = alpha
  return Color(red * a + base.red * (1 - a), green * a + base.green * (1 - a), blue * a + base.blue * (1 - a), 1f)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSection(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = if (c.isDark) DiaryTheme.shapes.section else RoundedCornerShape(24.dp)
  Column(
    Modifier.fillMaxWidth()
      .then(if (c.isDark) Modifier.glow(shape, c.shadow.copy(alpha = 0.5f), 16.dp, offsetY = 8.dp) else Modifier)
      .clip(shape)
      .background(if (c.isDark) c.surfaceContainer else c.surfaceContainerLow)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.outlineVariant.copy(alpha = 0.7f), shape))
      .padding(if (c.isDark) 20.dp else 16.dp),
    verticalArrangement = Arrangement.spacedBy(if (c.isDark) 16.dp else 14.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        if (c.isDark) Icons.Outlined.Interests else Icons.Outlined.AutoFixNormal, null,
        Modifier.size(20.dp), tint = c.secondary,
      )
      Spacer(Modifier.width(if (c.isDark) 8.dp else 6.dp))
      Text(
        if (c.isDark) "무슨 일이 있었나요?" else "오늘 무슨 일이 있었나요?",
        style = if (c.isDark) t.headlineSm else t.titleLg.copy(fontSize = 17.sp),
        color = if (c.isDark) c.onSurface else c.ink,
        modifier = Modifier.weight(1f),
      )
      if (c.isDark) Pill("다중 선택", c.surfaceContainerHighest, c.secondaryFixed, horizontal = 8.dp)
      else Pill("동화 조각 선택", c.surfaceContainerHigh, c.secondary, style = t.labelSm.copy(letterSpacing = 0.sp), horizontal = 8.dp)
    }
    if (!c.isDark) Box(Modifier.fillMaxWidth().height(1.dp).background(c.surfaceContainerHighest))
    TagCatalog.groups.forEach { group -> TagGroupView(group, draft.tags, actions.onTag) }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagGroupView(group: TagGroup, selected: Set<String>, onTag: (String) -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val tags = group.tags.filter { !c.isDark || it.darkLabel != null }
  val (dot, titleColor) = when (group.tone) {
    MoodTone.Gold -> c.gold to (if (c.isDark) c.outline else c.primary)
    MoodTone.Violet -> c.secondary to (if (c.isDark) c.outline else c.secondary)
    MoodTone.Peach -> (if (c.isDark) c.tertiary else c.primary) to (if (c.isDark) c.outline else c.tertiary)
  }
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(Modifier.padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
      Box(Modifier.size(6.dp).clip(CircleShape).background(dot))
      Spacer(Modifier.width(6.dp))
      Text(
        if (c.isDark) group.darkTitle else group.lightTitle,
        style = if (c.isDark) t.labelMd else t.labelMd.copy(fontSize = 12.sp),
        color = titleColor,
        fontWeight = if (c.isDark) FontWeight.SemiBold else FontWeight.Bold,
      )
    }
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(if (c.isDark) 8.dp else 6.dp),
      verticalArrangement = Arrangement.spacedBy(if (c.isDark) 8.dp else 6.dp),
    ) {
      tags.forEach { tag ->
        TagChip(
          if (c.isDark) tag.darkLabel!! else tag.lightLabel,
          tag.id in selected,
          violet = group.tone == MoodTone.Violet,
        ) { onTag(tag.id) }
      }
    }
  }
}

@Composable
private fun TagChip(label: String, selected: Boolean, violet: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = CircleShape
  val (bg, fg, border) = when {
    c.isDark && selected -> Triple(c.surfaceContainerHighest, c.primary, null)
    c.isDark -> Triple(c.surfaceContainerLow, c.onSurfaceVariant, null)
    selected && violet -> Triple(c.secondaryFixed.copy(alpha = 0.45f).compositeOn(c.surfaceContainerLowest), c.onToneVioletContainer, c.secondaryContainer)
    selected -> Triple(c.primaryFixed.copy(alpha = 0.3f).compositeOn(c.surfaceContainerLowest), c.onPrimaryContainer, c.gold.copy(alpha = 0.7f))
    else -> Triple(c.surfaceContainerLowest, c.onSurfaceVariant, c.outlineVariant.copy(alpha = 0.6f))
  }
  Row(
    Modifier
      .then(if (selected && c.isDark) Modifier.glow(shape, c.glow.copy(alpha = 0.22f), 12.dp) else Modifier)
      .clip(shape)
      .background(bg)
      .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
      .clickable(onClick = onClick)
      .padding(horizontal = if (c.isDark) 14.dp else 12.dp, vertical = if (c.isDark) 8.dp else 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Text(
      label,
      style = if (c.isDark) t.bodySm else t.bodySm.copy(fontSize = 13.sp),
      color = fg,
      fontWeight = if (selected && !c.isDark) FontWeight.Medium else null,
    )
    if (selected) {
      Icon(
        Icons.Outlined.Check, null, Modifier.size(14.dp),
        tint = if (c.isDark) c.primary else if (violet) c.secondary else c.primary,
      )
    }
  }
}

@Composable
private fun MemoSection(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    SectionTitle(
      if (c.isDark) "남기고 싶은 한 줄 메모" else "일기장 여백에 남기는 짧은 메모",
      Icons.Outlined.EditNote,
      "선택사항",
      c.primary,
    )
    val shape = if (c.isDark) DiaryTheme.shapes.section else RoundedCornerShape(24.dp)
    Column(
      Modifier.fillMaxWidth()
        .then(if (c.isDark) Modifier.glow(shape, c.shadow.copy(alpha = 0.4f), 8.dp, offsetY = 4.dp) else Modifier)
        .clip(shape)
        .background(if (c.isDark) c.surfaceContainerLow else c.surfaceContainerLow.copy(alpha = 0.6f).compositeOn(c.surfaceContainerLowest))
        .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.outlineVariant, shape))
        .padding(if (c.isDark) 16.dp else 14.dp),
    ) {
      Box {
        val textStyle = if (c.isDark) t.bodyMd.copy(color = c.onSurface)
        else t.bodyMd.copy(color = c.ink, fontFamily = t.headlineFamily, lineHeight = 24.sp)
        BasicTextField(
          value = draft.memo,
          onValueChange = actions.onMemo,
          textStyle = textStyle,
          cursorBrush = SolidColor(c.gold),
          modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp).padding(end = if (c.isDark) 0.dp else 24.dp),
          decorationBox = { inner ->
            if (draft.memo.isEmpty()) {
              Text(
                "퇴근길에 들른 작은 서점에서 따스한 바닐라 티를 마셨어. 오늘 밤하늘에 별이 참 밝더라.",
                style = textStyle,
                color = c.outline.copy(alpha = 0.7f),
              )
            }
            inner()
          },
        )
        if (!c.isDark) Icon(Icons.Outlined.Draw, null, Modifier.align(Alignment.TopEnd).size(18.dp), tint = c.secondary.copy(alpha = 0.3f))
      }
      if (!c.isDark) Box(Modifier.padding(top = 6.dp).fillMaxWidth().height(1.dp).background(c.surfaceContainerHighest.copy(alpha = 0.8f)))
      Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Lightbulb, null, Modifier.size(14.dp), tint = if (c.isDark) c.outline else c.primary)
        Spacer(Modifier.width(6.dp))
        Text(
          if (c.isDark) "선택한 키워드만으로도 충분해요!" else "키워드 조각만으로도 예쁜 동화가 지어져요",
          style = t.labelSm.copy(fontSize = 11.sp),
          color = if (c.isDark) c.outline else c.onSurfaceVariant,
          modifier = Modifier.weight(1f),
        )
        Text("${draft.memo.length} / ${AppViewModel.MEMO_LIMIT}", style = t.labelSm.copy(fontSize = 11.sp), color = c.outline)
      }
    }
  }
}

@Composable
private fun WhisperCard(draft: DiaryDraft) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val count = draft.pieceCount
  val phrase = draft.piecePhrase(c.isDark)
  val shape = if (c.isDark) DiaryTheme.shapes.section else RoundedCornerShape(24.dp)
  val accent = SpanStyle(
    color = c.primary,
    fontWeight = FontWeight.SemiBold,
    textDecoration = if (c.isDark) null else TextDecoration.Underline,
  )
  Row(
    Modifier.fillMaxWidth()
      .then(if (c.isDark) Modifier.glow(shape, c.shadow.copy(alpha = 0.5f), 16.dp, offsetY = 8.dp) else Modifier)
      .clip(shape)
      .background(
        if (c.isDark) Brush.horizontalGradient(listOf(c.secondaryContainer.copy(alpha = 0.4f).compositeOn(c.surfaceContainer), c.surfaceContainerHigh, c.surfaceContainer))
        else Brush.horizontalGradient(listOf(c.primaryFixed.copy(alpha = 0.25f).compositeOn(c.background), c.surfaceContainerLow, c.secondaryFixed.copy(alpha = 0.5f).compositeOn(c.background))),
      )
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.gold.copy(alpha = 0.4f), shape))
      .padding(if (c.isDark) 16.dp else 14.dp),
    verticalAlignment = Alignment.Top,
  ) {
    if (c.isDark) {
      IconOrb(Icons.Outlined.AutoFixHigh, 44.dp, 24.dp, c.secondary, c.onSecondary, glowColor = c.secondary.copy(alpha = 0.35f))
    } else {
      IconOrb(Icons.Outlined.AutoAwesome, 40.dp, 22.dp, c.secondaryContainer.copy(alpha = 0.8f), c.secondary, border = c.secondary.copy(alpha = 0.3f))
    }
    Spacer(Modifier.width(if (c.isDark) 14.dp else 12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          "아기 용 별지기의 속삭임",
          style = if (c.isDark) t.labelMd else t.titleMd.copy(fontFamily = t.headlineFamily, fontSize = 14.sp),
          color = c.primary,
          fontWeight = if (c.isDark) FontWeight.SemiBold else FontWeight.Bold,
        )
        Spacer(Modifier.width(6.dp))
        Box(Modifier.size(6.dp).clip(CircleShape).background(c.gold))
      }
      Text(
        buildAnnotatedString {
          if (count == 0) {
            append("마음 빛깔이나 조각을 하나 골라주시면, 그 조각으로 포근한 수채화 동화 장면을 그릴 준비를 할게요.")
          } else {
            append(if (c.isDark) "선택한 ${count}개의 조각으로 " else "고른 ${count}가지 빛깔로 ")
            withStyle(accent) { append("\"$phrase\"") }
            append(" 이야기가 담긴 포근한 수채화 동화 장면을 그릴 준비가 되었어요.")
          }
        },
        style = if (c.isDark) t.bodySm.copy(lineHeight = 18.sp) else t.bodySm.copy(fontSize = 12.5.sp, lineHeight = 20.sp),
        color = if (c.isDark) c.onSurface else c.onSurfaceVariant,
      )
    }
  }
}

/** 하단 수채화 배너 자리 (원본은 흐릿한 배경 사진 + 가운데 알약 문구) */
@Composable
private fun BannerSlot() {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = if (c.isDark) DiaryTheme.shapes.section else RoundedCornerShape(24.dp)
  Box(
    Modifier.fillMaxWidth().height(96.dp).clip(shape)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape)),
    contentAlignment = Alignment.Center,
  ) {
    ArtFrame(DiaryArt.background(ArtPlace.WriteBanner, c.isDark), Modifier.fillMaxSize()) {
      // 배경 그림이 없을 때: 바탕색 그라데이션만
      Box(
        Modifier.fillMaxSize().background(
          if (c.isDark) Brush.verticalGradient(listOf(c.secondaryContainer.copy(alpha = 0.25f), c.surfaceContainer.copy(alpha = 0.6f), c.background))
          else Brush.verticalGradient(listOf(c.primaryFixed.copy(alpha = 0.35f), c.surfaceContainerLow, c.background)),
        ),
      )
    }
    // 그림 위 아래쪽 페이드 (`bg-gradient-to-t from-background ...`)
    Box(
      Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color.Transparent, c.background.copy(alpha = if (c.isDark) 0.6f else 0.5f))),
      ),
    )
    Row(
      Modifier.clip(CircleShape)
        .background(if (c.isDark) c.surfaceContainerHighest.copy(alpha = 0.8f) else c.surfaceContainerLowest.copy(alpha = 0.95f))
        .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.gold.copy(alpha = 0.5f), CircleShape))
        .padding(horizontal = 14.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(Icons.Outlined.Palette, null, Modifier.size(15.dp), tint = c.primary)
      Spacer(Modifier.width(8.dp))
      Text(
        if (c.isDark) "별빛 수채화 스타일 동화 삽화 자동 생성" else "따스한 수채화 동화 삽화 자동 생성",
        style = if (c.isDark) t.labelSm else t.labelMd.copy(fontSize = 12.sp),
        color = if (c.isDark) c.onSurfaceVariant else c.onPrimaryContainer,
        fontWeight = if (c.isDark) null else FontWeight.SemiBold,
      )
    }
  }
}
