#include <SoftwareSerial.h>

// Arduino Nano + HC-05
// HC-05 TXD -> D10
// HC-05 RXD -> D11 (usar divisor de voltaje recomendado)
SoftwareSerial bluetooth(10, 11); // RX, TX

const int SENSOR_PIN = A0;
const int LED_VERDE = 2;
const int LED_AMARILLO = 3;
const int LED_ROJO = 4;
const int BUZZER = 5;

const int UMBRAL_MEDIO = 360;
const int UMBRAL_ALTO = 720;

void setup() {
  pinMode(LED_VERDE, OUTPUT);
  pinMode(LED_AMARILLO, OUTPUT);
  pinMode(LED_ROJO, OUTPUT);
  pinMode(BUZZER, OUTPUT);

  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop() {
  int valor = analogRead(SENSOR_PIN);
  String estado;

  if (valor >= UMBRAL_ALTO) {
    estado = "ROJO";
    digitalWrite(LED_VERDE, LOW);
    digitalWrite(LED_AMARILLO, LOW);
    digitalWrite(LED_ROJO, HIGH);
    tone(BUZZER, 1200, 120);
  } else if (valor >= UMBRAL_MEDIO) {
    estado = "AMARILLO";
    digitalWrite(LED_VERDE, LOW);
    digitalWrite(LED_AMARILLO, HIGH);
    digitalWrite(LED_ROJO, LOW);
    noTone(BUZZER);
  } else {
    estado = "VERDE";
    digitalWrite(LED_VERDE, HIGH);
    digitalWrite(LED_AMARILLO, LOW);
    digitalWrite(LED_ROJO, LOW);
    noTone(BUZZER);
  }

  String mensaje = estado + "," + String(valor);
  Serial.println(mensaje);
  bluetooth.println(mensaje);
  delay(700);
}
