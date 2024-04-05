package com.pru.misc.ui.posts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pru.misc.model.PostsResponse
import com.pru.misc.utils.AppUtils.mapToString
import com.pru.misc.utils.ScreenRoutes

@ExperimentalMaterialApi
@Composable
fun PostsScreen(
    navController: NavHostController,
    postsViewModel: PostsViewModel = hiltViewModel(),
) {

    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
        items(postsViewModel.posts.value.size) { index ->
            PostItem(postsResponse = postsViewModel.posts.value[index], showID = true) {
                navController.navigate(ScreenRoutes.PostDetailScreen.route.plus("/${it.mapToString()}"))
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun PostItem(
    postsResponse: PostsResponse,
    modifier: Modifier = Modifier,
    showID: Boolean = false,
    onClick: ((PostsResponse) -> Unit)? = null
) {
    Card(
        elevation = if (showID) 0.dp else 8.dp,
        modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        onClick = {
            onClick?.invoke(postsResponse)
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                if (showID) {
                    Text(
                        text = postsResponse.id.toString().plus("."),
                        style = MaterialTheme.typography.h5,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = postsResponse.title,
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = postsResponse.body,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}