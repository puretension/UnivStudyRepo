# Operating System

### 운영체제의 기본 기능들에 대한 설명으로 틀린 것은?

Manage resources(**컴퓨터 자원**을 효율적으로 **관리)**

컴퓨터를 **쉽게** 사용하게 하는게 목적

프로그래머, operator와의 **user interface**를 제공

사용자 프로그램의 **런타임(run-time) 환경** 제공

사용자는 일반적으로 usermode로 운영체제에 기능을 요청한다?(X) → SVC로 요청함(곧 등장)

### User <—> OS <—> H/W

**Provide Services**

- **User Interface: command Interpreter**
- **Manage resources(CPU, Memory, I/O)**
- **Process 처리 환경 제공**
- **Interrupt 처리, Process 관리, I/O 관리, 메모리(Real, Virtual) 관리, 파일 관리, CPU 스케쥴링**

### 운영체제의 기본 기능 도식화

<img width="161" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-01%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%204 58 39" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/5639afd4-494e-4b3f-9663-b3847650aa9a">

**위 그림 해석**

1. 운영체제는 operator, programmer와 상호작용하는 `user interface`(사용자 접속)을 제공한다
2. 사용자는 `Run P` 같은 명령문으로 프로그램을 로드하고 실행하기 위해 시스템 로더를 호출한다.
3. 운영체제는 프로그램들에게 공통작업들의 성능면에서 지원가능한 서비스를 제공한다.

   → ex) 프로그램 `P`가 파일에서 데이터를 순차적으로 읽기를 원한다고 가정해보자.
   운영체제는 `read(f)`와 같은 형태의 명령문으로 기동되는 서비스 루틴을 제공한다.

4. 이런 서비스 루틴들은 프로그램 실행을 위한 `실행시간 환경(runtime environment)` 을 제공한다.
5. 실제기계는 기계를 돌리는 실행환경으로 커널(kernel)

### 8 **Types of Operating Systems**(O는 operating, S는 System)

- `Batch processing S`  → 작업이 **machine-readable의** 일련의 제어문장들로 정의, sequence, 사람의 개입없이 읽고 실행가능
- `single-job S` → one job at one user
- `multiprogramming S` → 여러 작업 동시에 수행
- `multiprocessor S` -> **여러개의 CPU**
- `real-time S` → **외부 신호에 빠르게** 대응, **시간에 민감한 과정을 모니터링하고 제어**하는 컴퓨터에 사용
- `time-sharing S` → 여러 사용자에게 **상호작용 대화식(interactive conversational) 접근** 제공, 운영 체제는 사용자가 입력한 명령을 실행하며, 각 사용자의 명령을 합리적으로 짧은 응답 시간을 제공하려고 함
- `Network OS` → **login to remote machine, copy file from one machine to another**
- `Distributed OS` → **유저가 전체 네트워크를 Single S으로 보도록** 하드웨어와 소프트웨어 자원 관리

### User Interface

- 컴퓨터를 다루어야 하는 다양한 사용자 그룹의 필요를 충족시키도록 설계
- **사**용자 인터페이스 **디자인**의 중요성(많은 사람들이 사용)
- UI 지원을 위한 서비스 루틴

### **Run-Time Environment**

`SVC → Interrupt 발생 → userMode에서 supervisorMode → EMI`

- OS는 사용자에게 **런타임환경 제공**
- **I/O 연산**을 돕는 루틴을 포함
- 서비스 루틴과 확장된 기계(Extended Machine) 정의
- multiprogramming OS, 컴퓨터의 자원을 관리하고 필요에 따라 사용자 작업에 할당하는 루틴들도 포함

사용자들은 주로 특별한 하드웨어 명령어(ex.감독자 호출(Supervisor Call, `SVC`))를 사용하여 운영 체제에 기능을 요청함

**SVC 명령어의 실행**은 `인터럽트를 생성`하며, 이는 운영 서비스 루틴으로의 전환을 유발

