volatile byte rev;
 unsigned int rpm;
 unsigned long last_time;

void setup() {
  pinMode(2, INPUT);
  attachInterrupt(0, oneSpin, FALLING);
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(millis()-last_time >=1000){
    rpm = rev;
    rev = 0;
    last_time = millis();
  }
  Serial.println(rpm);
  delay(100);
}

void oneSpin(){
  rev++;
}

