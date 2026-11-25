package com.dragondiary.app.ui.screens.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.HotelClass
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.DiaryEntry
import com.dragondiary.app.data.DragonColor
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.ui.art.ArtFrame
import com.dragondiary.app.ui.art.ArtPlace
import com.dragondiary.app.ui.art.DiaryArt
import com.dragondiary.app.ui.components.AmbientBlob
import com.dragondiary.app.ui.components.BrandMark
import com.dragondiary.app.ui.components.DragonOrbPlaceholder
import com.dragondiary.app.ui.components.IconOrb
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.MonthCalendar
import com.dragondiary.app.ui.components.NightScenePlaceholder
import com.dragondiary.app.ui.components.NotificationButton
import com.dragondiary.app.ui.components.ParchmentScenePlaceholder
import com.dragondiary.app.ui.components.Pill
import com.dragondiary.app.ui.components.PrimaryCta
import com.dragondiary.app.ui.components.ProfileBadge
import com.dragondiary.app.ui.components.RecentEntryCard
import com.dragondiary.app.ui.components.SOON
import com.dragondiary.app.ui.components.SectionCard
import com.dragondiary.app.ui.components.SeeAllLink
import com.dragondiary.app.ui.components.TopBarActions
import com.dragondiary.app.ui.components.TopBarFrame
import com.dragondiary.app.ui.components.glow
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/** 홈 화면에서 필요한 사용자 동작 */
data class HomeActions(
  val onWrite: () -> Unit,
  val onOpenEntry: (Long) -> Unit,
  val onSeeAll: () -> Unit,
)

/**
 * 홈 화면. 다크(_1)와 라이트(_5)는 같은 정보와 동작을 갖되 배치가 달라 각각 그린다.
 */
@Composable
fun HomeScreen(
  entries: List<DiaryEntry>,
  dragonColor: DragonColor,
  actions: HomeActions,
  contentPadding: PaddingValues,
) {
  val today = remember { LocalDate.now() }
  var month by remember { mutableStateOf(YearMonth.from(today)) }
  val records: Map<LocalDate, MoodTone?> = remember(entries) {
    entries.groupBy { it.date }.mapValues { (_, list) -> list.first().mood?.tone }
  }
  val todayEntry = entries.firstOrNull { it.date == today }
  val recent = entries.take(3)
  val onDayClick: (LocalDate) -> Unit = { d ->
    val e = entries.firstOrNull { it.date == d }
    if (e != null) actions.onOpenEntry(e.id) else if (d == today) actions.onWrite()
  }
  val state = HomeState(today, month, records, todayEntry, recent, dragonColor)
  val calendar = CalendarActions({ month = month.minusMonths(1) }, { month = month.plusMonths(1) }, onDayClick)

  if (DiaryTheme.isDark) NightHome(state, actions, calendar, contentPadding)
  else StorybookHome(state, actions, calendar, contentPadding)
}

private data class HomeState(
  val today: LocalDate,
  val month: YearMonth,
  val records: Map<LocalDate, MoodTone?>,
  val todayEntry: DiaryEntry?,
  val recent: List<DiaryEntry>,
  val dragonColor: DragonColor,
)

private data class CalendarActions(val prev: () -> Unit, val next: () -> Unit, val day: (LocalDate) -> Unit)

private fun LocalDate.koreanFull(): String =
  "${year}년 ${monthValue}월 ${dayOfMonth}일 ${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)}"

@Composable
private fun pulse(): Float {
  val t = rememberInfiniteTransition(label = "pulse")
  val a by t.animateFloat(1f, 0.45f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "a")
  return a
}

// ---------------------------------------------------------------- 다크 (_1)

@Composable
private fun NightHome(s: HomeState, actions: HomeActions, cal: CalendarActions, contentPadding: PaddingValues) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame {
      BrandMark()
      Spacer(Modifier.width(8.dp))
      Column(Modifier.weight(1f)) {
        Text("별밤용 일기", style = t.headlineSm, color = c.primary, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(6.dp).alpha(pulse()).clip(CircleShape).background(c.gold))
          Spacer(Modifier.width(4.dp))
          Text("Home", style = t.labelSm, color = c.onSurfaceVariant)
        }
      }
      TopBarActions {
        NotificationButton({ notice(SOON) })
        ProfileBadge { notice(SOON) }
      }
    }
    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = DiaryTheme.shapes.screenMargin)
        .padding(top = 16.dp, bottom = contentPadding.calculateBottomPadding() + 32.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      NightGreeting(s)
      MonthCalendar(s.month, s.today, s.records, cal.prev, cal.next, cal.day)
      NightTodayCard(s, actions)
      NightRecent(s, actions)
    }
  }
}

