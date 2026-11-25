package com.dragondiary.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FlutterDash
import androidx.compose.material.icons.outlined.HotelClass
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DragonColor
import com.dragondiary.app.data.DragonHorn
import com.dragondiary.app.data.TextSize
import com.dragondiary.app.ui.SettingsState
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.BrandMark
import com.dragondiary.app.ui.components.DragonOrbPlaceholder
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.NotificationButton
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.ProfileBadge
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.SoonTag
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme
import com.dragondiary.app.ui.theme.ThemeMode
import java.time.LocalDate

data class SettingsActions(
  val onTheme: (ThemeMode) -> Unit,
  val onTextSize: (TextSize) -> Unit,
  val onDragonColor: (DragonColor) -> Unit,
  val onDragonHorn: (DragonHorn) -> Unit,
)

/**
 * 설정 화면 (_6).
 * 실제로 동작: 테마 모드, 글자 크기, 아기 용 체색/뿔 미리보기 선택(기기에 저장).
 * 준비 중으로 표시: 서체 변경, 알림, PDF 내보내기, 백업, 약관, 의견 보내기, 프로필 수정.
 */
@Composable
fun SettingsScreen(
  settings: SettingsState,
  entryCount: Int,
  actions: SettingsActions,
  contentPadding: PaddingValues,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current

  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BrandMark()
      Spacer(Modifier.width(8.dp))
      Text("별밤용 일기", style = t.headlineMd.copy(fontSize = 20.sp), color = c.primary)
      Text("  |  ", style = t.bodySm, color = c.outlineVariant)
      Text("설정", style = t.labelLg, color = c.onSurface)
      Spacer(Modifier.width(4.dp))
      Text("(Grimoire)", style = t.labelSm.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = c.secondary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
      Box(
        Modifier.clip(CircleShape).background(c.surfaceContainer.copy(alpha = 0.6f)).border(1.dp, c.outlineVariant.copy(alpha = 0.3f), CircleShape),
      ) { NotificationButton({ notice(SOON) }) }
      Spacer(Modifier.width(8.dp))
      ProfileBadge { notice(SOON) }
    }

    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(top = 16.dp, bottom = contentPadding.calculateBottomPadding() + 40.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      ProfileCard(settings, entryCount)
      DragonCard(settings, actions)
      DisplayCard(settings, actions)
      NotificationCard()
      DataCard()
      AboutCard()
      Text(
        "별밤용 일기와 함께 오늘 하루도 포근히 안식하기를.",
        style = t.labelSm,
        color = c.outline,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
      )
    }
  }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
  val c = DiaryTheme.colors
  val shape = RoundedCornerShape(24.dp)
  Column(
    Modifier.fillMaxWidth()
      .glow(shape, c.shadow.copy(alpha = if (c.isDark) 0.5f else 0.08f), 4.dp, offsetY = 1.dp)
      .clip(shape)
      .background(if (c.isDark) c.surfaceContainer else c.surfaceContainerLowest)
      .then(if (c.isDark) Modifier.border(1.dp, c.cardBorder, shape) else Modifier)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    content = content,
  )
}

@Composable
private fun CardTitle(title: String, icon: ImageVector, boxed: Color? = null, iconTint: Color = DiaryTheme.colors.primary.let { if (DiaryTheme.isDark) DiaryTheme.colors.gold else it }, subtitle: String? = null) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Row(verticalAlignment = if (subtitle != null) Alignment.Top else Alignment.CenterVertically) {
    if (boxed != null) {
      Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(boxed), contentAlignment = Alignment.Center) {
        Icon(icon, null, Modifier.size(20.dp), tint = iconTint)
      }
    } else {
      Icon(icon, null, Modifier.size(22.dp), tint = iconTint)
    }
    Spacer(Modifier.width(8.dp))
    Column {
      Text(title, style = t.headlineSm, color = c.onSurface)
      if (subtitle != null) Text(subtitle, style = t.bodySm, color = c.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
    }
  }
}

