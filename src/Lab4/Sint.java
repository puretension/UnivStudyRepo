package Lab4; // Sint.java
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
	        return Eval((Empty)s, state);
        if (s instanceof Assignment)  
	        return Eval((Assignment)s, state);
        if (s instanceof If)  
	        return Eval((If)s, state);
        if (s instanceof While)  
	        return Eval((While)s, state);
        if (s instanceof Stmts)  
	        return Eval((Stmts)s, state);
	    if (s instanceof Let)  
	        return Eval((Let)s, state);
	    if (s instanceof Read)  
	        return Eval((Read)s, state);
	    if (s instanceof Print)  
	        return Eval((Print)s, state);
        throw new IllegalArgumentException("no statement");
    }

    State Eval(Empty s, State state) {
        return state;
    }
  
    State Eval(Assignment a, State state) {
    	// replace array element in array represented by array name
        Value v = V(a.expr, state);
        
        if (a.ar == null)
	        return state.set(a.id, v);
        else {	// id[<expr>] = <expr>;
            // TODO: [Fill the code for assignment of an array element]
        	// Assign to the array element id[<expr>] of the array represented by the array name.
        	// Replace array element in array represented by array name (not use state.set(id, value!)
        	// ex) Value[] ar = ....;	// Array can be represented the array of Value
        	//     ar[index] = v;		// Replace array element

            Value indexValue = V(a.ar.expr, state); // 업데이트할 배열 요소를 찾기 위해 인덱스의 표현식을 평가
            // index => 정수?(안전추출)
            if (!(indexValue != null && ((Value)indexValue).type() == Type.INT)) {
                throw new IllegalArgumentException("Array index must be of type integer");
            }
            int index = ((Value)indexValue).intValue(); //인덱스(정수)값 추출

            Value arrayValue = state.get(a.ar.id); // 배열의 식별자(id)를 사용하여 상태에서 배열을 검색(안전 체크)
            if (!(arrayValue != null && ((Value)arrayValue).type() == Type.ARRAY)) {
                // 검색된 값이 실제로 배열인지 확인
                throw new IllegalArgumentException("Identifier does not refer to an array");
            }

            Value[] array = ((Value)arrayValue).arrValue(); // Value 객체에서 내부 배열에 접근(안전 체크)
            if (index < 0 || index >= array.length) {
                // 인덱스 배열 범위 내인지 체크
                throw new IndexOutOfBoundsException("Array index out of bounds");
            }
            array[index] = v; //배열 내 원소값 업데이트

        	return state;
        }
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
        if (V(c.expr, state).boolValue( ))
            return Eval(c.stmt1, state);
        else
            return Eval(c.stmt2, state);
    }
 
    State Eval(While l, State state) {
        if (V(l.expr, state).boolValue( ))
            return Eval(l, Eval(l.stmt, state));
        else 
	        return state;
    }

    State Eval(Let l, State state) {
        State s = allocate(l.decls, state);
        s = Eval(l.stmts,s);
	    return free(l.decls, s);
    }

    State allocate (Decls ds, State state) {
    	// add entries for declared variables (ds) on the state
        if (ds != null) {
        	for (Decl decl : ds) 
			    if (decl.arraysize > 0) { // <type> id[n];
	                // TODO: [Fill the code for array allocation]
			    	// Regarding array declaration, 
			    	// create an array (new Value[arraysize])
			    	// and push (id, new Value(array)) to the state.
                    Value[] array = new Value[decl.arraysize]; // 지정된 크기(arraysize)와 기본값을 가진 배열을 생성
                    for (int i = 0; i < decl.arraysize; i++) {
                        array[i] = new Value(decl.type); // Type을 입력으로 받아 Value를 생성하는 생성자를 가정
                    }
                    state.push(decl.id, new Value(array)); // 새 배열을 상태에 추가
	            }
	            else if (decl.expr == null)
	                state.push(decl.id, new Value(decl.type));
		        else
			        state.push(decl.id, V(decl.expr, state));
        }
        return state;
    }

    State free (Decls ds, State state) {
    	// free the entries for declared variables (ds) from the state
        if (ds != null) {
	        for (Decl decl : ds) 
	            state.pop();
        }
        return state;
    }

    Value binaryOperation(Operator op, Value v1, Value v2) {
        check(!v1.undef && !v2.undef,"reference to undef value");
	    switch (op.val) {
	    case "+":
            return new Value(v1.intValue() + v2.intValue());
        case "-": 
            return new Value(v1.intValue() - v2.intValue());
        case "*": 
            return new Value(v1.intValue() * v2.intValue());
        case "/": 
            return new Value(v1.intValue() / v2.intValue());
        case "==": 
            return new Value(v1.intValue() == v2.intValue());
        case "!=": 
            return new Value(v1.intValue() != v2.intValue());
        case "<": 
            return new Value(v1.intValue() < v2.intValue());
        case "<=": 
            return new Value(v1.intValue() <= v2.intValue());
        case ">": 
            return new Value(v1.intValue() > v2.intValue());
        case ">=": 
            return new Value(v1.intValue() >= v2.intValue());
        case "&": 
            return new Value(v1.boolValue() && v2.boolValue());
        case "|": 
            return new Value(v1.boolValue() || v2.boolValue());
	    default:
	        throw new IllegalArgumentException("no operation");
	    }
    } 
    
    Value unaryOperation(Operator op, Value v) {
        check( !v.undef, "reference to undef value");
	    switch (op.val) {
        case "!": 
            return new Value(!v.boolValue( ));
	    case "-": 
            return new Value(-v.intValue( ));
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
            return (Value)(state.get(v));
	    }
        if (e instanceof Array) { // id[<expr>]
	        // TODO: [Fill the code for array expr]
        	// For the array represented by the array name,
        	// return the value of the array element id[<expr>]
        	// ex) Value v = (Value) state.get(ar.id);
        	//     Value[] vs = v.arrValue();
        	//     return (vs[index])

            Array arrayAccess = (Array) e; // 배열 접근을 나타내는 객체를 생성
            Value arrayValue = state.get(arrayAccess.id); // 상태에서 배열 객체 추출
            // 가져온 객체가 실제로 배열인지 확인
            if (!(arrayValue != null && arrayValue.type() == Type.ARRAY)) {
                throw new IllegalArgumentException("Identifier does not refer to an array");
            }
            Value indexValue = V(arrayAccess.expr, state); // 인덱스 표현식을 평가하여 인덱스추출
            //정수인지 확인
            if (!(indexValue != null && indexValue.type() == Type.INT)) {
                throw new IllegalArgumentException("Array index must be of type integer");
            }
            int index = indexValue.intValue(); // 인덱스의 정수 값추출
            Value[] values = arrayValue.arrValue(); // 배열 객체에서 내부 배열가져오고
            // 인덱스가 배열의 범위 내에 있는지 확인
            if (index < 0 || index >= values.length) {
                throw new IndexOutOfBoundsException("Array index out of bounds");
            }
            return values[index]; // 배열의 해당 인덱스 위치에 있는 값을 반환

	    }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Value v1 = V(b.expr1, state);
            Value v2 = V(b.expr2, state);
            return binaryOperation (b.op, v1, v2); 
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
            System.out.println("Language S Interpreter 1.0");
            System.out.print(">> ");
	        Parser parser  = new Parser(new Lexer());

	        do { // Program = Command*
	            if (parser.token == Token.EOF)
		            parser.token = parser.lexer.getToken();
	       
	            Command command=null;
                try {
	                command = parser.command();
	                 //    if (command != null) command.display(0);    // display AST    
					 if (command == null) 
						 throw new Exception();
                } catch (Exception e) {
                    System.out.println("Error: " + e);
		            System.out.print(">> ");
                    continue;
                }

	            if (command.type != Type.ERROR) {
                    System.out.println("\nInterpreting..." );
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                         System.err.println("Error: " + e);  
                    }
                }
		        System.out.print(">> ");
	        } while (true);
	    }
        else {
	        System.out.println("Begin parsing... " + args[0]);
	        Command command = null;
	        Parser parser  = new Parser(new Lexer(args[0]));
	        Sint sint = new Sint();

	        do {	// Program = Command*
	            if (parser.token == Token.EOF)
                    break;
	         
                try {
		            command = parser.command();
//					if (command != null) command.display(0);      // display AST
					if (command == null) 
						 throw new Exception();
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                    continue;
                }

	            if (command.type!=Type.ERROR) {
                    System.out.println("\nInterpreting..." + args[0]);
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                        System.err.println("Error: " + e);  
                    }
                }
	        } while (command != null);
        }        
    }
}