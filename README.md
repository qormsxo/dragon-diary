# 용담 (Dragon Diary)

하루의 기분과 짧은 메모를 남기면, 작은 드래곤과 함께 동화 한 편으로 엮어 주는 일기 앱입니다.
Stitch로 만든 디자인 시안을 Jetpack Compose로 옮긴 안드로이드 앱입니다. WebView 없이 모두 네이티브로 그립니다.

> 현재는 화면과 로컬 저장까지 동작하는 UI 단계입니다. AI 동화 생성과 드래곤 그림은 아직 연결되지 않았습니다.

## 화면

| 화면 | 설명 |
|---|---|
| 홈 | 이번 달 달력과 기록 표시, 오늘 밤 기록, 최근 기록 |
| 빠른 작성 | 마음 빛깔(기분) 고르기, 이야기 조각(태그), 150자 메모 |
| 자유 작성 | 제목과 본문을 자유롭게 쓰기, 지난 기록 고쳐 쓰기 |
| 생성 대기 | 기록을 접수한 뒤 동화 생성 대기 상태 표시 |
| 동화 읽기 | 저장된 기록을 동화책 형식으로 보기 |
| 기록 보관소 | 월 선택, 기분 필터, 목록/격자 보기, 검색, 삭제 |
| 설정 | 테마 전환, 글자 크기, 드래곤 체색·뿔 모양 |

## 테마

두 테마는 서로 섞이지 않고 설정에서 바꿀 수 있습니다. 화면 구조와 기능은 같고 색과 글꼴만 바뀝니다.

- **아늑한 별밤** (다크): 밤하늘 남색 바탕, 금빛 강조
- **따스한 양장본** (라이트): 양피지 바탕, 세리프 제목

색·글꼴·모양 값은 컴포넌트에 직접 쓰지 않고 `ui/theme/` 의 테마 리소스에서만 가져옵니다.

## 동작 여부

**동작함**
- 일기 작성, 수정, 삭제, 임시 보관 (앱을 다시 켜도 유지)
- 테마 전환, 글자 크기, 드래곤 꾸미기 설정 저장
- 기록 보관소 검색과 필터, 달력 기록 표시

**아직 없음** (화면에 "준비 중" 또는 "연결 대기"로 표시)
- AI 동화 생성과 삽화 생성
- 드래곤·배경 그림 (지금은 테마별 플레이스홀더)
- 알림, 데이터 내보내기, 공유, 추가 글꼴

## 기술 스택

- Kotlin 2.3, Jetpack Compose (BOM 2026.03), Material 3
- Android Gradle Plugin 9.0, Gradle 9.1, JDK 17
- minSdk 26, targetSdk 36
- 저장: SharedPreferences + JSON (DB 없음)
- 화면 이동: 직접 만든 간단한 백스택 (내비게이션 라이브러리 없음)

## 빌드

```powershell
./gradlew assembleDebug
```

에뮬레이터나 기기에 바로 설치하려면:

```powershell
./gradlew installDebug
```

## 폴더 구조

```
app/src/main/java/com/dragondiary/app/
├─ MainActivity.kt
├─ data/            일기 모델, 로컬 저장소
└─ ui/
   ├─ AppViewModel.kt     앱 상태
   ├─ DragonDiaryApp.kt   화면 이동
   ├─ theme/              다크·라이트 테마 토큰
   ├─ art/                그림 선택 로직 (DiaryArt)
   ├─ components/         공용 컴포넌트, 플레이스홀더
   └─ screens/            화면별 Composable
stitch_dragon_fantasy_diary_app/   디자인 원본 시안 (HTML, PNG)
```

## 그림 넣기

그림 파일을 `app/src/main/res/drawable-nodpi/` 에 정해진 이름으로 넣고 다시 빌드하면, 코드를 고치지 않아도 플레이스홀더 대신 그림이 나옵니다.

드래곤은 아래 순서로 먼저 찾은 파일을 씁니다.

1. `dragon_<yyyyMMdd>` : 그 날짜 전용
2. `dragon_<감정>_<체색>`
3. `dragon_<감정>`
4. `dragon_default_<체색>`
5. `dragon_default`

- 감정: `gratitude`, `anticipation`, `joy`, `surprise`, `lethargy`, `sadness`, `affection`, `loneliness`, `pride`, `exhaustion`, `curiosity`, `excitement`, `hope`
- 체색: `amber`, `violet`, `peach`, `cream`, `moss`
- 배경: `bg_today_scene_night`, `bg_today_scene_day`, `bg_write_banner_night`, `bg_write_banner_day`
- 기록별 삽화: `story_<기록id>`

이름 규칙과 선택 순서는 [`DiaryArt.kt`](app/src/main/java/com/dragondiary/app/ui/art/DiaryArt.kt) 에 있습니다.
