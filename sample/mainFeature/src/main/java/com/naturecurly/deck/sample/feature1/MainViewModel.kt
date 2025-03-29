package com.naturecurly.deck.sample.feature1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturecurly.deck.ContainerEvent
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.RefreshProvider
import com.naturecurly.deck.annotations.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Provider("MainFeature")
class MainViewModel @Inject constructor() :
    ViewModel(),
    DeckProvider<String> {
    init {
        initDeckProvider(viewModelScope)
        onDeckReady(viewModelScope, "Hello, World!")
    }

    override fun onContainerEvent(containerEvent: ContainerEvent) {
        when (containerEvent) {
            RefreshProvider -> Log.d("MainViewModel", "Refresh Provider")
        }
    }

    override fun onCleared() {
        super.onCleared()
        onDeckClear()
    }
}
