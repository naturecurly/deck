package com.naturecurly.deck.sample.feature1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naturecurly.deck.compose.Deck
import com.naturecurly.deck.sample.designsystem.theme.DeckTheme

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    DeckTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Deck(viewModel) {
                Column {
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    Stub("FeatureOne")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeckTheme {
        Greeting("Android")
    }
}