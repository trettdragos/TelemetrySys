#include <SPI.h>
#include <RF24.h>
#include <Wire.h>

RF24 radio(7, 8); // CE, CSN

const byte address[6] = "00001";
int count = 0;

long accelX, accelY, accelZ;
float gForceX, gForceY, gForceZ;

long gyroX, gyroY, gyroZ;
float rotX, rotY, rotZ;

int ThermistorPinMotor = A2;
int ThermistorPinBatery2 = A1;
int ThermistorPinBatery1 = A0;
int voltagePin1 = A3;
int voltagePin2 = A4;
int VoM;
int VoB1;
int VoB2;
float R1Thermistors = 10000;
float logR2M, logR2B1, R2M, R2B1, TM, TB1, logR2B2, R2B2, TB2;
float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;
double Vout1 = 0.00;
double Vin1 = 0.00;
double Vout2 = 0.00;
double Vin2 = 0.00;
double R1Voltage = 200000.00;
double R2Voltage = 100000.00;
int val1 = 0;
int val2 = 0;
int rev;
int rpm;
int last_time;

void setup() {
  radio.begin();
  radio.openWritingPipe(address);
  radio.setPALevel(RF24_PA_MAX);
  pinMode(2, INPUT);
  attachInterrupt(0, oneSpin, FALLING);
  radio.stopListening();
  Serial.begin(9600);
  Wire.begin();
  setupMPU();
  pinMode(voltagePin1, INPUT);
  pinMode(voltagePin2, INPUT);
}

char sending[100];
String toSendA = "";
String toSendB = "";

void loop() {
  count = millis()/100;
  //accel
  recordAccelRegisters();
  
  //rmp calculating
  if(millis() - last_time >= 1000){
    rpm = rev*6;
    rev = 0;
    last_time = millis();
  }
  
  //thermistors
  VoM = analogRead(ThermistorPinMotor);
  VoB1 = analogRead(ThermistorPinBatery1);
  VoB2 = analogRead(ThermistorPinBatery2);
  R2M = R1Thermistors * (1023.0 / (float)VoM - 1.0);
  R2B1 = R1Thermistors * (1023.0 / (float)VoB1 - 1.0);
  R2B2 = R1Thermistors * (1023.0 / (float)VoB2 - 1.0);
  logR2M = log(R2M);
  logR2B1 = log(R2B1);
  logR2B2 = log(R2B2);
  TM = (1.0 / (c1 + c2*logR2M + c3*logR2M*logR2M*logR2M));
  TM = TM - 273.15;
  TB1 = (1.0 / (c1 + c2*logR2B1 + c3*logR2B1*logR2B1*logR2B1));
  TB1 = TB1 - 273.15;
  TB2 = (1.0 / (c1 + c2*logR2B2 + c3*logR2B2*logR2B2*logR2B2));
  TB2 = TB2 - 273.15;
  
  //voltages
  val1 = analogRead(voltagePin1);
  val2 = analogRead(voltagePin2);
  Vout1 = (val1 * 5.00) / 1024.00;
  Vin1 = Vout1 / (R2Voltage/(R1Voltage+R2Voltage));
  Vout2 = (val2 * 5.00) / 1024.00;
  Vin2 = Vout2 / (R2Voltage/(R1Voltage+R2Voltage));
  
  String textCount = String(count, DEC);
  String textMotor = String(TM, HEX);
  textMotor.remove(5);
  String textBatery1 = String(TB1, HEX);
  textBatery1.remove(5);
  String textBatery2 = String(TB2, HEX);
  textBatery2.remove(5);
  String textBateryVoltage1 = String(Vin1, HEX);
  textBateryVoltage1.remove(5);
  String textBateryVoltage2 = String(Vin2, HEX);
  textBateryVoltage2.remove(5);
  String textRPM = String(rpm, DEC);
  String textGFX = String(gForceX, HEX);
  textGFX.remove(5);
  String textGFY = String(gForceY, HEX);
  textGFY.remove(5);
  toSendA ="A:"+ textCount +";"+ textMotor +";"+ textBatery1 +";" + textBatery2 + ";";
  toSendB ="B:"+ textBateryVoltage1 + ";" + textBateryVoltage2 + ";" + textRPM + ";" + textGFX + ";" + textGFY +";";
  Serial.println(toSendA+toSendB);
  toSendA.toCharArray(sending, toSendA.length()+1);
  radio.write(&sending, sizeof(sending));
  toSendB.toCharArray(sending, toSendB.length()+1);
  delay(50);
  radio.write(&sending, sizeof(sending));
  delay(50);
}

void oneSpin(){
  rev++;
}

void setupMPU(){
  Wire.beginTransmission(0b1101000); //This is the I2C address of the MPU (b1101000/b1101001 for AC0 low/high datasheet sec. 9.2)
  Wire.write(0x6B); //Accessing the register 6B - Power Management (Sec. 4.28)
  Wire.write(0b00000000); //Setting SLEEP register to 0. (Required; see Note on p. 9)
  Wire.endTransmission();  
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1B); //Accessing the register 1B - Gyroscope Configuration (Sec. 4.4) 
  Wire.write(0x00000000); //Setting the gyro to full scale +/- 250deg./s 
  Wire.endTransmission(); 
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1C); //Accessing the register 1C - Acccelerometer Configuration (Sec. 4.5) 
  Wire.write(0b00000000); //Setting the accel to +/- 2g
  Wire.endTransmission(); 
}

void recordAccelRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x3B); //Starting register for Accel Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Accel Registers (3B - 40)
  while(Wire.available() < 6);
  accelX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  accelY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  accelZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processAccelData();
}

void processAccelData(){
  gForceX = accelX / 16384.0;
  gForceY = accelY / 16384.0; 
  gForceZ = accelZ / 16384.0;
}

void recordGyroRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x43); //Starting register for Gyro Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Gyro Registers (43 - 48)
  while(Wire.available() < 6);
  gyroX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  gyroY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  gyroZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processGyroData();
}

void processGyroData() {
  rotX = gyroX / 131.0;
  rotY = gyroY / 131.0; 
  rotZ = gyroZ / 131.0;
}

void printData() {

  Serial.print(" X=");
  Serial.print(gForceX);
  Serial.print(" Y=");
  Serial.println(gForceY);

}
