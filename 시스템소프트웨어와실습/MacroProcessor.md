# MacroProcessor

개념 서술보다 코드해석과 매크로 적용이 가능한지가 더 중요한 영역. 이해>암기

책으로 265까지

### Macro(Macro Instruction)

- 자주 사용 instruction 그룹
- 정의
    - macro-name `MACRO` arguments
    - `macro-body`
    - `MEND`
- 호출 → macro-name actual-parameters(macro-body)
- `확장` → by **Macro Processor**

### Quiz

**RDBUFF MACRO &INDEV, &BUFADR, &RECLTH 에서 각각은 무엇에 해당하는가?**

RDBUFF는 매크로 이름, MACRO는 매크로의 어셈블러 지시자, `&INDEV,&BUFADR,&RECLTH` 는 파라미터

### Macro Processor

- 메크로 호출문장을 정의된 instruction 그룹으로 대체 ⇒ 메크로 확장
- CPP 소스프로그램 → PrePreossor → C 소스프로그램 → Compiler → OBJ
- ex) CPP Preprocessor
    - 정의 #define max(A,B) ((A), (B) ? (A):(B))
    - 호출 x = max(p + q,r + s);
    - 확장 x = (( p + q) > (r + s) ? (p + q) : (r + s))
- 메크로 어셈블리 프로그램 → MacroProssor →어셈블리 프로그램 → 어셈블러 → OBJ

