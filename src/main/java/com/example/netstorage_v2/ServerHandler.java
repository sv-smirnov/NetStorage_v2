package com.example.netstorage_v2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>();
    private String filename;
    private String login;
    private ServerAuth serverAuth;
    private File dir;

    public ServerHandler(ServerAuth serverAuth) {
        this.serverAuth = serverAuth;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился " + ctx);
        channels.add(ctx.channel());
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
            } else channelHandlerContext.writeAndFlush("Неверный логин/пароль!");
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
        }

        if (s.startsWith("/delete")) {
            String filePath = dir + "\\" + filename;
            System.out.println(filePath);
            boolean b = Files.deleteIfExists(Paths.get(filePath));
            sendFileList(channelHandlerContext);
            System.out.println(b);
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

