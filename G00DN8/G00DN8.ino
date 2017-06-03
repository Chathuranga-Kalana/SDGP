#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <DHT.h>
//relay light
int led = 2;//D4
int states = LOW; //light


//dht sensor
#define DHTTYPE DHT11 
#define DHTPIN 4 //D2  
DHT dht(DHTPIN,DHTTYPE,11);

#define firebaseURl "g00dn8-f489b.firebaseio.com"
#define authCode "Ku9DZMQeOjlGAAqhpSi9XZ6G7YhLc9HUncwH87wB"

#define wifiName "Dialog 4G - Not for Free"
#define wifiPass "Goodsaneth123" 


String chipId = "GNID01";
float humidity, temp;

unsigned long previousMillis = 0;        //  store last temp
const long interval = 2000;              // to read sensor data


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
  dht.begin(); 

  setupPinsMode();
 
}
void getData() {

  String path = chipId + "/states";
  FirebaseObject object = Firebase.get(path);

  bool led1 = object.getBool("001");
  Serial.println("Led 1: ");
  Serial.println(led1);
// write output high or low to turn on or off light 
  digitalWrite(led, led1);
}
void loop() {
  getData();
  getTemp();
}

void setupPinsMode() {

    Serial.printf("Setup Output for pin %d", led);
    pinMode(led, OUTPUT);
  
}
// temp with humidity 
void getTemp(){

  unsigned long currentMillis = millis();
 
  if(currentMillis - previousMillis >= interval) {
    // save last time sensor data
    previousMillis = currentMillis;   
 
 
    humidity = dht.readHumidity();          
    temp = dht.readTemperature();    
    

       Serial.print("Temperature = ");

  Serial.println(temp);
  Serial.print("Humidity = ");
  Serial.println(humidity);
 // send json object to firebase
    StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.createObject();
        root["temperature"] = temp;
        root["Humidity"] = humidity;
        Firebase.set(chipId +"/DHTsensor",root);
    if (isnan(humidity) || isnan(temp)) {
      Serial.println("Failed to read from DHT sensor!");
      return;


     }  
    }
   }
