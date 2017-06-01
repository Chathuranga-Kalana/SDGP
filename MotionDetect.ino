
int calibrationTime = 10;        
//when sensor give low out put 
long unsigned int lowIn,first,last,duration;         


//to detect is there have more motion at the time
long unsigned int wait = 5000;  

boolean lockLow = true;
boolean takeLowTime;  

int sensorpin = 7;   




void setup(){
  Serial.begin(9600);
  pinMode(sensorpin, INPUT);
  digitalWrite(sensorpin, LOW);

  //give the sensor some time to calibrate
  Serial.print("calibrating sensor ");
    for(int i = 0; i < calibrationTime; i++){
      Serial.print(".");
      delay(1000);
      }
    Serial.println(" done");
    Serial.println("SENSOR ACTIVE");
    delay(50);
  }


void loop(){

     if(digitalRead(sensorpin) == HIGH){

       if(lockLow){  
        
         lockLow = false;            
         Serial.println("---");
         Serial.print("motion detected at ");
         first =millis()/1000;
         Serial.print(first);
         Serial.println(" sec"); 
         delay(50);
         }         
         takeLowTime = true;
       }

     if(digitalRead(sensorpin) == LOW){       
      
       if(takeLowTime){
        lowIn = millis();          //save the time of the transition from high to LOW
        takeLowTime = false;       //this will hapen start of low 
        }
       // assume isn't there hae any motion
       if(!lockLow && millis() - lowIn > wait){  
           // this part work only if there have any new motion happned
           lockLow = true;                        
           Serial.print("motion ended at ");      //output
           last =(millis() - wait)/1000;
           Serial.print(last);
           Serial.println(" sec");
           delay(50);
           }
       }
       if(last!=0){
       duration = last-first;
       Serial.print(duration);
         Serial.println(" sec");}
  }
