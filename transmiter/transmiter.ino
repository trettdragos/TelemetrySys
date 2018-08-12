#include <SPI.h>
#include <RF24.h>
RF24 radio(7, 8); // CE, CSN
const byte address[6] = "00001";
int count = 0;

int ThermistorPinMotor = A1;//Baterie
int ThermistorPinBatery = A0;//Motor
int VoM;
int VoB;
float R1 = 10000;
float logR2M, logR2B, R2M, R2B, TM, TB;
float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;

void setup() {
  radio.begin();
  radio.openWritingPipe(address);
  radio.setPALevel(RF24_PA_MAX);
  radio.stopListening();
  Serial.begin(9600);
}
char sending[100];
String toSend = "";
void loop() {
  count = millis()/100;
  VoM = analogRead(ThermistorPinMotor);
  VoB = analogRead(ThermistorPinBatery);
  R2M = R1 * (1023.0 / (float)VoM - 1.0);
  R2B = R1 * (1023.0 / (float)VoB - 1.0);
  logR2M = log(R2M);
  logR2B = log(R2B);
  TM = (1.0 / (c1 + c2*logR2M + c3*logR2M*logR2M*logR2M));
  TM = TM - 273.15;
  TB = (1.0 / (c1 + c2*logR2B + c3*logR2B*logR2B*logR2B));
  TB = TB - 273.15;
  
  String textCount = String(count, DEC);
  String textMotor = String(TM, HEX);
  textMotor.remove(5);
  String textBatery = String(TB, HEX);
  textBatery.remove(5);
  toSend = textCount +";"+ textMotor +";"+ textBatery +";";
  Serial.println(toSend);
  toSend.toCharArray(sending, toSend.length()+1);
  radio.write(&sending, sizeof(sending));
  delay(100);
}
