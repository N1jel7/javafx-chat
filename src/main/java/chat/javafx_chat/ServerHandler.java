package chat.javafx_chat;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable{
    private Server server;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ServerHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Error creating server.");
            e.printStackTrace();
            closeConnection();
        }

    }

    public void sendMessageToClient(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to the client");
            e.printStackTrace();
            closeConnection();
        }

    }

    private void closeConnection() {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String messageFromClient = bufferedReader.readLine();
                /*ServerController.AddLabel(messageFromClient, vBox);*/
                server.sendMessageToAllClients(this, messageFromClient);

            } catch (IOException e) {
                System.out.println("Error receiving message from client.");
                e.printStackTrace();
                closeConnection();
                break;
            }
        }
    }
}
