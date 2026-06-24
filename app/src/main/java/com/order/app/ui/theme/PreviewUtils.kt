package com.order.app.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun OrderPreview(content: @Composable () -> Unit) {
    OrderTheme(darkTheme = true) { content() }
}

@Composable
fun OrderPreviewLight(content: @Composable () -> Unit) {
    OrderTheme(darkTheme = false) { content() }
}
