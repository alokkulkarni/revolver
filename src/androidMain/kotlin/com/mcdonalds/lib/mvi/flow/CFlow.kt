package com.mcdonalds.lib.mvi.flow

import kotlinx.coroutines.flow.Flow

actual class CFlow<out T : Any> actual constructor(
    private val flow: Flow<T>,
) : Flow<T> by flow
