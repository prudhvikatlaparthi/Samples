package com.pru.misc.ui.post_detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pru.misc.model.PostsResponse
import com.pru.misc.ui.posts.PostItem

@ExperimentalMaterialApi
@Composable
fun PostDetailScreen(navController: NavHostController,postsResponse: PostsResponse?) {
    postsResponse?.apply {
        PostItem(postsResponse = this,showID = true,modifier = Modifier.fillMaxSize())
    }
}