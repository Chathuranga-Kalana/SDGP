 #include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Ticker.h>





#define firebaseURl "g00dn8-f489b.firebaseio.com"
#define authCode "Ku9DZMQeOjlGAAqhpSi9XZ6G7YhLc9HUncwH87wB"

#define wifiName "Scooby 4G"
#define wifiPass "0RL31Y1N4R4"

//heart Rate
volatile int BPM;            // Beats Per Minutes
volatile int Signal;  
volatile int IBI = 600;    // inter beat interval
volatile boolean Pulse = false; 
volatile boolean QS = false;

volatile int rate[10];
volatile unsigned long sampleCounter = 0;          // To verify pulse timing
volatile unsigned long lastBeatTime = 0;          // To find IBI
volatile int P = 512;                            // Find the peak in pulse wave, seeded
volatile int T = 512;                           // Find trough in pulse wave, seeded
volatile int thresh = 525;                     // Find instant moment of heart beat, seeded (mid point of the waveform)
volatile int amp = 100;                       // To hold amplitude of pulse waveform, seeded
volatile boolean firstBeat = true;           
volatile boolean secondBeat = false;

static boolean serialVisual = true;
                  // timer class
 Ticker flipper; // used a ticker library

 String chipId = "GNID01";


void setupFirebase() {

  Firebase.begin(firebaseURl, authCode);
}

void setupWifi() {

 WiFi.begin(wifiName, wifiPass);

  Serial.println("connecting...");

  while (WiFi.status() != WL_CONNECTED) {

    Serial.println(".");
    delay(500);
  }

  Serial.println();
  Serial.println(" connected and my IP address: ");
  Serial.println(WiFi.localIP());
}


void setup() {

  Serial.begin(115200);
  setupWifi();
  setupFirebase();
 // Serial.begin(115200);             
  interruptSetup(); 


}

void loop() {
 // heart rate
 serialOutput();  
   
  if (QS == true) // A Heartbeat Was Found
    {     
                // BPM and IBI have been Determined
               // Quantified Self "QS" true when arduino finds a heartbeat (self-knowledge through self tracking with technology)
     
      serialOutputWhenBeatHappens(); // when a Beat Happened, Output that to serial.     
      QS = false;                   // reset the Quantified Self flag for next time    
    }
     
  delay(20);                      
  
}


/////////////////////////////////////////////////// heart rate
void interruptSetup()
{  
  flipper.attach_ms(2, ISRTr);   // Ticker makes sure that we take a reading every 2 miliseconds
  sei();                        //  interupt service routing      
} 

void serialOutput()
{   // condition How To Output Serial. 
 if (serialVisual == true)
  {  
     arduinoSerialMonitorVisual('-', Signal);   // goes to function that makes Serial Monitor Visualizer
  } 
 else
  {
      sendDataToSerial('S', Signal);     // goes to sendDataToSerial function
   }        
}

void serialOutputWhenBeatHappens()
{    
 if (serialVisual == true) //  Code to Make the Serial Monitor Visualizer Work
   {            
     Serial.print("--- Heart-Beat recieved --- ");  
     Serial.print("BPM: ");
     Serial.println(BPM);

     //////////////////////////////////////////////////////////////////////////////////////////////////
   
    StaticJsonBuffer<200> jsonBuffer;
    
        JsonObject& heart = jsonBuffer.createObject();    // Sending the heart Rate data to firebase by Json Object
        heart["Heart Rate"] = BPM;
        Firebase.set(chipId +"/PulseSensor",heart);


   }
 else
   {
     sendDataToSerial('B',BPM);    // send heart rate with a 'B' prefix
     sendDataToSerial('Q',IBI);   // send time between beats with a 'Q' prefix
   }   
}

