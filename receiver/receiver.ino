#include <SPI.h>
#include <RF24.h>
RF24 radio(7, 8); // CE, CSN
const byte address[6] = "00001";
void setup() {
  Serial.begin(9600);
  radio.begin();
  radio.openReadingPipe(0, address);
  radio.setPALevel(RF24_PA_MAX);
  radio.startListening();
}

String A = String("");
String B = String("");
String C = String("");

void loop() {
  if (radio.available()) {
    char text[64] = "";
    radio.read(&text, sizeof(text));
    char *p = text;
    char *str, *res;
    str = strtok_r(p, ":", &p);
    res = strtok_r(p, ":", &p);
    String test = String(res);
    if(str[0] == 'A'){
      A = test;
    }else if(str[0] == 'B'){
      B = test;
    }else if(str[0] == 'C'){
      C = test;
    }
    if(!A.equals("") && !B.equals("") && !C.equals("")){
      Serial.println(A+B+C);
      A = "";
      B = "";
      C = "";
    }
  }
}
