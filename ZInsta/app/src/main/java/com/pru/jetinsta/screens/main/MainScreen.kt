package com.pru.jetinsta.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pru.jetinsta.composables.AppBar
import com.pru.jetinsta.composables.BottomBar
import com.pru.jetinsta.repository.DataRepository
import com.pru.jetinsta.ui.theme.ZInstaTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            AppBar()
        },
        bottomBar = {
            BottomBar()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Column(modifier = Modifier) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        items(DataRepository.images.size) { pos ->
                            val name = DataRepository.images[pos].name
                            val image = DataRepository.images[pos].image
                            Column(
                                modifier = Modifier.width(90.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier, contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 2.5.dp)
                                            .size(80.dp)
                                            .border(
                                                border = BorderStroke(
                                                    1.dp,
                                                    color = if (pos == 0) Color.White else Color.Red
                                                ),
                                                shape = CircleShape
                                            )
                                            .background(Color.White)

                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(image)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "null",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.clip(CircleShape)
                                        )

                                    }
                                    if (pos == 0) {
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 2.5.dp)
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(Color.Blue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = "Add",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                }
            }
            items(DataRepository.feedData) { feed ->
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(feed.userAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = "null",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = feed.userName, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "MoreVert",
                            tint = Color.Black,
                            modifier = Modifier.rotate(90f)
                        )

                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(feed.photo)
                            .crossfade(true)
                            .build(),
                        contentDescription = "null",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(400.dp)
                            .padding(top = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.Gray,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Filled.Message,
                            contentDescription = "Message",
                            tint = Color.Gray,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Gray,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Bookmark",
                            tint = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "${feed.likesCount} likes")
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = feed.userName)
                        feed.description?.let { des ->
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = des)
                        }
                        feed.hashTags?.let { tags ->
                            Spacer(modifier = Modifier.width(5.dp))
                            for (tag in tags) {
                                Text(text = "#$tag")
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Posted on ".plus(SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(feed.date)))
                }
            }
        }
    }
}


@Preview
@Composable
fun Prev() {
    ZInstaTheme {
        MainScreen()
    }
}