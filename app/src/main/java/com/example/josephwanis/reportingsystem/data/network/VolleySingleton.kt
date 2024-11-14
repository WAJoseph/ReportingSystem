package com.example.josephwanis.reportingsystem.data.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.Request

class VolleySingleton private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also { INSTANCE = it }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun addToRequestQueue(request: Request<*>) {
        requestQueue.add(request)
    }
}