인터럽트가 발생하면 CPU는 사용자 모드에서 감독자 모드로 전환(CPU to switch from `user mode to supervisor mode`)

**특권 명령어 사용 제한 ->**  **런타임환경이 제공하는 서비스를 이용하도록 강제**함

**유저들은** `Extended Machine Interface`  **사용해야함(**하드웨어 기능을 직접적으로 사용하는 대신**)**

# Machine-Dependent OS Features

- Interrupt Processing
- Process Scheduling
- I/O Supervision
- Management of Real Memory
- Management of Virtual Memory

## 1. Interrupt Processing

- `Interrupt`  → **정상적인 명령 실행의 순서를 변경하는 Signal**
    - 어떤 상황에 발생하나?
        - **the completion of an I/O operation(입출력 명령 종료)**
        - **the expiration of a preset time** **interval(설정된 시간 간격 종료)**
        - **an attempt to divide by zero(0나누기 시도)**
        - **Interrupt는 프로그램 내부에서 발생**
- 일을 하는 중에 중단하고 다른 일을 처리하고 돌아와야하는 경우에 사용
- 자동으로 제어를 **IPR**로 전달(Interrupt Process Routine = Interrpt Handler)
- Interrupt Processing 완료된 후, **실행이 중단된 지점으로 제어 return가능**

<img width="391" alt="Untitled-4" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/9f857aec-3102-4497-9a65-5e2668f30935">

### 간단 Quiz

- **Interrupt의 발생과 처리는 프로그램 A와 관련이 있다?**(X,)
    - 이유 → 다른 프로그램에 의해 요구된 입출력 명령의 종료에 의해 인터럽트가 발생될 가능성도 있음(위의 3가지 발생가능성 참고)
- 인터럽트는 프로그램 내에서 이루어진다? X IPR에서 이루어짐
- 인터럽트처리가 종료된 후,  제어는 인터럽트된 프로그램 A의 이전실행위치로 반환된다? O

### **SIC/XE Interrupt Type**

- 인터럽트 발생시, CPU에 상태가 저장되고 제어권은 **IPR으로 넘겨짐**
- 각 Interrupt Class에 상응되는 **고정된 Interrupt Work Area가 존재함(ex.SVC Work Area, Program Work Area…)**

**I. SVC(SuperVisor Call)** (인터럽트), (프로그램이 OS에 기능요청할때)

**II. Program(0으로 나누기)** (인터럽트)

**III..Timer** (인터럽트)

**IV. I/O** (인터럽트)

<img width="409" alt="Untitled-5" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/49d73dc4-bb05-486a-8f1e-a30197f1d452">

- **Timer 인터럽트가 발생**할 때, 모든 **register내용들이 작업영역(work area)**에 저장된다. **SW와 PC**에 작업영역의 **처음 2워드에 미리 저장되어있던 값들이 로드**된다(SW는 CPU 현재상태를 저장 및 복원, PC는  인터럽트 처리 루틴으로 제어를 전환하는 역할 수행)
- 인터럽트에 대한 작업 수행후, IPR은 `LPS` 명령어를 실행하여 제어(PC)와 인터럽트 될 때 존재햇던 CPU상태(SW)와 레지스터 내용(work area)을 반환한다.
- **CPU 상태와 레지스터 내용들을 저장하고 복원**하는 것을 `Context Switching`(문맥전환)연산 이라 부른다

IPR은 OOO명령어를 실행하여 제어를 인터럽트된 프로그램에게 반환한다 → **LPS**

### SW 부분 살펴보자!

### SIC/XE Status Word Contents

| 0 | MODE | 0 = userMode, 1 = supervisorMode |
| --- | --- | --- |
| 1 | IDLE | 0 = running, 1 = idle |
| 2~5 | ID | Process Identifier(PID) |
| 6~7 | CC | Condition Code |
| 8~11 | MASK | Interrupt Mask |
| 12~15 |  | Unused |
| 16~23 | ICODE | Interruption Code |