@Composable
private fun NightGreeting(s: HomeState) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Box(
    Modifier
      .fillMaxWidth()
      .glow(DiaryTheme.shapes.section, c.shadow.copy(alpha = 0.8f), 32.dp, offsetY = 12.dp)
      .clip(DiaryTheme.shapes.section)
      .background(c.surfaceContainer),
  ) {
    AmbientBlob(c.gold.copy(alpha = 0.10f), 144.dp, 24.dp, 24.dp, alignRight = true, alignBottom = true)
    AmbientBlob(c.secondaryContainer.copy(alpha = 0.25f), 112.dp, (-40).dp, (-40).dp)
    Column(Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Pill(
            "수면 전 마인드 케어",
            container = c.secondaryContainer.copy(alpha = 0.6f),
            content = c.secondary,
            icon = Icons.Outlined.Bedtime,
            horizontal = 10.dp,
            vertical = 4.dp,
          )
          Text(
            "오늘 밤도 수고했어요 🌙",
            style = t.headlineMd,
            color = c.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
          )
          Text("하루의 조각을 아기 용에게 건네보세요.", style = t.bodyMd, color = c.onSurfaceVariant)
        }
        Spacer(Modifier.width(16.dp))
        // 오늘의 드래곤 자리 (원본: 64dp 원 안의 불꽃 + Lv 배지)
        Box(
          Modifier.size(64.dp).glow(CircleShape, c.glow.copy(alpha = 0.2f), 20.dp).clip(CircleShape).background(c.surfaceContainerHigh),
          contentAlignment = Alignment.Center,
        ) {
          ArtFrame(
            DiaryArt.dragonFor(s.todayEntry?.mood, s.dragonColor, s.today),
            Modifier.size(48.dp).clip(CircleShape),
            contentDescription = "오늘의 아기 용",
          ) {
            Box(Modifier.alpha(pulse())) {
              DragonOrbPlaceholder(Icons.Outlined.LocalFireDepartment, 28.dp, c.gold, c.gold.copy(alpha = 0.2f))
            }
          }
        }
      }
      Spacer(Modifier.height(20.dp))
      Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.surfaceContainerHighest.copy(alpha = 0.8f)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(20.dp), tint = c.gold)
        Spacer(Modifier.width(8.dp))
        Text(
          if (s.todayEntry == null) "“오늘 밤하늘에 별 하나를 띄울 준비가 되었어요. 작은 감정도 소중해요.”"
          else "“오늘의 별 하나가 밤하늘에 떴어요. 이제 포근히 쉬어도 괜찮아요.”",
          style = t.bodySm,
          color = c.onSurface,
        )
      }
    }
  }
}

@Composable
private fun NightTodayCard(s: HomeState, actions: HomeActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val written = s.todayEntry != null
  Column(
    Modifier
      .fillMaxWidth()
      .glow(DiaryTheme.shapes.section, c.shadow.copy(alpha = 0.9f), 36.dp, offsetY = 16.dp)
      .clip(DiaryTheme.shapes.section)
      .background(c.surfaceContainer)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(Modifier.size(10.dp).alpha(pulse()).clip(CircleShape).background(c.gold))
      Spacer(Modifier.width(6.dp))
      Text(
        "오늘 밤 ${s.today.monthValue}월 ${s.today.dayOfMonth}일의 기록",
        style = t.labelLg,
        color = c.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.weight(1f),
      )
      Pill(if (written) "작성 완료" else "미작성", c.secondaryContainer.copy(alpha = 0.5f), c.secondaryFixedDim)
    }
    ArtFrame(
      s.todayEntry?.let { DiaryArt.storyFor(it.id) } ?: DiaryArt.background(ArtPlace.TodayScene, true),
      Modifier.fillMaxWidth().aspectRatio(ArtPlace.TodayScene.ratio).clip(DiaryTheme.shapes.panel),
    ) {
      NightScenePlaceholder(
        Icons.Outlined.Brush,
        "오늘 밤의 별밤용 삽화가 그려질 자리예요 (16:10 규격)",
        if (written) "기록은 저장되었어요. 삽화 생성 기능이 연결되면 이 자리에 들어와요"
        else "일기를 남기면 아기 별밤용이 포근한 삽화를 선물해드려요",
      )
    }
    Row(
      Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.surfaceContainerHigh).padding(16.dp),
      verticalAlignment = Alignment.Top,
    ) {
      Icon(Icons.Outlined.TipsAndUpdates, null, Modifier.padding(top = 2.dp).size(20.dp), tint = c.secondary)
      Spacer(Modifier.width(8.dp))
      Text("글을 길게 쓰지 않아도 괜찮아요. 오늘 일어난 일들을 톡톡 눌러서 남겨보세요.", style = t.bodySm, color = c.onSurfaceVariant)
    }
    if (written) {
      PrimaryCta("오늘의 기록 펼쳐보기", { actions.onOpenEntry(s.todayEntry!!.id) }, icon = Icons.Outlined.EditNote)
    } else {
      PrimaryCta("✨ 오늘 하루 톡톡 기록하기", actions.onWrite, icon = Icons.Outlined.EditNote)
    }
  }
}

