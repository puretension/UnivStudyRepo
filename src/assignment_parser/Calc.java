import java.io.*;

class Calc {
    int token;
    int value;
    int ch;
    private PushbackInputStream input;
    final int NUMBER=256;

    Calc(PushbackInputStream is) {
        input = is;
    }

    int getToken( )  { /* tokens are characters */
        while(true) {
            try  {
                ch = input.read();
                if (ch == ' ' || ch == '\t' || ch == '\r') ;
                else if (Character.isDigit(ch)) {
                    value = number( );
                    input.unread(ch);
                    return NUMBER;
                }
                else return ch;
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private int number( )  {
        /* number -> digit { digit } */
        int result = ch - '0';
        try  {
            ch = input.read();
            while (Character.isDigit(ch)) {
                result = 10 * result + ch -'0';
                ch = input.read();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return result;
    }

    void error( ) {
        System.out.printf("parse error : %d\n", ch);
        //System.exit(1);
    }

    void match(int c) {
        if (token == c)
            token = getToken();
        else error();
    }


    void command( ) {
        //  int result = aexp(); //이렇게 하면 int(숫자)만 처리가능하다는 한계점
        Object result = expr(); //expr()는 boolean(&,|,!)연산자와, 비교 연산자(<,>,==) 및 숫자 연산자(+,-) 모두 처리가능 -> Object타입 선언
        /* command -> expr '\n' */
        if (token == '\n') /* end the parse and print the result */
            System.out.println("The result is " + result);
        else error();
    }

    Object expr() {
        /* <expr> -> <bexp> {& <bexp> | '|'<bexp>} | !<expr> | true | false */
        Object result;
        if (token == '!'){
            match('!');
            result = !(boolean) expr();
        }
        else if (token == 't'){
            match('t');
            result = (boolean)true;
        }
        else if (token == 'f'){
            match('f');
            result = (boolean)false; // 위의 token == 't'의 처리와 똑같다. 'f'이므로 boolean false로 해준다
        }
        else {
            result = bexp();
            /* <bexp> {& <bexp> | '|'<bexp>} */
            while (token == '&' || token == '|') {
                if (token == '&'){
                    match('&');
                    Object bexpResult = bexp();
                    // bexp 함수를 호출하여 관계 표현식의 결과를 가져오고, result값과 AND연산자로 결합하여 boolean값 판단한다
                    result = (boolean)result && (boolean)bexpResult;
                }
                else{
                    match('|');
                    Object bexpResult = bexp();
                    // bexp 함수를 호출하여 관계 표현식의 결과를 가져오고, result값과 OR연산자로 결합하여 boolean값 판단한다
                    result = (boolean)result || (boolean)bexpResult;
                }
            }
        }
        return result;
    }

    Object bexp( ) {
        /* <bexp> -> <aexp> [<relop> <aexp>] */
        Object result;
        int aexp1 = aexp();
        result = aexp1; // aexp1으로 일단 result를 초기화 시킨다!
        if (token == '<' || token == '>' || token == '=' || token == '!'){ // <relop>
            String operator = relop(); //관계연산자 꺼내오고
            int aexp2 = aexp(); //산술연산자2

            switch (operator) {
                //각 토큰에 해당값들 result에 넣어준다
                case "==": result = aexp1 == aexp2; break;
                case "!=": result = aexp1 != aexp2; break;
                case "<":  result = aexp1 < aexp2;  break;
                case ">":  result = aexp1 > aexp2;  break;
                case "<=": result = aexp1 <= aexp2; break;
                case ">=": result = aexp1 >= aexp2; break;
            }
        }
        else {
            //위 조건문의 해당값 X시 역시나 aexp1으로 result를 초기화
            result = aexp1;
        }
        return result;
    }

    // TODO: [Fill in your code here]
    String relop() {
        //관계 연산자를 파싱하는 데 필요한 로직들
        /* <relop> -> ( < | <= | > | >= | == | != ) */
        String result = "";
        // <, <=
        if (token == '<') {
            match('<');
            if (token == '=') {
                match('=');
                result = "<=";
            } else {
                result = "<";
            }
        }
        // >, >=
        else if (token == '>') {
            match('>');
            if (token == '=') {
                match('=');
                result = ">=";
            } else {
                result = ">";
            }
        }
        // ==
        else if (token == '=') {
            match('=');
            if (token == '=') {
                match('=');
                result = "==";
            }
        }
        // !=
        else if (token == '!') {
            match('!');
            if (token == '=') {
                match('=');
                result = "!=";
            }
        }
        return result;
    }


    // TODO: [Modify code of aexp() for <aexp> -> <term> { + <term> | - <term> }]
    int aexp() {
        /* expr -> term { '+' term } */
        int result = term();
        // + 연산에 이어서 - (뺄셈)연산도 가능하게 추가
        while (token == '+' || token == '-') {
            int operation = token;
            match(token); // 연산자 소비시킴
            if (operation == '+')
                result += term(); //덧셈
            else
                result -= term(); //뺄셈
        }
        return result;
    }


    // TODO: [Modify code of term() for <term> -> <factor> { * <factor> | / <factor>}]
    int term( ) {
        int result = factor();
        //곱셈에 이어서 나눗셈도 추가
        while (token == '*' || token == '/') {
            int operation = token;
            match(token); //operator는 소모시킴
            if (operation == '*')
                result *= factor(); //곱셈 이라면 기존값에 factor(요소) 곱한다 => <factor> * <factor>
            else {
                int divisor = factor();
                if (divisor != 0)
                    result /= divisor; //나눗셈 이라면 기존값에 factor(요소) 나눈다 => <factor> / <factor>
                else {
                    System.out.println("Error: Division by zero.");
                    System.exit(1);
                }
            }
        }
        return result;
    }

    int factor() {
        /* factor -> '(' expr ')' | number */
        int result = 0;
        if (token == '(') {
            match('(');
            result = aexp();
            match(')');
        }
        else if (token == NUMBER) {
            result = value;
            match(NUMBER); //token = getToken();
        }
        return result;
    }

    void parse( ) {
        token = getToken(); // get the first token
        command();          // call the parsing command
    }

    public static void main(String args[]) {
        Calc calc = new Calc(new PushbackInputStream(System.in));
        while(true) {
            System.out.print(">> ");
            calc.parse();
        }
    }
}



