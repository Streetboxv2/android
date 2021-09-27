package com.zeepos.remotestorage

import android.util.Log
import com.google.gson.Gson
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.Message
import io.nats.client.*
import io.nats.client.impl.NatsImpl
import java.nio.charset.StandardCharsets

/**
 * Created by Arif S. on 5/22/20
 */
class NatsHelper {
    companion object {
        const val NEAR_BY_FOOD_TRUCK = "nearByFoodTruck"
    }

    private lateinit var connection: Connection
    private lateinit var dispatcher: Dispatcher

    fun connect() {
        val options = Options.Builder().server("http://myserver.com")
            .userInfo("user".toCharArray(), "password".toCharArray())
            .connectionListener { conn, type ->
                Log.i(ConstVar.TAG, "ConnectionEvent: $type  -> status: ${conn.status}")
                connection = conn
            }
            .errorListener(object : ErrorListener {
                override fun errorOccurred(conn: Connection?, error: String?) {
                    Log.e(ConstVar.TAG, "Nats error occurred $error")
                }

                override fun exceptionOccurred(conn: Connection?, exp: Exception?) {
                    Log.e(ConstVar.TAG, "Nats Exception $exp")
                }

                override fun slowConsumerDetected(conn: Connection?, consumer: Consumer?) {
                    Log.w(ConstVar.TAG, "Slow consumer $consumer")
                }

            })
            .build().let {
                try {
                    NatsImpl.createConnection(it, true)
                } catch (ex: Exception) {
                    Log.e(ConstVar.TAG, "Error! NATs Connection")
                    it.errorListener?.exceptionOccurred(null, ex)
                }
            }
    }

    fun sendMessage(message: Message) {
        when (message.type) {
            NEAR_BY_FOOD_TRUCK -> {
            }
        }

        val data = Gson().toJson(message.data)
        connection.publish(message.type, data.toByteArray(StandardCharsets.UTF_8))
    }

    fun onMessage(subject: String) {
        dispatcher = connection.createDispatcher {

        }

        dispatcher.subscribe(subject) {
            val response = String(it.data, StandardCharsets.UTF_8)
            Log.d(ConstVar.TAG, response)
        }
    }

    fun disconnect() {
        dispatcher.unsubscribe(NEAR_BY_FOOD_TRUCK)
        connection.close()
    }
}