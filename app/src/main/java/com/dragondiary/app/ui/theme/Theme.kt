package com.dragondiary.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/** 앱 테마 모드. 설정 화면에서 고르고 SharedPreferences 에 저장한다. */
enum class ThemeMode { Light, Dark }

private val LocalDiaryColors = staticCompositionLocalOf { StorybookColors }
private val LocalDiaryTypography = staticCompositionLocalOf { StorybookTypography }
private val LocalDiaryShapes = staticCompositionLocalOf { StorybookShapes }

/** 컴포넌트에서 테마 토큰을 읽는 입구. `DiaryTheme.colors.gold` 처럼 쓴다. */
object DiaryTheme {
  val colors: DiaryColors
    @Composable @ReadOnlyComposable get() = LocalDiaryColors.current
  val type: DiaryTypography
    @Composable @ReadOnlyComposable get() = LocalDiaryTypography.current
  val shapes: DiaryShapes
    @Composable @ReadOnlyComposable get() = LocalDiaryShapes.current
  val isDark: Boolean
    @Composable @ReadOnlyComposable get() = LocalDiaryColors.current.isDark
}

@Composable
fun DragonDiaryTheme(
  mode: ThemeMode,
  /** 설정의 '글자 크기' (0.9 / 1.0 / 1.12) */
  textScale: Float = 1f,
  content: @Composable () -> Unit,
) {
  val colors = if (mode == ThemeMode.Dark) NocturnalColors else StorybookColors
  val type = if (mode == ThemeMode.Dark) NocturnalTypography else StorybookTypography
  val shapes = if (mode == ThemeMode.Dark) NocturnalShapes else StorybookShapes

  // 기본 Material 컴포넌트(텍스트 필드 커서, 스낵바 등)도 같은 팔레트를 따르게 한다
  val scheme = if (colors.isDark) {
    darkColorScheme(
      primary = colors.primaryContainer,
      onPrimary = colors.onPrimaryContainer,
      secondary = colors.secondary,
      background = colors.background,
      surface = colors.surface,
      onSurface = colors.onSurface,
      onSurfaceVariant = colors.onSurfaceVariant,
      inverseSurface = colors.surfaceContainerHighest,
      inverseOnSurface = colors.onSurface,
      inversePrimary = colors.gold,
      outline = colors.outline,
    )
  } else {
    lightColorScheme(
      primary = colors.primary,
      onPrimary = colors.onPrimary,
      secondary = colors.secondary,
      background = colors.background,
      surface = colors.surface,
      onSurface = colors.onSurface,
      onSurfaceVariant = colors.onSurfaceVariant,
      inverseSurface = colors.ink,
      inverseOnSurface = colors.background,
      inversePrimary = colors.primaryFixedDim,
      outline = colors.outline,
    )
  }

  val density = LocalDensity.current
  CompositionLocalProvider(
    LocalDiaryColors provides colors,
    LocalDiaryTypography provides type,
    LocalDiaryShapes provides shapes,
    LocalDensity provides Density(density.density, density.fontScale * textScale),
  ) {
    MaterialTheme(colorScheme = scheme, content = content)
  }
}
