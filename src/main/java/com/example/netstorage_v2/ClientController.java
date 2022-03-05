package com.example.netstorage_v2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private String fileName;
    private String filePath;

    ClientConnection clientConnection;
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
    TextArea fileList2;
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
            login.setVisible(false);
            password.setVisible(false);
            loginLabel.setVisible(false);
            passwordLabel.setVisible(false);
            sendAuthButton.setVisible(false);
            registrationButton.setVisible(false);
            selectButton.setVisible(true);
            downloadButton.setVisible(true);
            deleteButton.setVisible(true);
            uploadButton.setVisible(true);
            fileInfo.setVisible(true);
        } else {
            serverStatus.setText("Введите логин/пароль");
            login.setVisible(true);
            password.setVisible(true);
            loginLabel.setVisible(true);
            passwordLabel.setVisible(true);
            sendAuthButton.setVisible(true);
            registrationButton.setVisible(true);
            selectButton.setVisible(false);
            downloadButton.setVisible(false);
            deleteButton.setVisible(false);
            uploadButton.setVisible(false);
            fileInfo.setVisible(false);
        }
    }

    public void registration(ActionEvent actionEvent) {
        String regMsg = "/reg " + login.getText() + " " + password.getText();
        System.out.println(regMsg);
        clientConnection.send(regMsg);
        login.clear();
        password.clear();
    }


    public void selectFile(ActionEvent actionEvent) {

            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                fileName = selectedFile.getName();
                clientConnection.send("/file " + fileName);
                filePath = selectedFile.getAbsolutePath();
                fileInfo.setText(filePath);
            } else System.out.println("Выберите файл");

    }

    public void listSelect(MouseEvent mouseEvent) {
        if (fileList.getSelectionModel().getSelectedItem() != null) {
            fileName = fileList.getSelectionModel().getSelectedItem().toString();
            fileInfo.setText(fileName);
            clientConnection.send("/file " + fileName);
            fileList.getSelectionModel().clearSelection();
        }
    }
}