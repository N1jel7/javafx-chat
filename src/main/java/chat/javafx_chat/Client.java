package chat.javafx_chat;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Error creating the client");
            e.printStackTrace();
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }

    public void sendMessageToServer(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to the server");
            e.printStackTrace();
            closeEverything(socket,bufferedWriter,bufferedReader);
        }

    }

    public void receiveMessageFromServer(VBox vBox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()) {
                    try {
                        String messageFromServer = bufferedReader.readLine();
                        ClientController.AddLabel(messageFromServer, vBox);
                    } catch (IOException e) {
                        System.out.println("Error receiving message from server.");
                        e.printStackTrace();
                        closeEverything(socket, bufferedWriter, bufferedReader);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if(socket != null) {
                socket.close();
            }
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
