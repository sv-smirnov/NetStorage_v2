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
        if (s.startsWith("/authok")) {
            controller.setAuthorized(true);
        }
        if (s.startsWith("/servMsg")) {
            controller.serverStatus.setText(s.substring(9));
        }
        if (s.contains("/list")) {
            controller.fileList.getItems().clear();
            String[] str = s.substring(s.indexOf("/list") + 7).split(",");
            for (int i = 0; i < str.length; i++) {
                controller.fileList.getItems().add(str[i]);
            }
        }
    }
}
