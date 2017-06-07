package ru.erdenian.studentassistant.netty

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import ru.erdenian.studentassistant.extensions.toByteBuf
import java.nio.charset.Charset

fun nettyQuery(query: String, onResponse: (String) -> Unit = {}) {
  class TimeClientHandler : ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
      ctx.writeAndFlush(query.toByteBuf())
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
      val s = (msg as ByteBuf).toString(Charset.forName("UTF-8"))
      onResponse(s)
      ctx.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
      cause.printStackTrace()
      ctx.close()
    }
  }

  val host = "192.168.1.2"
  val port = 1333
  val workerGroup = NioEventLoopGroup()

  try {
    val b = Bootstrap() // (1)
    b.group(workerGroup) // (2)
    b.channel(NioSocketChannel::class.java) // (3)
    b.option(ChannelOption.SO_KEEPALIVE, true) // (4)
    b.handler(object : ChannelInitializer<SocketChannel>() {
      override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(TimeClientHandler())
      }
    })

    // Start the client.
    val f = b.connect(host, port).sync() // (5)

    // Wait until the connection is closed.
    f.channel().closeFuture().sync()
  } finally {
    workerGroup.shutdownGracefully()
  }
}
