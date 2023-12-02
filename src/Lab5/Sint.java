package Lab5; // Sint.java
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

	    if (c instanceof Function) { 
	        Function f = (Function) c; 
	        state.push(f.id, new Value(f)); 
	        return state;
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
        if (s instanceof Call) 
	        return Eval((Call)s, state);
	    if (s instanceof Return) 
	        return Eval((Return)s, state);
        throw new IllegalArgumentException("no statement");
    }

    // [Function]
    // call without return value
    State Eval(Call c, State state) {
    	// TODO: [Fill the code of call stmt]
	     Value v = state.get(c.fid); //함수의 식별자(ID)를 의미하는 c.fid를 통해 이에 해당하는 값을 찾는다
         Function f = v.funValue(); // Value 객체 v에서 Function 객체를 추출함. 함수 호출이 함수 객체를 요구하기 때문
         state = newFrame(state, c, f); // 새로운 스택 프레임을 생성하고 함수의 매개변수를 스택에 푸시( 함수 호출 시에 필요한 새로운 변수 환경을 설정하기 위함)
         state = Eval(f.stmt, state); //f.stmt는 호출된 함수의 본문이며, Eval()은 이 함수 본문을 현재 상태(state)에서 실행
         state = deleteFrame(state, c, f); //끝낫으니, deleteFrame 메서드를 통해 스택 프레임을 제거
	    return state;
    }
    
    // [Function]
    // value-returning call 
    Value V (Call c, State state) { 
	    Value v = state.get(c.fid);  	 	// find function
        Function f = v.funValue();
        state = newFrame(state, c, f); 		// create the frame on the stack
        state = Eval(f.stmt, state); 	   	// interpret the call
	    v = state.peek().val;				// get the return value
        state = deleteFrame(state, c, f); 	// delete the frame from the stack
        return v;
    }
    
    // [Function]
    State Eval(Return r, State state) {
        Value v = V(r.expr, state);
        return state.set(new Identifier("return"), v); 
    }

    // [Function]
    State newFrame (State state, Call c, Function f) {
        if (c.args.size() == 0) 
            return state;
        
        // TODO: [Fill the code of newFrame]
    	// evaluate arguments
         Value val[] = new Value[c.args.size()]; //  호출된 함수의 인수 개수가 배열 크기인 Value 타입의 배열을 생성(함수 호출에 전달된 인수들을 저장함)
         int i=0; // 배열에 인수를 저장하고 추후에 매개변수와 매핑하는 데 사용될 index 변수 초기화
         for (Expr e : c.args) // for 루프를 사용하여 호출된 함수의 각 인수(Expr e)를 순회하며,
            val[i++] = V(e,state); //각 인수를 평가(V(e, state))하여 val 배열에 순차적으로 저장
        
	    // activate a new stack frame in the stack 
         i=0; //다음 단계에서 매개변수와 인수를 매핑하는 데 사용될 i를 0으로 재초기화
        //{: 함수의 매개변수 리스트(f.params)를 순회하는 for 루프를 시작
         for (Decl d : f.params) { // pass by value
            Identifier v = (Identifier)d.id; //현재 매개변수의 식별자(Identifier)를 추출
            state.push(v, val[i++]); //추출된 매개변수 식별자에 해당하는 인수 값을 스택에 푸시,
             //val[i++]는 현재 매개변수에 대응하는 인수 값을 의미하며, i는 다음 인수/매개변수 쌍으로 이동
         }
        state.push(new Identifier("return"), null); // allocate for return value
        return state;
    }

    // [Function]
    State deleteFrame (State state, Call c, Function f) {
    // free a stack frame from the stack
        state.pop();  // pop the return value	
        
        // TODO: [Fill the code of deleteFrame]
         if (f.params != null) //이 조건문은 함수에 매개변수가 있는지 확인
             //매개변수가 하나 이상 존재하면?
            state  = free(f.params, state); //free 메서드를 호출하여 함수의 매개변수에 해당하는 스택 요소들을 제거
        //f.params는 제거할 매개변수들의 리스트를 나타낸다
       // 함수 호출이 완료되었을 때 매개변수가 더 이상 필요하지 않음을 의미하므로, 스택에서 해당 매개변수들을 제거
        return state;            
    }

    State Eval(Empty s, State state) {
        return state;
    }
  
    State Eval(Assignment a, State state) {
        Value v = V(a.expr, state);

        if (a.ar == null)
	        return state.set(a.id, v);
        else {
            Value i = V(a.ar.expr, state);
            Value val = state.get(a.ar.id);
            Value[] ar = (Value[]) val.arrValue();
            ar[i.intValue()] = v;
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
            if (s instanceof Return)  
                return state;
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
        
        // [Function]
        if (l.funs != null) {
            // TODO: [Fill the code here]
            // Case with Function declaration inside Let
        	 for (Function fun : l.funs)//for 루프를 사용하여 Let 구문 내의 모든 함수 선언을 순회
        		s.push(fun.id, new Value(fun)); // 각 함수(Function fun)에 대해, 함수의 식별자(fun.id)와 함수 객체(new Value(fun))를 스택에 푸시
        }
        
        s = Eval(l.stmts, s);

	    return free(l.decls, s);
    }

    State allocate (Decls ds, State state) {
        if (ds != null)
        for (Decl decl : ds) 
		    if (decl.arraysize > 0) { 
                Value[] ar = new Value[decl.arraysize];
                state.push(decl.id, new Value(ar)); 
            }
            else if (decl.expr == null)
                state.push(decl.id, new Value(decl.type));
	        else
		        state.push(decl.id, V(decl.expr, state));

        return state;
    }

    State free (Decls ds, State state) {
        if (ds != null)
        for (Decl decl : ds) 
            state.pop();

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
        	if (v1.type == Type.STRING)
        		return new Value(v1.value == v2.value);
        	else if (v1.type == Type.BOOL)
        		return new Value(v1.boolValue() == v2.boolValue());
        	else
        		return new Value(v1.intValue() == v2.intValue());
        case "!=":
        	if (v1.type == Type.STRING)
        		return new Value(v1.value != v2.value);
        	else if (v1.type == Type.BOOL)
        		return new Value(v1.boolValue() != v2.boolValue());
        	else
        		return new Value(v1.intValue() != v2.intValue());
        case "<":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res < 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() < v2.intValue());
        case "<=":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res <= 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() <= v2.intValue());
        case ">":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res > 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() > v2.intValue());
        case ">=":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res >= 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
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
        if (e instanceof Array) {
	        Array ar = (Array) e;
            Value i = V(ar.expr, state);
            Value v = (Value) state.get(ar.id);
            Value[] vs = v.arrValue(); 
            return (vs[i.intValue()]); 
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
        if (e instanceof Call) 
    	    return V((Call)e, state);  
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
					 else  { 
						 command.type = TypeChecker.Check(command); 
//						  System.out.println("\nType: "+ command.type); 
				     }
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
					//  if (command != null) command.display(0);      // display AST
					if (command == null) 
						 throw new Exception();
					else  {
						 command.type = TypeChecker.Check(command); 
//                         System.out.println("\nType: "+ command.type);  
				    }
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