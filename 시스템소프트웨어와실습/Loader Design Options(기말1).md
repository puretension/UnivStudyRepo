## **Loader Design Options - Linkage Editiors, Dynamic Linking, Bootstrap Loaders**

**Linkage Editiors**

짧으니 QA방식으로 정리하고 PASS

**Linking Loader VS Linkage Editor 차이점?**

`Linking Loader`는 `모든 링킹`, `재배치 연산 수행`, `링크된 프로그램을 실행`하기 위해 `메모리에 로드`

`Linkage Editor`는 `나중에 실행`하기 위해 `링크된 프로그램을 생성`한다

### **각각 언제 사용하면 유리한가?(특징 2개씩 기억)**

**Linking Loade**r: 프로그램이 **매실행마다 reassemble**되야할때(1), 프로그램이 **너무 가끔쓰여** 어셈블, **링크된 버전**을 **저장해 둘 필요가 없을때**(2)

**Linking Editor**: 프로그램이 **reassemble되지 않고,** **자주 실행**될때(1), **다른 프로그램 실행중, 링크된 프로그램을 필요할때만 불러서 연결**가능(2) -> **동적 링킹 가능성** 제공


![Library.png](..%2F..%2F..%2FLibrary%2FContainers%2Fcom.apple.Notes%2FData%2Ftmp%2FTemporaryItems%2FNSIRD_%EB%A9%94%EB%AA%A8_Hfo7Vq%2FHardLinkURLTemp%2F05804689-99E1-48D1-A68E-C130943F0AB1%2F1701441557%2FLibrary.png)


### 주소 바인딩 방식(Address Binding)?

Symbolic Address는 Label
è Machine Address는 실제 기계에 올라가는 주소

**어떤 Time에 Binding 하느냐에따라** `Complexity`, `Flexibility` **달라짐**

3가지 Time이 존재함(Assemble, Load, Run)

**Assemble Time, Load Time, Run time => Dynamic Linking Library**

3개의 Linking Time은 각각 언제인가?

**Linkage Editiors: Before Load Time**

**Linking Loaders: at Load Time**

**Dynamic Linking: After Load Time**

- **실행될때까지 Linking 미룸**
- **서브루틴 첫 호출때 로드, 링크됨**
- **dynamic loading, load and call**

### **Dynamic Linking Application**

(동적 링킹을 통해) 여러 실행 중인 프로그램이 **한 복사본의 서브루틴이나 라이브러리를 공유한다**

(동적 링킹은) OOP에서 **공유 객체와 그 메소드들이 실행 시간에 결정될 수 있도록** 한다

`루틴이 필요할 때만(if needed)` 로드하는 기능을 제공한다 => **로드 시간 및 메모리 공간 절약**

## 예상문제

### **Linking Loader VS Linkage Editor, 링킹연산-로드시간 기준으로 차이점을 설명하라**

**Linking Loader는 모든링킹, 재배치연산수행, 링크된프로그램을 메모리에 로드하며 LoadTime에 작동한다**

**Linkage Editor는 링크가 진행된 상태의 프로그램을 생성하며 LoadTime 전에 작동한다**

### **동적 링킹 장점은 무엇인가**

**여러 실행중인 프로그램들이, 하나의 서브루틴/라이브러리 복사본 공유해서 사용가능**

**서브 루틴이 필요할때만 로드가능 -> LoadTime,메모리공간 절약**

### **동적링킹은 어떻게 구현하나?**

외부심볼을 참조하는 JSUB대신에,

**OS에게 load-and-call** 서비스를 **요청**한다(parameter는 호출된 루틴의 **symbolic name**)

### Bootstrap Loader

일부 컴퓨터에서는 절대 로더 프로그램이 읽기 전용 메모리(ROM)에 영구적으로 존재한다.

일부 컴퓨터에서는 내장 하드웨어가 특정 장치에서 고정 길이의 레코드를 메모리의 고정된 위치로 읽어들인다.

읽기 작업 후에는 자동으로 메모리의 해당 주소로 제어가 전달된다.

이런 로더를 뭐라 부르나? **Bootstrap Loader**