@Composable
private fun NightRecent(s: HomeState, actions: HomeActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Outlined.BookmarkAdded, null, Modifier.size(20.dp), tint = c.gold)
      Spacer(Modifier.width(4.dp))
      Text("최근 별밤 동화들", style = t.headlineSm, color = c.primary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
      SeeAllLink("전체 보기", Icons.Outlined.ChevronRight, actions.onSeeAll)
    }
    if (s.recent.isEmpty()) {
      EmptyRecent()
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        s.recent.forEach { RecentEntryCard(it) { actions.onOpenEntry(it.id) } }
      }
    }
  }
}

// ---------------------------------------------------------------- 라이트 (_5)

@Composable
private fun StorybookHome(s: HomeState, actions: HomeActions, cal: CalendarActions, contentPadding: PaddingValues) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val notice = LocalNotice.current
  Column(Modifier.fillMaxSize().background(c.background)) {
    TopBarFrame(height = 56) {
      BrandMark()
      Spacer(Modifier.width(8.dp))
      Text("별밤용 일기", style = t.headlineMd.copy(fontSize = 20.sp), color = c.ink, fontWeight = FontWeight.Bold)
      Spacer(Modifier.width(6.dp))
      Text("✦", style = t.labelMd, color = c.outlineVariant.copy(alpha = 0.6f))
      Spacer(Modifier.width(6.dp))
      Text("STORYBOOK", style = t.labelLg.copy(fontSize = 13.sp, letterSpacing = 0.05.sp * 13), color = c.secondary, modifier = Modifier.weight(1f))
      TopBarActions {
        NotificationButton({ notice(SOON) }, showDot = true)
        ProfileBadge { notice(SOON) }
      }
    }
    Column(
      Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = DiaryTheme.shapes.screenMargin)
        .padding(top = 12.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      StorybookGreeting(s)
      MonthCalendar(s.month, s.today, s.records, cal.prev, cal.next, cal.day)
      StorybookTodayCard(s, actions)
      StorybookRecent(s, actions)
    }
  }
}

@Composable
private fun StorybookGreeting(s: HomeState) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Pill(
        "별빛이 깃든 온화한 밤",
        container = c.surfaceContainer,
        content = c.onTertiaryContainer,
        border = c.outlineVariant.copy(alpha = 0.6f),
        icon = Icons.Outlined.HotelClass,
        iconTint = c.gold,
        style = t.labelLg.copy(fontSize = 11.sp, letterSpacing = 0.sp),
        horizontal = 12.dp,
        vertical = 4.dp,
      )
      Spacer(Modifier.weight(1f))
      Text(s.today.koreanFull(), style = t.bodySm.copy(fontFamily = t.headlineFamily), color = c.outline, fontStyle = FontStyle.Italic)
    }
    Text(
      buildAnnotatedString {
        append("오늘 밤 당신의 감정을\n")
        withStyle(SpanStyle(color = c.primary)) { append("황금빛 동화") }
        append("로 엮어볼까요?")
      },
      style = t.headlineLg.copy(fontSize = 24.sp, lineHeight = 31.sp),
      color = c.onSurface,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(top = 4.dp),
    )
    Text("작은 풀잎 위의 이슬, 스쳐간 바람결도 포근한 모험담이 돼요.", style = t.bodySm.copy(fontSize = 13.sp, lineHeight = 21.sp), color = c.onSurfaceVariant)
  }
}

