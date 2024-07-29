package com.pru.ktorcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pru.ktorcompose.ui.theme.KtorComposeTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

private val serializeJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

val KtorClient = HttpClient {
    install(ContentNegotiation) {
        json(json = serializeJson)
    }

    install(HttpRequestRetry) {
        //function enables retrying a request if a 5xx response is received from a server and specifies the number of retries.
        retryOnServerErrors(5)
        //specifies an exponential delay between retries, which is calculated using the Exponential backoff algorithm.
        exponentialDelay()
        //If you want to add some additional params in header
        modifyRequest { request ->
            request.headers.append("x-retry-count", 2.toString())
        }
    }

    //If you want to change user agent
    install(UserAgent) {
        agent = "Ktor"
    }

    install(DefaultRequest) {
        headers.appendIfNameAbsent("X-custom-header", "Some Value")
        contentType(ContentType.Application.Json)
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = {
            scope.launch {
                val response : List<Post>  = KtorClient.get("https://jsonplaceholder.typicode.com/todos/") {
                    contentType(ContentType.Application.Json)
                }.body()
                Log.i("Prudhvi Log", "Greeting: $response")
            }

        }
    ){
        Text(text = "Fetch")
    }
}

@Serializable
data class Post(
    @SerialName("completed")
    val completed: Boolean? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("userId")
    val userId: Int? = null
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KtorComposeTheme {
        Greeting("Android")
    }
}