### 기존 SIC/XE

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010 20 06](https://github.com/puretension/UnivStudyRepo/assets/106448279/5da9f8eb-9901-4088-b9cb-25430d575a71)
### Macro 사용한 SIC/XE

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010 20 24](https://github.com/puretension/UnivStudyRepo/assets/106448279/d36574af-ff1b-48b7-9331-bcca2b00d3c0)
### Macro 확장한 후의 SIC/XE

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010 21 11](https://github.com/puretension/UnivStudyRepo/assets/106448279/1b170979-9be7-49bc-8e31-e17953156258)
### Quiz

Q. **Macro 명령어가 무엇인가?** RDBUFF F1, BUFFER, LENGTH

Q. **RDBUFF F1, BUFFER, LENGTH로** `매크로 확장`**을 수행하시오**

$INDEV → F1, &BUFADR → BUFFER 이런식으로 바꿔서 만들어주자

Q. JEQ *+11과 JLT *-19가 의미하는 바를 서술하고 기존의 어떤 문장과 상응하는가

`JEQ *+11` 는 Status Word가 EQ라면 11바이트 뒤로 점프, JEQ RLOOP

`JLT *-19`는 Status Word가 LT라면 19바이트 전으로 점프, JLT RLOOP

# Macro processor algorithm and data structures

### 2 Pass 매크로 프로세서

- Pass 1: 매크로 정의 처리
- Pass 2: 매크로 확장

### Single Pass 매크로 프로세서 → 매크로 정의,확장 처리

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 03 52](https://github.com/puretension/UnivStudyRepo/assets/106448279/ecf00f07-be99-4ae5-aa2b-f494e05ce93a)
### 사용되는 자료구조

- DEFTAB(매크로 정의 문장들)
- NAMTAB(매크로 이름, DEFTAB 가리키는 것)
- ARGTAB(실제 전달되는 인수들)

### **definition of macro within a macro body**

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2010 59 52](https://github.com/puretension/UnivStudyRepo/assets/106448279/a17e4fae-ec4b-40f6-9f03-78a6c73cc220)
### **매크로 프로세서 테이블 내용(테이블 그려보시오?)**


![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 01 29](https://github.com/puretension/UnivStudyRepo/assets/106448279/06146ed7-6944-498c-b4bf-13bcb97498ee)
## Machine Independent Macro Processor Features

## **Concatenation of Macro Parameters**

아래처럼 하면 “ID1”과 “ID” “1”구분 어렵

```
SUM Macro &ID
LDA X&ID1
LDA X&ID2
LDA X&ID3
LDA X&IDS
```

**In SIC or SIC/XE**, **special concatenation operator(화살표 →)**

```
SUM Macro &ID
		LDA X&ID->1
		LDA X&ID->2
		LDA X&ID->3
		LDA X&ID->S
		MEND
```

### **⭐️Generation of Unique Labels⭐️(이미지 2.5,4.1,4.2와 비교해보자.)**

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 08 29](https://github.com/puretension/UnivStudyRepo/assets/106448279/5f7b90af-e15f-4053-a412-fa13ca0137e5)
기존 SIC/XE참고하여

매크로정의에서는 **`$`**를 덧붙여주고

매크로 확장에서 `$AA, $AB,…`를 덧 붙여주는게 포인트

### Quiz

SIC/XE에서 사용하던 기존 라벨과 다른 `Unique Label`을 사용하는 이유는 무엇인가?

(SIC/XE 프로그램 내에서)기존 라벨은 데이터 위치를 식별하는 데 사용되었지만

매크로 라벨은 매크로 정의 내부에서만 사용되며(유효하며), `매크로 확장시` (매크로 내/외부) `라벨 이름 충돌 방지` 가능하다.

Unique Label이 있는 매크로 확장된 프로그램에서 Unique Label을 지우고 확장을 실행하면 어떻게되는가?

라벨 충돌의 가능성이 존재하게되고, (매크로 확장 수행을 위해서는 JEQ *-13과같은 조건부 분기 명령어가 필요해진다.)

### **Conditional Macro Expansion**

매크로 프로세서가 특정 조건에 따라 다르게 확장하는 기능

- `RDBUFF` **매크로 정의**: RDBUFF 매크로는 두 개의 추가 파라미터 **&EOR**와 **&MAXLTH**를 가짐. 이 파라미터들은 매크로 호출 시에 제공.
- **매크로 프로세서 지시어 SET**: SET 지시어는 매크로 시간 변수에 값을 할당하는 데 사용. 예를 들어, **`&EORCK`** 변수에 1을 할당.
- **매크로 시간 변수**: **`&EORCK`**와 같은 변수는 매크로 확장 과정에서 작동 값들을 저장하기 위해 사용
- **RDBUFF 매크로 호출 예시**:
    - **RDBUFF F3,BUF,RECL,04,2048**: 이 호출은 RDBUFF 매크로에 다섯 개의 인자를 전달. 아래에 대입해보면 $INDEV → F3, $BUFADR → BUF
    - **RDBUFF 0E,BUFFER,LENGTH, ,80**: 이 호출에서는 **`&EOR`**에 대한 인자가 생략되어 있으며,아래를 보면 **80이 &MAXLTH에 해당**
    - **RDBUFF F1,BUFF,RLENG,04**:  **04는 &EOR에 해당**

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 27 16](https://github.com/puretension/UnivStudyRepo/assets/106448279/d3562c83-b128-4d57-931c-c1dd36dde71d)
![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 27 36](https://github.com/puretension/UnivStudyRepo/assets/106448279/374b1a3b-7eb6-4d65-b1d5-95f9ad431388)
### Quiz

**확장 수행 이후, COMP옆에 값들을 각각 비워놓고, 무슨값이 들어가는지 빈칸 OR 코드전체 재작성하라.**

### Positional Parameters

매크로 명령어를 정의할 때 사용되는 매개변수와,

매크로를 실제로 호출할 때 제공되는 인자들이 그들의 위치에 따라 연관되어 있다

특정한 매크로 명령어 GENER는 10개의 가능한 매개변수를 가지고 있다

### Keyword Macro Parameter

매크로 호출 시 사용되는 각 인자 값이 해당 매개변수의 이름(키워드)과 함께 명시되어야 한다.

즉, 매크로를 호출할 때 각 인자는 그에 해당하는 매개변수의 이름과 함께 제공된다.

예를 들어, 매크로에 `type`이라는 매개변수가 있을 경우, 매크로 호출 시 `type=값` 형태로 인자를 전달한다.

![%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-12-03%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2011 28 17](https://github.com/puretension/UnivStudyRepo/assets/106448279/06b4a58d-720a-4b89-adf4-c8946d6385e1)
### Quiz

코드 주어지고, 매개변수를 키워드 매개변수로 변경하여 코드를 재작성 하시오