package com.example.netstorage_v2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public String fileName;
    public String filePath;
    public RandomAccessFile downloadedFile;
    ClientConnection clientConnection;
    ChannelHandlerContext dataCtx;

    @FXML
    TextField login;
    @FXML
    TextField password;
    @FXML
    Label loginLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Button sendAuthButton;
    @FXML
    TextField serverStatus;
    @FXML
    Button registrationButton;
    @FXML
    Button selectButton;
    @FXML
    Button uploadButton;
    @FXML
    Button downloadButton;
    @FXML
    Button deleteButton;
    @FXML
    TextField fileInfo;
    @FXML
    ListView<String> fileList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientConnection = new ClientConnection(this);
        setAuthorized(false);
    }

    public void sendAuth(ActionEvent actionEvent) {
        String authMsg = "/auth " + login.getText() + " " + password.getText();
        System.out.println(authMsg);
        clientConnection.send(authMsg);
        login.clear();
        password.clear();
    }

    public void setAuthorized(boolean b) {
        if (b) {
            serverStatus.setText("Подключение установлено");
            login.setDisable(true);
            password.setDisable(true);
            loginLabel.setDisable(true);
            passwordLabel.setDisable(true);
            sendAuthButton.setDisable(true);
            registrationButton.setDisable(true);
            selectButton.setDisable(false);
            downloadButton.setDisable(false);
            deleteButton.setDisable(false);
            uploadButton.setDisable(false);
            fileInfo.setDisable(false);
        } else {
            serverStatus.setText("Введите логин/пароль");
            login.setDisable(false);
            password.setDisable(false);
            loginLabel.setDisable(false);
            passwordLabel.setDisable(false);
            sendAuthButton.setDisable(false);
            registrationButton.setDisable(false);
            selectButton.setDisable(true);
            downloadButton.setDisable(true);
            deleteButton.setDisable(true);
            uploadButton.setDisable(true);
            fileInfo.setDisable(true);
        }
    }

    public void registration(ActionEvent actionEvent) {
        String regMsg = "/reg " + login.getText() + " " + password.getText();
        clientConnection.send(regMsg);
        login.clear();
        password.clear();
    }


    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileName = selectedFile.getName();
            filePath = selectedFile.getAbsolutePath();
            fileInfo.setText(filePath);
        } else System.out.println("Выберите файл");

    }

    public void listSelect(MouseEvent mouseEvent) {
        if (fileList.getSelectionModel().getSelectedItem() != null) {
            fileName = fileList.getSelectionModel().getSelectedItem().toString();
            fileInfo.setText(fileName);
            fileList.getSelectionModel().clearSelection();
        }
    }

    public void delete(ActionEvent actionEvent) {
        fileInfo.clear();
        fileList.getItems().clear();
        clientConnection.send("/delete " + fileName);
        fileInfo.setText(fileName + " удален с сервера");

    }

    public void download(ActionEvent actionEvent) throws FileNotFoundException {
        clientConnection.send("/download " + fileName);
        downloadedFile = new RandomAccessFile("download" + "\\" + fileName, "rw");
        fileInfo.setText(fileName + " сохранен в ...\\download");
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        clientConnection.send("/upload " + fileName);
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        FileChannel fileChannel = file.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        System.out.println("Отправляю файл " + fileName);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(mappedByteBuffer);
        dataCtx.writeAndFlush(byteBuf);
        fileInfo.setText(fileName + " загружен на сервер");
        clientConnection.send("/list");
    }
}
