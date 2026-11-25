package com.dragondiary.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.ui.theme.DiaryTheme

/** 아직 연결되지 않은 기능을 눌렀을 때 짧은 안내를 띄운다. DragonDiaryApp 에서 제공. */
val LocalNotice = staticCompositionLocalOf<(String) -> Unit> { {} }

const val SOON = "아직 준비 중인 기능이에요"

/** 원본 섹션 카드. 다크는 떠 있는 밤색 판, 라이트는 금박 테두리의 흰 양피지. */
@Composable
fun SectionCard(
  modifier: Modifier = Modifier,
  shape: Shape = DiaryTheme.shapes.section,
  padding: PaddingValues = PaddingValues(if (DiaryTheme.isDark) 16.dp else 20.dp),
  container: Color = DiaryTheme.colors.card,
  border: Color = DiaryTheme.colors.cardBorder,
  elevated: Boolean = true,
  content: @Composable ColumnScope.() -> Unit,
) {
  val c = DiaryTheme.colors
  val shadowMod = when {
    !elevated -> Modifier
    c.isDark -> Modifier.glow(shape, c.shadow.copy(alpha = 0.75f), 28.dp, offsetY = 12.dp)
    else -> Modifier.glow(shape, c.shadow.copy(alpha = 0.10f), 24.dp, offsetY = 6.dp)
  }
  Column(
    modifier
      .then(shadowMod)
      .clip(shape)
      .background(container)
      .then(if (border.alpha > 0f) Modifier.border(1.dp, border, shape) else Modifier)
      .padding(padding),
    content = content,
  )
}

/** 작은 알약 배지 (`px-2.5 py-0.5 rounded-full`) */
@Composable
fun Pill(
  text: String,
  container: Color,
  content: Color,
  modifier: Modifier = Modifier,
  border: Color? = null,
  icon: ImageVector? = null,
  iconTint: Color = content,
  style: TextStyle = DiaryTheme.type.labelSm,
  horizontal: Dp = 10.dp,
  vertical: Dp = 2.dp,
) {
  Row(
    modifier
      .clip(CircleShape)
      .background(container)
      .then(if (border != null) Modifier.border(1.dp, border, CircleShape) else Modifier)
      .padding(horizontal = horizontal, vertical = vertical),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    if (icon != null) Icon(icon, null, Modifier.size(13.dp), tint = iconTint)
    Text(text, style = style, color = content, maxLines = 1)
  }
}

/** 감정 톤 → 배지 색 */
@Composable
fun toneColors(tone: MoodTone): Pair<Color, Color> {
  val c = DiaryTheme.colors
  return when (tone) {
    MoodTone.Gold -> c.toneGoldContainer to c.onToneGoldContainer
    MoodTone.Violet -> c.toneVioletContainer to c.onToneVioletContainer
    MoodTone.Peach -> c.tonePeachContainer to c.onTonePeachContainer
  }
}

@Composable
fun toneDot(tone: MoodTone): Color {
  val c = DiaryTheme.colors
  return when (tone) {
    MoodTone.Gold -> c.toneGold
    MoodTone.Violet -> c.toneViolet
    MoodTone.Peach -> c.tonePeach
  }
}

/**
 * 주요 행동 버튼.
 * 다크: 별빛 금색 알약 + 금빛 광채 (`bg-primary-container shadow-[0_4px_20px_rgba(246,200,95,0.35)]`)
 * 라이트: 금박 그라데이션 + 호박색 그림자 (`bg-gradient-to-r from-[#e5a93c] ...`)
 */
