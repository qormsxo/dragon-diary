package com.dragondiary.app.ui.screens.generating

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CropPortrait
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.TagCatalog
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.BackButton
import com.dragondiary.app.ui.components.SecondaryButton
import com.dragondiary.app.ui.components.SmallIconBadge
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme

data class GeneratingActions(val onBack: () -> Unit, val onOpenEntry: () -> Unit)

/**
 * 동화 장면 생성 진행 화면 (_9 '별밤 공방').
 *
 * 실제 AI 생성은 이번 범위 밖이다. 그래서
 * - 1단계(일기 조각 접수)만 '완료'로 보여준다 — 기록은 정말 저장되었기 때문.
 * - 이후 단계와 진행 막대는 '생성 엔진 연결 대기' 상태로 움직이기만 하고, 퍼센트나 완료를 표시하지 않는다.
 */
@Composable
fun GeneratingScreen(entry: DiaryEntry, actions: GeneratingActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val anim = rememberInfiniteTransition(label = "studio")
  val spin by anim.animateFloat(0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "spin")
  val ping by anim.animateFloat(0f, 1f, infiniteRepeatable(tween(2800, easing = LinearEasing)), label = "ping")
  val pulse by anim.animateFloat(1f, 0.4f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse")
  val track by anim.animateFloat(-0.35f, 1f, infiniteRepeatable(tween(2400), RepeatMode.Restart), label = "track")

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BackButton(actions.onBack, arrow = true)
      Spacer(Modifier.width(4.dp))
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(18.dp), tint = if (c.isDark) c.gold else c.primary)
          Spacer(Modifier.width(6.dp))
          Text(
            "별밤 공방 | 동화 장면 빚는 중",
            style = t.titleLg.copy(fontSize = 16.sp, lineHeight = 22.sp),
            color = c.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Text(
          "Grimoire Studio · Starry Night Dragon",
          style = t.labelSm.copy(fontSize = 10.sp),
          color = c.outline,
          modifier = Modifier.padding(start = 24.dp),
        )
      }
      SmallIconBadge(Icons.AutoMirrored.Outlined.MenuBook, actions.onOpenEntry, "기록 보기")
    }

    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(top = 12.dp, bottom = 32.dp)
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.HistoryEdu, null, Modifier.size(18.dp).rotate(spin), tint = if (c.isDark) c.gold else c.primary)
        Spacer(Modifier.width(6.dp))
        Text("마법 양피지 조율 중...", style = t.labelMd.copy(fontSize = 12.sp), color = if (c.isDark) c.gold else c.primary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Row(
          Modifier.glow(CircleShape, c.shadow.copy(alpha = 0.08f), 3.dp, offsetY = 1.dp).clip(CircleShape)
            .background(c.surfaceContainerLow).border(1.dp, c.outlineVariant.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(Modifier.size(6.dp).alpha(1f - ping * 0.7f).clip(CircleShape).background(c.gold))
          Spacer(Modifier.width(6.dp))
          Text("별빛 공방 아틀리에", style = t.labelSm, color = if (c.isDark) c.tertiaryContainer else c.tertiary, fontWeight = FontWeight.Medium)
        }
      }

      Spacer(Modifier.height(12.dp))
      Canvas34(entry, spin, ping, pulse, track)

      Text(
        "오늘의 감정 색채를 황금빛 동화로 빚을 준비를 하고 있어요",
        style = t.headlineMd.copy(fontSize = 20.sp, lineHeight = 28.sp),
        color = c.onSurface,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 24.dp, start = 8.dp, end = 8.dp),
      )
      val phrase = entry.tags.take(2)
        .mapNotNull { id -> TagCatalog.find(id)?.let { if (c.isDark) it.darkLabel ?: it.lightLabel else it.lightLabel }?.substringAfter(' ') }
        .joinToString(", ")
      Text(
        if (phrase.isNotEmpty()) "$phrase 이야기를 소중히 맡아두었어요." else "남겨주신 오늘의 조각을 소중히 맡아두었어요.",
        style = t.bodyMd.copy(fontSize = 13.sp, lineHeight = 20.sp),
        color = c.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 6.dp).widthIn(max = 300.dp),
      )

      // 진행 막대 — 생성 엔진이 연결되지 않았으므로 퍼센트 없이 대기 흐름만 보여준다
      Column(Modifier.padding(top = 16.dp).widthIn(max = 320.dp).fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 4.dp).padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.Brush, null, Modifier.size(14.dp), tint = if (c.isDark) c.gold else c.primary)
          Spacer(Modifier.width(4.dp))
          Text("스케치 다듬기 & 마법 덧칠", style = t.labelSm, color = c.onSurfaceVariant, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
          Text("연결 대기", style = t.labelMd, color = if (c.isDark) c.gold else c.primary, fontWeight = FontWeight.Bold)
        }
        BoxWithConstraints(
          Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(c.surfaceContainerHighest)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.3f), CircleShape).padding(2.dp),
        ) {
          val w = maxWidth
          Box(
            Modifier.offset(x = w * track).width(w * 0.35f).fillMaxHeight().clip(CircleShape)
              .background(Brush.horizontalGradient(listOf(c.primary.let { if (c.isDark) c.gold else it }, c.primaryContainer))),
          )
        }
      }

      Column(Modifier.padding(top = 16.dp).widthIn(max = 320.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val pieces = (if (entry.mood != null) 1 else 0) + entry.tags.size
        StepRow(
          StepState.Done, "1. 일기 조각 접수 완료",
          "조각 ${pieces}개 · 글 ${entry.charCount}자 저장됨",
          Icons.Outlined.Check, pulse,
        )
        StepRow(StepState.Waiting, "2. 아기 용 표정 & 포즈 구상 대기", "AI 생성 기능이 연결되면 시작돼요", Icons.Outlined.HourglassTop, pulse)
        StepRow(StepState.Idle, "3. 밤하늘 배경 조율 대기", "새벽 별빛 및 파스텔 조명 텍스처", Icons.Outlined.Schedule, pulse)
      }

      Column(Modifier.padding(top = 20.dp).widthIn(max = 320.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val shape = RoundedCornerShape(16.dp)
        Row(
          Modifier.fillMaxWidth().glow(shape, c.shadow.copy(alpha = 0.08f), 3.dp, offsetY = 1.dp).clip(shape)
            .background(c.surfaceContainerHigh).border(1.dp, c.outlineVariant.copy(alpha = 0.5f), shape).padding(14.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(Modifier.padding(top = 2.dp).size(24.dp).clip(CircleShape).background(c.tertiaryFixed.let { if (c.isDark) it.copy(alpha = 0.2f) else it }), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.NotificationsActive, null, Modifier.size(16.dp), tint = if (c.isDark) c.tertiaryContainer else c.tertiary)
          }
          Spacer(Modifier.width(10.dp))
          Text(
            "삽화 생성 기능은 아직 연결되지 않았어요. 기록은 안전하게 저장되었으니, 기능이 준비되면 이곳에서 장면을 빚어드릴게요.",
            style = t.bodySm.copy(fontSize = 12.sp, lineHeight = 19.sp),
            color = c.onSurface,
          )
        }
        SecondaryButton("저장된 기록 보러 가기", actions.onOpenEntry, Modifier.fillMaxWidth(), icon = Icons.AutoMirrored.Outlined.ArrowForward)
      }
    }
  }
}

