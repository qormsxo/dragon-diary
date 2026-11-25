package com.dragondiary.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 테마별 모서리 곡률과 여백.
 * 다크는 roundedness 3 (구름 같은 큰 곡률), 라이트는 roundedness 2 (낡은 책 모서리).
 */
@Immutable
data class DiaryShapes(
  /** 화면 섹션 카드 (다크 rounded-lg 2rem / 라이트 rounded-3xl 1.5rem) */
  val section: RoundedCornerShape,
  /** 목록 카드 (다크 2rem / 라이트 rounded-2xl 1rem) */
  val listCard: RoundedCornerShape,
  /** 내부 패널, 입력창, 삽화 자리 (다크 1rem / 라이트 1rem) */
  val panel: RoundedCornerShape,
  /** 작은 썸네일 (다크 1rem / 라이트 rounded-xl 0.75rem) */
  val thumb: RoundedCornerShape,
  /** 주요 CTA (다크 pill / 라이트 rounded-2xl) */
  val cta: RoundedCornerShape,
  val pill: RoundedCornerShape,
  val screenMargin: Dp,
)

val NocturnalShapes = DiaryShapes(
  section = RoundedCornerShape(32.dp),
  listCard = RoundedCornerShape(32.dp),
  panel = RoundedCornerShape(16.dp),
  thumb = RoundedCornerShape(16.dp),
  cta = RoundedCornerShape(percent = 50),
  pill = RoundedCornerShape(percent = 50),
  screenMargin = 20.dp,
)

val StorybookShapes = DiaryShapes(
  section = RoundedCornerShape(24.dp),
  listCard = RoundedCornerShape(16.dp),
  panel = RoundedCornerShape(16.dp),
  thumb = RoundedCornerShape(12.dp),
  cta = RoundedCornerShape(16.dp),
  pill = RoundedCornerShape(percent = 50),
  screenMargin = 20.dp,
)