0, 1, 2-5, 6-7, 8-11, 12-15, 16-23

### **Context Switching의 정의 및 사용 이유?**

- 프로세스의 전환이 일어날때, **CPU 상태와 레지스터 내용들을 저장하고 복원**하는 과정을 의미한다.(정의)
- 프로세스의 SW(IP에 중요한 정보, CC값 보존), ID 필드(현재 실행되는 P 식별), IDLE 상태(0,1) 등이 저장되고 복원되어, 각 프로세스가 중단된 지점에서 안전하게 재개할 수 있기에 사용한다.

`SW`는 **인터럽트 처리**에 중요한 정보O, **SW를 자동 저장**함으로써, 인터럽트 발생시 사용되는 CC(조건코드값)도 보존

`IDLE`은 CPU 명령이 running(0) OR idle(1)인지 지정

`ID`**필드는** 4비트 값으로 구성, **현재 실행되는 유저 프로그램 식별한다(through PID).**

### **MASK의 정의 및 사용 이유?**

- `MASK`는 **인터럽트의 허용여부를 제어하는 데 사용**된다
- 필요이유? → **처리 중인 I가 완료될 때까지 특정 I가 발생하지 않도록 할 필요**

MASK 비트가 1? ->  해당 클래스의 I가 허용(반대로 0이라면? 금지)

인터럽트가 금지되면, 그것은 '마스킹되었다고(Masked)’함

IPR 종료, 인터럽트가 가능하게 되었을 때 하나 이상의 인터럽트가 보류되어 있을 수도 있다

→ 일시적으로 지연되는 인터럽트는 '대기 중인**(Pending)** 인터럽트

**인터럽트 클래스 우선순위**가 잇었듯이, **pending 인터럽트**도 최우선 순위가 젤 먼저 인식

SIC/XE 기계에서는 각 인터럽트 클래스에 우선 순위가 할당됨

### **Nested Interrupt Processing**

<img width="466" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-02%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%207 51 39" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/4701cb75-7434-487f-ad7f-a29d3e7919ca">

(제어의 이동을 중심으로 보자)

1. **프로그램 A에서 I/O 인터럽트가 발생할때, CPU의 제어는 프로그램 A에게 있다.(아직 I/O 인터럽트에겐 없음)**
2. 인터럽트에 의해 **A의 상태가 저장**되고(그제서야), **제어**는 **I/O 인터럽트 프로세서에게 전이**된다.
3. I/O 인터럽트 실행중 타이머 인터럽트 발생시**, 제어**는 타이머 인터럽트 프로세서에게 전이된다.
4. Timer 인터럽트 처리가 완료된 후 **LPS 명령**을 통해 제어를 I/O 인터럽트 프로세서에게 반납된다.
5. I/O 인터럽트 처리가 완료된 후 또다른 **LPS 명령**을 통해 CPU상태를 1번째 인터럽트가 발생했을때의 상태로 복구함으로써 제어는 프로그램A에게 반환된다.

괄호부분생략해도됨

(MASK이전값이 리로드되엇으므로 억제된 타이머 인터럽트가 다시 허용

BUT I/O 인터럽트는 계속 억제됨. I/O 인터럽트 처리이후)

I/O 인터럽트 처리끝난후

**또다른 LPS로 CPU상태를 1번째 인터럽트가 발생했을때 상태로 복구함으로써**

**제어는 프로그램A에게 반환**

이제는 프로그램 A가 사용중인 상태 워드의

모든 MASK 비트가 1 로 설정되었기 때문에 모든 인터럽트가 허용

여기까지이해햇다면, Program B-IO Interrupt Handler-Timer Interrupt Handler가 있을때,

### LPS단어를 사용해서 NIP 과정을 간단히 설명해보라

## 2. Process Scheduling

### Process, running-blocked-ready

- 다양한 프로세스들 사이에서 CPU 제어를 전환하는 관리 방식
- **실행 중**인 프로그램을 뭐라 부르는가 → `Process`

