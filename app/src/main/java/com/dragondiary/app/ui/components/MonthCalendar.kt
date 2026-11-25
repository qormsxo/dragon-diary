package com.dragondiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragondiary.app.data.MoodTone
import com.dragondiary.app.ui.theme.DiaryTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val WEEKDAYS = listOf("일", "월", "화", "수", "목", "금", "토")

/** 달력 칸 하나 */
private data class DayCell(val date: LocalDate, val inMonth: Boolean)

private fun cellsOf(month: YearMonth): List<DayCell> {
  val first = month.atDay(1)
  val lead = first.dayOfWeek.value % 7 // 일요일 시작
  val start = first.minusDays(lead.toLong())
  val total = ((lead + month.lengthOfMonth() + 6) / 7) * 7
  return (0 until total).map { i ->
    val d = start.plusDays(i.toLong())
    DayCell(d, d.monthValue == month.monthValue && d.year == month.year)
  }
}

/**
 * 월간 달력 카드.
 * 다크(_1): '2025년 5월' 머리글 + 이번 달 기록 수, 기록한 날은 금빛 점.
 * 라이트(_5): '5월의 마법 2025' 머리글, 주말 색, 감정 톤 색 점, 오늘은 금박 원.
 *
 * @param records 날짜별 기록 감정 톤 (점 색). 기록이 없는 날은 키가 없다.
 */
@Composable
fun MonthCalendar(
  month: YearMonth,
  today: LocalDate,
  records: Map<LocalDate, MoodTone?>,
  onPrev: () -> Unit,
  onNext: () -> Unit,
  onDayClick: (LocalDate) -> Unit,
  modifier: Modifier = Modifier,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val count = records.keys.count { YearMonth.from(it) == month }

  if (c.isDark) {
    SectionCard(modifier) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("${month.year}년 ${month.monthValue}월", style = t.headlineSm, color = c.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        MonthArrow(Icons.Outlined.ChevronLeft, "이전 달", onPrev)
        MonthArrow(Icons.Outlined.ChevronRight, "다음 달", onNext)
        Spacer(Modifier.weight(1f))
        Row(
          Modifier.clip(CircleShape).background(c.surfaceContainerHigh).padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(Modifier.size(8.dp).glow(CircleShape, c.glow.copy(alpha = 0.8f), 6.dp).clip(CircleShape).background(c.gold))
          Spacer(Modifier.width(6.dp))
          Text("이번 달 기록 ${count}개", style = t.labelSm, color = c.gold, fontWeight = FontWeight.SemiBold)
        }
      }
      Spacer(Modifier.height(16.dp))
      Grid(month, today, records, onDayClick)
    }
  } else {
    SectionCard(modifier, padding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
      // 위쪽 금빛 선 (`h-1 bg-gradient-to-r from-transparent via-[#e5a93c]/40 to-transparent`)
      Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp).padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("${month.monthValue}월의 마법", style = t.headlineMd.copy(fontSize = 20.sp), color = c.onSurface, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text("${month.year}", style = t.bodySm.copy(fontFamily = t.headlineFamily), color = c.outline)
        Spacer(Modifier.width(8.dp))
        Pill(
          "${count}편의 기록",
          container = c.secondaryFixed,
          content = c.onToneVioletContainer,
          border = c.secondaryContainer.copy(alpha = 0.8f),
          icon = Icons.Outlined.AutoAwesome,
          iconTint = c.secondary,
          style = t.labelMd,
        )
        Spacer(Modifier.weight(1f))
        MonthArrow(Icons.Outlined.ChevronLeft, "이전 달", onPrev)
        MonthArrow(Icons.Outlined.ChevronRight, "다음 달", onNext)
      }
      Box(Modifier.fillMaxWidth().height(1.dp).background(c.surfaceContainer))
      Spacer(Modifier.height(10.dp))
      Grid(month, today, records, onDayClick)
    }
  }
}

@Composable
private fun MonthArrow(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit) {
  val c = DiaryTheme.colors
  Box(
    Modifier.size(if (c.isDark) 28.dp else 32.dp).clip(CircleShape).clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) { Icon(icon, desc, Modifier.size(18.dp), tint = c.onSurfaceVariant) }
}

