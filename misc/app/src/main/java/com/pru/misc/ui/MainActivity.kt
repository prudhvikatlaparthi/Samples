package com.pru.misc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pru.misc.model.PostsResponse
import com.pru.misc.theme.MiscTheme
import com.pru.misc.ui.post_detail.PostDetailScreen
import com.pru.misc.ui.posts.PostsScreen
import com.pru.misc.utils.AppUtils.mapToObject
import com.pru.misc.utils.ScreenRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiscTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenRoutes.PostsScreen.route
                    ) {
                        composable(ScreenRoutes.PostsScreen.route) {
                            PostsScreen(
                                navController = navController
                            )
                        }
                        composable(
                            ScreenRoutes.PostDetailScreen.route.plus("/{post}"),
                            arguments = listOf(navArgument("post") {
                                type = NavType.StringType
                            })
                        ) {
                            var postsResponse: PostsResponse? = null
                            it.arguments?.getString("post")?.let { data ->
                                postsResponse = PostsResponse.serializer().mapToObject(data)
                            }
                            PostDetailScreen(
                                navController = navController,
                                postsResponse = postsResponse
                            )
                        }
                    }
                }
            }
        }
    }
}