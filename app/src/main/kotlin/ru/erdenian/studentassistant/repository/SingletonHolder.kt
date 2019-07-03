package ru.erdenian.studentassistant.repository

open class SingletonHolder<T, A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        instance?.let { return it }

        return synchronized(this) {
            val i = instance
            if (i != null) i
            else {
                val created = checkNotNull(creator)(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
