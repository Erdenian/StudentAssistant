package ru.erdenian.studentassistant.extensions

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset

val String.asSingleLine: String
  get() = replace(System.getProperty("line.separator"), " ")

fun String.toByteBuf(): ByteBuf = Unpooled.copiedBuffer(this.toByteArray(Charset.forName("UTF-8")))
