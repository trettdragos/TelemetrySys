#include <TinyGPS++.h>
#include <SoftwareSerial.h>

static const int RXPin = 4, TXPin = 3;
static const uint32_t GPSBaud = 9600;

// The TinyGPS++ object
TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial ss(10, 11);

void setup(){
  Serial.begin(9600);
  ss.begin(GPSBaud);
}

void loop(){
  // This sketch displays information every time a new sentence is correctly encoded.
  if (ss.available() > 0){
    gps.encode(ss.read());
    if (gps.location.isUpdated()){
      Serial.print("Latitude= "); 
      Serial.print(gps.location.lat(), 6);
      Serial.print(" Longitude= "); 
      Serial.print(gps.location.lng(), 6);
      Serial.print(" Speed= "); 
      Serial.println(gps.speed.kmph(), 6);
    }
  }
}