/** 3:4 일러스트 캔버스 자리 */
@Composable
private fun Canvas34(entry: DiaryEntry, spin: Float, ping: Float, pulse: Float, track: Float) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(24.dp)
  Box(
    Modifier.widthIn(max = 320.dp).fillMaxWidth().aspectRatio(3f / 4f)
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.5f else 0.06f), 24.dp, offsetY = 8.dp)
      .clip(shape)
      .background(c.surfaceContainerLow)
      .border(2.dp, c.outlineVariant.copy(alpha = 0.6f), shape),
  ) {
    ArtFrame(DiaryArt.storyFor(entry.id), Modifier.fillMaxSize()) {
      Box(
        Modifier.fillMaxSize().background(
          Brush.verticalGradient(listOf(c.primaryFixed.copy(alpha = 0.2f), Color.Transparent, c.tertiaryFixed.copy(alpha = 0.2f))),
        ),
      )
      // 안쪽 금박 테두리와 모서리 꺾쇠
      Box(Modifier.fillMaxSize().padding(10.dp).border(1.dp, c.gold.copy(alpha = 0.2f), RoundedCornerShape(22.dp)))
      Corner(Alignment.TopStart, top = true, start = true)
      Corner(Alignment.TopEnd, top = true, start = false)
      Corner(Alignment.BottomStart, top = false, start = true)
      Corner(Alignment.BottomEnd, top = false, start = false)

      Column(Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
          Box(Modifier.fillMaxSize().scale(1f + ping * 0.6f).alpha((1f - ping) * 0.6f).clip(CircleShape).background(c.gold.copy(alpha = 0.25f)))
          Box(Modifier.fillMaxSize().padding(4.dp).alpha(pulse).border(1.dp, c.gold.copy(alpha = 0.4f), CircleShape))
          Icon(Icons.Outlined.AutoAwesome, null, Modifier.align(Alignment.TopEnd).offset(4.dp, (-4).dp).size(16.dp).rotate(spin / 4), tint = c.gold)
          Icon(Icons.Outlined.Star, null, Modifier.align(Alignment.BottomStart).offset((-4).dp, 4.dp).size(14.dp).alpha(pulse), tint = c.tertiaryContainer)
          Box(
            Modifier.size(64.dp).glow(CircleShape, c.shadow.copy(alpha = 0.15f), 8.dp, offsetY = 2.dp).clip(CircleShape)
              .background(c.surfaceContainerLowest).border(1.dp, c.outlineVariant.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
          ) { Icon(Icons.Outlined.AutoFixHigh, null, Modifier.size(30.dp), tint = if (c.isDark) c.gold else c.primary) }
        }
        Row(
          Modifier.padding(top = 8.dp, bottom = 4.dp).clip(CircleShape).background(c.surfaceContainerHigh)
            .border(1.dp, c.outlineVariant.copy(alpha = 0.5f), CircleShape).padding(horizontal = 12.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Outlined.CropPortrait, null, Modifier.size(14.dp), tint = if (c.isDark) c.gold else c.primary)
          Spacer(Modifier.width(6.dp))
          Text("[ 3:4 일러스트 캔버스 준비 중 ]", style = t.labelSm.copy(fontSize = 11.sp), color = c.onSurface, fontWeight = FontWeight.SemiBold)
        }
        Text("960 × 1280 px 채색 대기", style = t.labelSm.copy(fontSize = 11.sp), color = c.outline, fontWeight = FontWeight.Normal)
      }
    }
    // 아래쪽 흐르는 금빛 선 (`animation: moveTrack`)
    BoxWithConstraints(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(6.dp).background(c.surfaceContainerHighest)) {
      Box(
        Modifier.offset(x = maxWidth * track).width(maxWidth / 3).fillMaxHeight().clip(CircleShape)
          .background(Brush.horizontalGradient(listOf(c.primaryContainer, c.primaryFixedDim, c.primaryContainer))),
      )
    }
  }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.Corner(align: Alignment, top: Boolean, start: Boolean) {
  val c = DiaryTheme.colors
  val color = c.gold.copy(alpha = 0.8f)
  val size: Dp = 20.dp
  Box(
    Modifier.align(align).padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 16.dp).size(size),
  ) {
    Box(Modifier.align(if (top) Alignment.TopCenter else Alignment.BottomCenter).fillMaxWidth().height(2.dp).background(color))
    Box(Modifier.align(if (start) Alignment.CenterStart else Alignment.CenterEnd).fillMaxHeight().width(2.dp).background(color))
  }
}

