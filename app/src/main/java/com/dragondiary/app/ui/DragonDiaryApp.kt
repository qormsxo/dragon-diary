package com.dragondiary.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dragondiary.app.ui.components.DiaryBottomBar
import com.dragondiary.app.ui.components.LocalNotice
import com.dragondiary.app.ui.components.MainTab
import com.dragondiary.app.ui.screens.archive.ArchiveActions
import com.dragondiary.app.ui.screens.archive.ArchiveScreen
import com.dragondiary.app.ui.screens.generating.GeneratingActions
import com.dragondiary.app.ui.screens.generating.GeneratingScreen
import com.dragondiary.app.ui.screens.home.HomeActions
import com.dragondiary.app.ui.screens.home.HomeScreen
import com.dragondiary.app.ui.screens.reader.ReaderActions
import com.dragondiary.app.ui.screens.reader.StoryReaderScreen
import com.dragondiary.app.ui.screens.settings.SettingsActions
import com.dragondiary.app.ui.screens.settings.SettingsScreen
import com.dragondiary.app.ui.screens.write.FreeWriteScreen
import com.dragondiary.app.ui.screens.write.QuickWriteScreen
import com.dragondiary.app.ui.screens.write.WriteActions
import com.dragondiary.app.ui.theme.DiaryTheme
import com.dragondiary.app.ui.theme.DragonDiaryTheme
import kotlinx.coroutines.launch

/** 화면 경로. 문자열로 바꿔 저장해 화면 회전 뒤에도 뒤로 가기 기록이 유지된다. */
sealed interface Route {
  data class Tab(val tab: MainTab) : Route
  data object Write : Route
  data object FreeWrite : Route
  data class Reader(val id: Long) : Route
  data class Generating(val id: Long) : Route

  fun encode(): String = when (this) {
    is Tab -> "tab:${tab.name}"
    Write -> "write"
    FreeWrite -> "free"
    is Reader -> "reader:$id"
    is Generating -> "gen:$id"
  }

  companion object {
    fun decode(s: String): Route = when {
      s.startsWith("tab:") -> Tab(MainTab.valueOf(s.removePrefix("tab:")))
      s == "write" -> Write
      s == "free" -> FreeWrite
      s.startsWith("reader:") -> Reader(s.removePrefix("reader:").toLong())
      s.startsWith("gen:") -> Generating(s.removePrefix("gen:").toLong())
      else -> Tab(MainTab.Home)
    }
  }
}

@Composable
fun DragonDiaryApp(vm: AppViewModel = viewModel()) {
  val settings by vm.settings.collectAsStateWithLifecycle()
  DragonDiaryTheme(mode = settings.themeMode, textScale = settings.textSize.scale) {
    AppContent(vm, settings)
  }
}

