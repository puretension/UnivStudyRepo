import java.io.*;

class Calc {
    int token; int value; int ch;
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
                else
                if (Character.isDigit(ch)) {
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

//    void command( ) {
//        /* command -> expr '\n' */
//        int result = aexp();		// TODO: [Remove this line!!]
//        // Object result = expr();  // TODO: [Use this line for solution]
//        if (token == '\n') /* end the parse and print the result */
//            System.out.println(result);
//        else error();
//    }

    void command( ) {
        Object result = expr();
        if (token == '\n')
            System.out.println(result);
        else error();
    }


//    Object expr() {
//        /* <expr> -> <bexp> {& <bexp> | '|'<bexp>} | !<expr> | true | false */
//        Object result;
//        result = ""; // TODO: [Remove this line!!]
//        if (token == '!'){
//            // !<expr>
//            match('!');
//            result = !(boolean) expr();
//        }
//        else if (token == 't'){
//            // true
//            match('t');
//            result = (boolean)true;
//        }
//        else if (token == 'f'){
//            // false
//            // TODO: [Fill in your code here]
//        }
//        else {
//            /* <bexp> {& <bexp> | '|'<bexp>} */
//            result = bexp();
//            while (token == '&' || token == '|') {
//                if (token == '&'){
//                    // TODO: [Fill in your code here]
//                }
//                else if (token == '|'){
//                    // TODO: [Fill in your code here]
//                }
//            }
//        }
//        return result;
//    }

    Object expr() {
        Object result;
        if (token == '!'){
            match('!');
            result = !(boolean) expr();
        }
        else if (token == 't'){
            match('t');
            result = true;
        }
        else if (token == 'f'){
            match('f');
            result = false;
        }
        else {
            result = bexp();
//            while (token == '&' || token == '|') {
//                Object bexpResult = bexp();
//                if (token == '&'){
//                    result = (boolean)result && (boolean)bexpResult;
//                }
//                else if (token == '|'){
//                    result = (boolean)result || (boolean)bexpResult;
//                }
//            }
            while (true) {
                if (token == '&'){
                    match('&');
                    Object bexpResult = bexp();
                    result = (boolean)result && (boolean)bexpResult;
                }
                else if (token == '|'){
                    match('|');
                    Object bexpResult = bexp();
                    result = (boolean)result || (boolean)bexpResult;
                }
                else {
                    break;
                }
            }
        }
        return result;
    }

//    Object bexp( ) {
//        /* <bexp> -> <aexp> [<relop> <aexp>] */
//        Object result;
//        result = ""; // TODO: [Remove this line!!]
//        int aexp1 = aexp();
//        if (token == '<' || token == '>' || token == '=' || token == '!'){ // <relop>
//            /* Check each string using relop(): "<", "<=", ">", ">=", "==", "!=" */
//            // TODO: [Fill in your code here]
//        }
//        else {
//            result = aexp1;
//        }
//        return result;
//    }

    Object bexp( ) {
        int aexp1 = aexp();
        if (token == '<' || token == '>' || token == '=' || token == '!'){
            String operator = relop();
            int aexp2 = aexp();
            switch (operator) {
                case "==": return aexp1 == aexp2;
                case "!=": return aexp1 != aexp2;
                case "<": return aexp1 < aexp2;
                case ">": return aexp1 > aexp2;
                case "<=": return aexp1 <= aexp2;
                case ">=": return aexp1 >= aexp2;
                default: error();
            }
        }
        return aexp1;
    }

//    String relop() {
//        /* <relop> -> ( < | <= | > | >= | == | != ) */
//        String result = "";
//        // TODO: [Fill in your code here]
//        return result;
//    }


    String relop() {
        StringBuilder result = new StringBuilder();
        result.append((char) token);
        match(token);
        if (token == '=' || token == '>') {
            result.append((char) token);
            match(token);
        }
        return result.toString();
    }

    // TODO: [Modify code of aexp() for <aexp> -> <term> { + <term> | - <term> }]
//    int aexp() {
//        /* expr -> term { '+' term } */
//        int result = term();
//        while (token == '+') {
//            match('+');
//            result += term();
//        }
//        return result;
//    }

    int aexp() {
        int result = term();
        while (token == '+' || token == '-') {
            int operation = token;
            match(token);
            if (operation == '+')
                result += term();
            else
                result -= term();
        }
        return result;
    }


    // TODO: [Modify code of term() for <term> -> <factor> { * <factor> | / <factor>}]
//    int term( ) {
//        /* term -> factor { '*' factor } */
//        int result = factor();
//        while (token == '*') {
//            match('*');
//            result *= factor();
//        }
//        return result;
//    }

    int term( ) {
        int result = factor();
        while (token == '*' || token == '/') {
            int operation = token;
            match(token);
            if (operation == '*')
                result *= factor();
            else {
                int divisor = factor();
                if (divisor != 0)
                    result /= divisor;
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