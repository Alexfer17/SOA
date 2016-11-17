#include <SPI.h>
#include <Ethernet.h>

byte mymac[] = { 0x90, 0xA2, 0xDA, 0x00, 0x9B, 0x36 };
byte myip[] = { 192, 168, 1, 148 };

EthernetServer server(80);

int led1 = 5;
int led2 = 2;
int led3 = 3;
int led4 = 4;

String readString = String(30);

float Sensibilidad=0.139; //sensibilidad en V/A para nuestro sensor
float offset=0.000; // Equivale a la amplitud del ruido

String estadoCircu;

float max=-1;

void setup() {
  Ethernet.begin(mymac, myip);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  pinMode(led4, OUTPUT);

  digitalWrite(led1,LOW);
  digitalWrite(led2,LOW);
  digitalWrite(led3,LOW);
  digitalWrite(led4,LOW);  
  //rele y sensor de corriente

  Serial.begin(9600);
}

float get_corriente()
{
  float voltajeSensor;
  float corriente=0;
  long tiempo=millis();
  float Imax=0;
  float Imin=0;
  while(millis()-tiempo<100)//realizamos mediciones durante 0.2 segundos
  { 
    voltajeSensor = analogRead(A0) * (5.0 / 1023.0);//lectura del sensor
    corriente=0.9*corriente+0.1*((voltajeSensor-2.495)/Sensibilidad); //Ecuación  para obtener la corriente
    if(corriente>Imax)Imax=corriente;
    if(corriente<Imin)Imin=corriente;
  }
  return(((Imax-Imin)/2)-offset);
}

void loop() {
  
  EthernetClient client = server.available();
  
  if(client) 
  {
    while(client.connected())
    {
      if(client.available()) 
      {
        char c = client.read();
        
        if(readString.length() < 30) {
          readString += (c);
        }
        
        if(c == '\n')
        {
          
          if(readString.indexOf("led1") >= 0) {
            digitalWrite(led1, !digitalRead(led1));
            delay(100);
          }
          
          if(readString.indexOf("led2") >= 0) {
            digitalWrite(led2, !digitalRead(led2));
            delay(100);
          }
          
          if(readString.indexOf("led3") >= 0) {
            digitalWrite(led3, !digitalRead(led3));
            delay(100);
          }

          
          if(readString.indexOf("led4") >= 0) {
            digitalWrite(led4, !digitalRead(led4));
            delay(100);
          }
          
          float volta=get_corriente();//obtenemos la corriente pico
          delay(50);
          
          if( (readString.indexOf("maximo=") )>= 0) {
            String aux;
            int aux1 = readString.indexOf("maximo=");  
            aux = readString.substring(aux1+7);
              max = aux.toFloat();
          }
          
          
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println();
         
          client.println("<!doctype html>");
          client.println("<html>");
          client.println("<head>");
          client.println("<title>Tutorial</title>");
          /*client.println("<meta name=\"viewport\" content=\"width=320\">");
          client.println("<meta name=\"viewport\" content=\"width=device-width\">");
          client.println("<meta charset=\"utf-8\">");
          client.println("<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">");*/
          //ver aca para refrescar la pagina
          client.println("<meta http-equiv=\"refres\" content=\"2,URL=http://192.168.1.148\">");
          client.println("</head>");
          client.println("<body>");
          client.println("<center>");
         
          /*
          client.println("<font size=\"5\" face=\"verdana\" color=\"green\">Android</font>");
          client.println("<font size=\"3\" face=\"verdana\" color=\"red\"> & </font>");
          client.println("<font size=\"5\" face=\"verdana\" color=\"blue\">Arduino</font><br />");*/
          
          if(!digitalRead(led1) && !digitalRead(led2) && !digitalRead(led3) && !digitalRead(led4)){
            volta=0;
          }
          
          
          if(digitalRead(led1)) {
            estadoCircu = "ON";
          } else {
            estadoCircu = "OFF";
          }
          client.println("<form action=\"led1\" method=\"get\">");
          client.println("<button type=submit style=\"width:200px;\">Led1 - "+estadoCircu+"</button>");
          
          client.println("</form>volt-");
          client.println(volta);
          client.println("-end<br />");
          
          if(digitalRead(led2)) {
            estadoCircu = "ON";
          } else {
            estadoCircu = "OFF";
          }
          client.println("<form action=\"led2\" method=\"get\">");
          client.println("<button type=submit style=\"width:200px;\">Led2 - "+estadoCircu+"</button>");
          client.println("</form> <br />");
          
          if(digitalRead(led3)) {
            estadoCircu = "ON";
          } else {
            estadoCircu = "OFF";
          }
          client.println("<form action=\"led3\" method=\"get\">");
          client.println("<button type=submit style=\"width:200px;\">Led3 - "+estadoCircu+"</button>");
          client.println("</form> <br />");

          if(digitalRead(led4)) {
            estadoCircu = "ON";
          } else {
            estadoCircu = "OFF";
          }
          client.println("<form action=\"led4\" method=\"get\">");
          client.println("<button type=submit style=\"width:200px;\">Led4 - "+estadoCircu+"</button>");
          client.println("</form> <br />");
          
          client.println("</center>");
          client.println("</body>");
          client.println("</html>");
          
          readString = "";
          
          if(volta >= max && max!= -1){
            
            if(digitalRead(led1)){
              digitalWrite(led1,!digitalRead(led1));
              break;
            }
            else{
              if(digitalRead(led2)){
              digitalWrite(led2,!digitalRead(led2));
              break;
              }else{
                if(digitalRead(led3)){
                digitalWrite(led4,!digitalRead(led3));
                break;
                }else{
                  if(digitalRead(led4)){
                  digitalWrite(led4,!digitalRead(led4));
                  break;
                  }
               }
            }
          }

          }
          
          delay(100);
          client.stop();

          
        }
      }
      
    }
  }
  
}

