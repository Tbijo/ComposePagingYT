package com.plcoding.composepagingyt

interface Paginator<Key, Item> {
    suspend fun loadNextItems()

    // if want to start all over again
    fun reset()
}