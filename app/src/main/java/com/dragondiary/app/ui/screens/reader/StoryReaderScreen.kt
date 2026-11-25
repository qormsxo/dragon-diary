package com.dragondiary.app.ui.screens.reader

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CrueltyFree
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Flare
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.data.TagCatalog
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.AmbientBlob
import com.dragondiary.app.ui.components.BackButton
import com.dragondiary.app.ui.components.IconOrb
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.PrimaryCta
import com.dragondiary.app.ui.components.ProfileBadge
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.SecondaryButton
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class ReaderActions(
  val onBack: () -> Unit,
  val onRegenerate: () -> Unit,
  val onEdit: () -> Unit,
)

private val TIME = DateTimeFormatter.ofPattern("h:mm")

/**
 * 동화 읽기 / 기록 상세 화면 (_3 Story Reader).
 * 삽화와 동화는 아직 생성 기능이 없으므로 '대기' 상태로 보여주고, 사용자가 남긴 기록을 그대로 보여준다.
 *
 * @param volume 이 기록이 몇 번째 기록인지 (원본의 'grimoire vol. 42' 자리)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryReaderScreen(entry: DiaryEntry, volume: Int, actions: ReaderActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  val pulse by rememberInfiniteTransition(label = "p")
    .animateFloat(1f, 0.4f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "a")

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BackButton(actions.onBack)
      Spacer(Modifier.width(4.dp))
      Text("Story Reader", style = t.headlineSm.copy(fontFamily = t.bodyFamily), color = c.onSurface, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
      ProfileBadge { notice(SOON) }
    }
    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(top = 12.dp, bottom = 32.dp)
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      // 상태 줄
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).alpha(pulse).glow(CircleShape, c.glow, 8.dp).clip(CircleShape).background(c.gold))
        Spacer(Modifier.width(8.dp))
        Text(
          "${entry.date.monthValue}월 ${entry.date.dayOfMonth}일 밤의 기록 저장됨",
          style = t.labelMd,
          color = c.primary,
          modifier = Modifier.weight(1f),
        )
        Pill(
          "삽화 생성 대기",
          container = c.surfaceContainerHigh,
          content = c.secondary,
          icon = Icons.Outlined.AutoAwesome,
          style = t.labelSm,
          horizontal = 12.dp,
          vertical = 4.dp,
        )
      }

      StoryPlate(entry, volume)

      // 날짜, 제목, 해시태그
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Schedule, null, Modifier.size(15.dp), tint = c.secondary)
          Spacer(Modifier.width(6.dp))
          Text(
            "${entry.date.year}년 ${entry.date.monthValue}월 ${entry.date.dayOfMonth}일 " +
              "${entry.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)} 밤 ${entry.createdAt.format(TIME)}",
            style = t.labelSm,
            color = c.onSurfaceVariant,
          )
        }
        Text(entry.displayTitle, style = t.headlineMd, color = c.onSurface, fontWeight = FontWeight.Bold)
        val tags = entry.tags.mapNotNull(TagCatalog::hashtag)
        if (tags.isNotEmpty() || entry.mood != null) {
          FlowRow(
            Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            tags.forEachIndexed { i, tag ->
              if (i == 0) Pill(tag, c.secondaryContainer.copy(alpha = 0.5f), c.secondary, horizontal = 12.dp, vertical = 4.dp)
              else Pill(tag, c.surfaceContainerHigh, c.onSurface, horizontal = 12.dp, vertical = 4.dp)
            }
            entry.mood?.let {
              Pill("#${it.label.replace(" ", "")}", c.gold.copy(alpha = 0.2f), if (c.isDark) c.gold else c.primary, horizontal = 12.dp, vertical = 4.dp)
            }
          }
        }
      }

      StoryCard(entry)
      MoodCard(entry)

      Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
        PrimaryCta(
          "동화 카드 저장 / 인스타 공유",
          { notice(SOON) },
          icon = Icons.Outlined.Download,
          shape = CircleShape,
          textStyle = t.labelLg.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          SecondaryButton("장면 다시 빚기", actions.onRegenerate, Modifier.weight(1f), icon = Icons.Outlined.Refresh)
          SecondaryButton("기록 수정하기", actions.onEdit, Modifier.weight(1f), icon = Icons.Outlined.EditNote)
        }
      }
    }
  }
}

/** 3:4 동화 삽화 판 */
@Composable
private fun StoryPlate(entry: DiaryEntry, volume: Int) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  val shape = DiaryTheme.shapes.section
  Box(
    Modifier.fillMaxWidth().aspectRatio(3f / 4f)
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.7f else 0.12f), 36.dp, offsetY = 12.dp)
      .clip(shape)
      .background(c.surfaceContainerLow)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.cardBorder, shape)),
  ) {
    ArtFrame(DiaryArt.storyFor(entry.id), Modifier.fillMaxSize(), contentDescription = "동화 삽화") {
      Box(
        Modifier.fillMaxSize().background(
          Brush.verticalGradient(listOf(c.secondaryContainer.copy(alpha = 0.2f), c.surfaceContainerLow, c.surfaceContainerLowest)),
        ),
      ) {
        AmbientBlob(c.gold.copy(alpha = 0.10f), 192.dp, 48.dp, (-48).dp, alignRight = true)
        AmbientBlob(c.secondaryContainer.copy(alpha = 0.3f), 224.dp, (-64).dp, 64.dp, alignBottom = true)
        Column(
          Modifier.align(Alignment.Center).padding(20.dp).fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.surfaceContainer.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          IconOrb(Icons.AutoMirrored.Outlined.MenuBook, 56.dp, 28.dp, c.surfaceContainerHigh.copy(alpha = 0.9f), c.gold, glowColor = c.glow.copy(alpha = 0.15f))
          Text(
            "[ 3:4 동화 완성 삽화 영역 ]",
            style = t.labelMd,
            color = c.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
          )
          Text(
            "이 기록의 별밤용 동화 삽화가 들어올 자리예요.\n삽화 생성 기능은 아직 준비 중이에요.",
            style = t.bodySm,
            color = c.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 260.dp),
          )
        }
      }
    }
    // 위·아래 장식 줄
    Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
      Row(
        Modifier.clip(CircleShape).background(c.surfaceContainerHighest.copy(alpha = 0.8f)).padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(Icons.Outlined.Nightlight, null, Modifier.size(16.dp), tint = c.gold)
        Spacer(Modifier.width(6.dp))
        Text("별밤 grimoire vol. $volume", style = t.labelSm, color = c.onSurface)
      }
      Spacer(Modifier.weight(1f))
      Box(
        Modifier.size(32.dp).clip(CircleShape).background(c.surfaceContainerHighest.copy(alpha = 0.8f)).clickable { notice(SOON) },
        contentAlignment = Alignment.Center,
      ) { Icon(Icons.Outlined.Fullscreen, "크게 보기", Modifier.size(18.dp), tint = c.onSurface) }
    }
    Row(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Outlined.Flare, null, Modifier.size(14.dp), tint = c.secondary)
      Spacer(Modifier.width(6.dp))
      Text("1536 × 2048 px · Bedtime Story", style = t.labelSm, color = c.onSurfaceVariant.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
      Text("별밤용과 함께하는 밤", style = t.labelSm, color = if (c.isDark) c.primaryFixedDim else c.primary)
    }
  }
}

