package com.example.netstorage_v2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    ClientController controller;


    public ClientHandler(ClientController clientController) {
        this.controller = clientController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
        if (s.startsWith("/authok")) {
            controller.setAuthorized(true);
        }
        if (s.startsWith("/servMsg")) {
            controller.serverStatus.setText(s.substring(9));
        }
        if (s.contains("/list")) {
            controller.fileList.getItems().clear();
            String[] str = s.substring(s.indexOf("/list")).split(",");
            for (int i = 1; i < str.length; i++) {
                controller.fileList.getItems().add(str[i]);
            }
        }
        if (s.contains("/size")) {
            Long size = Long.parseLong(s.substring(s.indexOf("/size" )+ 6))/1048576;
            controller.freeSpace.clear();
            controller.freeSpace.setText(size + "/1000 MB");
            if (size >= 1000) {
                controller.freeSpace.setText(size + "/1000 MB. Объем превышен!");
                controller.uploadButton.setDisable(true);
            }
            else controller.uploadButton.setDisable(false);
        }
    }
}
