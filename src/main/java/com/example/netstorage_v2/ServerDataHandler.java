package com.example.netstorage_v2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.nio.ByteBuffer;

import java.nio.channels.FileChannel;


public class ServerDataHandler extends ChannelInboundHandlerAdapter {

    ChannelHandlerContext channelHandlerContext;

    public ServerDataHandler() {
            Server.lastServerDataHandler = this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        channelHandlerContext=ctx;
        System.out.println(channelHandlerContext);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    public void download(File dir, String filename, String login) throws IOException {
        String filePath = dir + "\\" + filename;
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        System.out.println("Отправляю " + login + " файл " + filename);
        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            channelHandlerContext.writeAndFlush(byteBuffer);
            System.out.println(byteBuffer);
            byteBuffer.clear();
        }
        System.out.println("Готово");
    }
}
