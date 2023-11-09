package assignment_s_interpreter.Lab3;// Sint.java
// Interpreter for S

import java.util.Scanner;

public class Sint {
    static Scanner sc = new Scanner(System.in);
    static State state = new State();

    State Eval(Command c, State state) {
        if (c instanceof Decl) {
            Decls decls = new Decls();
            decls.add((Decl) c);
            return allocate(decls, state);
        }
        if (c instanceof Stmt)
            return Eval((Stmt) c, state);

        throw new IllegalArgumentException("no command");
    }

    State Eval(Stmt s, State state) {
        if (s instanceof Empty)
            return Eval((Empty) s, state);
        if (s instanceof Assignment)
            return Eval((Assignment) s, state);
        if (s instanceof If)
            return Eval((If) s, state);
        if (s instanceof While)
            return Eval((While) s, state);
        if (s instanceof Stmts)
            return Eval((Stmts) s, state);
        if (s instanceof Let)
            return Eval((Let) s, state);
        if (s instanceof Read)
            return Eval((Read) s, state);
        if (s instanceof Print)
            return Eval((Print) s, state);
        throw new IllegalArgumentException("no statement");
    }

    State Eval(Empty s, State state) {
        return state;
    }

    State Eval(Assignment a, State state) {
        Value v = V(a.expr, state);
        return state.set(a.id, v);
    }

    State Eval(Read r, State state) {
        if (r.id.type == Type.INT) {
            int i = sc.nextInt();
            state.set(r.id, new Value(i));
        }

        if (r.id.type == Type.BOOL) {
            boolean b = sc.nextBoolean();
            state.set(r.id, new Value(b));
        }

        if (r.id.type == Type.STRING) {
            String s = (String) sc.next();
            state.set(r.id, new Value(s));
        }

        return state;
    }

    State Eval(Print p, State state) {
        System.out.println(V(p.expr, state));
        return state;
    }

    State Eval(Stmts ss, State state) {
        for (Stmt s : ss.stmts) {
            state = Eval(s, state);
        }
        return state;
    }

    State Eval(If c, State state) {
        if (V(c.expr, state).boolValue())
            return Eval(c.stmt1, state);
        else
            return Eval(c.stmt2, state);
    }

    State Eval(While l, State state) {
        if (V(l.expr, state).boolValue())
            return Eval(l, Eval(l.stmt, state));
        else
            return state;
    }

    State Eval(Let l, State state) {
        State s = allocate(l.decls, state);
        s = Eval(l.stmts, s);
        return free(l.decls, s);
    }

    State allocate(Decls ds, State state) {
        // add entries for declared variables (ds) on the state
        /* student exercise */
        //Decls 객체로부터 받은 선언된 변수들을 현재 상태에 추가하는게 목표
        Value val;
        // ds가 null이 아닌 경우(즉, 선언된 변수들이 존재하는 경우)
        if (ds != null) {
            for (Decl decl : ds) {
                // Decls 컬렉션에 있는 각각의 선언(Decl)에 대해서 반복문을 실행
                if (decl.expr == null) {
                    // 만약 선언된 변수에 초기화 표현식이 없다면 (즉, expr가 null인 경우)
                    // TODO: [push a new variable with id and default Value]
                    // 새로운 Value 객체를 생성, 기본 타입으로 초기화하여 상태에 추가
                    val = new Value(decl.id.type);
                } else { // 그게 아니라면?
                    // TODO: [push a new variable with id and value of V(expr)]
                    // 표현식을 평가하는 V 메서드를 호출하여 그 결과값으로 Value 객체를 생성
                    val = V(decl.expr, state);
                }
                // 변수 식별자와 값을 현재 상태(state)에 푸시
                state.push(decl.id, val);
            }
            return state; // 모든 변수가 상태에 추가된 후 변경된 상태!를 반환
        }

        return null;
    }

    State free(Decls ds, State state) {
        // free the entries for declared variables (ds) from the state
        /* student exercise */
        if (ds != null) {
            for (Decl decl : ds) {
                //마찬가지로 반복문수행하면서
                // TODO: [pop a variable located at the top of stack]
                if (!state.isEmpty()) { // 스택이 비어있지 않은지 확인
                    state.pop(); // 스택의 맨 위에 있는 요소를 제거
                } else {
                    throw new IllegalStateException("스택이 비어 있습니다!");
                }
            }
            return state; //상태 반환
        }
        return null;
    }

