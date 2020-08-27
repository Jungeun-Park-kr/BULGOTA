# BULGOTA - Team MINCHO

​																																										작성자 박정은

우리 프로젝트에 사용 코드 : 첫 번째 코드

## Arduino Codes

### Breathe Testing Using Ultrasonic waves & RGB module

```c
/* MQ-3 Alcohol Sensor Circuit with Arduino */
#include <DigitShield.h>
#include <Wire.h>
#include <SoftwareSerial.h>

/*블루투스 관련 변수*/
int Tx = 6; //전송
int Rx = 7; //수신
SoftwareSerial btSerial(Tx, Rx);

/*음주측정 관련 변수*/
const int AOUTpin = 0;
float sensor_volt;
float RS_gas;
float R0;
int R2 = 2000;
float ratio;
float BAC;

/*초음파 관련 변수*/
int trigPin = 13; // trigPin을 13으로 설정합니다.
int echoPin = 12; // echoPin을 12로 설정합니다.

/*LED 관련 변수*/
int LED_Blue = 11;
int LED_Green = 10;
int LED_Red = 9;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
  DigitShield.begin();
  pinMode(trigPin, OUTPUT); // trigPin 핀을 출력핀으로 설정(초음파 보내기)
  pinMode(echoPin, INPUT); // echoPin 핀을 입력핀으로 설정(초음파 받기)
  pinMode(LED_Red,OUTPUT);
  pinMode(LED_Blue, OUTPUT);
  pinMode(LED_Green, OUTPUT);
}

void loop()
{
  int sensorValue = analogRead(AOUTpin); //알코올 센서 값 받기
  long duration, distance; // 초음파 변수를 선언

  /* 초음파 측정 시작 */
  digitalWrite(trigPin, LOW); // trigPin에 LOW를 출력하고
  delayMicroseconds(2); // 2 마이크로초가 지나면
  digitalWrite(trigPin, HIGH); // trigPin에 HIGH를 출력
  delayMicroseconds(10); // trigPin을 10마이크로초 동안 기다렸다가
  digitalWrite(trigPin, LOW); // trigPin에 LOW를 출력
  duration = pulseIn(echoPin, HIGH); // echoPin핀에서 펄스값을 받아옴
  // trigPin핀에서 초음파를 발사하였고 그 초음파가 다시 돌아 올 때까지 기다림.
  //만약 벽이나 장애물에 닿아서 다시 echoPin으로 돌아왔다면 그동안의 시간을 duration에 저장.

 distance = duration * 17 / 1000;          //  duration을 연산하여 센싱한 거리값을 distance에 저장
  /*
     거리는 시간 * 속도입니다.
     속도는 음속으로 초당 340mm이므로 시간 * 340m이고 cm단위로 바꾸기 위해 34000cm로 변환합니다.
     시간 값이 저장된 duration은 마이크로초 단위로 저장되어 있어, 변환하기 위해 1000000을 나눠줍니다.
     그럼 시간 * 34000 / 1000000이라는 값이 나오고, 정리하여 거리 * 34 / 1000이 됩니다.
     하지만 시간은 장애물에 닿기까지와 돌아오기까지 총 두 번의 시간이 걸렸으므로 2를 나누어줍니다.
     그럼 시간 * 17 / 1000이라는 공식이 나옵니다.
  */

  if (0 <=distance &&distance <= 10) { //10cm이내에 있을 때만 측정 하기
    Serial.print("------ distance : ");
    Serial.print(distance);
    Serial.println(" cm ------");
      /* 알코올 센서 측정 시작 */
    sensor_volt = (float)sensorValue/1024*5.0;
    RS_gas = ((5.0 * R2)/sensor_volt) + R2;
    R0 = 2000;
    ratio = RS_gas/R0; //ratio = RS/R0
    double x = 0.4*ratio;
    BAC = pow(x,-1.431); //BAC in mg/L
  
    if(btSerial.available()) { //블루투스와 연결 되어있으면 시리얼 모니터에 BAC, AOut값 출력
     Serial.write(btSerial.read());
    }
    if (Serial.available()) { //시리얼 연결이 되어있으면 블루투스로 BAC 값 전송
      btSerial.write(Serial.read());
    }
    
    DigitShield.setValue(sensorValue); //BAC값 디지털 실드에 출력
    btSerial.println(BAC*0.1); //블루투스로 BAC 값 전송
    Serial.print("BAC = ");
    Serial.print(BAC*0.1);
    Serial.print("mg/100mL   -------  SensorValue = ");
    Serial.println(sensorValue);

    if(BAC*0.1 < 0.03) { //운전 가능일경우
      digitalWrite(LED_Green, HIGH);
      delay(1000);
      digitalWrite(LED_Green, LOW);
    }
    else { //음주 불가일 경우
      digitalWrite(LED_Red, HIGH);
      delay(2000);
      digitalWrite(LED_Red, LOW);
    }
    
    //Serial.print(BAC*0.0001); //convert to g/dL
    //Serial.print(" g/DL\n\n");
  }
  else {
    Serial.println("@@@10cm보다 멀리 있습니다.@@@");
    //Serial.println(-999);
    btSerial.println(-999);
  }
//   else if (distance >= 200 || distance <= 0)       // 거리가 200cm가 넘거나 0보다 작으면
//  {
//    Serial.println("거리를 측정할 수 없음");   // 에러를 출력합니다.
//  }
  
  delay(1000); //측정 딜레이 (테스트 용으로 넣어둔 것)
}
```



