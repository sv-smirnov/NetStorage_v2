package com.example.netstorage_v2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.channels.FileChannel;

public class ClientConnection {

    private SocketChannel channel;
    ClientController clientController;


    public ClientConnection(ClientController clientController) {
        this.clientController = clientController;
        Thread t = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ClientHandler(clientController) {
                                });
                            }
                        });
                ChannelFuture channelFuture = b.connect("localhost", 45001).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });

        Thread t2 = new Thread(() -> {
            EventLoopGroup workerGroup2 = new NioEventLoopGroup();
            try {
                Bootstrap b2 = new Bootstrap();
                b2.group(workerGroup2)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel2) throws Exception {
                                socketChannel2.pipeline().addLast(new ClientDataHandler(clientController) {
                                });
                            }
                        });
                ChannelFuture channelFuture2 = b2.connect("localhost", 45002).sync();
                channelFuture2.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup2.shutdownGracefully();
            }
        });


        t.setDaemon(true);
        t.start();
        t2.setDaemon(true);
        t2.start();
    }

    public void send(String s) {
        channel.writeAndFlush(s);
    }

}
