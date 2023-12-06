# I/O Supervision

- I/O Channels → 데이터 전송, I/O장치 제어
- 채널명령, 채널 프로그램
- SIO → I/O 연산 수행 명령어, 2가지 필요한건?
- I/O연산이 수행되는 동안 CPU는 작업처리가 가능하다 이유는?
- I/O 연산 결과 기다릴때, I/O요청처리할때 각각의 명령어는?
- SVC 0(WAIT) SVC 1(SIGNAL) SVC 2(IO Request)

전형적인 소형 컴퓨터에서 **입력과 출력**은 일반적으로 한번에 `1 바이트`씩 수행

advanced 컴퓨터는 **data 전송과 I/O 장치의 제어**를 다루기 위해 특별한 하드웨어를 갖는다

이 기능은 입출력 채널들`(I/O channels)` 로 알려진 단순한 프로세서들에 의해 수행된다.

**I/O Channel**에 의해 수행될 일련의 연산들은

**channel command로** 구성된 **channel program**에 의해 명시됨

**I/O 연산**을 **수행**하기 위해서 **CPU는 채널 번호와 채널 프로그램의 시작 주소를 명시**하는

**입출력 시작start명령IO(**`SIO`**)을 실행**시킨다

**I/O Channels는** `CPU`와 독립적으로 각각의 I/O 연산을 수행한다

**After channel program**, **channel은 I/O Interrupt 를 발생**시킨다

다수의 채널들이 동시에 각각의 채널 프로그램들을 실행시키기에 여러 개의 I/O 연산 동시 수행

**각각의 채널들은** `CPU와 독립적으로 동작`**하기 때문에 I/O연산이 수행되는동안 CPU는  작업 처리를 계속할 수 있다**

(1. 시스템은 사용자 프로그램의 I/O 요구를 받아들여야 하며

1. 요구된 명령이 종료되었을 때 사용자 프로그램에게 통지해야 한다
2. 또한 시스템은 입출력 채널의 연산을 제어해야 하며
3. 채널에 의해 생성되는 입출력 인터럽트를 처리해야 한다)

<img width="418" alt="Untitled-6" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/ea50f889-687c-4ec3-84ef-0e9a7587b7c5">

### **Processing I/O Request**

- 프로그램이 **I/O 연산의 결과를 기다려야만 할 때 SVC 0(WAIT)** 명령을 수행(**IO 요청은 2 → SVC 2**)
    - **용도**: 현재 실행 중인 **채널번호**와 **채널 프로그램의 시작 주소**와 **ESB주소** 포함
    - **채널 프로그램**: 입출력(I/O) 작업을 관리 프로그램
- `Channel Work Area`에는 ****현재 실행 중인 **채널 프로그램의 시작 주소**와 (현재 작업에 해당하는) **ESB 주소**를 포함하고 있음
- I/O 작업이 완료되면, 그 결과는 **채널 작업 영역에 저장된 status flag**로 나타나고, flag가 비정상적인 상태를 나타내면, 운영 체제는 적절한 오류 복구 작업을 시작
- **인터럽트 처리 후 제어권 반환**
- 인터럽트가 발생했을 때 **CPU가 대기 상태**였다면, `디스패처`**를 호출**

### Quiz

- Channel Work Area에는 현재 실행중인 2가지 주소를 포함한다. 빈칸은 무엇인가?
- I/O작업이 완료되면 그 결과는 OO으로 나타나고 이게 비정상이라면 운영체제는 오류복구 작업 시작. 빈칸은 무엇인가?

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012 35 46](https://github.com/puretension/UnivStudyRepo/assets/106448279/6995cd5d-c632-4fb0-b892-1295103b602c)

### Processing I/O Request(SVC2), Processing **I/O Interrupt**

`TIO` 사용하여 채널상태 검사한 후에, 채널이 사용중이 아니라면

채널 작업영역에 요구된 입출력을 저장하고 채널을 구동시킨다.

status flag검사하여 정상종료 되었다면,인터럽트 프로세서는 I/O 요구에 명시된 ESB에 표시하여 I/O종료 알림

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012 36 07](https://github.com/puretension/UnivStudyRepo/assets/106448279/c5ef2e3a-c841-462a-929e-62bb9391deb7)

### **I/O Supervision and Process Scheduling Functions(아래 그림보고 해석?)**

(자주 보자. 익숙해지는게 답이다.)

### 아래 그림을 주고 dispatch를 진행하였을때

### wait가 발생 시점,SVC가 호출 시점,block으로변환 시점,디스패치 호출시점

1. 프로세스 P1 `SVC` 로 1번째 I/O 요청(인터럽트 발생→제어를 SVC IH에 전이)
2. (I/O요청은 idle상태인 채널1 명시), SVC IH는 `CP`실행후 제어를 P1에게 반환
3. P1이 또 다른 SVC interrupt (`SVC 0`)를 실행시켜 (a)를 위한 `WAIT`를 요구(나 작업중이야.)
4. 제어는 다시 SVC interrupt processor로 전이(WAIT에 명시된 ESB는 해당 event가 아직 발생안했음을 표시), P1은 `block` 상태, 디스패처 호출
5. P2에게 제어 전이
6. 프로세스 P2는 채널2의 장치2에 대한 I/O 요청
7. 채널 2가 idle상태이므로 입출력 연산이 시작되고 제어는 P2로 반환(위 1,2과정과 동일)
8. P2는 I/O 요청 완료를 기다림, 해당되는 사건이 발생하지 않았기 때문에 P2는 blocked 상태
9. 디스패처 호출
10. 2 Process 모두 block → Dispatcher make CPU Idle.(why? 2 Channel already used),채널 1이 자신의 입출력 명령을 완료해도 (11)번이 될 때까지 CPU는 idle상태로 남아있게 됨

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012 36 23](https://github.com/puretension/UnivStudyRepo/assets/106448279/f113d1e3-ba37-4537-9f97-e02055c2fbb0)

1. 채널은 I/O interrupt 를 발생시킴. 이 interrupt로 idle 상태였던 CPU는 I/O IPR 수행. 연산의 수행이 정상적으로 종료되었는가를 판단한 후 I/O interrupt process 는 해당되는 ESB에 대한 `SIGNAL` 요구(`SVC 1`)를 제기. **SIGNAL 요구**는 제어를 **SVC interrupt process 로 전이**시킴
2. 이 ESB를 기다리던 P1은 대기 상태. SVC IH는 그 후 I/O interrupt process 에게 제어를 반환
3. 디스패처 호출
4. 제어가 P1에게 전이
5. 채널 2가 자신의 명령을 완료했을 때에도 유사한 상황이 발생하게한다. P2가 WAIT 상태.

But interrupt 발생시 CPU가 idle 상태가 아니므로 제어가 P2로 전이되지 않는다. I/O interrupt process 는interrupt된 P1에게 제어를 복귀시킨다. P2는 (22)번에서 P1이 WAIT 요구를 할 때까지 제어를 갖지 못한다.

### 디스패처는 언제 호출되는가?

**I/O Interrupt Process중 인터럽트 발생시 CPU가 Idle 상태일때**

**preemptive process scheduling이 사용될때**