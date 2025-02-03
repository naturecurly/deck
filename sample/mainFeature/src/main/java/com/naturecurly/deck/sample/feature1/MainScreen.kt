package com.naturecurly.deck.sample.feature1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naturecurly.deck.compose.Deck
import com.naturecurly.deck.compose.DeckPreview
import com.naturecurly.deck.compose.DeckScope
import com.naturecurly.deck.sample.designsystem.theme.DeckTheme

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    DeckTheme {
        Deck(viewModel) {
            Content()
        }
    }
}

@Composable
private fun DeckScope.Content() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding),
            )
            Stub("FeatureOne")
            Stub("FeatureTwo", modifier = Modifier.padding(top = 20.dp))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeckTheme {
        DeckPreview {
            Content()
        }
    }
}
