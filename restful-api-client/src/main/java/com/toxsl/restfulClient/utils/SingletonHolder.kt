package com.toxsl.restfulClient.utils

open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        return instance ?: synchronized(this) {
            instance ?: creator!!(arg).also {
                instance = it
                creator = null
            }
        }
    }
}