@Composable
private fun AppContent(vm: AppViewModel, settings: SettingsState) {
  val entries by vm.entries.collectAsStateWithLifecycle()
  val draft = vm.draft

  val saved = rememberSaveable { arrayListOf(Route.Tab(MainTab.Home).encode()) }
  val stack = remember { saved.map(Route::decode).toMutableStateList() }
  fun sync() {
    saved.clear(); saved.addAll(stack.map { it.encode() })
  }
  fun push(r: Route) { stack.add(r); sync() }
  fun pop() { if (stack.size > 1) stack.removeAt(stack.lastIndex); sync() }
  fun replaceTop(r: Route) { stack[stack.lastIndex] = r; sync() }
  fun selectTab(tab: MainTab) { stack.clear(); stack.add(Route.Tab(tab)); sync() }

  val snackbar = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val notice: (String) -> Unit = { msg ->
    scope.launch {
      snackbar.currentSnackbarData?.dismiss()
      snackbar.showSnackbar(msg)
    }
  }

  val current = stack.last()
  BackHandler(enabled = stack.size > 1) { pop() }

  val writeActions = WriteActions(
    onBack = ::pop,
    onMood = vm::selectMood,
    onTag = vm::toggleTag,
    onMemo = vm::updateMemo,
    onTitle = vm::updateTitle,
    onBody = vm::updateBody,
    onGenerate = {
      val editing = draft.editingId != null
      val id = vm.commitDraft()
      if (editing) {
        // 고쳐 쓰기를 마치면 상세 화면으로 돌아간다
        while (stack.size > 1 && stack.last() !is Route.Reader) stack.removeAt(stack.lastIndex)
        sync()
        notice("고친 기록을 저장했어요")
      } else {
        // 작성 화면들은 뒤로 가기 기록에서 지우고 생성 화면으로
        while (stack.size > 1 && (stack.last() == Route.Write || stack.last() == Route.FreeWrite)) stack.removeAt(stack.lastIndex)
        push(Route.Generating(id))
      }
    },
    onSaveForLater = {
      vm.saveDraftForLater()
      notice("임시 보관했어요. 다음에 이어서 쓸 수 있어요")
    },
    onFreeMode = { push(Route.FreeWrite) },
  )

  val openWrite: () -> Unit = { vm.startNewDraft(); push(Route.Write) }
  val openEntry: (Long) -> Unit = { push(Route.Reader(it)) }

  CompositionLocalProvider(LocalNotice provides notice) {
    Box(Modifier.fillMaxSize().background(DiaryTheme.colors.background)) {
      val tab = (current as? Route.Tab)?.tab
      // 탭 화면 아래쪽 여백: 내비게이션 바 높이 + 시스템 내비게이션 영역
      val barHeight = if (DiaryTheme.isDark) 80.dp else 64.dp
      val tabPadding = PaddingValues(bottom = barHeight)

      AnimatedContent(
        targetState = current,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "route",
        modifier = Modifier.fillMaxSize(),
      ) { route ->
        when (route) {
          is Route.Tab -> Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars)) {
            when (route.tab) {
              MainTab.Home -> HomeScreen(
                entries, settings.dragonColor,
                HomeActions(onWrite = openWrite, onOpenEntry = openEntry, onSeeAll = { selectTab(MainTab.Archive) }),
                tabPadding,
              )
              MainTab.Archive -> ArchiveScreen(
                entries,
                ArchiveActions(onOpen = openEntry, onDelete = { vm.deleteEntry(it); notice("기록을 지웠어요") }, onWrite = openWrite),
                tabPadding,
              )
              MainTab.Settings -> SettingsScreen(
                settings, entries.size,
                SettingsActions(vm::setTheme, vm::setTextSize, vm::setDragonColor, vm::setDragonHorn),
                tabPadding,
              )
            }
          }
          Route.Write -> QuickWriteScreen(draft, writeActions)
          Route.FreeWrite -> FreeWriteScreen(draft, writeActions)
          is Route.Reader -> {
            val e = entries.firstOrNull { it.id == route.id }
            if (e == null) {
              MissingEntry(::pop)
            } else {
              val volume = entries.sortedBy { it.createdAt }.indexOfFirst { it.id == e.id } + 1
              StoryReaderScreen(
                e, volume,
                ReaderActions(
                  onBack = ::pop,
                  onRegenerate = { push(Route.Generating(e.id)) },
                  onEdit = { vm.startEditing(e.id); push(Route.FreeWrite) },
                ),
              )
            }
          }
          is Route.Generating -> {
            val e = entries.firstOrNull { it.id == route.id }
            if (e == null) MissingEntry(::pop)
            else GeneratingScreen(e, GeneratingActions(onBack = ::pop, onOpenEntry = { replaceTop(Route.Reader(e.id)) }))
          }
        }
      }

      if (tab != null) {
        Box(Modifier.align(Alignment.BottomCenter)) {
          DiaryBottomBar(tab, ::selectTab)
        }
      }

      SnackbarHost(
        snackbar,
        Modifier.align(Alignment.BottomCenter).windowInsetsPadding(WindowInsets.navigationBars)
          .padding(bottom = if (tab != null) barHeight + 8.dp else 16.dp),
      ) { data ->
        val c = DiaryTheme.colors
        Snackbar(
          data,
          containerColor = if (c.isDark) c.surfaceContainerHighest else c.ink,
          contentColor = if (c.isDark) c.onSurface else c.background,
          shape = DiaryTheme.shapes.panel,
        )
      }
    }
  }
}

@Composable
private fun MissingEntry(onBack: () -> Unit) {
  // 지워진 기록으로 돌아온 경우: 바로 이전 화면으로
  androidx.compose.runtime.LaunchedEffect(Unit) { onBack() }
}