@Composable
private fun StorybookTodayCard(s: HomeState, actions: HomeActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val written = s.todayEntry != null
  SectionCard {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(18.dp).clip(CircleShape).background(c.primaryFixed.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
          Box(Modifier.size(10.dp).clip(CircleShape).background(c.gold))
        }
        Spacer(Modifier.width(6.dp))
        Text("오늘 밤 이야기", style = t.headlineSm.copy(fontSize = 19.sp), color = c.onSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Pill(
          if (written) "기록 완료" else "별빛 대기 중",
          container = c.surfaceContainerLow,
          content = c.outline,
          border = c.surfaceContainerHighest,
          style = t.labelMd,
        )
      }
      val shape = RoundedCornerShape(16.dp)
      ArtFrame(
        s.todayEntry?.let { DiaryArt.storyFor(it.id) } ?: DiaryArt.background(ArtPlace.TodayScene, false),
        Modifier.fillMaxWidth().aspectRatio(ArtPlace.TodayScene.ratio).clip(shape),
      ) {
        ParchmentScenePlaceholder(Icons.Outlined.AutoFixHigh, "별밤용이 동화 삽화가 그려질 자리예요", "16:10 삽화 영역 (400x250)", shape)
      }
      Column(Modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          "\"오늘 당신의 하늘엔 어떤 별이 떴나요?\n사소한 온기 한 조각이라도 좋아요.\"",
          style = t.titleMd.copy(fontFamily = t.headlineFamily, lineHeight = 26.sp),
          color = c.onSurface,
          fontWeight = FontWeight.Bold,
        )
        Text(
          if (written) "오늘의 기록이 저장되었어요. 삽화 생성 기능이 연결되면 이 자리에 동화 삽화가 피어나요."
          else "기록을 남겨주시면 이 자리에 아기용의 마법 동화 삽화가 피어날 거예요.",
          style = t.bodySm,
          color = c.outline,
        )
      }
      if (written) {
        PrimaryCta("오늘의 기록 펼쳐보기", { actions.onOpenEntry(s.todayEntry!!.id) }, icon = Icons.Outlined.AutoFixHigh, trailing = Icons.Outlined.ChevronRight)
      } else {
        PrimaryCta("별빛 조각으로 하루 기록하기", actions.onWrite, icon = Icons.Outlined.AutoFixHigh, trailing = Icons.Outlined.ChevronRight)
      }
    }
  }
}

@Composable
private fun StorybookRecent(s: HomeState, actions: HomeActions) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.AutoMirrored.Outlined.MenuBook, null, Modifier.size(20.dp), tint = c.primary)
      Spacer(Modifier.width(8.dp))
      Text("펼쳐진 일기 조각들", style = t.headlineSm, color = c.onSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
      SeeAllLink("모두 보기", Icons.AutoMirrored.Outlined.ArrowForward, actions.onSeeAll)
    }
    if (s.recent.isEmpty()) {
      EmptyRecent()
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        s.recent.forEach { RecentEntryCard(it) { actions.onOpenEntry(it.id) } }
      }
    }
  }
}

/** 기록이 하나도 없을 때. 가짜 목록 대신 빈 상태를 보여준다. */
@Composable
private fun EmptyRecent() {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val shape = DiaryTheme.shapes.listCard
  Row(
    Modifier
      .fillMaxWidth()
      .clip(shape)
      .background(if (c.isDark) c.surfaceContainer else c.card)
      .then(if (c.isDark) Modifier else Modifier.border(1.dp, c.cardBorder, shape))
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconOrb(Icons.Outlined.AutoAwesome, 40.dp, 20.dp, if (c.isDark) c.surfaceContainerHigh else c.surfaceContainer, c.gold)
    Spacer(Modifier.width(12.dp))
    Column {
      Text("아직 펼쳐진 이야기가 없어요", style = t.labelLg, color = if (c.isDark) c.primary else c.onSurface, fontWeight = FontWeight.SemiBold)
      Text("오늘 밤 첫 조각을 남기면 이곳에 차곡차곡 쌓여요.", style = t.bodySm, color = c.onSurfaceVariant, modifier = Modifier.offset(y = 2.dp))
    }
  }
}
