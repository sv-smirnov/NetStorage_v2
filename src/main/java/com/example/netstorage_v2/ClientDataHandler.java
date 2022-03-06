package com.example.netstorage_v2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ClientDataHandler extends ChannelInboundHandlerAdapter {
    ClientController clientController;

    public ClientDataHandler(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("!!!");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("???");
        RandomAccessFile file = new RandomAccessFile(clientController.fileName, "rw");
        FileChannel fileChannel = file.getChannel();
        fileChannel.read((ByteBuffer) msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
