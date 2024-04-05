package com.pru.ktorteams.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pru.ktorteams.Greeting
import com.pru.ktorteams.Post
import com.pru.ktorteams.RepositorySDK
import com.pru.ktorteams.android.databinding.ActivityMainBinding

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val postList = mutableListOf<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = PostListAdapter(posts = postList)
        binding.rcView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            kotlin.runCatching {
                postList.clear()
                RepositorySDK.getPosts()
            }.onSuccess { result ->
                postList.addAll(result)
                adapter.notifyDataSetChanged()
            }.onFailure {
                postList.clear()
                it.printStackTrace()
                Log.i("Prudhvi Log", "onCreate: $it")
            }
        }
    }
}

/*@Composable
private fun UsersList() {
    val postList: MutableList<Post> = remember { mutableStateListOf() }
    var message = "Loading..."
    LaunchedEffect(key1 = true) {
        kotlin.runCatching {
            postList.clear()
            message = "Loading..."
            RepositorySDK.getPosts()
        }.onSuccess {
            postList.addAll(it)
        }.onFailure {
            message = it.message ?: "Not found"
            postList.clear()
            it.printStackTrace()
            Log.i("Prudhvi Log", "onCreate: $it")
        }
    }
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
}*/