### Breathe Testing Using Ultrasonic waves

```c++
/* MQ-3 Alcohol Sensor Circuit with Arduino */
#include <DigitShield.h>
#include <Wire.h>
#include <SoftwareSerial.h>

/*블루투스 관련 변수*/
int Tx = 6; //전송
int Rx = 7; //수신
SoftwareSerial btSerial(Tx, Rx);

/*음주측정 관련 변수*/
const int AOUTpin = 0;
float sensor_volt;
float RS_gas;
float R0;
int R2 = 2000;
float ratio;
float BAC;

/*초음파 관련 변수*/
int trigPin = 13; // trigPin을 13으로 설정합니다.
int echoPin = 12; // echoPin을 12로 설정합니다.


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
  DigitShield.begin();
  pinMode(trigPin, OUTPUT); // trigPin 핀을 출력핀으로 설정(초음파 보내기)
  pinMode(echoPin, INPUT); // echoPin 핀을 입력핀으로 설정(초음파 받기)

}

void loop()
{
  int sensorValue = analogRead(AOUTpin); //알코올 센서 값 받기
  long duration, distance; // 초음파 변수를 선언

  /* 초음파 측정 시작 */
  digitalWrite(trigPin, LOW); // trigPin에 LOW를 출력하고
  delayMicroseconds(2); // 2 마이크로초가 지나면
  digitalWrite(trigPin, HIGH); // trigPin에 HIGH를 출력
  delayMicroseconds(10); // trigPin을 10마이크로초 동안 기다렸다가
  digitalWrite(trigPin, LOW); // trigPin에 LOW를 출력
  duration = pulseIn(echoPin, HIGH); // echoPin핀에서 펄스값을 받아옴
  // trigPin핀에서 초음파를 발사하였고 그 초음파가 다시 돌아 올 때까지 기다림.
  //만약 벽이나 장애물에 닿아서 다시 echoPin으로 돌아왔다면 그동안의 시간을 duration에 저장.

 distance = duration * 17 / 1000;          //  duration을 연산하여 센싱한 거리값을 distance에 저장
  /*
     거리는 시간 * 속도입니다.
     속도는 음속으로 초당 340mm이므로 시간 * 340m이고 cm단위로 바꾸기 위해 34000cm로 변환합니다.
     시간 값이 저장된 duration은 마이크로초 단위로 저장되어 있어, 변환하기 위해 1000000을 나눠줍니다.
     그럼 시간 * 34000 / 1000000이라는 값이 나오고, 정리하여 거리 * 34 / 1000이 됩니다.
     하지만 시간은 장애물에 닿기까지와 돌아오기까지 총 두 번의 시간이 걸렸으므로 2를 나누어줍니다.
     그럼 시간 * 17 / 1000이라는 공식이 나옵니다.
  */

  if (0 <=distance &&distance <= 10) { //10cm이내에 있을 때만 측정 하기
    Serial.print("------ distance : ");
    Serial.print(distance);
    Serial.println(" cm ------");
      /* 알코올 센서 측정 시작 */
    sensor_volt = (float)sensorValue/1024*5.0;
    RS_gas = ((5.0 * R2)/sensor_volt) + R2;
    R0 = 1800;
    ratio = RS_gas/R0; //ratio = RS/R0
    double x = 0.4*ratio;
    BAC = pow(x,-1.431); //BAC in mg/L
  
    if(btSerial.available()) { //블루투스와 연결 되어있으면 시리얼 모니터에 BAC, AOut값 출력
     Serial.write(btSerial.read());
    }
    if (Serial.available()) { //시리얼 연결이 되어있으면 블루투스로 BAC 값 전송
      btSerial.write(Serial.read());
    }
    
    DigitShield.setValue(sensorValue); //BAC값 디지털 실드에 출력
    btSerial.println(BAC*0.1); //블루투스로 BAC 값 전송
    Serial.print("BAC = ");
    Serial.print(BAC*0.1);
    Serial.print("mg/100mL   -------  SensorValue = ");
    Serial.println(sensorValue);
    
    //Serial.print(BAC*0.0001); //convert to g/dL
    //Serial.print(" g/DL\n\n");
  }
  else {
    Serial.println("@@@10cm보다 멀리 있습니다.@@@");
    //Serial.println(-999);
    btSerial.println(-999);
  }
//   else if (distance >= 200 || distance <= 0)       // 거리가 200cm가 넘거나 0보다 작으면
//  {
//    Serial.println("거리를 측정할 수 없음");   // 에러를 출력합니다.
//  }
  
  delay(1000); //측정 딜레이 (테스트 용으로 넣어둔 것)
}
```



