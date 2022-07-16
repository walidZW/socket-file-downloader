package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class FileDownloadClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("attend la réponse du client!");
        label.setTranslateY(50);
        Button button = new Button("telecharger");

        button.setOnAction(event -> {
            try {

                Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 10086);
                OutputStream outputStream = socket.getOutputStream();

                byte[] bytes = "fichierAvantTelechargement.txt".getBytes();
                outputStream.write(bytes,0,bytes.length);
                socket.shutdownOutput();

                new Thread(() -> {
                    while (true) {
                        // The client receives feedback from the server
                        InputStream inputStream;
                        try {
                            inputStream = socket.getInputStream();
                            if (inputStream.available() == 0) {
                                continue;
                            }

                            String fileName = "fichierApresTelechargement.txt";
                            File file = new File(new File("").getAbsolutePath() + "/src/sample/" + fileName);
                            if(file.exists()){
                                file.delete();
                            }

                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            byte[] fileBytes = new byte[1024];
                            int length;
                            while((length = inputStream.read(fileBytes)) != -1){
                                fileOutputStream.write(fileBytes, 0, length);
                            }

                            Platform.runLater(() -> label.setText("Telechargement fini, le fichier est:" + fileName));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        primaryStage.setTitle("TelechargementClient");
        Pane pane = new Pane(label, button);
        primaryStage.setScene(new Scene(pane, 400, 200));
        primaryStage.setX(500);
        primaryStage.setY(100);
        primaryStage.show();

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}