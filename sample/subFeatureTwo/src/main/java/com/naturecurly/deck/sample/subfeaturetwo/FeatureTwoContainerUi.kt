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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturecurly.deck.annotations.ContainerUi
import com.naturecurly.deck.compose.DeckComposeContainerUi
import com.naturecurly.deck.sample.subfeaturetwo.model.FeatureTwoModel
import javax.inject.Inject

@ContainerUi(bindTo = "MainFeature")
class FeatureTwoContainerUi @Inject constructor() : DeckComposeContainerUi<FeatureTwoModel, FeatureTwoContainer>() {
    @Composable
    override fun Content(modifier: Modifier) {
        val state by container.uiStateFlow.collectAsStateWithLifecycle()
        Column(modifier) {
            Text(state.title)
            Text(state.subtitle)
        }
    }

    override val id: String = "FeatureTwo"
}
