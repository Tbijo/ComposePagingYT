package com.plcoding.composepagingyt

import kotlinx.coroutines.delay

class Repository {

    // Generating fake data from a source
    private val remoteDataSource = (1..100).map {
        ListItem(
            title = "Item $it",
            description = "Description $it"
        )
    }

    // function to get the data
    // page - first page, second page, key, on which item we are currently
    // pageSize - how many items we want to load
    // Result is a kotlin class that returns Success or Error
    suspend fun getItems(page: Int, pageSize: Int): Result<List<ListItem>> {
        delay(2000L)

        // here we specify the next starting position
        // in the viewModel we need to increment it by 1
        // so from 0th page to to the 10 pages it will start at 0 and end at 10
        // and from 1th page to the next 10 pages it will start at (1*10) and end at (10 + 10)
        val startingIndex = page * pageSize

        return if(startingIndex + pageSize <= remoteDataSource.size) {
            Result.success(
                // return a specific part of a list from the last index shown in the view with a specific size
                remoteDataSource.slice(startingIndex until startingIndex + pageSize)
            )
        } else Result.success(emptyList())
    }
}