#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

int led = 2;
int states = LOW;

#define firebaseURl "g00dn8-fcc2a.firebaseio.com"
#define authCode "7v8uKh7jG17GFRz7PXPef97dHcyms30lzfWldsuT"

#define wifiName "kalana's iPhone"
#define wifiPass "kalana123"


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

  Serial.begin(9600);

  setupWifi();
  setupFirebase();

  setupPinsMode();

}
void getData() {

  String path = chipId + "/states";
  FirebaseObject object = Firebase.get(path);

  bool led1 = object.getBool("001");
  Serial.println("Led 1: ");
  Serial.println(led1);
// write output high or low to turn on or off led
  digitalWrite(led, led1);
}
void loop() {
  getData();
}

void setupPinsMode() {

    Serial.printf("Setup Output for pin %d", led);
    pinMode(led, OUTPUT);
  
}