@Composable
fun PrimaryCta(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  trailing: ImageVector? = null,
  enabled: Boolean = true,
  height: Dp = if (DiaryTheme.isDark) 56.dp else 52.dp,
  shape: Shape = DiaryTheme.shapes.cta,
  textStyle: TextStyle = if (DiaryTheme.isDark) DiaryTheme.type.headlineSm.copy(fontWeight = FontWeight.Bold)
  else DiaryTheme.type.headlineSm.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
) {
  val c = DiaryTheme.colors
  Row(
    modifier
      .fillMaxWidth()
      .height(height)
      .alpha(if (enabled) 1f else 0.45f)
      .glow(shape, c.glow.copy(alpha = 0.35f), 20.dp, offsetY = 4.dp)
      .clip(shape)
      .background(Brush.horizontalGradient(c.ctaGradient))
      .then(if (!c.isDark) Modifier.border(1.dp, c.primaryFixedDim.copy(alpha = 0.7f), shape) else Modifier)
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (icon != null) {
      Icon(icon, null, Modifier.size(22.dp), tint = c.onGold)
      Spacer(Modifier.width(8.dp))
    }
    Text(text, style = textStyle, color = c.onGold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    if (trailing != null) {
      Spacer(Modifier.width(4.dp))
      Icon(trailing, null, Modifier.size(16.dp), tint = c.onGold)
    }
  }
}

/** 보조 버튼 (다크 _3 '장면 다시 빚기', 라이트 _9 '백그라운드에서 계속하기') */
@Composable
fun SecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  height: Dp = 52.dp,
) {
  val c = DiaryTheme.colors
  val shape = CircleShape
  Row(
    modifier
      .height(height)
      .then(if (!c.isDark) Modifier.glow(shape, c.shadow.copy(alpha = 0.12f), 12.dp, offsetY = 4.dp) else Modifier)
      .clip(shape)
      .background(if (c.isDark) c.surfaceContainerHigh else c.surfaceContainerLowest)
      .then(if (!c.isDark) Modifier.border(1.dp, c.outlineVariant.copy(alpha = 0.6f), shape) else Modifier)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (icon != null) {
      Icon(icon, null, Modifier.size(19.dp), tint = if (c.isDark) c.onSurface else c.primary)
      Spacer(Modifier.width(6.dp))
    }
    Text(
      text,
      style = if (c.isDark) DiaryTheme.type.labelMd.copy(fontSize = 13.sp) else DiaryTheme.type.titleMd.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
      color = c.onSurface,
      maxLines = 1,
    )
  }
}

/** 동그란 아이콘 받침 (`w-12 h-12 rounded-full ... flex items-center justify-center`) */
@Composable
fun IconOrb(
  icon: ImageVector,
  size: Dp,
  iconSize: Dp,
  container: Color,
  tint: Color,
  modifier: Modifier = Modifier,
  border: Color? = null,
  glowColor: Color? = null,
) {
  Box(
    modifier
      .size(size)
      .then(if (glowColor != null) Modifier.glow(CircleShape, glowColor, 16.dp) else Modifier)
      .clip(CircleShape)
      .background(container)
      .then(if (border != null) Modifier.border(1.dp, border, CircleShape) else Modifier),
    contentAlignment = Alignment.Center,
  ) {
    Icon(icon, null, Modifier.size(iconSize), tint = tint)
  }
}

/** 섹션 머리글: 아이콘 + 제목 + 오른쪽 보조 글 */
@Composable
fun SectionHeader(
  title: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  iconTint: Color = DiaryTheme.colors.gold,
  titleStyle: TextStyle = DiaryTheme.type.headlineSm,
  titleColor: Color = if (DiaryTheme.isDark) DiaryTheme.colors.onSurface else DiaryTheme.colors.ink,
  trailing: @Composable RowScope.() -> Unit = {},
) {
  Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, null, Modifier.size(20.dp), tint = iconTint)
    Spacer(Modifier.width(8.dp))
    Text(title, style = titleStyle, color = titleColor, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
    trailing()
  }
}

/** '전체 보기 >' 링크 */
@Composable
fun SeeAllLink(text: String, icon: ImageVector, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  Row(
    Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text, style = if (c.isDark) DiaryTheme.type.labelMd else DiaryTheme.type.bodySm.copy(fontWeight = FontWeight.Medium), color = c.secondary)
    Spacer(Modifier.width(2.dp))
    Icon(icon, null, Modifier.size(16.dp), tint = c.secondary)
  }
}

/** 설정 등에서 쓰는 '준비 중' 표시 */
@Composable
fun SoonTag(modifier: Modifier = Modifier) {
  val c = DiaryTheme.colors
  Pill(
    "준비 중",
    container = c.surfaceContainerHigh,
    content = c.outline,
    modifier = modifier,
    style = DiaryTheme.type.labelSm.copy(fontSize = 10.sp),
    horizontal = 8.dp,
  )
}

/** 줄 단위 텍스트 — Text 의 기본값을 테마 토큰으로 맞춘 얇은 래퍼 */
@Composable
fun DText(
  text: String,
  style: TextStyle,
  color: Color,
  modifier: Modifier = Modifier,
  maxLines: Int = Int.MAX_VALUE,
  fontWeight: FontWeight? = null,
  fontSize: TextUnit = TextUnit.Unspecified,
  textAlign: androidx.compose.ui.text.style.TextAlign? = null,
) {
  Text(
    text,
    modifier = modifier,
    style = style,
    color = color,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis,
    fontWeight = fontWeight,
    fontSize = fontSize,
    textAlign = textAlign,
  )
}

/** 테두리만 있는 둥근 상자 */
fun Modifier.outlined(shape: Shape, color: Color, width: Dp = 1.dp) = border(BorderStroke(width, color), shape)