@Composable
private fun ProfileCard(settings: SettingsState, entryCount: Int) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  SettingsCard {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // 아기 용 미리보기 자리 (원본: 얼굴 아이콘 + 별 배지)
      Box {
        Box(
          Modifier.size(56.dp).clip(CircleShape).background(c.secondaryFixed.copy(alpha = if (c.isDark) 0.15f else 0.5f))
            .border(1.dp, c.secondary.copy(alpha = 0.3f), CircleShape),
        ) {
          ArtFrame(
            DiaryArt.dragonFor(null, settings.dragonColor, LocalDate.now()),
            Modifier.size(56.dp).clip(CircleShape),
            contentDescription = "아기 용 미리보기",
          ) {
            DragonOrbPlaceholder(Icons.Outlined.Face, 28.dp, c.secondary, Color.Transparent)
          }
        }
        Icon(
          Icons.Outlined.Stars, null,
          Modifier.align(Alignment.BottomEnd).offset(2.dp, 2.dp).size(18.dp).clip(CircleShape).background(c.background),
          tint = c.primary.let { if (c.isDark) c.gold else it },
        )
      }
      Spacer(Modifier.width(16.dp))
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("나의 별밤 서재", style = t.headlineSm, color = c.onSurface)
          Spacer(Modifier.width(6.dp))
          Icon(Icons.Outlined.Verified, null, Modifier.size(18.dp), tint = if (c.isDark) c.gold else c.primary)
        }
        Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
          Pill(
            "이야기 ${entryCount}편",
            container = c.primaryFixed.let { if (c.isDark) c.gold.copy(alpha = 0.2f) else it },
            content = if (c.isDark) c.gold else c.onPrimaryFixed,
            border = c.primaryContainer.copy(alpha = 0.4f),
            style = t.labelSm.copy(fontWeight = FontWeight.SemiBold),
            horizontal = 8.dp,
          )
          Spacer(Modifier.width(8.dp))
          Text("별빛 기록을 엮는 중", style = t.bodySm, color = c.outline)
        }
      }
      Box(
        Modifier.size(36.dp).clip(CircleShape).background(c.surfaceContainerLow)
          .border(1.dp, c.outlineVariant.copy(alpha = 0.4f), CircleShape).clickable { notice(SOON) },
        contentAlignment = Alignment.Center,
      ) { Icon(Icons.Outlined.Edit, "프로필 수정", Modifier.size(18.dp), tint = c.onSurfaceVariant) }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DragonCard(settings: SettingsState, actions: SettingsActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  SettingsCard {
    CardTitle(
      "나의 아기 용 설정 (미리보기)",
      Icons.Outlined.Palette,
      boxed = c.primaryContainer.copy(alpha = 0.3f),
      subtitle = "생성될 아기 용의 바탕 체색과 수호 뿔 형태를 선택합니다.",
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(15.dp), tint = if (c.isDark) c.gold else c.primary)
        Spacer(Modifier.width(4.dp))
        Text("별빛 바탕 체색", style = t.labelMd, color = c.onSurface)
      }
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        DragonColor.entries.forEach { color ->
          Swatch(color, settings.dragonColor == color) { actions.onDragonColor(color) }
        }
      }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("뿔 및 수호 날개 형태", style = t.labelMd, color = c.onSurface)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DragonHorn.entries.forEach { horn ->
          val selected = settings.dragonHorn == horn
          Row(
            Modifier.clip(CircleShape)
              .background(if (selected) c.primaryContainer else c.surfaceContainer)
              .clickable { actions.onDragonHorn(horn) }
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              when (horn) {
                DragonHorn.Constellation -> Icons.Outlined.HotelClass
                DragonHorn.Wing -> Icons.Outlined.FlutterDash
                DragonHorn.Round -> Icons.Outlined.Verified
              },
              null, Modifier.size(16.dp),
              tint = if (selected) c.onPrimaryContainer.let { if (c.isDark) c.onPrimaryFixed else it } else c.onSurfaceVariant,
            )
            Spacer(Modifier.width(6.dp))
            Text(
              horn.label,
              style = t.labelMd,
              color = if (selected) c.onPrimaryContainer.let { if (c.isDark) c.onPrimaryFixed else it } else c.onSurfaceVariant,
              fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
          }
        }
      }
    }
    Row(
      Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(c.tertiaryFixed.copy(alpha = if (c.isDark) 0.08f else 0.3f))
        .border(1.dp, c.tertiaryContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        .padding(12.dp),
      verticalAlignment = Alignment.Top,
    ) {
      Icon(Icons.Outlined.AutoStories, null, Modifier.padding(top = 2.dp).size(18.dp), tint = if (c.isDark) c.tertiaryContainer else c.tertiary)
      Spacer(Modifier.width(8.dp))
      Text(
        androidx.compose.ui.text.buildAnnotatedString {
          pushStyle(androidx.compose.ui.text.SpanStyle(color = if (c.isDark) c.tertiaryContainer else c.tertiary, fontWeight = FontWeight.SemiBold))
          append("※ 별밤 감정 조율: ")
          pop()
          append("오늘 밤 작성된 일기의 감정에 따라 아기 용의 날개와 보석 빛깔이 부드럽게 물들 예정이에요. (감정 분석 기능 준비 중)")
        },
        style = t.bodySm.copy(lineHeight = 19.sp),
        color = c.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun Swatch(color: DragonColor, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val (fill, check) = when (color) {
    DragonColor.Amber -> c.primaryContainer to c.onPrimaryContainer
    DragonColor.Violet -> c.secondaryContainer to c.onSecondaryContainer
    DragonColor.Peach -> c.tertiaryContainer to c.onTertiaryContainer
    DragonColor.Cream -> c.primaryFixed to c.onPrimaryFixed
    DragonColor.Moss -> c.surfaceContainerHighest to c.onSurface
  }
  Column(
    Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(2.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Box(
      Modifier.size(52.dp).then(
        if (selected) Modifier.border(2.dp, if (c.isDark) c.gold else c.primary, CircleShape) else Modifier,
      ),
      contentAlignment = Alignment.Center,
    ) {
      Box(
        Modifier.size(if (selected) 44.dp else 48.dp).clip(CircleShape).background(fill)
          .border(1.dp, c.outlineVariant.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center,
      ) { if (selected) Icon(Icons.Outlined.Check, null, Modifier.size(20.dp), tint = check) }
    }
    Text(
      color.label,
      style = t.labelSm,
      color = if (selected) (if (c.isDark) c.gold else c.primary) else c.outline,
      fontWeight = if (selected) FontWeight.SemiBold else null,
    )
  }
}

@Composable
private fun DisplayCard(settings: SettingsState, actions: SettingsActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  SettingsCard {
    CardTitle("화면 및 서재 설정", Icons.AutoMirrored.Outlined.MenuBook, boxed = c.secondaryFixed.copy(alpha = if (c.isDark) 0.15f else 0.6f), iconTint = c.secondary)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("테마 모드", style = t.labelMd, color = c.onSurface)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeOption(
          "따스한 양장본", "라이트 테마 (기본)", Icons.Outlined.LightMode,
          settings.themeMode == ThemeMode.Light, Modifier.weight(1f),
        ) { actions.onTheme(ThemeMode.Light) }
        ThemeOption(
          "아늑한 별밤", "다크 테마", Icons.Outlined.Bedtime,
          settings.themeMode == ThemeMode.Dark, Modifier.weight(1f),
        ) { actions.onTheme(ThemeMode.Dark) }
      }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("동화책 본문 서체", style = t.labelMd, color = c.onSurface)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val shape = RoundedCornerShape(16.dp)
        Row(
          Modifier.weight(1f).clip(shape).background(c.secondaryContainer).border(1.dp, c.secondary.copy(alpha = 0.3f), shape).padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(Modifier.weight(1f)) {
            Text("포근한 기본체", style = t.labelMd, color = c.onSecondaryContainer, fontWeight = FontWeight.SemiBold)
            Text("지금 쓰는 서체", style = t.labelSm, color = c.onSecondaryContainer.copy(alpha = 0.8f))
          }
          Icon(Icons.Outlined.CheckCircle, null, Modifier.size(18.dp), tint = c.onSecondaryContainer)
        }
        Row(
          Modifier.weight(1f).clip(shape).background(c.surfaceContainer).clickable { notice("서체 파일이 준비되면 고를 수 있어요") }.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(Modifier.weight(1f)) {
            Text("단정한 고딕", style = t.labelMd, color = c.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            Text("깔끔하고 정돈된 가독성", style = t.labelSm, color = c.outline)
          }
          SoonTag()
        }
      }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("글자 크기", style = t.labelMd, color = c.onSurface, modifier = Modifier.weight(1f))
        Text(settings.textSize.label, style = t.labelMd, color = if (c.isDark) c.gold else c.primary, fontWeight = FontWeight.SemiBold)
      }
      Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("작게", style = t.labelSm, color = c.outline)
        Slider(
          value = settings.textSize.ordinal.toFloat(),
          onValueChange = { actions.onTextSize(TextSize.entries[it.toInt().coerceIn(0, 2)]) },
          valueRange = 0f..2f,
          steps = 1,
          modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
          colors = SliderDefaults.colors(
            thumbColor = if (c.isDark) c.gold else c.primary,
            activeTrackColor = if (c.isDark) c.gold else c.primary,
            inactiveTrackColor = c.track,
            activeTickColor = c.track,
            inactiveTickColor = c.outlineVariant,
          ),
        )
        Text("크게", style = t.labelSm, color = c.outline)
      }
    }
  }
}

@Composable
private fun ThemeOption(title: String, sub: String, icon: ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = RoundedCornerShape(16.dp)
  val accent = if (c.isDark) c.gold else c.primary
  Column(
    modifier
      .clip(shape)
      .background(if (selected) c.surfaceContainerLow else c.surfaceContainer)
      .then(if (selected) Modifier.border(2.dp, accent.copy(alpha = 0.5f), shape) else Modifier)
      .clickable(onClick = onClick)
      .padding(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Box(
      Modifier.size(32.dp).clip(CircleShape).background(if (selected) c.primaryContainer else c.surfaceContainerHighest),
      contentAlignment = Alignment.Center,
    ) {
      Icon(icon, null, Modifier.size(20.dp), tint = if (selected) c.onPrimaryContainer.let { if (c.isDark) c.onPrimaryFixed else it } else c.onSurfaceVariant)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(title, style = t.labelMd, color = if (selected) accent else c.onSurfaceVariant, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
      Text(sub, style = t.labelSm, color = c.outline)
    }
  }
}

@Composable
private fun NotificationCard() {
  val c = DiaryTheme.colors
  SettingsCard {
    CardTitle("알림 및 기록 습관", Icons.Outlined.NotificationsActive)
    Column {
      ToggleRow("매일 밤 일기 작성 알림", "오후 9시 30분, 따뜻한 안부와 함께 도착해요")
      Box(Modifier.fillMaxWidth().height(1.dp).background(c.surfaceContainerHighest))
      ToggleRow("삽화 생성 완료 시 푸시 알림", "아기 용이 그림을 완성하면 즉시 알려드립니다")
    }
  }
}

/** 알림 기능이 아직 없어 꺼진 채로 비활성화하고 '준비 중'을 붙인다. */
@Composable
private fun ToggleRow(title: String, sub: String) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  Row(
    Modifier.fillMaxWidth().clickable { notice("알림 기능은 아직 준비 중이에요") }.padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = t.labelLg, color = c.onSurface)
        Spacer(Modifier.width(6.dp))
        SoonTag()
      }
      Text(sub, style = t.bodySm, color = c.onSurfaceVariant)
    }
    Switch(
      checked = false,
      onCheckedChange = null,
      enabled = false,
      modifier = Modifier.alpha(0.7f),
      colors = SwitchDefaults.colors(
        disabledUncheckedTrackColor = c.track,
        disabledUncheckedThumbColor = c.surfaceContainerLowest,
        disabledUncheckedBorderColor = c.track,
      ),
    )
  }
}

