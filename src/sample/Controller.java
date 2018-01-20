package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

import javax.xml.soap.Text;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller {

    /*public long time;
    public long temp1;
    public long temp2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");
        try {
            serialPort.openPort();
            serialPort.setParams(2000000, 8, 1, 0);
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if (serialPortEvent.isRXCHAR()) {
                    try {

                        String string = serialPort.readString();
                        System.out.println(string);
                        if(string.length()>10){
                            String[] parts2 = string.split("\n");
                            String[] parts = parts2[1].split(";");
                            String[] tempsec1 = parts[1].split(".");
                            String[] tempsec2 = parts[2].split(".");
                            time = Long.parseLong(parts[0]);
                            temp1 = Long.parseLong(tempsec1[0]);
                            temp2 = Long.parseLong(tempsec2[0]);
                            System.out.println(time+" "+temp1 +" "+ temp2);
                            //System.out.println(string);
                        }
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                        System.out.println("shit got fuked man");
                    }

                }
            });
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }*/

}
