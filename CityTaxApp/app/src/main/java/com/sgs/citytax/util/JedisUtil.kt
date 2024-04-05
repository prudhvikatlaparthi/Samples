package com.sgs.citytax.util

import android.content.Intent
import android.util.Log
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.ui.LoginActivity
import kotlinx.coroutines.*
import redis.clients.jedis.JedisShardInfo
import redis.clients.jedis.StreamEntry
import redis.clients.jedis.StreamEntryID
import redis.clients.jedis.params.XReadParams
import java.util.*

object JedisUtil {
    private var mainScope: CoroutineScope? = null

    private lateinit var HOST: String
    private var PORT: Int = 0
    private lateinit var PASSWORD: String
    private const val TIME_OUT = 5000
    private const val USERID_KEY = "UserID"
    private const val PASSWORD_KEY = "Password"
    private const val STATUS_CODE_KEY = "StatusCode"
    private const val USER_ACTIVE_KEY = "UserActive"

    fun startJedis() {
        HOST = MyApplication.getPrefHelper().jedisConnectionHost
        PORT = MyApplication.getPrefHelper().jedisConnectionPort
        PASSWORD = MyApplication.getPrefHelper().jedisConnectionPassword
        mainScope = MainScope()
        mainScope?.launch(Dispatchers.IO) {
            readJedisData()
        }
    }

    private suspend fun readJedisData() {
        logger("Jedis Log", "readJedisData: Host : $HOST Port : $PORT PASS : $PASSWORD")
        kotlin.runCatching {
            val shardInfo = JedisShardInfo(HOST, PORT, TIME_OUT)
            shardInfo.password = PASSWORD
            val jedisClient = shardInfo.createResource()
            jedisClient?.let { jedis ->
                val valueOnMap: MutableMap<String, StreamEntryID> = HashMap()
                if (MyApplication.getPrefHelper().jedisLogoutEntryID.isEmpty()) {
                    valueOnMap[getStreamName()] =
                        StreamEntryID()
                } else {
                    valueOnMap[getStreamName()] =
                        StreamEntryID(MyApplication.getPrefHelper().jedisLogoutEntryID)
                }
                val dataList: List<Map.Entry<String, List<StreamEntry>>>? = jedis.xread(
                    XReadParams.xReadParams().block(0),
                    valueOnMap
                )
                dataList
            }
        }.onSuccess { dataList ->
            logger("Jedis Log", "readJedisData: $dataList")
            var changeFlag = false
            dataList?.let { data ->
                if (data.isNotEmpty()) {
                    data.forEach {
                        if (it.key == getStreamName()) {
                            it.value.forEachIndexed { index, streamEntry ->
                                if (streamEntry.fields.containsKey(USERID_KEY)) {
                                    val userID = streamEntry.fields[USERID_KEY]
                                    if (userID == MyApplication.getPrefHelper().loggedInUserID) {
                                        if (streamEntry.fields.containsKey(PASSWORD_KEY)) {
                                            val password = streamEntry.fields[PASSWORD_KEY]
                                            password?.let { pass ->
                                                changeFlag =
                                                    pass != MyApplication.getPrefHelper().agentPassword
                                            }
                                        }
                                        if (streamEntry.fields.containsKey(STATUS_CODE_KEY)) {
                                            val statusCode = streamEntry.fields[STATUS_CODE_KEY]
                                            changeFlag =
                                                statusCode == Constant.AgentStatus.INACTIVE.value
                                        }
                                        if (streamEntry.fields.containsKey(USER_ACTIVE_KEY)) {
                                            val activeCode = streamEntry.fields[USER_ACTIVE_KEY]
                                            changeFlag =
                                                activeCode == "N"
                                        }
                                    }
                                }
                                if (index == it.value.size - 1) {
                                    MyApplication.getPrefHelper().jedisLogoutEntryID =
                                        streamEntry.id.toString()
                                }
                            }
                        }
                    }
                }
            }
            if (changeFlag) {
                navigateToLogin()
            } else {
                readJedisData()
            }
        }.onFailure {
            LogHelper.writeLog(exception = java.lang.Exception(it))
            logger("Jedis Log", "readJedisData: $it")
            delay(15000)
            readJedisData()
        }
    }

    private fun navigateToLogin() {
        logger("Jedis Log", "navigateToLogin: ")
        val context = MyApplication.getContext()
        cancelJedis()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private fun getStreamName(): String {
        val streamName = MyApplication.getPrefHelper().domain.plus(":Force_Logout:Stream")
        logger("Jedis Log", "getStreamName: $streamName")
        return streamName
    }

    @JvmStatic
    fun cancelJedis() {
        try {
            if (mainScope?.isActive == true) {
                mainScope?.cancel()
            }
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    private fun logger(tag: String, message: String) {
        if (BuildConfig.BUILD_VARIANT != Constant.BuildVariant.PROD.value) {
            Log.i(tag, message)
        }
    }
}