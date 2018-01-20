package sample;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jssc.SerialPort;
import static jssc.SerialPort.MASK_RXCHAR;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class Main extends Application {
    SerialPort arduinoPort = null;
    ObservableList<String> portList;

    Label labelValue;
    final int NUM_OF_POINT = 50;
    XYChart.Series series;

    private void detectPort(){

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for(String name: serialPortNames){
            System.out.println(name);
            portList.add(name);
        }
    }

    @Override
    public void start(Stage primaryStage) {

        labelValue = new Label();
        labelValue.setFont(new Font("Arial", 28));

        detectPort();
        final ComboBox comboBoxPorts = new ComboBox(portList);
        comboBoxPorts.valueProperty()
                .addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {

                        System.out.println(newValue);
                        disconnectArduino();
                        connectArduino(newValue);
                    }

                });

        //LineChart
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Voltage");

        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Arduino Uno A0 Analog Input");
        series = new XYChart.Series();
        series.setName("A0 analog input");
        lineChart.getData().add(series);
        lineChart.setAnimated(false);

        //pre-load with dummy data
        for(int i=0; i<NUM_OF_POINT; i++){
            series.getData().add(new XYChart.Data(i, 0));
        }
        //

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                comboBoxPorts, labelValue, lineChart);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle(
                "arduino-er.blogspot.com: Java + JavaFX + jSSC demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void shiftSeriesData(float newValue)
    {
        for(int i=0; i<NUM_OF_POINT-1; i++){
            XYChart.Data<String, Number> ShiftDataUp =
                    (XYChart.Data<String, Number>)series.getData().get(i+1);
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn =
                    (XYChart.Data<String, Number>)series.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData =
                (XYChart.Data<String, Number>)series.getData().get(NUM_OF_POINT-1);
        lastData.setYValue(newValue);
    }

    public boolean connectArduino(String port){

        System.out.println("connectArduino");

        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if(serialPortEvent.isRXCHAR()){
                    try {
                        /*int[] value = serialPort.readIntArray(32);
                        String string = String.valueOf(value[0]);
                        System.out.println(string);*/
                        String string = serialPort.readString(2);
                        System.out.println(string);
                        if(string.length()==2){
                            int value = Integer.parseInt(string);
                            String finalString = string;
                            Platform.runLater(() -> {
                                labelValue.setText(finalString);
                                shiftSeriesData(value); //in 5V scale
                            });
                        }
                    } catch (SerialPortException ex) {
                        Logger.getLogger(Main.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }

                }
            });

            arduinoPort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }

        return success;
    }

    public void disconnectArduino(){

        System.out.println("disconnectArduino()");
        if(arduinoPort != null){
            try {
                arduinoPort.removeEventListener();

                if(arduinoPort.isOpened()){
                    arduinoPort.closePort();
                }

            } catch (SerialPortException ex) {
                Logger.getLogger(Main.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        disconnectArduino();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

}