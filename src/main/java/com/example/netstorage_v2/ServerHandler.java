package com.example.netstorage_v2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    public String filename;
    public String login;
    public ServerAuth serverAuth;
    public File dir;
    public ServerDataHandler serverDataHandler;
    public long usedSpace = 0;
    List<String> userFiles = new ArrayList<>();

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
        while (Server.lastServerDataHandler == null) {
        }
        serverDataHandler = Server.lastServerDataHandler;
        Server.lastServerDataHandler = null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
        if (s.startsWith("/auth")) {
            login = s.split("\\s")[1];
            String pass = s.split("\\s")[2];
            if (serverAuth.authCheck(login, pass)) {
                channelHandlerContext.writeAndFlush("/authok");
                createDirectory();
                sendFileList(channelHandlerContext);
                getSize(channelHandlerContext);
            } else channelHandlerContext.writeAndFlush("/servMsg " + "Неверный логин/пароль!");
        }
        if (s.startsWith("/reg")) {
            login = s.split("\\s")[1];
            String pass = s.split("\\s")[2];
            if (serverAuth.registration(login, pass)) {
                channelHandlerContext.writeAndFlush("/authok");
                createDirectory();
                sendFileList(channelHandlerContext);
                getSize(channelHandlerContext);
            } else channelHandlerContext.writeAndFlush("Такая учетная запись уже существует!");
        }

        if (s.startsWith("/delete")) {
            filename = s.substring(8);
            String filePath = dir + "\\" + filename;
            try {
                boolean b = Files.deleteIfExists(Paths.get(filePath));
                System.out.println(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendFileList(channelHandlerContext);
            getSize(channelHandlerContext);
        }

        if (s.startsWith("/download")) {
            filename = s.substring(10);
            serverDataHandler.download(dir, filename, login);
        }

        if (s.startsWith("/upload")) {
            filename = s.substring(8);
            RandomAccessFile uploadedFile = new RandomAccessFile(dir + "\\" + filename, "rw");
            FileChannel fileChannel = uploadedFile.getChannel();
            serverDataHandler.fileChannel = fileChannel;
            sendFileList(channelHandlerContext);
            getSize(channelHandlerContext);
        }

        if (s.startsWith("/list")) {
            sendFileList(channelHandlerContext);
            getSize(channelHandlerContext);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println(login + " отключился");
        ctx.close();
    }

    public void sendFileList(ChannelHandlerContext chc) {
        userFiles.clear();
        usedSpace = 0;
        for (File f : dir.listFiles()) {
            if (f.isFile())
                userFiles.add(f.getName());
            usedSpace = usedSpace + f.length();
        }
        String sendingMsg = "/list ";
        for (int i = 0; i < userFiles.size(); i++) {
            String s = userFiles.get(i);
            sendingMsg = sendingMsg.concat("," + s);
        }
        chc.writeAndFlush(sendingMsg);
//        chc.writeAndFlush("/size " + usedSpace/1024/1024);
    }
    public void getSize (ChannelHandlerContext chc) {chc.writeAndFlush("/size " + usedSpace/1024/1024);}


    public void createDirectory() {
        dir = new File("storage/" + login);
        if (!dir.exists()) dir.mkdirs();
    }

}

