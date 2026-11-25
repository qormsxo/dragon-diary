package com.dragondiary.app.ui.art

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.dragondiary.app.data.DragonColor
import com.dragondiary.app.data.DragonEmotion
import com.dragondiary.app.data.Mood
import java.time.LocalDate

/**
 * 그림 자리(드래곤/배경/삽화)와 실제 이미지 파일을 잇는 곳.
 *
 * 화면은 "어떤 그림이 필요한지"([ArtRequest])만 말하고,
 * 어떤 파일을 고를지는 [DiaryArt.dragonFor] / [DiaryArt.background] 가 정한다.
 * 이미지 파일이 없으면 화면별 플레이스홀더가 그 자리를 채운다.
 *
 * 파일을 추가하는 방법 (코드 수정 없이 반영됨):
 *   app/src/main/res/drawable-nodpi/ 에 아래 이름으로 PNG 또는 WebP 를 넣는다.
 *   - 드래곤:  dragon_<감정키>_<체색>.webp  > dragon_<감정키>.webp  > dragon_default.webp
 *             감정키는 [DragonEmotion.resKey] (예: dragon_joy.webp, dragon_pride_violet.webp)
 *   - 배경:    bg_<자리>_<night|day>.webp  > bg_<자리>.webp
 *             자리 이름은 [ArtPlace.key] (예: bg_today_scene_night.webp)
 *   - 날짜별:  dragon_<yyyyMMdd>.webp 가 있으면 그날은 그 그림이 가장 먼저 쓰인다.
 */
data class ArtRequest(val candidates: List<String>)

/** 화면 안의 그림 자리. 비율은 Stitch 원본 표기 그대로. */
enum class ArtPlace(val key: String, val ratio: Float) {
  /** 홈 '오늘 밤 기록' 16:10 삽화 (_1, _5) */
  TodayScene("today_scene", 16f / 10f),
  /** 목록 썸네일 1:1 (_1, _5, _8) */
  Thumbnail("thumb", 1f),
  /** 동화 읽기 완성 삽화 3:4 (_3) */
  StoryScene("story_scene", 3f / 4f),
  /** 동화 생성 캔버스 3:4 (_9) */
  StudioCanvas("studio_canvas", 3f / 4f),
  /** 일기 작성 하단 수채화 배너 (_2, _4) */
  WriteBanner("write_banner", 4f),
}

object DiaryArt {
  fun dragonFor(mood: Mood?, color: DragonColor, date: LocalDate): ArtRequest {
    val emotion: DragonEmotion? = mood?.dragon
    val day = "%04d%02d%02d".format(date.year, date.monthValue, date.dayOfMonth)
    return ArtRequest(
      buildList {
        add("dragon_$day")
        if (emotion != null) {
          add("dragon_${emotion.resKey}_${color.name.lowercase()}")
          add("dragon_${emotion.resKey}")
        }
        add("dragon_default_${color.name.lowercase()}")
        add("dragon_default")
      },
    )
  }

  fun background(place: ArtPlace, isDark: Boolean): ArtRequest = ArtRequest(
    listOf("bg_${place.key}_${if (isDark) "night" else "day"}", "bg_${place.key}"),
  )

  /** 기록별 삽화. 생성 기능이 붙기 전에는 항상 비어 있다. */
  fun storyFor(entryId: Long): ArtRequest = ArtRequest(listOf("story_$entryId"))

  private val cache = HashMap<String, Int>()

  @SuppressLint("DiscouragedApi") // 파일만 넣으면 바로 반영되도록 이름으로 찾는다
  fun resolve(context: Context, request: ArtRequest): Int {
    for (name in request.candidates) {
      val id = cache.getOrPut(name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
      }
      if (id != 0) return id
    }
    return 0
  }
}

/**
 * 그림 자리 하나. 이미지가 있으면 꽉 채워 그리고, 없으면 [placeholder] 를 그린다.
 * 실제 에셋을 넣은 뒤에는 플레이스홀더가 자동으로 사라진다.
 */
@Composable
fun ArtFrame(
  request: ArtRequest,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
  placeholder: @Composable BoxScope.() -> Unit,
) {
  val context = LocalContext.current
  val resId = remember(request) { DiaryArt.resolve(context, request) }
  Box(modifier) {
    if (resId != 0) {
      Image(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    } else {
      placeholder()
    }
  }
}
