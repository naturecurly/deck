/*
 * Copyright (c) 2025 naturecurly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.naturecurly.deck.sample.subfeaturetwo

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.sample.subfeaturetwo.model.FeatureTwoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Container(bindTo = "MainFeature")
class FeatureTwoContainer @Inject constructor() : DeckContainer<String, FeatureTwoModel>() {
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