### Get R0 for Breath Testing

```c
/* MQ-3 Alcohol Sensor Circuit with Arduino */
#include <DigitShield.h>
#include <Wire.h>
#include <SoftwareSerial.h>

/*블루투스 관련 변수*/
int Tx = 6; //전송
int Rx = 7; //수신
float sensor_volt;
float RS_gas;
float R0;
int R2 = 2000;

/*음주측정 관련 변수*/
const int AOUTpin = 0;//the AOUT pin of the alcohol sensor goes into analog pin A0 of the arduino
int value; //측정값 저장할 변수

SoftwareSerial btSerial(Tx, Rx);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
    DigitShield.begin();
}

void loop()
{
  int sensorValue = analogRead(A0);
   DigitShield.setValue(sensorValue);
  sensor_volt = (float)sensorValue/1024*5.0;
  RS_gas = ((5.0 * R2)/sensor_volt) + R2;
  R0 = RS_gas/60;
  Serial.print("R0: ");
  Serial.print(R0);
  Serial.print("  ---  sensorValue: ");
  Serial.println(sensorValue);
  delay(1000);
}
```



### Breath Testing Using Arduino and Android Studio(RS/R0) (unit-BAC)

```c
/* MQ-3 Alcohol Sensor Circuit with Arduino */
#include <DigitShield.h>
#include <Wire.h>
#include <SoftwareSerial.h>

/*블루투스 관련 변수*/
int Tx = 6; //전송
int Rx = 7; //수신
SoftwareSerial btSerial(Tx, Rx);

/*음주측정 관련 변수*/
const int AOUTpin = 0;
float sensor_volt;
float RS_gas;
float R0;
int R2 = 2000;
float ratio;
float BAC;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
  DigitShield.begin();
}

void loop()
{
  int sensorValue = analogRead(AOUTpin);

  sensor_volt = (float)sensorValue/1024*5.0;
  RS_gas = ((5.0 * R2)/sensor_volt) + R2;
  R0 = 1800;
  ratio = RS_gas/R0; //ratio = RS/R0
  double x = 0.4*ratio;
  BAC = pow(x,-1.431); //BAC in mg/L

  if(btSerial.available()) { //블루투스와 연결 되어있으면 시리얼 모니터에 BAC, AOut값 출력
   Serial.write(btSerial.read());
  }
  if (Serial.available()) { //시리얼 연결이 되어있으면 블루투스로 BAC 값 전송
    btSerial.write(Serial.read());
  }
  DigitShield.setValue(sensorValue); //BAC값 디지털 실드에 출력
  btSerial.println(BAC*0.1); //블루투스로 BAC 값 전송
  Serial.print("BAC = ");
  Serial.print(BAC*0.1);
  Serial.print("mg/100mL   -------  SensorValue = ");
  Serial.println(sensorValue);
  
  //Serial.print(BAC*0.0001); //convert to g/dL
  //Serial.print(" g/DL\n\n");
  delay(1000); //측정 딜레이 (테스트 용으로 넣어둔 것)
}
```



