package com.dragondiary.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.ui.theme.DiaryTheme

/*
 * 그림 파일이 없을 때 그 자리를 채우는 테마별 플레이스홀더.
 * 원본 Stitch 화면의 '삽화가 그려질 자리예요' 프레임을 그대로 옮겼다.
 * ArtFrame(placeholder = { ... }) 안에서만 쓰이므로, 에셋을 넣으면 자동으로 보이지 않는다.
 */

/** 점선 테두리 (`border-2 border-dashed`) */
fun Modifier.dashedBorder(shape: Shape, color: Color, width: Dp = 2.dp, dash: Dp = 6.dp): Modifier =
  this.then(
    Modifier.drawWithOutline(shape, color, width, dash),
  )

private fun Modifier.drawWithOutline(shape: Shape, color: Color, width: Dp, dash: Dp) =
  drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    drawOutline(
      outline,
      color,
      style = Stroke(width.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.toPx(), dash.toPx() * 0.7f))),
    )
  }

/** 다크 테마 16:10 '오늘 밤 삽화' 자리 (_1): 성좌 선 + 붓 아이콘 */
@Composable
fun NightScenePlaceholder(
  icon: ImageVector,
  title: String,
  body: String,
  modifier: Modifier = Modifier,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Box(
    modifier
      .fillMaxSize()
      .background(c.artPlaceholder)
      .background(Brush.verticalGradient(listOf(c.secondaryContainer.copy(alpha = 0.10f), Color.Transparent, c.gold.copy(alpha = 0.05f)))),
    contentAlignment = Alignment.Center,
  ) {
    Constellation(Modifier.fillMaxSize())
    Column(
      Modifier.padding(16.dp).widthIn(max = 320.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      IconOrb(icon, 48.dp, 26.dp, c.surfaceContainerHigh.copy(alpha = 0.8f), c.gold, glowColor = c.glow.copy(alpha = 0.25f))
      Text(title, style = t.labelMd, color = c.primary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
      Text(body, style = t.bodySm, color = c.onSurfaceVariant, textAlign = TextAlign.Center)
    }
  }
}

/** 희미한 별자리 (`<svg ... opacity-20>` 원 5개 + 점선 3개) */
@Composable
fun Constellation(modifier: Modifier) {
  val c = DiaryTheme.colors
  val gold = c.gold.copy(alpha = 0.2f)
  val violet = c.secondary.copy(alpha = 0.2f)
  Canvas(modifier) {
    fun p(x: Float, y: Float) = Offset(size.width * x, size.height * y)
    val dash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))
    drawLine(violet, p(.20f, .30f), p(.45f, .25f), 0.5.dp.toPx(), pathEffect = dash)
    drawLine(violet, p(.45f, .25f), p(.75f, .40f), 0.5.dp.toPx(), pathEffect = dash)
    drawLine(violet, p(.75f, .40f), p(.85f, .75f), 0.5.dp.toPx(), pathEffect = dash)
    drawCircle(gold, 2.dp.toPx(), p(.20f, .30f))
    drawCircle(gold, 1.5.dp.toPx(), p(.45f, .25f))
    drawCircle(violet, 2.dp.toPx(), p(.75f, .40f))
    drawCircle(gold, 1.5.dp.toPx(), p(.85f, .75f))
    drawCircle(violet, 2.5.dp.toPx(), p(.35f, .70f))
  }
}

/** 라이트 테마 삽화 자리 (_5): 양피지 바탕 + 점선 + 원형 아이콘 + 규격 표기 */
@Composable
fun ParchmentScenePlaceholder(
  icon: ImageVector,
  title: String,
  spec: String,
  shape: Shape,
  modifier: Modifier = Modifier,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Box(
    modifier.fillMaxSize().background(c.artPlaceholder).dashedBorder(shape, c.artPlaceholderBorder),
    contentAlignment = Alignment.Center,
  ) {
    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      IconOrb(icon, 48.dp, 24.dp, c.surfaceContainer, c.artPlaceholderIcon, border = c.outlineVariant.copy(alpha = 0.6f))
      Text(
        title,
        style = t.labelLg.copy(fontFamily = t.headlineFamily, fontSize = 14.sp),
        color = c.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp),
      )
      Text(
        spec,
        style = t.bodySm.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
        color = c.outline,
        modifier = Modifier.padding(top = 4.dp),
      )
    }
  }
}

/** 1:1 썸네일 자리 (_5, _8 '1:1 삽화') — 다크(_1)는 같은 크기의 밤색 판 */
@Composable
fun ThumbPlaceholder(
  icon: ImageVector = Icons.Outlined.AutoStories,
  iconTint: Color = DiaryTheme.colors.secondary,
  shape: Shape = DiaryTheme.shapes.thumb,
  dashed: Boolean = false,
  label: String? = "1:1 삽화",
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Box(
    Modifier
      .fillMaxSize()
      .background(if (c.isDark) c.surfaceContainerLow else c.artPlaceholder)
      .then(
        when {
          c.isDark -> Modifier
          dashed -> Modifier.dashedBorder(shape, c.outlineVariant, 1.dp, 4.dp)
          else -> Modifier.border(1.dp, c.outlineVariant.copy(alpha = 0.7f), shape)
        },
      ),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Icon(icon, null, Modifier.size(20.dp), tint = if (c.isDark) c.gold.copy(alpha = 0.7f) else iconTint)
      if (label != null) {
        Text(label, style = t.bodySm.copy(fontSize = 10.sp, lineHeight = 12.sp), color = c.outline, textAlign = TextAlign.Center)
      }
    }
  }
}

/** 동그란 드래곤 자리 (홈 상단 오브, 설정 프로필) */
@Composable
fun DragonOrbPlaceholder(icon: ImageVector, iconSize: Dp, tint: Color, container: Color) {
  Box(Modifier.fillMaxSize().clip(CircleShape).background(container), contentAlignment = Alignment.Center) {
    Icon(icon, null, Modifier.size(iconSize), tint = tint)
  }
}
