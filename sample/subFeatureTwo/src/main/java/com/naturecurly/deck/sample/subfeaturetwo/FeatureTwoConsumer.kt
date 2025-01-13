package com.naturecurly.deck.sample.subfeaturetwo

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.annotations.Consumer
import com.naturecurly.deck.sample.subfeaturetwo.model.FeatureTwoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Consumer(bindTo = "MainFeature")
class FeatureTwoConsumer @Inject constructor() : DeckConsumer<String, FeatureTwoModel>() {
    override fun init(scope: CoroutineScope) {
        // DO NOTHING
    }

    override fun onDataReady(scope: CoroutineScope, data: String) {
        _uiStateFlow.value = FeatureTwoModel(title = "Feature Two", subtitle = data)
    }

    private val _uiStateFlow = MutableStateFlow<FeatureTwoModel>(FeatureTwoModel())

    override val uiStateFlow: StateFlow<FeatureTwoModel> = _uiStateFlow

    override fun <Any> onEvent(event: Any) {
        TODO("Not yet implemented")
    }
}
