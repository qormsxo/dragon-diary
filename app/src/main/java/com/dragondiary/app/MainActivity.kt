package com.dragondiary.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dragondiary.app.ui.AppViewModel
import com.dragondiary.app.ui.DragonDiaryApp
import com.dragondiary.app.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val vm: AppViewModel = viewModel()
      val settings by vm.settings.collectAsStateWithLifecycle()
      // 상태바 아이콘 색을 앱 테마에 맞춘다 (다크: 밝은 아이콘, 라이트: 어두운 아이콘)
      LaunchedEffect(settings.themeMode) {
        val style = if (settings.themeMode == ThemeMode.Dark) SystemBarStyle.dark(Color.TRANSPARENT)
        else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
      }
      DragonDiaryApp(vm)
    }
  }
}