사용자 작업이 실행을 시작할 때 프로세스가 생성되고, 작업이 종료될 때 프로세스가 소멸

→ `running/blocked/ready`

프로세스가 실제로 **CPU를 사용하여 명령 실행중 →** 그 프로세스는 **running** 상태

어떤 **이벤트가 발생하기를 기다려야만 실행을 계속할 수 있는** 프로세스는 **blocked** 상태

(ex. 실행을 계속하기 전에 입출력 명령의 완료를 기다려야 하기 때문에 그 프로세스는 블록된다)

**running도 blocked도** 아닌 상태는 **ready** 상태

### Time-Slice, Dispatching

운영체제는 사용자 프로세스에 제어를 전달할 때 **타임 슬라이스(time-slice)**로 시간간격 타이머 설정

운영 체제는 프로세스에게 주어지는 최대한의 CPU 제어 기능 시간을 의미하는

`Time-Slice` → ****프로세스에게 주어지는 `최대한의 CPU 제어 가능 시간`

**이게끝나면** 그 프로세스는 **running -> ready**

**OS는 (**스케줄링 정책에 따라) **ready상태에 있는** 프로세스 중 하나를 선택하여 실행

`Dispatching` → 프로세스를 **선택**하고 **control을 넘기는** 과정**, dispatcher**

`Running ****프로세스`**가** 할당된 시간을 모두 사용하기 이전에

어떤 이벤트를 기다려야 할 필요가 생길 수 있음(바로아래)

→ 실행 중인 프로세스는 **blocked 상태로 전환**되고, 새로운 프로세스가 Dispatch

### **PSB, ESB, Wait, Signal**

### **프로세스가 실행 상태를 떠날 때**마다, 그 **현재 상태는 저장**되어야 한다!

- 프로세스가 다음 번에 **디스패치**될 때, 이전에 저장된 상태가 **복원되어야한다.**
- Q) 각 프로세스의 상태 정보는 OS에 의해 어디에서 저장되어야하나?

**PSB(Process Status Block)**

PSB는 프로세스가 처음 실행될 때 생성되며, 프로세스가 종료될 때 삭제된다.

**ESB(Event Status Block)**

awaited/signaled 이벤트와 연관된 이벤트 상태 블록(ESB)의 주소를 제공함으로써 지정

⭐️**preemptive process scheduling**⭐️

만약 **ready**에 있는 프로세스 중 하나 이상이 **Running 프로세스보다 higher 순위**를 가진다면,

디스패처는 **ready의 가장 높은 우선 순위의 프로세스에게** 제어권을 넘김

<img width="543" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-02%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%208 06 24" src="https://github.com/puretension/UnivStudyRepo/assets/106448279/e86f7983-979b-49d6-bf42-fe8eb7c96b40">

### 위의 **프로세스 상태변화 다이어그램을 해석해보라**

**임의의 순간에 하나의 프로세스만 실행상태에 있을 수 잇다**(기본 원칙)

1. 프로세스는 사용자 작업 **실행(running)이 시작될때 생성**되고 **끝날때 소멸(terminated)**된다
2. 프로세스 존재중에는 Running-Blocked-Ready중 하나의 상태이다
3. 대기중인 프로세스들은 현재 Running 프로세스가 CPU제어를 반환할때 CPU제어를 할당받을 대상이 된다

(운영체제가 사용자 프로세스에 제어를 전달할때 time-slice로 시간간격을 설정한다)

1. **time-slice가 만료되면** 프로세스는 **running상태에서 종료되고 ready상태**가 된다.
2. 운영체제는 스케쥴 방식에 따라 대기상태에 있는 다른 프로세스를 선택한다(Dispatching)
3. Running 프로세스는 time-slice로 할당된 시간을 모두 사용하기 이전에 사건이 만료되기를 기다릴 수도 있다.=> 이런경우 Running 프로세스는 Blocked 상태가 되고 새로운 프로세스가 디스패치 된다.
4. 기다리던 사건이 발생하면, Blocked 프로세스는 Ready로 옮겨져서 다시 디스패치될 후보가 된다.

