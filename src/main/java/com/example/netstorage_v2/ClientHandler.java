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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);

        if (s.startsWith("/authok")) {
            controller.setAuthorized(true);
        }
        if (s.startsWith("/servMsg")) {
            controller.serverStatus.setText(s.substring(9));
        }
        if (s.startsWith("/list")) {
            System.out.println(s);
            String[] str = s.substring(7).split(",");
            System.out.println(str);
            for (int i = 0; i < str.length; i++) {
                controller.fileList.getItems().add(str[i]);
            }





        }


    }
}
