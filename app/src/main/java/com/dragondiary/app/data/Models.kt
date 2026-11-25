package com.dragondiary.app.data

import java.time.LocalDate
import java.time.LocalDateTime

/** 감정 톤. 테마 토큰의 toneGold / toneViolet / tonePeach 로 색이 정해진다. */
enum class MoodTone { Gold, Violet, Peach }

/**
 * '오늘 나의 마음 빛깔' (화면 _2, _4) 다섯 가지.
 * [short]는 기록 보관소 분류 칩(_8), [adjective]는 자유 모드 마음 날씨 칩(_7)에 쓴다.
 * [dragon]은 나중에 감정별 드래곤 그림을 고를 때 쓰는 연결 고리다.
 */
enum class Mood(
  val emoji: String,
  val label: String,
  val short: String,
  val shortEmoji: String,
  val adjective: String,
  val tone: MoodTone,
  val dragon: DragonEmotion,
) {
  Healing("🌙", "포근한 힐링", "포근함", "🌸", "포근한", MoodTone.Violet, DragonEmotion.Affection),
  Proud("🍯", "달콤한 뿌듯함", "달콤함", "✨", "뿌듯한", MoodTone.Gold, DragonEmotion.Pride),
  Thoughtful("☕", "조용한 사색", "사색", "☕", "차분한", MoodTone.Violet, DragonEmotion.Curiosity),
  Tired("🌧️", "토닥토닥 지침", "지침", "🌙", "지친", MoodTone.Peach, DragonEmotion.Exhaustion),
  Excited("💫", "반짝이는 설렘", "설렘", "🍃", "설레는", MoodTone.Gold, DragonEmotion.Anticipation),
}

/**
 * 드래곤 감정 그림 키. 프로젝트 루트 asset/ 폴더의 파일 이름(한글)과 1:1로 맞춘다.
 * 리소스 이름 규칙: res/drawable-nodpi/dragon_<resKey>.webp (또는 .png)
 */
enum class DragonEmotion(val korean: String, val resKey: String) {
  Gratitude("감사", "gratitude"),
  Anticipation("기대감", "anticipation"),
  Joy("기쁨", "joy"),
  Surprise("놀라움", "surprise"),
  Lethargy("무기력", "lethargy"),
  Sadness("슬픔", "sadness"),
  Affection("애정과 호감", "affection"),
  Loneliness("외로움", "loneliness"),
  Pride("자부심", "pride"),
  Exhaustion("탈진", "exhaustion"),
  Curiosity("호기심", "curiosity"),
  Excitement("흥분", "excitement"),
  Hope("희망", "hope"),
}

/** '무슨 일이 있었나요?' 조각. 다크(_2)와 라이트(_4) 원본의 문구가 달라 둘 다 보관한다. */
data class StoryTag(val id: String, val darkLabel: String?, val lightLabel: String)

data class TagGroup(
  val id: String,
  val darkTitle: String,
  val lightTitle: String,
  val tone: MoodTone,
  val tags: List<StoryTag>,
)

object TagCatalog {
  val groups = listOf(
    TagGroup(
      "rest", "일상 & 휴식", "따스한 휴식과 온기", MoodTone.Gold,
      listOf(
        StoryTag("tea", "☕ 따뜻한 차 한 잔", "☕ 따뜻한 차 한 잔"),
        StoryTag("book", "📚 서점/책 읽기", "📚 골목 서점과 시집"),
        StoryTag("walk", "🐾 동네 밤 산책", "🐾 밤안개 산책"),
        StoryTag("meal", "🍲 맛있는 저녁", "🍲 포근하고 든든한 식사"),
        StoryTag("nap", "🛏️ 꿀잠 낮잠", "🛏️ 나른한 낮잠"),
      ),
    ),
    TagGroup(
      "people", "만남 & 하루 일감", "인연과 보람찬 시간", MoodTone.Violet,
      listOf(
        StoryTag("friend", "💬 친구와의 수다", "💬 친구와의 담소"),
        StoryTag("work", "💻 업무/공부 마무리", "💻 알차게 마친 오늘의 일"),
        StoryTag("praise", "🌟 칭찬 들은 날", "🌟 마음에 남은 칭찬"),
        StoryTag("alone", "🎧 혼자만의 여유", "🎧 귓가의 비밀 음악"),
      ),
    ),
    TagGroup(
      "scene", "날씨 & 장소", "별과 바람의 풍경", MoodTone.Peach,
      listOf(
        // 다크 원본에는 '밤하늘 별보기' 조각이 없다
        StoryTag("stars", null, "🌌 밤하늘 별보기"),
        StoryTag("breeze", "🍃 선선한 밤바람", "🍃 선선한 창가 바람"),
        StoryTag("rain", "☔ 비 내리는 오후", "☔ 다정한 빗소리"),
        StoryTag("room", "🛋️ 포근한 내 방", "🛋️ 포근한 나의 성채"),
      ),
    ),
  )

  private val byId = groups.flatMap { it.tags }.associateBy { it.id }

  fun find(id: String): StoryTag? = byId[id]

  /** 해시태그 표기 (이모지 제거, 공백 제거) */
  fun hashtag(id: String): String? = find(id)?.lightLabel
    ?.substringAfter(' ')
    ?.replace(" ", "")
    ?.let { "#$it" }
}

/** 사용자가 저장한 일기 한 편 */
data class DiaryEntry(
  val id: Long,
  val date: LocalDate,
  val createdAt: LocalDateTime,
  val mood: Mood?,
  val tags: List<String>,
  val memo: String,
  val title: String,
  val body: String,
) {
  /** 카드에 보여줄 본문 (긴 글 우선, 없으면 한 줄 메모) */
  val preview: String get() = body.ifBlank { memo }
  val charCount: Int get() = (body.ifBlank { memo }).length
  val displayTitle: String get() = title.ifBlank { "제목 없는 기록" }
}

/**
 * 작성 중인 일기. 간편 기록(_2/_4)과 자유 모드(_7)가 같은 초안을 공유한다.
 * [editingId]가 있으면 기존 기록을 고치는 중이다.
 */
data class DiaryDraft(
  val editingId: Long? = null,
  val date: LocalDate = LocalDate.now(),
  val mood: Mood? = null,
  val tags: Set<String> = emptySet(),
  val memo: String = "",
  val title: String = "",
  val body: String = "",
  val savedAt: LocalDateTime? = null,
) {
  val isEmpty: Boolean
    get() = mood == null && tags.isEmpty() && memo.isBlank() && title.isBlank() && body.isBlank()
}

/** 설정 화면 '나의 아기 용 설정 (미리보기)' 의 바탕 체색 */
enum class DragonColor(val label: String) { Amber("골드앰버"), Violet("별빛보라"), Peach("따스한피치"), Cream("달빛크림"), Moss("새벽이끼") }

/** 뿔 및 수호 날개 형태 */
enum class DragonHorn(val label: String) { Constellation("별자리 뿔 (기본)"), Wing("꼬마 날개형"), Round("클래식 둥근 뿔") }

/** 글자 크기 3단계 */
enum class TextSize(val label: String, val scale: Float) {
  Small("작게 (14pt)", 0.9f), Normal("보통 (16pt)", 1f), Large("크게 (18pt)", 1.12f)
}
