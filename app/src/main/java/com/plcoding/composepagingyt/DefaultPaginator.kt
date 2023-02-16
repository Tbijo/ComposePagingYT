package com.plcoding.composepagingyt

class DefaultPaginator<Key, Item>(
    // initial page we want to start with ex. 0, we will provide it to API to load the first page
    private val initialKey: Key,

    // callback when the loading value is updated
    private inline val onLoadUpdated: (Boolean) -> Unit,

    // function in which we define how we get the next load of items
    // given the key to know how to get the data
    private inline val onRequest: suspend (nextKey: Key) -> Result<List<Item>>,

    // we need to specify how do we get the next key, how does paginator know that it is a new key
    // if we paginate by pages it is easy because the key is the next page incremented by one
    // if it was a string key we would need to specify some logic that is why are passing List<Item>
    private inline val getNextKey: suspend (List<Item>) -> Key,

    // when a error happens
    private inline val onError: suspend (Throwable?) -> Unit,

    // when we successfully loaded some items
    // it will give us the list of Items and the next key (so that we can update the current key/page in our State)
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
): Paginator<Key, Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems() {
        if(isMakingRequest) {
            // if we are currently making a request
            // we do not load the next items
            return
        }
        // if we are loading
        isMakingRequest = true

        // we need to update the loading status
        onLoadUpdated(true)

        // get the result from onRequest and provide the current key
        val result = onRequest(currentKey)

        // after that we ended making the request
        isMakingRequest = false

        // if everything went well we will get the items
        // otherwise it will throw the Exception, the issue that did happen
        val items = result.getOrElse {
            // in the Exception case call our error function
            onError(it)
            // update the state loading
            onLoadUpdated(false)
            // and leave this function
            return
        }

        // if we got the items update our current key and pass our items list
        currentKey = getNextKey(items)

        // we got the new part of items
        onSuccess(items, currentKey)

        // and we stopped loading
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}