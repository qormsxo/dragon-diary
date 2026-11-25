package com.dragondiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.ui.theme.DiaryTheme

/** 하단 탭 */
enum class MainTab { Home, Archive, Settings }

/** 상단바 공통 틀: 상태바 여백 + 반투명 바탕 + (라이트) 아래 헤어라인 */
@Composable
fun TopBarFrame(
  modifier: Modifier = Modifier,
  height: Int = 64,
  content: @Composable RowScope.() -> Unit,
) {
  val c = DiaryTheme.colors
  Column(
    modifier
      .fillMaxWidth()
      .then(
        if (c.isDark) Modifier.glow(RectangleShape, c.shadow.copy(alpha = 0.4f), 24.dp, offsetY = 4.dp)
        else Modifier,
      )
      .background(c.bar)
      .statusBarsPadding(),
  ) {
    Row(
      Modifier.fillMaxWidth().height(height.dp).padding(horizontal = DiaryTheme.shapes.screenMargin),
      verticalAlignment = Alignment.CenterVertically,
      content = content,
    )
    if (!c.isDark) Box(Modifier.fillMaxWidth().height(1.dp).background(c.barBorder.copy(alpha = 0.6f)))
  }
}

/** 별 문장 원형 로고 (`w-8 h-8 rounded-full ... auto_awesome`) */
@Composable
fun BrandMark() {
  val c = DiaryTheme.colors
  if (c.isDark) {
    IconOrb(
      Icons.Outlined.AutoAwesome, 32.dp, 18.dp,
      container = c.gold.copy(alpha = 0.2f), tint = c.gold, glowColor = c.glow.copy(alpha = 0.3f),
    )
  } else {
    IconOrb(
      Icons.Outlined.AutoAwesome, 32.dp, 19.dp,
      container = c.primaryFixed.copy(alpha = 0.75f), tint = c.primary, border = c.gold.copy(alpha = 0.3f),
    )
  }
}

/** 오른쪽 위 프로필/드래곤 동그라미. 프로필 기능은 아직 없어 그림 자리만 둔다. */
@Composable
fun ProfileBadge(onClick: () -> Unit = {}) {
  val c = DiaryTheme.colors
  if (c.isDark) {
    Box(
      Modifier
        .size(32.dp)
        .glow(CircleShape, c.glow.copy(alpha = 0.25f), 12.dp)
        .clip(CircleShape)
        .background(c.primary)
        .clickable(onClick = onClick),
      contentAlignment = Alignment.Center,
    ) { Icon(Icons.Outlined.Person, "프로필", Modifier.size(18.dp), tint = c.onPrimary) }
  } else {
    Box(
      Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(Brush.linearGradient(listOf(c.gold.copy(alpha = 0.5f), c.secondaryContainer.copy(alpha = 0.5f))))
        .clickable(onClick = onClick)
        .padding(2.dp),
      contentAlignment = Alignment.Center,
    ) {
      Box(Modifier.size(28.dp).clip(CircleShape).background(c.surfaceContainer), contentAlignment = Alignment.Center) {
        Icon(Icons.Outlined.Person, "프로필", Modifier.size(16.dp), tint = c.outline)
      }
    }
  }
}

@Composable
fun NotificationButton(onClick: () -> Unit, showDot: Boolean = false) {
  val c = DiaryTheme.colors
  Box(
    Modifier.size(if (c.isDark) 44.dp else 40.dp).clip(CircleShape).clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Icon(Icons.Outlined.Notifications, "알림", Modifier.size(22.dp), tint = c.onSurfaceVariant)
    if (showDot) {
      Box(Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp).size(6.dp).clip(CircleShape).background(c.gold))
    }
  }
}

/** 뒤로 가기 버튼. 다크/라이트(_4)는 iOS 꺾쇠, 라이트(_7, _9)는 화살표. */
@Composable
fun BackButton(onBack: () -> Unit, arrow: Boolean = false) {
  val c = DiaryTheme.colors
  Box(
    Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onBack, role = Role.Button),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      if (arrow) Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.ArrowBackIosNew,
      "뒤로 가기",
      Modifier.size(if (arrow) 24.dp else 22.dp),
      tint = when {
        arrow -> c.onSurfaceVariant
        c.isDark -> c.gold
        else -> c.primary
      },
    )
  }
}