@Composable
private fun Grid(
  month: YearMonth,
  today: LocalDate,
  records: Map<LocalDate, MoodTone?>,
  onDayClick: (LocalDate) -> Unit,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  Column(verticalArrangement = Arrangement.spacedBy(if (c.isDark) 8.dp else 6.dp)) {
    Row(Modifier.fillMaxWidth()) {
      WEEKDAYS.forEachIndexed { i, w ->
        val color = when {
          c.isDark -> c.onSurfaceVariant.copy(alpha = 0.6f)
          i == 0 -> c.sunday
          i == 6 -> c.saturday
          else -> c.outline
        }
        Text(
          w,
          Modifier.weight(1f).padding(vertical = 4.dp),
          style = if (c.isDark) t.labelSm else t.labelMd.copy(fontSize = 11.sp),
          color = color,
          textAlign = TextAlign.Center,
          fontWeight = if (c.isDark) null else FontWeight.SemiBold,
        )
      }
    }
    cellsOf(month).chunked(7).forEach { week ->
      Row(Modifier.fillMaxWidth()) {
        week.forEach { cell ->
          Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            DayView(cell, today, records, onDayClick)
          }
        }
      }
    }
  }
}

@Composable
private fun DayView(
  cell: DayCell,
  today: LocalDate,
  records: Map<LocalDate, MoodTone?>,
  onDayClick: (LocalDate) -> Unit,
) {
  val c = DiaryTheme.colors
  val t = DiaryTheme.type
  val d = cell.date
  val isToday = d == today && cell.inMonth
  val recorded = cell.inMonth && records.containsKey(d)
  val tone = records[d]
  val clickable = cell.inMonth && (recorded || d == today)

  val dotColor: Color = when {
    c.isDark -> c.gold
    tone == MoodTone.Violet -> c.toneViolet
    tone == MoodTone.Peach -> c.tonePeach
    else -> c.toneGold
  }

  Column(
    Modifier
      // 자식의 광채가 잘리지 않도록 clip 대신 경계 없는 물결 효과를 쓴다
      .clickable(
        enabled = clickable,
        interactionSource = null,
        indication = ripple(bounded = false, radius = 20.dp),
      ) { onDayClick(d) }
      .padding(vertical = if (c.isDark) 6.dp else 4.dp)
      .size(width = 40.dp, height = if (c.isDark) 30.dp else 38.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    if (c.isDark) {
      if (isToday) {
        Column(
          Modifier.size(32.dp).glow(CircleShape, c.glow.copy(alpha = 0.5f), 14.dp).clip(CircleShape).background(c.gold.copy(alpha = 0.2f)),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          Text("${d.dayOfMonth}", style = t.bodySm, color = c.primary, fontWeight = FontWeight.Bold)
          Box(Modifier.size(4.dp).clip(CircleShape).background(c.primary))
        }
      } else {
        val color = when {
          !cell.inMonth -> c.onSurfaceVariant.copy(alpha = 0.3f)
          recorded -> c.primary
          d.isAfter(today) -> c.onSurfaceVariant
          else -> c.onSurface
        }
        Text("${d.dayOfMonth}", style = t.bodySm, color = color, fontWeight = if (recorded) FontWeight.SemiBold else null)
        if (recorded) {
          Spacer(Modifier.height(2.dp))
          Box(Modifier.size(6.dp).glow(CircleShape, c.glow.copy(alpha = 0.9f), 6.dp).clip(CircleShape).background(dotColor))
        }
      }
    } else {
      if (isToday) {
        Box(contentAlignment = Alignment.Center) {
          Box(
            Modifier.size(36.dp).glow(CircleShape, c.glow.copy(alpha = 0.45f), 10.dp, offsetY = 2.dp).clip(CircleShape)
              .background(Brush.linearGradient(listOf(c.primaryFixedDim, c.primaryContainer))),
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${d.dayOfMonth}", style = t.labelLg.copy(fontFamily = t.headlineFamily, fontSize = 14.sp), color = c.onPrimaryFixed, fontWeight = FontWeight.Bold)
            if (recorded) Box(Modifier.size(5.dp).clip(CircleShape).background(c.surfaceContainerLowest))
          }
        }
      } else {
        val color = when {
          !cell.inMonth -> c.surfaceDim.copy(alpha = 0.7f)
          d.dayOfWeek == DayOfWeek.SUNDAY -> c.sunday
          d.dayOfWeek == DayOfWeek.SATURDAY -> c.saturday
          else -> c.onSurface
        }
        Text(
          "${d.dayOfMonth}",
          style = t.bodySm.copy(fontSize = 12.sp),
          color = color,
          fontWeight = if (recorded) FontWeight.SemiBold else FontWeight.Medium,
        )
        if (recorded) {
          Spacer(Modifier.height(3.dp))
          Box(Modifier.size(6.dp).glow(CircleShape, dotColor, 6.dp).clip(CircleShape).background(dotColor))
        }
      }
    }
  }
}

/** 라이트 카드 위쪽 금빛 선 */
@Composable
fun GoldTopLine(modifier: Modifier = Modifier, alpha: Float = 0.4f) {
  val c = DiaryTheme.colors
  Box(
    modifier.fillMaxWidth().height(4.dp)
      .background(Brush.horizontalGradient(listOf(Color.Transparent, c.gold.copy(alpha = alpha), Color.Transparent))),
  )
}

