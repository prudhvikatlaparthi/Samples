package com.pru.ktordemo.android

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.ktordemo.Greeting
import com.pru.ktordemo.RepositorySDK
import com.pru.ktordemo.Post
import com.pru.ktordemo.android.databinding.ActivityMainBinding

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent {
            MaterialTheme {
                UsersList(mainViewModel.dataList)
            }
        }
    }
}

@Composable
private fun UsersList(postList: List<Post>) {
    val message = "Loading..."
    if (postList.isEmpty()){
        Box{
            Text(text = message,modifier = Modifier.align(alignment = Alignment.Center))
        }
    }else {
        LazyColumn {
            items(postList.size) { index ->
                UserListItem(post = postList[index])
            }
        }
    }
}

@Composable
fun UserListItem(post: Post) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = post.id.toString(), fontSize = 16.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = post.body, fontSize = 14.sp)
        }
    }
}