void arduinoSerialMonitorVisual(char symbol, int data )
{    
  
  const int sensorMin = 0;        // sensor minimum, discovered through experiment
  const int sensorMax = 1024;    // sensor maximum, discovered through experiment
  int sensorReading = data;     // map the sensor range to a range of 12 options:
  int range = map(sensorReading, sensorMin, sensorMax, 0, 11);
  // do something different depending on the range value:
 // 
 
}


void sendDataToSerial(char symbol, int data )
{
   Serial.print(symbol);
   Serial.println(data);                
}

void ISRTr() //triggered when Timer2 counts to 124

{  
  
  cli();                                        // disable interrupts while running
  Signal = analogRead(A0);                     // read the Pulse Sensor 
  sampleCounter += 2;                         // keep track of the time in mS with this variable
  int N = sampleCounter - lastBeatTime;      // monitor the time since the last beat to avoid noise
                                            //  find the peak and trough of the pulse wave
  if(Signal < thresh && N > (IBI/5)*3)     // avoid dicrotic notch by waiting 3/5 of last IBI
    {      
      if (Signal < T) // T is the trough
      {                        
        T = Signal; // keep track of lowest point in pulse wave 
      }
    }

  if(Signal > thresh && Signal > P)
    {                                          // thresh condition helps avoid notch
      P = Signal;                             // P = peak
    }                                        // keep track of highest point in pulse wave

  //   Looking for the heart beat
  // signal surges up in value every time there is a pulse
  if (N > 250)
  {                                   // avoid high frequency noise
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) )
      {        
        Pulse = true;                                 // setting Pulse flag when we assume there's pulse
       
        IBI = sampleCounter - lastBeatTime;         // measure time between beats in miliSeconds
        lastBeatTime = sampleCounter;              // keep track of time for thr next pulse
  
        if(secondBeat)
        {                                       // if this is the second beat, if secondBeat == TRUE
          secondBeat = false;                  // clear secondBeat flag
          for(int i=0; i<=9; i++) // seed the running total to get a realisitic BPM at startup
          {             
            rate[i] = IBI;                      
          }
        }
  
        if(firstBeat) // if it's the first time we found a beat, if firstBeat == TRUE
        {                         
          firstBeat = false;                   // clear firstBeat flag
          secondBeat = true;                  // set the second beat flag
          sei();                             // enable interrupts again
          return;                           // IBI value is unreliable so discard it
        }   
      // keep a running total of the last 10 IBI values
      word runningTotal = 0;              // clear the runningTotal variable    

      for(int i=0; i<=8; i++)
        {                // shift data in the rate array
          rate[i] = rate[i+1];                  // drop the oldest IBI value 
          runningTotal += rate[i];             // add up the 9 oldest IBI values
        }

      rate[9] = IBI;                            // add the latest IBI to the rate array
      runningTotal += rate[9];                 // add the latest IBI to runningTotal
      runningTotal /= 10;                     // taking the average of last 10 IBI values 
      BPM = 60000/runningTotal;              // how many beats could fit into a minute
      QS = true;                            // set Quantified Self flag 
      // !!QS FLAG IS NOT CLEARED INSIDE THIS ISR!!
      
    }                       
  }

  if (Signal < thresh && Pulse == true)
    {  
      // when the values are going down, the beat is over
     
      Pulse = false;                          // reset the Pulse flag so we can repeat it
      amp = P - T;                           // get amplitude of the pulse wave
      thresh = amp/2 + T;                   // set thresh at 50% of the amplitude
      P = thresh;                          // reset these for next time
      T = thresh;
      
    }

  if (N > 2500)                                   // if 2.5 seconds go by without a beat
    {     
                   
      thresh = 512;                             // set thresh default
      P = 512;                                 // set P default
      T = 512;                                // set T default
      lastBeatTime = sampleCounter;          // bring the lastBeatTime up to date        
      firstBeat = true;                     // set these to avoid notch
      secondBeat = false;                  // when we get the heartbeat back
      
    }

  sei();                                 // enable interrupts when youre done!
  
}
