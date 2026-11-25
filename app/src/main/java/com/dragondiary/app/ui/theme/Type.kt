package com.dragondiary.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * 테마별 글꼴 체계.
 *
 * 디자인 원본은 Plus Jakarta Sans(본문)와 Literata(라이트 테마 제목)를 쓰지만
 * 두 글꼴 모두 한글 글리프가 없어 원본 스크린샷에서도 한글은 시스템 글꼴로 그려진다.
 * 그래서 외부 글꼴을 내려받지 않고 시스템 글꼴로 같은 결과를 낸다.
 * - 다크: 모든 텍스트 산세리프 (Plus Jakarta Sans 계열)
 * - 라이트: 제목은 세리프(Literata → 시스템 명조), 본문은 산세리프
 *
 * 글꼴 파일을 추가하려면 res/font 에 넣고 [headlineFamily]/[bodyFamily]만 바꾸면 된다.
 */
@Immutable
data class DiaryTypography(
  val headlineFamily: FontFamily,
  val bodyFamily: FontFamily,
  val display: TextStyle,
  val headlineLg: TextStyle,
  val headlineMd: TextStyle,
  val headlineSm: TextStyle,
  val titleLg: TextStyle,
  val titleMd: TextStyle,
  val bodyLg: TextStyle,
  val bodyMd: TextStyle,
  val bodySm: TextStyle,
  val labelLg: TextStyle,
  val labelMd: TextStyle,
  val labelSm: TextStyle,
)

private fun style(
  family: FontFamily,
  size: Int,
  line: Int,
  weight: FontWeight,
  tracking: Double = 0.0,
) = TextStyle(
  fontFamily = family,
  fontSize = size.sp,
  lineHeight = line.sp,
  fontWeight = weight,
  letterSpacing = tracking.em,
)

/** nocturnal_storybook/DESIGN.md typography */
val NocturnalTypography: DiaryTypography = run {
  val sans = FontFamily.SansSerif
  DiaryTypography(
    headlineFamily = sans,
    bodyFamily = sans,
    display = style(sans, 32, 42, FontWeight.Bold, -0.02),
    headlineLg = style(sans, 28, 38, FontWeight.Bold, -0.015),
    headlineMd = style(sans, 22, 32, FontWeight.SemiBold, -0.01),
    headlineSm = style(sans, 18, 26, FontWeight.SemiBold),
    titleLg = style(sans, 18, 26, FontWeight.SemiBold),
    titleMd = style(sans, 16, 24, FontWeight.SemiBold),
    bodyLg = style(sans, 17, 28, FontWeight.Normal),
    bodyMd = style(sans, 15, 24, FontWeight.Normal),
    bodySm = style(sans, 13, 20, FontWeight.Normal),
    labelLg = style(sans, 14, 20, FontWeight.SemiBold, 0.01),
    labelMd = style(sans, 12, 16, FontWeight.SemiBold, 0.02),
    labelSm = style(sans, 11, 14, FontWeight.Medium, 0.03),
  )
}

/** starry_night_dragon_diary/DESIGN.md typography */
val StorybookTypography: DiaryTypography = run {
  val serif = FontFamily.Serif
  val sans = FontFamily.SansSerif
  DiaryTypography(
    headlineFamily = serif,
    bodyFamily = sans,
    display = style(serif, 32, 42, FontWeight.Bold, -0.01),
    headlineLg = style(serif, 24, 32, FontWeight.SemiBold),
    headlineMd = style(serif, 22, 30, FontWeight.SemiBold),
    headlineSm = style(serif, 18, 26, FontWeight.SemiBold),
    titleLg = style(serif, 18, 26, FontWeight.SemiBold),
    titleMd = style(sans, 16, 24, FontWeight.SemiBold),
    bodyLg = style(sans, 16, 26, FontWeight.Normal),
    bodyMd = style(sans, 14, 22, FontWeight.Normal),
    bodySm = style(sans, 12, 18, FontWeight.Normal),
    labelLg = style(sans, 13, 18, FontWeight.SemiBold, 0.02),
    labelMd = style(sans, 11, 16, FontWeight.SemiBold, 0.03),
    labelSm = style(sans, 10, 14, FontWeight.Bold, 0.05),
  )
}
