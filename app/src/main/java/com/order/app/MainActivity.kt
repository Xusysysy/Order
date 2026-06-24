package com.order.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.order.app.ui.screen.MainScreen
import com.order.app.ui.theme.OrderTheme
import com.order.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as OrderApp
        setContent {
            val dbState by app.dbState.collectAsStateWithLifecycle()
            var isDark by remember { mutableStateOf(true) }

            OrderTheme(darkTheme = isDark) {
                when (val state = dbState) {
                    is DbState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is DbState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                    is DbState.Ready -> OrderAppContent(
                        helper = state.helper, isDark = isDark,
                        onToggleTheme = { isDark = !isDark }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderAppContent(
    helper: com.order.app.data.db.DatabaseHelper,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val viewModel: MainViewModel = viewModel(
        factory = com.order.app.viewmodel.MainViewModelFactory(
            helper = helper,
            prefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("order_prefs", 0)
        )
    )

    MainScreen(viewModel = viewModel, isDark = isDark, onToggleTheme = onToggleTheme)
}
