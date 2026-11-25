package com.dragondiary.app.ui.screens.write

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Flare
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryDraft
import com.dragondiary.app.data.Mood
import com.dragondiary.app.ui.components.AmbientBlob
import com.dragondiary.app.ui.components.BackButton
import com.dragondiary.app.ui.components.IconOrb
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.PrimaryCta
import com.dragondiary.app.ui.components.ProfileBadge
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val AMPM = DateTimeFormatter.ofPattern("a hh:mm", Locale.US)
private val HM = DateTimeFormatter.ofPattern("HH:mm")

/**
 * 자유 모드 / 기록 고치기 화면 (_7 '별밤 일기장 Grimoire Writing & Chronicle').
 * 제목과 본문을 자유롭게 쓴다. 간편 기록에서 고른 마음 빛깔과 조각은 그대로 이어진다.
 */
@Composable
fun FreeWriteScreen(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  val editing = draft.editingId != null
  val now = remember { LocalTime.now() }

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BackButton(actions.onBack, arrow = true)
      Spacer(Modifier.width(8.dp))
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("별밤 일기장", style = t.headlineSm.copy(fontFamily = t.headlineFamily), color = c.ink, fontWeight = FontWeight.SemiBold)
          Spacer(Modifier.width(6.dp))
          Icon(Icons.Outlined.AutoFixHigh, null, Modifier.size(18.dp), tint = c.gold)
        }
        Text("Grimoire Writing & Chronicle", style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.03.sp * 11), color = c.outline)
      }
      ProfileBadge { notice(SOON) }
    }

    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(horizontal = 20.dp)
        .padding(top = 8.dp, bottom = 24.dp)
        .navigationBarsPadding(),
    ) {
      // 상단 줄: 기록 종류 + 임시저장 버튼
      Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.AutoMirrored.Outlined.MenuBook, null, Modifier.size(18.dp), tint = c.gold)
        Spacer(Modifier.width(4.dp))
        Text(
          if (editing) "지난 기록 고쳐 쓰기" else "새로운 하루 한 켠의 기록",
          style = t.labelMd.copy(fontFamily = t.headlineFamily, fontSize = 12.sp),
          color = c.onToneGoldContainer,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.weight(1f),
        )
        if (!editing) {
          Row(
            Modifier
              .glow(CircleShape, c.shadow.copy(alpha = 0.1f), 4.dp, offsetY = 1.dp)
              .clip(CircleShape)
              .background(c.surfaceContainer)
              .border(1.dp, c.outlineVariant.copy(alpha = 0.6f), CircleShape)
              .clickable(onClick = actions.onSaveForLater)
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Outlined.HistoryEdu, null, Modifier.size(14.dp), tint = c.primary)
            Spacer(Modifier.width(4.dp))
            Text(
              draft.savedAt?.let { "임시저장 (${it.format(HM)})" } ?: "임시저장",
              style = t.labelMd.copy(fontSize = 12.sp),
              color = c.onToneGoldContainer,
            )
          }
        }
      }
      Spacer(Modifier.height(8.dp))

      // 날짜 + 시간
      Row(verticalAlignment = Alignment.Top) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Text(
            "${draft.date.year}년 ${draft.date.monthValue}월 ${draft.date.dayOfMonth}일 ${draft.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)}",
            style = t.headlineMd.copy(fontSize = 20.sp, lineHeight = 28.sp),
            color = c.ink,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f, fill = false),
          )
          Spacer(Modifier.width(8.dp))
          Pill(
            "달빛 깃든 밤",
            container = c.primaryFixed.copy(alpha = if (c.isDark) 0.15f else 0.7f),
            content = if (c.isDark) c.gold else c.primary,
            border = c.primaryFixedDim.copy(alpha = 0.5f),
            style = t.labelMd.copy(fontSize = 12.sp),
            horizontal = 8.dp,
          )
        }
        Row(Modifier.padding(start = 8.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Schedule, null, Modifier.size(14.dp), tint = c.gold)
          Spacer(Modifier.width(4.dp))
          Text(now.format(AMPM), style = t.labelSm.copy(fontSize = 11.sp), color = c.outline)
        }
      }

      // 마음 날씨
      Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(15.dp), tint = c.gold)
        Spacer(Modifier.width(6.dp))
        Text("오늘의 마음 날씨", style = t.labelSm.copy(fontSize = 11.sp), color = c.onToneGoldContainer, fontWeight = FontWeight.SemiBold)
        Text("  ·  ", style = t.labelSm, color = c.outlineVariant)
        Text("기운을 북돋을 마법 조각 하나", style = t.labelSm.copy(fontSize = 11.sp), color = c.tertiary, fontWeight = FontWeight.Medium)
      }
      Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Mood.entries.forEach { mood -> WeatherPill(mood, draft.mood == mood) { actions.onMood(mood) } }
      }

      // 제목
      Spacer(Modifier.height(8.dp))
      val titleShape = RoundedCornerShape(16.dp)
      Row(
        Modifier.fillMaxWidth()
          .glow(titleShape, c.shadow.copy(alpha = if (c.isDark) 0.4f else 0.06f), 12.dp, offsetY = 2.dp)
          .clip(titleShape)
          .background(c.card.copy(alpha = 0.9f))
          .border(1.dp, if (c.isDark) c.cardBorder else c.outlineVariant.copy(alpha = 0.7f), titleShape)
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(Icons.Outlined.Draw, null, Modifier.size(20.dp), tint = c.gold)
        Spacer(Modifier.width(8.dp))
        val titleStyle = t.titleLg.copy(fontFamily = t.headlineFamily, fontSize = 17.sp, color = c.ink, fontWeight = FontWeight.SemiBold)
        BasicTextField(
          value = draft.title,
          onValueChange = actions.onTitle,
          singleLine = true,
          textStyle = titleStyle,
          cursorBrush = SolidColor(c.gold),
          modifier = Modifier.weight(1f),
          decorationBox = { inner ->
            if (draft.title.isEmpty()) Text("제목 (선택 사항)", style = titleStyle, color = c.outline.copy(alpha = 0.6f), maxLines = 1)
            inner()
          },
        )
      }

      // 본문 양피지
      Spacer(Modifier.height(16.dp))
      ChapterCard(draft, actions)

      // 안내
      Spacer(Modifier.height(16.dp))
      val infoShape = RoundedCornerShape(16.dp)
      Row(
        Modifier.fillMaxWidth()
          .clip(infoShape)
          .background(Brush.horizontalGradient(listOf(c.background, c.surfaceContainerLow)))
          .border(1.dp, c.primaryFixedDim.copy(alpha = 0.5f), infoShape)
          .padding(16.dp),
        verticalAlignment = Alignment.Top,
      ) {
        IconOrb(Icons.Outlined.Flare, 32.dp, 18.dp, c.primaryFixed.copy(alpha = if (c.isDark) 0.2f else 1f), c.primary.let { if (c.isDark) c.gold else it }, modifier = Modifier.padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Text(
          buildAnnotatedString {
            append("적어주신 일기 조각을 마법 재료로 삼아, 오늘 밤 ")
            withStyle(SpanStyle(color = if (c.isDark) c.gold else c.primary, fontWeight = FontWeight.SemiBold)) { append("아기 용과 함께하는 동화 삽화") }
            append("가 탄생합니다. 당신만의 문장으로 포근한 밤을 빚어보세요.")
          },
          style = t.bodySm.copy(fontFamily = t.headlineFamily, fontSize = 13.sp, lineHeight = 21.sp),
          color = c.onSurfaceVariant,
        )
      }

      Spacer(Modifier.height(24.dp))
      PrimaryCta(
        if (editing) "고친 기록 저장하기" else "오늘의 동화 장면 빚기",
        actions.onGenerate,
        icon = Icons.Outlined.AutoAwesome,
        enabled = !draft.isEmpty,
        height = 58.dp,
        shape = RoundedCornerShape(16.dp),
        textStyle = t.headlineSm.copy(fontFamily = t.headlineFamily, fontWeight = FontWeight.Bold),
      )
      if (!editing) {
        Row(
          Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp).clip(CircleShape)
            .clickable(onClick = actions.onSaveForLater).padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Outlined.BookmarkAdded, null, Modifier.size(15.dp), tint = c.onToneGoldContainer)
          Spacer(Modifier.width(6.dp))
          Text("나중에 쓰기 (별빛 임시저장)", style = t.labelMd.copy(fontSize = 12.sp), color = c.onToneGoldContainer)
        }
      }
    }
  }
}

