package ru.erdenian.studentassistant.netty

data class User(val login: String, val password: String, val name: String = "",
                val universityId: Long? = null, val facultyId: Long? = null, val groupId: Long? = null)
