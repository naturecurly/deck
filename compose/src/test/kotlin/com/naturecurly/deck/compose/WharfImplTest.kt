package com.naturecurly.deck.compose

import android.app.Application
import android.content.Context
import android.util.Log
import com.naturecurly.deck.ConsumerEvent
import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.compose.log.DeckLog
import dagger.hilt.EntryPoints
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class WharfImplTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `verify WharfImpl init success`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)
        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf()
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify(exactly = 0) { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl entryPoints init failed`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } throws RuntimeException()
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify { DeckLog.e(any(), any()) }
    }

    @Test
    fun `verify WharfImpl with non Application context`() {
        // Given
        val mockedContext: Context = mockk(relaxed = true)
        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf()
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl registerNewProvider`() {
        // Given
        val mockedContext: Application = mockk(relaxed = true)

        val entryPoints: DeckDependenciesEntryPoint = object : DeckDependenciesEntryPoint {
            override fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>> {
                return mapOf(DeckProviderTest::class.java to DeckDependenciesTest::class.java)
            }
        }
        mockkStatic(EntryPoints::class)
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesEntryPoint::class.java,
            )
        } returns entryPoints
        every {
            EntryPoints.get(
                mockedContext,
                DeckDependenciesTest::class.java,
            )
        } returns DeckDependenciesTest()
        // When
        val wharf = WharfImpl()
        wharf.init(mockedContext)
        wharf.registerNewProvider<String>(DeckProviderTest::class, 123)
        // Then
        verify(exactly = 0) { DeckLog.e(any(), any()) }
        verify(exactly = 0) { DeckLog.w(any()) }
    }

    @Test
    fun `verify WharfImpl registerNewProvider without init`() {
        // Given
        // When
        val wharf = WharfImpl()
        wharf.registerNewProvider<String>(DeckProviderTest::class, 123)
    }

    class DeckProviderTest : DeckProvider<String> {
        override fun onConsumerEvent(consumerEvent: ConsumerEvent) {
        }
    }

    class DeckConsumerTest : DeckConsumer<String, String>() {
        override fun init(scope: CoroutineScope) {
        }

        override fun onDataReady(scope: CoroutineScope, data: String) {
        }

        override val uiStateFlow: StateFlow<String>
            get() = mockk()

        override fun <Event> onEvent(event: Event) {
        }
    }

    class DeckContainerTest : DeckContainer<String, DeckConsumerTest>() {
        override val id: String
            get() = "1"
    }

    class DeckDependenciesTest : DeckDependencies {
        override fun providerClass(): KClass<out DeckProvider<*>> = DeckProviderTest::class

        override fun consumers(): Set<@JvmSuppressWildcards DeckConsumer<*, *>> =
            setOf(DeckConsumerTest())

        override fun containerToConsumerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>>> =
            setOf(DeckContainerTest() to DeckConsumerTest::class)
    }
}