private enum class StepState { Done, Waiting, Idle }

@Composable
private fun StepRow(state: StepState, title: String, sub: String, icon: ImageVector, pulse: Float) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  val (bg, border) = when (state) {
    StepState.Done -> c.surfaceContainerLow to c.outlineVariant.copy(alpha = 0.5f)
    StepState.Waiting -> c.secondaryFixed.copy(alpha = if (c.isDark) 0.1f else 0.3f) to c.secondary.copy(alpha = 0.3f)
    StepState.Idle -> c.surfaceContainer.copy(alpha = 0.6f) to c.outlineVariant.copy(alpha = 0.3f)
  }
  Row(
    Modifier.fillMaxWidth().alpha(if (state == StepState.Idle) 0.7f else 1f)
      .clip(shape).background(bg).border(1.dp, border, shape).padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val (orb, tint) = when (state) {
      StepState.Done -> (if (c.isDark) c.gold else c.primary) to (if (c.isDark) c.onPrimaryFixed else c.onPrimary)
      StepState.Waiting -> c.secondary to c.onSecondary
      StepState.Idle -> c.surfaceDim.let { if (c.isDark) c.surfaceContainerHighest else it } to c.onSurfaceVariant
    }
    Box(
      Modifier.size(28.dp).alpha(if (state == StepState.Waiting) 0.6f + pulse * 0.4f else 1f).clip(CircleShape).background(orb),
      contentAlignment = Alignment.Center,
    ) { Icon(icon, null, Modifier.size(16.dp), tint = tint) }
    Spacer(Modifier.width(8.dp))
    Column(Modifier.weight(1f)) {
      Text(
        title,
        style = t.labelMd.copy(fontSize = 12.sp),
        color = when (state) {
          StepState.Done -> c.onSurface
          StepState.Waiting -> if (c.isDark) c.secondaryFixed else c.onSecondaryContainer
          StepState.Idle -> c.onSurfaceVariant
        },
        fontWeight = if (state == StepState.Idle) FontWeight.Medium else FontWeight.Bold,
      )
      Text(
        sub,
        style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp),
        color = if (state == StepState.Waiting) c.secondary else c.outline,
        fontWeight = FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    when (state) {
      StepState.Done -> Icon(Icons.Outlined.Verified, null, Modifier.size(18.dp), tint = if (c.isDark) c.gold else c.primary)
      StepState.Waiting -> Box(Modifier.size(8.dp).alpha(pulse).clip(CircleShape).background(c.secondary))
      StepState.Idle -> {}
    }
  }
}
