package com.pru.ktordemo.android.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pru.ktordemo.Post
import com.pru.ktordemo.RepositorySDK
import com.pru.ktordemo.android.databinding.ActivityDemoBinding
import com.pru.ktordemo.android.di.Electric
import com.pru.ktordemo.android.di.Petrol
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class DemoActivity : AppCompatActivity() {
    @Inject
    lateinit var sample: Repository

    @Inject
    @Electric
    lateinit var electriCar: Car

    @Inject
    @Petrol
    lateinit var petrolCar: Car

    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*sample.executeTask().forEach {
            Log.i("Prudhvi Log", "onCreate: $it")
        }*/
        electriCar.startCar()
        electriCar.stopCar()

        petrolCar.startCar()
        petrolCar.stopCar()
        binding.btnFetch.setOnClickListener {
            lifecycleScope.launch {
                kotlin.runCatching {
                    RepositorySDK.getData(binding.etEndPoint.text?.toString() ?: "WLCA")
                }.onSuccess {
                    binding.apply {
                        tvDecimal.text =
                            it.bigDecimal.bd.times(BigDecimal.valueOf(2)).toPlainString()
                        tvInt.text = it.bigInt?.toString()
                    }
                }.onFailure {
                    binding.tvInt.text = "${it.message}"
                }

            }
        }
    }
}


class API {
    fun getPosts(): List<Post> {
        val data = List(10) {
            Post(body = "Body $it", id = it, title = "Title $it", userId = 1)
        }
        return data
    }
}

class Repository(val API: API) {
    fun executeTask(): List<Post> {
        return API.getPosts()
    }
}

class ElecticEngine : Engine {
    override fun startEngine() {
        Log.i("Prudhvi Log", "Electric Engine Started")
    }

    override fun stopEngine() {
        Log.i("Prudhvi Log", "Electric Engine Stopped")
    }

}

class PetrolEngine : Engine {
    override fun startEngine() {
        Log.i("Prudhvi Log", "Petrol Engine Started")
    }

    override fun stopEngine() {
        Log.i("Prudhvi Log", "Petrol Engine Stopped")
    }

}

val Double?.bd: BigDecimal
    get() = this?.toBigDecimal() ?: BigDecimal.ZERO

class Car(val engine: Engine) {

    fun startCar() {
        engine.startEngine()
    }

    fun stopCar() {
        engine.stopEngine()
    }
}

interface Engine {
    fun startEngine()

    fun stopEngine()
}