    Value binaryOperation(Operator op, Value v1, Value v2) { // Lab03
        check(!v1.undef && !v2.undef, "reference to undef value");
        switch (op.val) {
            case "+":
                return new Value(v1.intValue() + v2.intValue());
            case "-":
                return new Value(v1.intValue() - v2.intValue());
            case "*":
                return new Value(v1.intValue() * v2.intValue());
            case "/":
                return new Value(v1.intValue() / v2.intValue());

            // TODO: [Add relational & logical operations]
            /* student exercise */
            // Tip: Use String's compareTo() function for <, <=, >, => operations
            case "<": // 첫 번째 값(v1)이 정수형인지 확인
                if (v1.type() == Type.INT) {
                    // 정수 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.intValue() < v2.intValue());
                } else if (v1.type() == Type.STRING) {
                    // compareTo를 사용한 문자열 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.stringValue().compareTo(v2.stringValue()) < 0);
                }
            case "<=":  // 첫 번째 값(v1)이 정수형인지 확인
                if (v1.type() == Type.INT) {
                    // 정수 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.intValue() <= v2.intValue());
                } else if (v1.type() == Type.STRING) {
                    // compareTo를 사용한 문자열 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.stringValue().compareTo(v2.stringValue()) <= 0);
                }
            case ">":
                if (v1.type() == Type.INT) { // 첫 번째 값(v1)이 정수형인지 확인
                    return new Value(v1.intValue() > v2.intValue()); // 정수 비교를 수행하고 결과를 새로운 Value 객체로 반환
                } else if (v1.type() == Type.STRING) {
                    // compareTo를 사용한 문자열 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.stringValue().compareTo(v2.stringValue()) > 0);
                }
            case ">=":
                if (v1.type() == Type.INT) {  // 첫 번째 값(v1)이 정수형인지 확인
                    return new Value(v1.intValue() >= v2.intValue()); // 정수 비교를 수행하고 결과를 새로운 Value 객체로 반환
                } else if (v1.type() == Type.STRING) {
                    // compareTo를 사용한 문자열 비교를 수행하고 결과를 새로운 Value 객체로 반환
                    return new Value(v1.stringValue().compareTo(v2.stringValue()) >= 0);
                }
            case "==":
                if (v1.type() == Type.INT) {  // 첫 번째 값(v1)이 정수형인지 확인
                    return new Value(v1.intValue() == v2.intValue());  // 정수 값을 비교하고 동일한 경우 true를, 그렇지 않은 경우 false를 반환
                } else if (v1.type() == Type.STRING) {
                    // 문자열 값을 비교하고 동일한 경우 true를, 그렇지 않은 경우 false를 반환
                    return new Value(v1.stringValue().equals(v2.stringValue()));
                } else if (v1.type() == Type.BOOL) {
                    // 불리언 값을 비교하고 동일한 경우 true를, 그렇지 않은 경우 false를 반환
                    return new Value(v1.boolValue() == v2.boolValue());
                }
            case "&":
                // 두 값(v1과 v2) 모두 참일 경우 true를, 그렇지 않으면 false를 반환
                return new Value(v1.boolValue() && v2.boolValue());
            case "|":
                // 두 값(v1과 v2) 중 하나라도 참일 경우 true를, 둘 다 거짓일 경우 false를 반환
                return new Value(v1.boolValue() || v2.boolValue());
            default:
                // 정의되지 않은 연산자에 대해 예외를 발생시킴
                throw new IllegalArgumentException("no operation");
        }
    }

    Value unaryOperation(Operator op, Value v) {
        check(!v.undef, "reference to undef value");
        switch (op.val) {
            case "!":
                return new Value(!v.boolValue());
            case "-":
                return new Value(-v.intValue());
            default:
                throw new IllegalArgumentException("no operation: " + op.val);
        }
    }

    static void check(boolean test, String msg) {
        if (test) return;
        System.err.println(msg);
    }

    Value V(Expr e, State state) {
        if (e instanceof Value)
            return (Value) e;

        if (e instanceof Identifier) {
            Identifier v = (Identifier) e;
            return (Value) (state.get(v));
        }

        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Value v1 = V(b.expr1, state);
            Value v2 = V(b.expr2, state);
            return binaryOperation(b.op, v1, v2);
        }

        if (e instanceof Unary) {
            Unary u = (Unary) e;
            Value v = V(u.expr, state);
            return unaryOperation(u.op, v);
        }
        throw new IllegalArgumentException("no operation");
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            Sint sint = new Sint();
            Lexer.interactive = true;
            System.out.println("Language S Interpreter 2.0");
            System.out.print(">> ");
            Parser parser = new Parser(new Lexer());

            do { // Program = Command*
                if (parser.token == Token.EOF)
                    parser.token = parser.lexer.getToken();

                Command command = null;
                try {
                    command = parser.command();
                    if (command != null) command.display(0);    // display AST
                    else
                        throw new Exception();
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.print(">> ");
                    continue;
                }

                if (command.type != Type.ERROR) {
                    System.out.println("\nInterpreting...");
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
                System.out.print(">> ");
            } while (true);
        } else {
            System.out.println("Begin parsing... " + args[0]);
            Command command = null;
            Parser parser = new Parser(new Lexer(args[0]));
            Sint sint = new Sint();

            do {    // Program = Command*
                if (parser.token == Token.EOF)
                    break;

                try {
                    command = parser.command();
                    if (command != null) command.display(0);    // display AST
                    else
                        throw new Exception();
                } catch (Exception e) {
                    System.out.println(e);
                    continue;
                }

                if (command.type != Type.ERROR) {
                    System.out.println("\nInterpreting..." + args[0]);
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            } while (command != null);
        }
    }
}