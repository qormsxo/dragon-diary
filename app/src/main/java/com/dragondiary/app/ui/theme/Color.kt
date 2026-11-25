package com.dragondiary.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * 테마 하나가 갖는 색 토큰 전체.
 *
 * 값은 Stitch 디자인 가이드에서 그대로 옮겨 왔다.
 * - 다크: stitch_design/nocturnal_storybook/DESIGN.md (+ _1, _2, _3 code.html)
 * - 라이트: stitch_design/starry_night_dragon_diary/DESIGN.md (+ _4, _5, _8, _9 code.html)
 *
 * 컴포넌트는 hex 값을 직접 쓰지 않고 반드시 이 토큰을 통해 색을 얻는다.
 */
@Immutable
data class DiaryColors(
  val isDark: Boolean,

  // Material 3 역할 (디자인 가이드의 colors 블록)
  val background: Color,
  val surface: Color,
  val surfaceContainerLowest: Color,
  val surfaceContainerLow: Color,
  val surfaceContainer: Color,
  val surfaceContainerHigh: Color,
  val surfaceContainerHighest: Color,
  val surfaceDim: Color,
  val onSurface: Color,
  val onSurfaceVariant: Color,
  val outline: Color,
  val outlineVariant: Color,
  val primary: Color,
  val onPrimary: Color,
  val primaryContainer: Color,
  val onPrimaryContainer: Color,
  val primaryFixed: Color,
  val primaryFixedDim: Color,
  val onPrimaryFixed: Color,
  val secondary: Color,
  val onSecondary: Color,
  val secondaryContainer: Color,
  val onSecondaryContainer: Color,
  val secondaryFixed: Color,
  val secondaryFixedDim: Color,
  val tertiary: Color,
  val tertiaryContainer: Color,
  val onTertiaryContainer: Color,
  val tertiaryFixed: Color,
  val error: Color,

  // 화면 공통 의미 토큰 (가이드 본문 + 각 화면 HTML에서 반복되는 값)
  /** 제목/본문 강조 잉크. 다크는 크림 금빛, 라이트는 짙은 갈색 잉크 */
  val ink: Color,
  /** 빛나는 금색 포인트 (아이콘, 점, 오늘 표시) */
  val gold: Color,
  /** 금색 위 글자색 (CTA 텍스트) */
  val onGold: Color,
  /** CTA 그라데이션 (라이트는 3색 금박, 다크는 단색) */
  val ctaGradient: List<Color>,
  /** 카드 테두리 (라이트: 고운 금박 선, 다크: 5% 흰빛 헤어라인) */
  val cardBorder: Color,
  /** 카드 바탕 */
  val card: Color,
  /** 그림자/광채 색 */
  val glow: Color,
  val shadow: Color,
  /** 이미지 자리(삽화/드래곤) 플레이스홀더 */
  val artPlaceholder: Color,
  val artPlaceholderBorder: Color,
  val artPlaceholderIcon: Color,
  /** 상·하단 바 바탕 */
  val bar: Color,
  val barBorder: Color,
  /** 달력 요일 색 */
  val sunday: Color,
  val saturday: Color,
  /** 감정 톤 (기록 점, 감정 배지) */
  val toneGold: Color,
  val toneViolet: Color,
  val tonePeach: Color,
  val toneGoldContainer: Color,
  val onToneGoldContainer: Color,
  val toneVioletContainer: Color,
  val onToneVioletContainer: Color,
  val tonePeachContainer: Color,
  val onTonePeachContainer: Color,
  /** 스위치/슬라이더 비활성 트랙 */
  val track: Color,
)