더 쉽게 적어보자!!

Running 상태는 프로세스가 **CPU를 사용**하여 **명령을 실행중**인 상태

Ready 상태는 프로세스가 CPU를 사용할 준비가 되어 있지만, 아직 **CPU 제어권을 받지 못한 상태**

Blocked 상태는 프로세스가 특정 사건의 발생을 **기다리는 동안** **CPU를 사용할 수 없는 상태**

1. 운영체제는 Ready 상태에 있는 프로세스 중 하나를 선택하여 Running 상태로 전환시킨다(Dispatch)
2. Running상태에서 Time-slice expired되거나, Awaited event occured되면, Running상태에서 나오게 된다.
3. Time-slice가 만료될경우, Ready상태가 되어 CPU할당을 기다리고, 대기해야할 사건이 발생할 경우, Blocked상태가 된다.
4. Blocked 상태에서 기다리던 사건이 해결되면, 프로세스는 다시 Ready 상태가 되어 CPU 할당을 기다리는 Cycle

![IMG_9B35F59D122E-1](https://github.com/puretension/UnivStudyRepo/assets/106448279/6dee23dd-953a-47fa-a1d9-c435968aae13)

1. **이전에 Running 프로세스의 상태저장(**이전의 **Running** 프로세스가 있었다면, 그 프로세스의 **현재 상태 정보를 PSB**에 저장. why? → 이는 프로세스가 다시 실행될 때 이전 상태를 복원하는 데 사용**)**
2. **다음으로 실행할 Ready 상태의 프로세스 선택**(시스템은 **Ready** 상태에 있는 프로세스들 중 하나를 선택하여 **control**을 넘겨줄 준비)
3. **Ready 프로세스가 있는 경우**
    - 선택된 프로세스 상태를 **Running**으로 표시
    - `STI`를 사용하여 **시간 할당량 설정**
    - `LPS`**를 사용하여** 선택된 프로세스에 **제어권 전환**
4. **Ready 프로세스가 없는 경우 →** **CPU는 Ready 상태로 전환된다.**

![IMG_36DF2D8148D9-1](https://github.com/puretension/UnivStudyRepo/assets/106448279/31e20831-4837-432a-842e-b5dd45e3564c)



### **Algorithm For Wait&Signal 해석하기**

### Wait

**Running -> Blocked**

**WAIT** 요구는 **Running 프로세스에 의해 제기**되며

**ESB와 연관된 사건이 발생할 때까지** 그 프로세스를 처리하지 못함을 표시함

실행 중인 process 가 특정한event의 발생을 기다려야 하는 시점에 도달했을 때

process는 WAIT(SVC 0) service를 요구함으로써 OS에 통보

1. ESB의 플래그(ESBFLAG)를 확인하여 이벤트가 이미 발생했는지 여부를 판단
2. 만약 ESBFLAG가 1이면 (이벤트가 이미 발생했다면), 요청한 프로세스에게 **제어권을 LPS**를 사용하여 반환
3. **이벤트 미발생 시**,요청한 프로세스를 **Blocked** 표시, **ESBQUEUE에 추가**

### **SIGNAL**

**Blcoked → ReadyQueue**

ESB에 해당하는 사건이 발생한 것을 발견하는 프로세스에 의해 행해진다.

다른 process들이 기다리고 있는 event의 발생은 SIGNAL(SVC1) 요구에 의해 OS로 전달된다

**SIGNAL(ESB)**

**ESBFLAG := 1,** ESBFLAG를 1로 설정하여 이벤트가 발생했음을 나타낸다.

**ESBQUEUE의 모든 프로세스 처리한다.**

ESBQUEUE에 있는 각 프로세스를 대기 상태로 표시후 ESBQUEUE에서 제거

요청한 프로세스에게 **LPS를 사용하여 제어권을 반환한다.**