package com.naturecurly.deck.compose

import android.app.Application
import android.content.Context
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.Wharf
import com.naturecurly.deck.compose.log.DeckLog
import dagger.hilt.EntryPoints
import kotlin.reflect.KClass

class WharfImpl : Wharf() {
    private var application: Application? = null

    // synchronizedMap is used to make the map thread-safe?
    private val entryPoints: MutableMap<Class<*>, Class<out DeckDependencies>> = mutableMapOf()

    internal fun init(context: Context) {
        entryPoints.clear()
        deckEntry.clear()
        runCatching {
            entryPoints.putAll(
                EntryPoints.get(context, DeckDependenciesEntryPoint::class.java).dependencies(),
            )
        }.onFailure {
            DeckLog.e("WharfImpl initialization failed", it)
            return
        }
        if (context is Application) {
            application = context
        } else {
            DeckLog.w("Context is not an instance of Application")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <INPUT> registerNewProvider(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    ) {
        entryPoints[providerClass.java]?.let { dep ->
            application?.let { app ->
                val dependencies = EntryPoints.get(app, dep)
                val containers = dependencies.containers() as Set<DeckContainer<INPUT, *>>
                deckEntry.addProvider(providerIdentity)
                containers.forEach {
                    deckEntry.addContainer(
                        providerIdentity = providerIdentity,
                        containerClass = it::class,
                        container = it,
                    )
                }
                dependencies.containerUiToContainerPairs().forEach { uiContainerPair ->
                    val containerUi = uiContainerPair.first
                    deckEntry.addContainerUi(
                        containerUiClass = containerUi::class,
                        containerClass = uiContainerPair.second,
                        containerUi = containerUi,
                    )
                    deckEntry.getDeckContainer(containerUi::class)?.let { container ->
                        setContainerToContainerUi(containerUi, container)
                    }
                }
            }
        }
    }
}
