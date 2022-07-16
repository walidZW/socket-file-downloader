package sample;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class FileDownloadServer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("attend la demande du client!");
        try {

            ServerSocket serverSocket = new ServerSocket(10086, 50, InetAddress.getByName("127.0.0.1"));
            new Thread(() -> {
                try {
                    Socket socket;

                    while (true) {

                        socket = serverSocket.accept();

                        String message = "";
                        InputStream inputStream = socket.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String lineMessage;
                        while ((lineMessage = bufferedReader.readLine()) != null) {
                            message += lineMessage;
                        }

                        String finalMessage = message;
                        Platform.runLater(() -> label.setText("telecharger" + finalMessage));

                        socket.shutdownInput();

                        OutputStream outputStream = socket.getOutputStream();
                        FileInputStream fileInputStream = new FileInputStream(new File("").getAbsolutePath() + "/src/sample/" + message);
                        byte[] bytes = new byte[1024];
                        int length;
                        while((length = fileInputStream.read(bytes))!=-1){
                            outputStream.write(bytes,0,length);
                        }

                        socket.shutdownOutput();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("TelechargementServeur");
        Pane pane = new Pane(label);
        primaryStage.setScene(new Scene(pane, 400, 200));
        primaryStage.setX(100);
        primaryStage.setY(100);
        primaryStage.show();

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}