@Composable
private fun StoryCard(entry: DiaryEntry) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = DiaryTheme.shapes.section
  Box(
    Modifier.fillMaxWidth()
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.5f else 0.08f), 16.dp, offsetY = 8.dp)
      .clip(shape)
      .background(c.card)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.cardBorder, shape)),
  ) {
    AmbientBlob(c.secondaryContainer.copy(alpha = 0.2f), 128.dp, 32.dp, 32.dp, alignRight = true, alignBottom = true)
    Column(Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.Top) {
        IconOrb(Icons.Outlined.CrueltyFree, 40.dp, 22.dp, c.secondaryContainer, if (c.isDark) c.secondary else c.onSecondaryContainer, glowColor = c.secondary.copy(alpha = 0.3f))
        Spacer(Modifier.width(12.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("아기 별밤용의 야화", style = t.labelLg, color = c.secondary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            Pill("수호 기록", c.surfaceContainerHighest, if (c.isDark) c.gold else c.primary, horizontal = 8.dp)
          }
          Text(
            "동화 생성 기능이 연결되면 아기 용이 이 기록을 작은 이야기로 엮어드려요. 지금은 남겨주신 기록을 그대로 보여드려요.",
            style = t.bodySm,
            color = c.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
          )
        }
      }
      Box(
        Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.surfaceContainerLow).padding(16.dp),
      ) {
        Icon(Icons.Outlined.FormatQuote, null, Modifier.align(Alignment.TopEnd).size(24.dp), tint = c.surfaceContainerHighest)
        val text = buildList {
          if (entry.body.isNotBlank()) add(entry.body)
          if (entry.memo.isNotBlank()) add(entry.memo)
        }.joinToString("\n\n")
        Text(
          text.ifBlank { "남긴 글 없이 마음 빛깔과 조각만 기록했어요." },
          style = t.bodyMd.copy(lineHeight = 28.sp),
          color = if (text.isBlank()) c.outline else c.onSurface,
          modifier = Modifier.padding(end = 12.dp),
        )
      }
    }
  }
}

