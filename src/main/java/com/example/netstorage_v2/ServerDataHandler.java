package com.example.netstorage_v2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.*;
import java.nio.ByteBuffer;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class ServerDataHandler extends ChannelInboundHandlerAdapter {

    ChannelHandlerContext channelHandlerContext;

    public ServerDataHandler() {
            Server.lastServerDataHandler = this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerDataHandler.channelActive");
        channelHandlerContext=ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ServerDataHandler.channelRead");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    public void download(File dir, String filename, String login) throws IOException {
        System.out.println("ServerDataHandler.download");
        String filePath = dir + "\\" + filename;
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        FileChannel fileChannel = file.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        System.out.println("Отправляю " + login + " файл " + filename);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(mappedByteBuffer);
        channelHandlerContext.writeAndFlush(byteBuf);
        System.out.println("Готово");
    }
}
