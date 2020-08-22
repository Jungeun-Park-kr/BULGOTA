# BULGOTA - Team MINCHO

​																																										작성자 박정은

우리 프로젝트에 사용 코드 : 첫 번째 코드로 R0값을 구하고 두 번째 코드로 BAC값을 구함

## Arduino Codes

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