@Composable
private fun DataCard() {
  val c = DiaryTheme.colors
  SettingsCard {
    CardTitle("데이터 및 책장 내보내기", Icons.AutoMirrored.Outlined.MenuBook)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.offset(y = (-4).dp)) {
      DataRow("나의 동화책 PDF로 내보내기", "기록된 삽화와 이야기를 소장용 책으로 제본", Icons.Outlined.PictureAsPdf, c.tertiaryContainer.copy(alpha = 0.4f), c.onTertiaryContainer)
      DataRow("데이터 백업 및 복원", "소중한 감정 기록을 안전하게 동기화", Icons.Outlined.CloudSync, c.primaryFixed.let { if (c.isDark) c.gold.copy(alpha = 0.2f) else it }, if (c.isDark) c.gold else c.onPrimaryFixed)
    }
  }
}

@Composable
private fun DataRow(title: String, sub: String, icon: ImageVector, orb: Color, tint: Color) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  val shape = RoundedCornerShape(16.dp)
  Row(
    Modifier.fillMaxWidth().clip(shape).background(c.surfaceContainerLow).clickable { notice(SOON) }.padding(14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(Modifier.size(40.dp).clip(CircleShape).background(orb), contentAlignment = Alignment.Center) {
      Icon(icon, null, Modifier.size(20.dp), tint = tint)
    }
    Spacer(Modifier.width(12.dp))
    Column(Modifier.weight(1f)) {
      Text(title, style = t.labelLg, color = c.onSurface)
      Text(sub, style = t.bodySm, color = c.outline)
    }
    SoonTag()
    Icon(Icons.Outlined.ChevronRight, null, Modifier.size(20.dp), tint = c.outline)
  }
}

@Composable
private fun AboutCard() {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  val context = LocalContext.current
  val version = remember {
    runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "-"
  }
  SettingsCard {
    CardTitle("앱 정보", Icons.Outlined.Info)
    Column(Modifier.offset(y = (-6).dp)) {
      Row(Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("버전 정보", style = t.bodyMd, color = c.onSurface, modifier = Modifier.weight(1f))
        Text("$version (Storybook Edition)", style = t.labelMd, color = c.outline)
      }
      LinkRow("이용약관 및 개인정보 처리방침", Icons.Outlined.ChevronRight) { notice(SOON) }
      LinkRow("개발자에게 의견 보내기", Icons.AutoMirrored.Outlined.OpenInNew) { notice(SOON) }
    }
  }
}

@Composable
private fun LinkRow(text: String, icon: ImageVector, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Row(
    Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(vertical = 8.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text, style = t.bodyMd, color = c.onSurface, modifier = Modifier.weight(1f))
    Icon(icon, null, Modifier.size(18.dp), tint = c.outline)
  }
}
