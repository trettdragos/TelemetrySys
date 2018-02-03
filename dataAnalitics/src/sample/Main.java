package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.applet.AppletListener;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main extends Application {

    private Desktop desktop = Desktop.getDesktop();

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Data Analitics");
        final FileChooser fileChooser = new FileChooser();
        String currentDir = System.getProperty("user.dir") + File.separator+"src"+File.separator+"sample"+File.separator+"db"+File.separator;
        File file = new File(currentDir);
        fileChooser.setInitialDirectory(file);
        Button btn = new Button();
        btn.setText("Open Data File");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    EventHandler<ActionEvent> thread = this;
                    primaryStage.close();
                    Stage stage = new Stage();
                    stage.setTitle("Data Chart");
                    final NumberAxis xAxis = new NumberAxis();
                    final NumberAxis yAxis = new NumberAxis();
                    xAxis.setLabel("Time");
                    final AreaChart<Number,Number> lineChart =
                            new AreaChart<Number,Number>(xAxis,yAxis);
                    lineChart.setPrefSize(1280, 720);
                    lineChart.setTitle("Monitoring, "+file.getName());

                    XYChart.Series series = new XYChart.Series();
                    series.setName("Temperatura");

                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] data = line.split(",");
                            series.getData().add(new XYChart.Data(Float.parseFloat(data[1])/10, Float.parseFloat(data[0])));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    BorderPane root = new BorderPane();

                    root.setCenter(lineChart);
                    Button button = new Button();
                    button.setText("Close Chart");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            try {
                                Platform.exit();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    root.setTop(button);
                    Scene scene  = new Scene(root,1280,720);
                    lineChart.getData().addAll(series);

                    stage.setScene(scene);
                    stage.show();
                }
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
