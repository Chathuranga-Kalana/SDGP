OneWire  ds(4);
 // wifi id
const char* id     = "<<wifi BAT-CAVE>>";  
       // wifi password            
const char* password = "<<wifi camarose123456>>";                
          
                                                           
                                                            //    String auth 
                                                            // is the encoding for "user:password"


String getTemperature()
{
  byte type_s;
  byte datarr[12];
  byte addarr[8];
  float celsius;
  String Ccelsius;

  if ( !ds.search(addarr)) {                                            // find the chip id
    if ( !ds.search(addarr)) {
      ds.reset_search();
      delay(250);
      return "Error1";
    }
  }

  if (OneWire::crc8(addarr, 7) != addarr[7]) {                            // check the CRC
    return "Error2";
  }

  switch (addarr[0]) {                                                  // check if it's the actual chip id
    case 0x10: type_s = 1; break;
    case 0x28: type_s = 0; break;
    case 0x22: type_s = 0; break;
    default:
      Serial.println("The device is not a DS18x20 family device.");       // Not an actual chip 
      return "Error3";
  }

  ds.reset();
  ds.select(addarr);
  ds.write(0x44, 1);                                                  // start conversion

  delay(750); 

  ds.reset();
  ds.select(addarr);
  ds.write(0xBE);

  for (byte i = 0; i < 9; i++) {                                     // reading the data
    datarr[i] = ds.read();
  }

  
  int16_t raw = (datarr[1] << 8) | datarr[0];                            // Converting data to temperature digits
  if (type_s) {
    raw = raw << 3;
    if (datarr[7] == 0x10) {
      raw = (raw & 0xFFF0) + 12 - datarr[6];
    }
  } else {
    byte cfg = (datarr[4] & 0x60);
    if (cfg == 0x00) raw = raw & ~7;                                // based on the resolution make the conversion 
    else if (cfg == 0x20) raw = raw & ~3;
    else if (cfg == 0x40) raw = raw & ~1;
  }
  celsius = (float)raw / 16.0;
  Ccelsius = String(celsius);
  return Ccelsius;
}

void sendTemperature() {
  Serial.print("Connecting to..... ");
  Serial.println(host);
  
  // Using WiFiClient class to generate TCP connections
  WiFiClient client;
  if (!client.connect(host, httpPort)) {
    Serial.println("failed to connect");
    return;
  }
  
  // creating an URI for the request
  String url = "/Wire/connector/set?id=" + devId + "&TEMPERATURE="+getTemperature();
  
  Serial.print("Requesting URL: ");
  Serial.println(url);
  
  // Send the request to the server
  client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Authorization: Basic " + auth + "\r\n" + 
               "Host: " + host + "\r\n" + 
               "Connection: close\r\n\r\n");
  delay(10);
  
  // Read all lines of the reply from server and print to Serial
  while(client.available()){
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }
  
  Serial.println();
  Serial.println("closing the connection");
}

void setup() {
  Serial.begin(115200);
  delay(10);

  //connect to a WiFi network

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(id);
  
  WiFi.begin(id, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("...");
  }


  Serial.println("WiFi is succesfully connected");  
  Serial.println("IP address is: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  sendTemperature();
  delay(2500);
}