/** '별밤용의 밤 무드' — 감정 톤에 맞춘 팔레트 (테마 토큰에서 가져옴) */
@Composable
private fun MoodCard(entry: DiaryEntry) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val (moodName, palette) = when (entry.mood?.tone) {
    MoodTone.Violet -> "고요한 트와일라잇 라벤더 🌙" to listOf(c.secondary, c.surfaceContainerLowest, c.secondaryContainer, c.secondaryFixed)
    MoodTone.Peach -> "포근한 난롯가 피치 🔥" to listOf(c.tertiaryContainer, c.surfaceContainerLowest, c.secondaryContainer, c.tertiaryFixed)
    MoodTone.Gold -> "깊고 아늑한 미드나잇 앰버 🌌" to listOf(c.gold, c.surfaceContainerLowest, c.secondaryContainer, c.primary)
    null -> "아직 고르지 않은 밤 ✨" to listOf(c.gold, c.surfaceContainerLowest, c.secondaryContainer, c.primaryFixed)
  }
  Column(
    Modifier.fillMaxWidth().clip(DiaryTheme.shapes.section)
      .background(if (c.isDark) c.surfaceContainerHigh.copy(alpha = 0.6f) else c.surfaceContainerLow)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.cardBorder, DiaryTheme.shapes.section))
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(20.dp), tint = c.gold)
      Spacer(Modifier.width(8.dp))
      Text("별밤용의 밤 무드", style = t.labelLg, color = c.onSurface, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
      Text(moodName, style = t.labelSm, color = c.primary, fontWeight = FontWeight.Medium)
    }
    Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
      Text("오늘의 수면 팔레트", style = t.labelSm, color = c.onSurfaceVariant, modifier = Modifier.weight(1f))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        palette.forEachIndexed { i, color -> Swatch(color, glow = i == 0) }
      }
    }
  }
}

@Composable
private fun Swatch(color: Color, glow: Boolean) {
  val c = DiaryTheme.colors
  Box(
    Modifier.size(24.dp)
      .then(if (glow) Modifier.glow(CircleShape, color, 8.dp) else Modifier)
      .clip(CircleShape)
      .background(color)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.outlineVariant.copy(alpha = 0.5f), CircleShape)),
  )
}
