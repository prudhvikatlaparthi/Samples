package com.pru.ktorteams.android.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pru.ktorteams.Post
import com.pru.ktorteams.android.databinding.ActivityDemoBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding
//    private val viewModel by viewModels<DemoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*viewModel.counterState.observe(this){

        }*/
        /*binding.btnIncrement.setOnClickListener {
            viewModel.increment()
        }*//*



        viewModel.postsState.observe(this){
            val adapter = PostListAdapter(it.toMutableList())
            binding.rcPosts.adapter = adapter
            Log.i("Prudhvi Log", "onCreate: $it")
        }

        *//*viewModel.counterState.observe(this){
            binding.tvCounterView.text = it.toString()
        }*/

//        val remote = Remote()
        /*repository.getPosts().forEach {
            Log.i("Prudhvi Log", "onCreate: $it")
        }*/
    }
}


class Remote {
    fun getPosts() : List<Post> {
        return List(10) {
            Post(body = "Body $it", id = it, title = "Title $it")
        }
    }
}

class Repository(val remote: Remote) {

    fun getPosts() : List<Post> {
        return remote.getPosts()
    }
}