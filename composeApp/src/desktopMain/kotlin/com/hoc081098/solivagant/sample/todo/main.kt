package com.hoc081098.solivagant.sample.todo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
  startKoinCommon {

  }

  application {
    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
      App()
    }
  }
}

@Preview
@Composable
fun AppDesktopPreview() {
  App()
}