/**
 * 하단 내비게이션.
 * 다크(_1): 아이콘+라벨, 선택 시 금빛 글자와 광채.
 * 라이트(_5, _6, _8): 선택 탭이 금색 알약으로 채워짐.
 */
@Composable
fun DiaryBottomBar(current: MainTab, onSelect: (MainTab) -> Unit) {
  val c = DiaryTheme.colors
  val items = if (c.isDark) {
    listOf(
      Triple(MainTab.Home, Icons.Outlined.Cottage, "홈"),
      Triple(MainTab.Archive, Icons.Outlined.AutoStories, "기록 보관소"),
      Triple(MainTab.Settings, Icons.Outlined.Tune, "설정"),
    )
  } else {
    listOf(
      Triple(MainTab.Home, Icons.Outlined.AutoStories, if (current == MainTab.Home) "별밤 홈" else "홈"),
      Triple(MainTab.Archive, Icons.Outlined.PhotoLibrary, "기록 보관소"),
      Triple(MainTab.Settings, Icons.Outlined.Settings, "설정"),
    )
  }
  Column(
    Modifier
      .fillMaxWidth()
      .glow(RectangleShape, c.shadow.copy(alpha = if (c.isDark) 0.7f else 0.09f), if (c.isDark) 32.dp else 20.dp, offsetY = (-6).dp)
      .background(c.bar)
      .drawBehind {
        if (!c.isDark) drawLine(c.barBorder, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
      }
      .navigationBarsPadding(),
  ) {
    Row(
      Modifier.fillMaxWidth().height(if (c.isDark) 80.dp else 64.dp).padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      items.forEach { (tab, icon, label) -> NavItem(icon, label, tab == current) { onSelect(tab) } }
    }
  }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  if (c.isDark) {
    val tint = if (selected) c.gold else c.onSurfaceVariant
    Column(
      Modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick, role = Role.Tab)
        .defaultMinSize(minWidth = 56.dp, minHeight = 44.dp)
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(icon, null, Modifier.size(24.dp), tint = tint)
      Text(
        label,
        style = DiaryTheme.type.labelMd,
        color = tint,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
      )
    }
  } else {
    val shape = RoundedCornerShape(16.dp)
    Column(
      Modifier
        .then(if (selected) Modifier.glow(shape, c.glow.copy(alpha = 0.35f), 8.dp, offsetY = 2.dp) else Modifier)
        .clip(shape)
        .background(if (selected) c.primaryContainer else c.bar.copy(alpha = 0f))
        .clickable(onClick = onClick, role = Role.Tab)
        .defaultMinSize(minWidth = 76.dp, minHeight = 44.dp)
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      val tint = if (selected) c.onPrimaryFixed else c.secondary
      Icon(icon, null, Modifier.size(21.dp), tint = tint)
      Text(
        label,
        style = DiaryTheme.type.bodySm.copy(fontSize = 11.sp, lineHeight = 14.sp),
        fontFamily = if (selected) DiaryTheme.type.headlineFamily else DiaryTheme.type.bodyFamily,
        color = tint,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      )
    }
  }
}

/** 상단바 브랜드 묶음: 로고 + '별밤용 일기' + 보조 표기 */
@Composable
fun BrandTitle(subtitle: @Composable () -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    BrandMark()
    Spacer(Modifier.width(8.dp))
    subtitle()
  }
}

/** 상단바 오른쪽 묶음 */
@Composable
fun TopBarActions(content: @Composable RowScope.() -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), content = content)
}

/** 라이트 테마 상단바 오른쪽의 작은 아이콘 동그라미 (_4 auto_stories, _8 magic_button, _9 menu_book) */
@Composable
fun SmallIconBadge(icon: ImageVector, onClick: () -> Unit, description: String? = null) {
  val c = DiaryTheme.colors
  Box(
    Modifier
      .size(32.dp)
      .clip(CircleShape)
      .background(if (c.isDark) c.surfaceContainerHigh else c.surfaceContainerHigh)
      .border(1.dp, c.gold.copy(alpha = 0.4f), CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) { Icon(icon, description, Modifier.size(18.dp), tint = if (c.isDark) c.gold else c.primary) }
}
