#include <SimpleTimer.h>

SimpleTimer timer;

void repeatMe() {
  float temperatura1 = readTempInCelsius(1,1);
  float temperatura2 = readTempInCelsius(1,2);
  int temp1 = (int)(temperatura1);
  int temp2 = (int)(temperatura2);
  //Serial.write(temp);
  Serial.println(String(temperatura1)+","+millis()/100);
}

void setup(){
  Serial.begin(9600);
  timer.setInterval(100, repeatMe);
}

void loop(){
  timer.run();
}

float readTempInCelsius(int count, int pin) {
  float temperaturaMediata = 0;
  float sumaTemperatura = 0;
  for (int i =0; i < count; i++) {
    int reading = analogRead(pin);
    float voltage = reading * 5.0;
    voltage /= 1024.0;
    float temperatureCelsius = (voltage - 0.5) * 100 ;
    sumaTemperatura = sumaTemperatura + temperatureCelsius;
  }
  return sumaTemperatura / (float)count;
}