/** Nocturnal Storybook — 깊은 남색 밤하늘, 별빛 금색 */
val NocturnalColors = DiaryColors(
  isDark = true,
  background = Color(0xFF0F1321),
  surface = Color(0xFF0F1321),
  surfaceContainerLowest = Color(0xFF090E1B),
  surfaceContainerLow = Color(0xFF171B29),
  surfaceContainer = Color(0xFF1B1F2D),
  surfaceContainerHigh = Color(0xFF252938),
  surfaceContainerHighest = Color(0xFF303443),
  surfaceDim = Color(0xFF0F1321),
  onSurface = Color(0xFFDFE1F6),
  onSurfaceVariant = Color(0xFFD1C5B1),
  outline = Color(0xFF9A8F7D),
  outlineVariant = Color(0xFF4E4636),
  primary = Color(0xFFFFE8BC),
  onPrimary = Color(0xFF3F2E00),
  primaryContainer = Color(0xFFF6C85F),
  onPrimaryContainer = Color(0xFF705300),
  primaryFixed = Color(0xFFFFDF9D),
  primaryFixedDim = Color(0xFFEEC058),
  onPrimaryFixed = Color(0xFF251A00),
  secondary = Color(0xFFCEBEFA),
  onSecondary = Color(0xFF35285A),
  secondaryContainer = Color(0xFF4E4174),
  onSecondaryContainer = Color(0xFFC0B0EB),
  secondaryFixed = Color(0xFFE8DDFF),
  secondaryFixedDim = Color(0xFFCEBEFA),
  tertiary = Color(0xFFFFE4E3),
  tertiaryContainer = Color(0xFFFFBEBD),
  onTertiaryContainer = Color(0xFF874445),
  tertiaryFixed = Color(0xFFFFDAD9),
  error = Color(0xFFFFB4AB),

  ink = Color(0xFFFFE8BC),
  gold = Color(0xFFF6C85F),
  onGold = Color(0xFF705300),
  ctaGradient = listOf(Color(0xFFF6C85F), Color(0xFFF6C85F)),
  cardBorder = Color(0x0DFFFFFF),
  card = Color(0xFF1B1F2D),
  glow = Color(0xFFF6C85F),
  shadow = Color(0xFF090E1B),
  artPlaceholder = Color(0xFF171B29),
  artPlaceholderBorder = Color(0x00000000),
  artPlaceholderIcon = Color(0xFFF6C85F),
  bar = Color(0xD90F1321),
  barBorder = Color(0x00000000),
  sunday = Color(0x99D1C5B1),
  saturday = Color(0x99D1C5B1),
  toneGold = Color(0xFFF6C85F),
  toneViolet = Color(0xFFCEBEFA),
  tonePeach = Color(0xFFFFBEBD),
  toneGoldContainer = Color(0x33F6C85F),
  onToneGoldContainer = Color(0xFFF6C85F),
  toneVioletContainer = Color(0x994E4174),
  onToneVioletContainer = Color(0xFFCEBEFA),
  tonePeachContainer = Color(0x33FFBEBD),
  onTonePeachContainer = Color(0xFFFFBEBD),
  track = Color(0xFF303443),
)

/** Starry Night Dragon Diary — 아이보리 양피지, 황금 잉크 */
val StorybookColors = DiaryColors(
  isDark = false,
  background = Color(0xFFFFF9EF),
  surface = Color(0xFFFFF9EF),
  surfaceContainerLowest = Color(0xFFFFFFFF),
  surfaceContainerLow = Color(0xFFF9F3EA),
  surfaceContainer = Color(0xFFF3EDE4),
  surfaceContainerHigh = Color(0xFFEDE7DE),
  surfaceContainerHighest = Color(0xFFE7E2D9),
  surfaceDim = Color(0xFFDFD9D1),
  onSurface = Color(0xFF1D1B16),
  onSurfaceVariant = Color(0xFF504535),
  outline = Color(0xFF827563),
  outlineVariant = Color(0xFFD4C4B0),
  primary = Color(0xFF7E5700),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFE5A93C),
  onPrimaryContainer = Color(0xFF5E4000),
  primaryFixed = Color(0xFFFFDEAD),
  primaryFixedDim = Color(0xFFFABC4D),
  onPrimaryFixed = Color(0xFF281900),
  secondary = Color(0xFF645786),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFD6C7FD),
  onSecondaryContainer = Color(0xFF5D517F),
  secondaryFixed = Color(0xFFE8DDFF),
  secondaryFixedDim = Color(0xFFCEBEF5),
  tertiary = Color(0xFF8D4E2E),
  tertiaryContainer = Color(0xFFF0A07B),
  onTertiaryContainer = Color(0xFF6E3618),
  tertiaryFixed = Color(0xFFFFDBCC),
  error = Color(0xFFBA1A1A),

  ink = Color(0xFF281900),
  gold = Color(0xFFE5A93C),
  onGold = Color(0xFF281900),
  ctaGradient = listOf(Color(0xFFE5A93C), Color(0xFFECB44E), Color(0xFFD99525)),
  cardBorder = Color(0xFFEDE3D2),
  card = Color(0xFFFFFFFF),
  glow = Color(0xFFE5A93C),
  shadow = Color(0xFFB4916E),
  artPlaceholder = Color(0xFFF7F1E5),
  artPlaceholderBorder = Color(0xB3D4C4B0),
  artPlaceholderIcon = Color(0xFF7E5700),
  bar = Color(0xF2FFF9EF),
  barBorder = Color(0xFFEDE3D2),
  sunday = Color(0xCCBA1A1A),
  saturday = Color(0xFF645786),
  toneGold = Color(0xFFE5A93C),
  toneViolet = Color(0xFF7C6FA0),
  tonePeach = Color(0xFFF0A07B),
  toneGoldContainer = Color(0xFFFFDEAD),
  onToneGoldContainer = Color(0xFF604100),
  toneVioletContainer = Color(0xFFE8DDFF),
  onToneVioletContainer = Color(0xFF4B406D),
  tonePeachContainer = Color(0xFFFFDBCC),
  onTonePeachContainer = Color(0xFF703719),
  track = Color(0xFFE7E2D9),
)