### Simple Example) Bluetooth - Serial Monitor 

```c
#include <SoftwareSerial.h>

int Tx = 6; //전송
int Rx = 7; //수신

SoftwareSerial btSerial(Tx, Rx);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (btSerial.available()) {
    Serial.write(btSerial.read());
  }
  if (Serial.available()) {
    btSerial.write(Serial.read());
  }
}
```

##### Arduino - Bluetooth AT Commands

연결 확인 : **AT**

기기이름 변경 : **AT+NAME**이름

비밀번호 변경 : **AT+PIN**0000

### Test Example) Breath Testing Using Arduino and Android Studio (unit - SensorValue)

```c
/* MQ-3 Alcohol Sensor Circuit with Arduino */
#include <DigitShield.h>
#include <Wire.h>
#include <SoftwareSerial.h>

/*블루투스 관련 변수*/
int Tx = 6; //전송
int Rx = 7; //수신

/*음주측정 관련 변수*/
const int AOUTpin = 3;//the AOUT pin of the alcohol sensor goes into analog pin A0 of the arduino
int value; //측정값 저장할 변수

SoftwareSerial btSerial(Tx, Rx);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  btSerial.begin(9600);
  DigitShield.begin(); //7-세그먼트 초기화
}

void loop()
{
  value= analogRead(AOUTpin);//reads the analaog value from the alcohol sensor's AOUT pin 측정값 저장

  if(btSerial.available()) {
    Serial.write(btSerial.read());
  }
  if (Serial.available()) {
    btSerial.write(Serial.read());
  }

//  if (btSerial.available()) {
    Serial.println(value);
    btSerial.println(value);
    //btSerial.write(value);
    DigitShield.setValue(value); //7-세그먼트에 알코올 측정값 출력
//  }
  
  delay(1000); //0.1초 딜레이

}
```



## 액티비티 

| 액티비티 이름          | 기능이름                      | 기타 |
| ---------------------- | ----------------------------- | ---- |
| DeviceMapActivity      | 기기 지도, 마커, 기기상세화면 |      |
| BreathTestInfoActivity | 측정(방법) 안내               |      |
| BreathTestingActivity  | 측정 중 화면                  |      |
| DetoxAnalysisActivity  | 해독 시간 분석 화면           |      |
| QRCodeScanActivity     | QR코드 촬영                   |      |
| CertCompletionActivity | 인증 완료                     |      |



