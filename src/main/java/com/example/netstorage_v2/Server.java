package com.example.netstorage_v2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.sql.SQLException;


public class Server {
    public static ServerHandler lastServerHandler;
    public static ServerDataHandler lastServerDataHandler;

    public static void main(String[] args) throws InterruptedException, SQLException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ServerHandler(new ServerAuth()));
                    }
                });

        EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup2 = new NioEventLoopGroup();
        ServerBootstrap b2 = new ServerBootstrap();
        b2.group(bossGroup2, workerGroup2)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel2) throws Exception {
                        socketChannel2.pipeline().addLast(new ServerDataHandler());
                    }
                });

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChannelFuture future = b.bind(45001).sync();
                    System.out.println("Сервис метаданных запущен");
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        });
        t1.start();
        try {
            ChannelFuture future2 = b2.bind(45002).sync();
            System.out.println("Сервис передачи данных запущен");
            future2.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup2.shutdownGracefully();
            workerGroup2.shutdownGracefully();
        }


    }
}
