package sample;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

                    Stage stage = new Stage();
                    stage.setTitle("Data Chart");
                    final NumberAxis xAxis = new NumberAxis();
                    final NumberAxis yAxis = new NumberAxis();
                    xAxis.setLabel("Time");
                    final AreaChart<Number,Number> lineChart =
                            new AreaChart<Number,Number>(xAxis,yAxis);
                    lineChart.setPrefSize(1280, 720);
                    lineChart.setTitle("Monitoring, "+file.getName());

                    final NumberAxis xAxisVolts = new NumberAxis();
                    final NumberAxis yAxisVolts = new NumberAxis();
                    xAxisVolts.setLabel("MAsurat in V");
                    final AreaChart<Number,Number> lineChartVolts =
                            new AreaChart<Number,Number>(xAxisVolts,yAxisVolts);
                    lineChartVolts.setPrefSize(1280, 720);
                    lineChartVolts.setTitle("Monitoring voltaj, "+file.getName());

                    final NumberAxis xAxisTemp = new NumberAxis();
                    final NumberAxis yAxisTemp = new NumberAxis();
                    xAxisTemp.setLabel("Masurat in grade C");
                    final AreaChart<Number,Number> lineChartTemps =
                            new AreaChart<Number,Number>(xAxisTemp,yAxisTemp);
                    lineChartTemps.setPrefSize(1920, 1080);
                    lineChartTemps.setTitle("Monitoring temperature,  "+file.getName());

                    XYChart.Series series = new XYChart.Series();
                    series.setName("tester");
                    XYChart.Series temperaturaMotor = new XYChart.Series();
                    temperaturaMotor.setName("temperatura Motor");
                    XYChart.Series temperaturaBaterie = new XYChart.Series();
                    temperaturaBaterie.setName("Temperatura Baterie");
                    XYChart.Series voltajBaterii = new XYChart.Series();
                    voltajBaterii.setName("Voltaj Baterii");
                    XYChart.Series voltajConsum = new XYChart.Series();
                    voltajConsum.setName("Voltaj Consum");

                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] data = line.split(";");
                            series.getData().add(new XYChart.Data(Float.parseFloat(data[1])/10, Float.parseFloat(data[0])/10));
                            temperaturaMotor.getData().add(new XYChart.Data((Float.parseFloat(data[0])/10), Float.parseFloat(data[1])));
                            temperaturaBaterie.getData().add(new XYChart.Data((Float.parseFloat(data[0])/10), Float.parseFloat(data[2])));
                            voltajBaterii.getData().add(new XYChart.Data((Float.parseFloat(data[1])/10), Float.parseFloat(data[0])/10));
                            voltajConsum.getData().add(new XYChart.Data((Float.parseFloat(data[1])/10), Float.parseFloat(data[0])/10));

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lineChart.getData().addAll(series);
                    lineChartVolts.getData().addAll(voltajBaterii, voltajConsum);
                    lineChartTemps.getData().addAll(temperaturaMotor, temperaturaBaterie);
                    Tab temps = new Tab();
                    temps.setText("Temps");
                    Tab volts = new Tab();
                    volts.setText("Volts");
                    Tab RPM = new Tab();
                    RPM.setText("RPM");

                    BorderPane root = new BorderPane();
                    temps.setContent(lineChartTemps);
                    volts.setContent(lineChartVolts);
                    RPM.setContent(lineChart);
                    TabPane tabPane = new TabPane();
                    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                    tabPane.getTabs().addAll(temps, volts, RPM);

                    root.setCenter(tabPane);
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
                    Scene scene  = new Scene(root,1920,1080);

                    stage.setScene(scene);
                    primaryStage.close();
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
