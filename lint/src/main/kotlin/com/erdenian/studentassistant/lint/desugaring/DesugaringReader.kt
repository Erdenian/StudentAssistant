package com.erdenian.studentassistant.lint.desugaring

import java.net.URL

internal fun readDesugaringClasses(uri: URL): Map<String, ClassMembers> = uri.readText()
    .split("\n\n\n")
    .map { it.split('\n') }
    .associate { items ->
        val className = items.first()

        val constructors = mutableListOf<ClassMember.Constructor>()
        val fields = mutableListOf<ClassMember.Field>()
        val methods = mutableListOf<ClassMember.Method>()

        items.asSequence()
            .filter { it.isNotBlank() }
            .filter { !it.startsWith("#") }
            .filter { !it.startsWith("//") }
            .drop(1)
            .map { it.trim() }
            .forEach { item ->
                if (item.startsWith("Additional methods on existing class.")) return@forEach
                if (item.startsWith("Fully implemented class.")) return@forEach

                fun String.parametersSequence(): Sequence<String> {
                    val indexOfFirst = indexOf('(')
                    check(last() == ')')
                    val indexOfLast = lastIndex

                    return substring(indexOfFirst + 1, indexOfLast)
                        .splitToSequence(", ")
                        .filter { it.isNotEmpty() }
                }

                val split = item.takeWhile { it != '(' }.split(" ")
                val visibility = split[0].toVisibility()
                val isStatic = split.contains("static")
                when {
                    split.size == 2 -> { // Constructor
                        check(className.endsWith(split[1].takeWhile { it != '(' }))
                        constructors += ClassMember.Constructor(
                            visibility,
                            item.parametersSequence().map { parameter ->
                                val typeWithName = parameter.split(" ").filter { it.isNotEmpty() }
                                check(typeWithName.size == 2)
                                Parameter(typeWithName[0], typeWithName[1])
                            }.toList()
                        )
                    }
                    !item.contains('(') -> { // Field
                        fields += ClassMember.Field(
                            visibility,
                            isStatic,
                            split[split.lastIndex - 1],
                            split.last()
                        )
                    }
                    else -> { // Method
                        methods += ClassMember.Method(
                            visibility,
                            isStatic,
                            split[split.lastIndex - 1],
                            split.last(),
                            item.parametersSequence().map { parameter ->
                                val typeWithName = parameter.split(" ")
                                Parameter(typeWithName[0], typeWithName[1])
                            }.toList()
                        )
                    }
                }
            }

        className to ClassMembers(constructors, fields, methods)
    }
