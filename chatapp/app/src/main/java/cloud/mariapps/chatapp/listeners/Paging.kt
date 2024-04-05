package cloud.mariapps.chatapp.listeners

import cloud.mariapps.chatapp.utils.Constants.kInitialPage
import cloud.mariapps.chatapp.utils.Constants.kPageSize

interface Paging {
    var pageIndex: Int
    var isLoading: Boolean
    var isLastPage: Boolean

    fun fetchData()

    fun resetPaging() {
        pageIndex = kInitialPage
        isLastPage = false
        isLoading = false
        fetchData()
    }

    fun getPageSize(): Int {
        return if (pageIndex == kInitialPage) kPageSize else kPageSize
    }
}