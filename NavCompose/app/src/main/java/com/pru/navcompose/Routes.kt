package com.pru.navcompose

sealed class Routes(var routeName: String) {
    object Profile : Routes("profile")
    object FriendsList : Routes("friendsList")
    object FinalList : Routes("finalList")
}