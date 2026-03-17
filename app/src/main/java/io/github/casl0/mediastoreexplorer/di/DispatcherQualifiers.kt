package io.github.casl0.mediastoreexplorer.di

import javax.inject.Qualifier

/** [kotlinx.coroutines.Dispatchers.IO] の DI 修飾子。 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
