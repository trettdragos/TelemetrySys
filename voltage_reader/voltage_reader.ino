int analogInput = 1;
double Vout = 0.00;
double Vin = 0.00;
double R1 = 200000.00; // resistance of R1 (100K) 
double R2 = 100000.00; // resistance of R2 (10K) 
int val = 0;
void setup(){
   pinMode(A4, INPUT); //assigning the input port
   Serial.begin(9600); //BaudRate
}
void loop(){
   
   val = analogRead(A4);//reads the analog input
   Vout = (val * 5.00) / 1024.00; // formula for calculating voltage out i.e. V+, here 5.00
   Vin = Vout / (R2/(R1+R2)); // formula for calculating voltage in i.e. GND
   if (Vin<0.09)//condition 
   {
     Vin=0.00;//statement to quash undesired reading !
  } 
  Serial.print(val);
  Serial.print("\t");
  Serial.print(Vout);
  Serial.print("\t");
Serial.println(Vin);
delay(100); //for maintaining the speed of the output in serial moniter
}
