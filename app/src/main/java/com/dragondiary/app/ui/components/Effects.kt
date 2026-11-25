package com.dragondiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * Tailwind 의 `shadow-[0_0_20px_rgba(...)]` 처럼 퍼지는 빛/그림자.
 * 다크 테마의 별빛 광채, 라이트 테마의 호박색 확산 그림자에 쓴다.
 */
fun Modifier.glow(
  shape: Shape,
  color: Color,
  radius: Dp,
  offsetY: Dp = 0.dp,
  spread: Dp = 0.dp,
): Modifier = dropShadow(
  shape = shape,
  shadow = Shadow(radius = radius, color = color, spread = spread, offset = DpOffset(0.dp, offsetY)),
)

/**
 * 카드 뒤에 흐릿하게 떠 있는 둥근 빛 (`absolute ... rounded-full bg-x/10 blur-2xl`).
 * 블러 대신 방사형 그라데이션으로 같은 느낌을 낸다.
 */
@Composable
fun BoxScope.AmbientBlob(
  color: Color,
  size: Dp,
  x: Dp,
  y: Dp,
  alignRight: Boolean = false,
  alignBottom: Boolean = false,
) {
  val align = when {
    alignRight && alignBottom -> androidx.compose.ui.Alignment.BottomEnd
    alignRight -> androidx.compose.ui.Alignment.TopEnd
    alignBottom -> androidx.compose.ui.Alignment.BottomStart
    else -> androidx.compose.ui.Alignment.TopStart
  }
  Box(
    Modifier
      .align(align)
      .offset(x, y)
      .size(size)
      .background(Brush.radialGradient(listOf(color, color.copy(alpha = 0f))), CircleShape),
  )
}
