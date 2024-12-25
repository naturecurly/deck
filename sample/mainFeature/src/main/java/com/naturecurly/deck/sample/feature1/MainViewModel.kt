package com.naturecurly.deck.sample.feature1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.annotations.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Provider("MainFeature")
class MainViewModel @Inject constructor() : ViewModel(), DeckProvider<String> {
    init {
        initDeckProvider(viewModelScope)
        onDeckReady(viewModelScope, "Hello, World!")
    }

    override fun onCleared() {
        super.onCleared()
        onDeckClear()
    }
}