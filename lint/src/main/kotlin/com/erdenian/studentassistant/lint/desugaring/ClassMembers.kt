package com.erdenian.studentassistant.lint.desugaring

import java.io.File
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import org.jetbrains.uast.UastVisibility

internal data class ClassMembers(
    val constructors: List<ClassMember.Constructor>,
    val fields: List<ClassMember.Field>,
    val methods: List<ClassMember.Method>
)

internal sealed class ClassMember {

    data class Constructor(
        val visibility: UastVisibility,
        val parameters: List<Parameter>
    ) : ClassMember()

    data class Field(
        val visibility: UastVisibility,
        val isStatic: Boolean,
        val type: String,
        val name: String
    ) : ClassMember()

    data class Method(
        val visibility: UastVisibility,
        val isStatic: Boolean,
        val returnType: String,
        val name: String,
        val parameters: List<Parameter>
    ) : ClassMember()
}

internal data class Parameter(val type: String, val name: String)

internal fun String.toVisibility() = UastVisibility.valueOf(toUpperCaseAsciiOnly())

internal fun List<ClassMember.Constructor>.contains(parameters: List<String>): Boolean {
    val count = asSequence()
        .filter { it.parameters.size == parameters.size }
        .filter { method ->
            method.parameters.asSequence()
                .zip(parameters.asSequence()) { a, b -> b.endsWith(a.type) }
                .all { it }
        }
        .count()

    return when (count) {
        0 -> false
        1 -> true
        else -> error("More than one constructor found")
    }
}

internal fun List<ClassMember.Field>.contains(isStatic: Boolean, type: String, name: String): Boolean {
    File("log.txt").appendText("$type $name\n")
    val count = asSequence()
        .filter { it.isStatic == isStatic }
        .filter { it.name == name }
        .filter { type.endsWith(it.type) }
        .count()

    return when (count) {
        0 -> false
        1 -> true
        else -> error("More than one field found")
    }
}

internal fun List<ClassMember.Method>.contains(
    isStatic: Boolean,
    returnType: String,
    name: String,
    parameters: List<String>
): Boolean {
    val count = asSequence()
        .filter { it.isStatic == isStatic }
        .filter { it.name == name }
        .filter { returnType.endsWith(it.returnType) }
        .filter { it.parameters.size == parameters.size }
        .filter { method ->
            method.parameters.asSequence()
                .zip(parameters.asSequence()) { a, b -> b.endsWith(a.type) }
                .all { it }
        }
        .count()

    return when (count) {
        0 -> false
        1 -> true
        else -> error("More than one method found")
    }
}
