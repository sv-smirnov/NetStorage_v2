package com.example.netstorage_v2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>();
    public String filename;
    public String login;
    public ServerAuth serverAuth;
    public File dir;
    public ServerDataHandler serverDataHandler;

    public ServerHandler(ServerAuth serverAuth) throws SQLException {
        Server.lastServerHandler = this;

        try {
            this.serverAuth = serverAuth;
            serverAuth.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerHandler.channelActive");
        while (Server.lastServerDataHandler == null) {
        }
        serverDataHandler = Server.lastServerDataHandler;
        Server.lastServerDataHandler = null;
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("ServerHandler.channelRead");
        System.out.println(s);
        if (s.startsWith("/auth")) {
            login = s.split("\\s")[1];
            String pass = s.split("\\s")[2];
            if (serverAuth.authCheck(login, pass)) {
                channelHandlerContext.writeAndFlush("/authok");
                createDirectory();
                sendFileList(channelHandlerContext);
            } else channelHandlerContext.writeAndFlush("/servMsg " + "Неверный логин/пароль!");
        }
        if (s.startsWith("/reg")) {
            login = s.split("\\s")[1];
            String pass = s.split("\\s")[2];
            if (serverAuth.registration(login, pass)) {
                channelHandlerContext.writeAndFlush("/authok");
                createDirectory();
                sendFileList(channelHandlerContext);
            } else channelHandlerContext.writeAndFlush("Такая учетная запись уже существует!");
        }

        if (s.startsWith("/file")) {
            filename = s.substring(6);
            RandomAccessFile uploadedFile = new RandomAccessFile(dir + "\\" + filename, "rw");
            FileChannel fileChannel = uploadedFile.getChannel();
            serverDataHandler.fileChannel = fileChannel;
        }

        if (s.startsWith("/delete")) {
            String filePath = dir + "\\" + filename;
            boolean b = Files.deleteIfExists(Paths.get(filePath));
            sendFileList(channelHandlerContext);
            System.out.println(b);
        }

        if (s.startsWith("/download")) {
            filename = s.substring(10);
            serverDataHandler.download(dir, filename, login);
        }

        if (s.startsWith("/upload")) {
            filename = s.substring(8);
            sendFileList(channelHandlerContext);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println(login + " отключился");
        channels.remove(ctx.channel());
        ctx.close();
    }

    public void sendFileList(ChannelHandlerContext chc) {
        List<String> userFiles = new ArrayList<>();
        for (File f : dir.listFiles()) {
            if (f.isFile())
                userFiles.add(f.getName());
        }
        String sendingMsg = "/list ";
        for (int i = 0; i < userFiles.size(); i++) {
            String s = userFiles.get(i);
            sendingMsg = sendingMsg.concat("," + s);
        }
        chc.writeAndFlush(sendingMsg);
    }

    public void createDirectory() {
        dir = new File("storage/" + login);
        if (!dir.exists()) dir.mkdirs();
    }

}