@Composable
private fun WeatherPill(mood: Mood, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = CircleShape
  Box(
    Modifier
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.3f else 0.08f), 3.dp, offsetY = 1.dp)
      .clip(shape)
      .background(
        when {
          selected && c.isDark -> c.secondaryContainer
          selected -> c.secondaryFixed
          else -> c.card
        },
      )
      .border(if (selected) 2.dp else 1.dp, if (selected) c.secondaryFixedDim else c.outlineVariant.copy(alpha = 0.7f), shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 6.dp),
  ) {
    Text(
      "${mood.adjective} ${mood.shortEmoji}",
      style = t.labelMd.copy(fontSize = 12.sp),
      color = when {
        selected && c.isDark -> c.secondaryFixed
        selected -> c.onToneVioletContainer
        else -> c.onSurfaceVariant
      },
      fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
    )
  }
}

@Composable
private fun ChapterCard(draft: DiaryDraft, actions: WriteActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(24.dp)
  val infinite = rememberInfiniteTransition(label = "pen")
  val dotAlpha by infinite.animateFloat(1f, 0.4f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "dot")

  Box(
    Modifier.fillMaxWidth()
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.5f else 0.06f), 24.dp, offsetY = 4.dp)
      .clip(shape)
      .background(c.card)
      .border(2.dp, if (c.isDark) c.surfaceContainerHigh else c.surfaceContainer, shape),
  ) {
    AmbientBlob(c.primaryFixed.copy(alpha = 0.2f), 144.dp, 40.dp, 40.dp, alignRight = true, alignBottom = true)
    Box(Modifier.fillMaxWidth().height(6.dp).background(Brush.horizontalGradient(listOf(c.primaryFixedDim, c.gold, c.secondaryContainer))))
    Column(Modifier.padding(24.dp).padding(top = 6.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(13.dp), tint = c.gold)
        Spacer(Modifier.width(4.dp))
        Text(
          "CHAPTER OF THE DAY",
          style = t.labelSm.copy(fontFamily = t.headlineFamily, fontSize = 11.sp, letterSpacing = 0.08.sp * 11),
          color = c.tertiary.let { if (c.isDark) c.tertiaryContainer else it },
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.weight(1f),
        )
        Box(Modifier.size(6.dp).clip(CircleShape).background(c.gold))
        Spacer(Modifier.width(4.dp))
        Text("양피지 펜 작성 모드", style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = c.outline)
      }
      Box(Modifier.padding(top = 8.dp, bottom = 8.dp).fillMaxWidth().height(1.dp).background(c.surfaceContainer))
      val bodyStyle = t.bodyLg.copy(fontFamily = t.headlineFamily, fontSize = 15.5.sp, lineHeight = 28.sp, color = c.onSurface)
      BasicTextField(
        value = draft.body,
        onValueChange = actions.onBody,
        textStyle = bodyStyle,
        cursorBrush = SolidColor(c.gold),
        modifier = Modifier.fillMaxWidth().heightIn(min = 224.dp),
        decorationBox = { inner ->
          if (draft.body.isEmpty()) {
            Text(
              "오늘 어떤 별빛이 마음에 머물렀나요? 마음에 남은 순간, 스쳐간 생각들을 자유롭게 적어보세요...",
              style = bodyStyle,
              color = c.outline.copy(alpha = 0.6f),
            )
          }
          inner()
        },
      )
      Box(Modifier.padding(top = 16.dp).fillMaxWidth().height(1.dp).background(c.surfaceContainer))
      Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).alpha(dotAlpha).clip(CircleShape).background(c.gold))
        Spacer(Modifier.width(6.dp))
        Text(
          "용의 온기로 기록 중",
          style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp),
          color = if (c.isDark) c.gold else c.primary,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.weight(1f),
        )
        Text(
          buildAnnotatedString {
            withStyle(SpanStyle(color = c.ink, fontWeight = FontWeight.Bold)) { append("${draft.body.length}") }
            append(if (draft.body.length < 50) "자 / 천천히 적어보세요" else "자 / 여유롭게 작성 중")
          },
          style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp),
          color = c.outline,
        )
      }
    }
  }
}
