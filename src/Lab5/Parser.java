package Lab5;// Parser.java
// Parser for language S

public class Parser {
    Token token;          // current token 
    Lexer lexer;
    String funId = "";

    public Parser(Lexer scan) { 
        lexer = scan;		  
        token = lexer.getToken(); // get the first token
    }
  
    private String match(Token t) {
        String value = token.value();
        if (token == t)
            token = lexer.getToken();
        else
            error(t);
        return value;
    }

    private void error(Token tok) {
        System.err.println("Syntax error: " + tok + " --> " + token);
        token=lexer.getToken();
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: " + tok + " --> " + token);
        token=lexer.getToken();
    }
  
    public Command command() {
    // <command> ->  <decl> | <function> | <stmt>
	    if (isType()) {
	        Decl d = decl();
	        return d;
	    }
	    if (token == Token.FUN) { 
	        Function f = function();
	        return f;
	    }
	    if (token != Token.EOF) {
	        Stmt s = stmt();
            return s;
	    }
	    return null;
    }

    private Decl decl() {
       // <decl> -> <type> id [n]; 
       // <decl>  -> <type> id [=<expr>]; 
       Type t = type();
	   String id = match(Token.ID);
	   Decl d = null;
	   
	   if (token == Token.LBRACKET) {
           match(Token.LBRACKET);
           Value v = literal(); 
	       d = new Decl(id, t, v.intValue());
           match(Token.RBRACKET);
       } else if (token == Token.ASSIGN) {
	        match(Token.ASSIGN);
            Expr e = expr();
	       d = new Decl(id, t, e);
	   } else 
            d = new Decl(id, t);

		match(Token.SEMICOLON);
		return d;
    }

    private Decls decls () {
    // <decls> -> {<decl>}
        Decls ds = new Decls ();
	    while (isType()) {
	        Decl d = decl();
	        ds.add(d);
	    }
        return ds;             
    }

    // [Function]
    private Functions functions () {
    // <functions> -> { <function> }
	    Functions fs = new Functions();  
        while (token == Token.FUN) {
	        Function f = function(); 
	        fs.add(f);
        }  
        return fs;          	
    }
    
    // [Function]
    private Function function() {
    // <function>  -> fun <type> id(<params>) <stmt> 
	    match(Token.FUN);
	    Type t = type();
	    String str = match(Token.ID);
	    funId = str; 
	    Function f = new Function(str, t);
	    match(Token.LPAREN);
        if (token != Token.RPAREN)
            f.params = params();
	    match(Token.RPAREN);
	    Stmt s = stmt();		
	    f.stmt = s;
	    return f;
    }

    // [Function]
    private Decls params() {
	    Decls params = new Decls();
	    // TODO: [Implement the code of params]
	     Type t = type(); //type() 메서드를 호출하여 현재 토큰의 데이터 타입 가져옴(int | bool | string)
	     String id = match(Token.ID); // match 메서드를 사용하여 현재 토큰이 식별자(ID)인지 확인하고, 그 값을 문자열 id에 저장
	     params.add(new Decl(id, t)); //새로 생성된 Decl 객체를 params 리스트에 추가(t 타입의 id객체)
         while (token == Token.COMMA) { //쉼표(,)가 나타나면, 이는 여러 매개변수가 있다는 것을 의미 => 반복 시작!
	        match(Token.COMMA); //: 쉼표 토큰을 확인후
	        t = type(); //다음 매개변수의 타입을 파싱
            id = match(Token.ID); //다음 매개변수의 이름을 파싱
            params.add(new Decl(id, t)); //새 매개변수를 Decl 객체로 생성하고 params 리스트에 추가
         }
        return params;
    }

    private Type type () {
    // <type>  ->  int | bool | void | string 
        Type t = null;
        switch (token) {
	    case INT:
            t = Type.INT; break;
        case BOOL:
            t = Type.BOOL; break;
        case VOID:
            t = Type.VOID; break;
        case STRING:
            t = Type.STRING; break;
        default:
	        error("int | bool | void | string");
	    }
        match(token);
        return t;       
    }
  
    private Stmt stmt() {
    // <stmt> -> <stmts> | <assignment> | <ifStmt> | <whileStmt> | ...
        Stmt s = new Empty();
        switch (token) {
	    case SEMICOLON:
            match(token.SEMICOLON); return s;
        case LBRACE:			
	        match(Token.LBRACE);		
            s = stmts();
            match(Token.RBRACE);	
	        return s;
        case IF: 	// if statement 
            s = ifStmt(); return s;
        case WHILE:      // while statement 
            s = whileStmt(); return s;
	    case DO:      // while statement 
            s = doStmt(); return s;
	    case FOR:      // while statement 
            s = forStmt(); return s;
        case ID:	// assignment
            s = assignment(); return s;
	    case LET:	// let statement 
            s = letStmt(); return s;
	    case READ:	// read statement 
            s = readStmt(); return s;
	    case PRINT:	// print statment 
            s = printStmt(); return s;
        // [Function]
	    case RETURN:	// return statement  
            s = returnStmt(); return s;
        default:  
	        error("Illegal stmt"); return null; 
	}
    }
  
    private Stmts stmts () {
    // <stmts> -> {<stmt>}
        Stmts ss = new Stmts();
	    while((token != Token.RBRACE) && (token != Token.END))
	        ss.stmts.add(stmt()); 
        return ss;
    }

    private Let letStmt () {
    // <letStmt> -> let <decls> <functions> in <stmts> end
	    match(Token.LET);
        Decls ds = decls();
        
        // [Function]
        // TODO: [Implement the code for function declaration in let stmt]
        Functions fs = null;//함수 선언을 저장할 Functions 객체를 초기화! 이는 아래에 있듯이, 'let' 문 내에 선언된 모든 함수를 저장할
        // There is a Function declaration in Let statement
         if(token == Token.FUN) { //{: 현재 토큰이 함수 선언을 나타내는 'fun' 키워드인지 확인
        	fs = functions(); // functions() 메서드를 호출하여 'let' 문 내의 모든 함수 선언을 파싱하고 Functions 객체에 저장함
             //functions()의 역할은 'fun' 키워드로 시작하는 함수 선언을 찾아 해당 함수의 이름, 반환 타입, 매개변수, 그리고 본문을 파싱
         }
	    match(Token.IN);
        Stmts ss = stmts();
        match(Token.END);	
        match(Token.SEMICOLON);
        return new Let(ds, fs, ss);
    }

    private Read readStmt() {
    // <readStmt> -> read id;
        match(Token.READ);
        Identifier id = new Identifier(match(Token.ID));
        match(Token.SEMICOLON);
        return new Read(id);
    }

    private Print printStmt() {
    // <printStmt> -> print <expr>;
        match(Token.PRINT);
        Expr e = expr();
        match(Token.SEMICOLON);
        return new Print(e);
    }

    private Return returnStmt() {
    // <returnStmt> -> return <expr>; 
        match(Token.RETURN);
        Expr e = expr();
        match(Token.SEMICOLON);
        return new Return(funId, e);
    }

    private Stmt assignment() {
    // <assignment> -> id = <expr>;   
	    Array ar = null;  
        Identifier id = new Identifier(match(Token.ID));
	    if (token == Token.LPAREN) 
	        return call(id);
        if (token == Token.LBRACKET) {  
            match(Token.LBRACKET); 
            ar = new Array(id, expr());
            match(Token.RBRACKET);
        }

        match(Token.ASSIGN);
        Expr e = expr();
        match(Token.SEMICOLON);

        if (ar == null)
            return new Assignment(id, e);
        else 
            return new Assignment(ar, e);
    }
    
    // [Function]
    private Call call(Identifier id) {
    // <call> -> id(<expr>{,<expr>});
    	// TODO: [Implement the code of call stmt]
    	 match(Token.LPAREN); //현재 토큰이 왼쪽 괄호('(')인지 확인
         Call c = new Call(id, arguments()); //함수의 이름을 나타내는 식별자 id, 함수에 전달된 인수들을 파싱하고,  괄호 안의 표현식들을 리스트로 반환하는 arguments()
    	 match(Token.RPAREN); //오른쪽 괄호(')')를 확인
    	 match(Token.SEMICOLON); //세미콜론(';')을 확인하여 문장의 끝
    	 return c; //파싱된 함수 호출을 나타내는 Call 객체를 반환
    	
//    	return null; //call메서드를 구현완료했으므로 주석처리
    }

    private If ifStmt () {
    // <ifStmt> -> if (<expr>) then <stmt> [else <stmt>]
        match(Token.IF);
	    match(Token.LPAREN);
        Expr e = expr();
	    match(Token.RPAREN);
        match(Token.THEN);
        Stmt s1 = stmt();
        Stmt s2 = new Empty();
        if (token == Token.ELSE){
            match(Token.ELSE); 
            s2 = stmt();
        }
        return new If(e, s1, s2);
    }

    private While whileStmt () {
    // <whileStmt> -> while (<expr>) <stmt>
        match(Token.WHILE);
        match(Token.LPAREN);
        Expr e = expr();
        match(Token.RPAREN);
        Stmt s = stmt();
        return new While(e, s);
    }

    private Stmts doStmt() {
    // <doStmt> -> do <stmt> while (<expr>) 
        match(Token.DO);
        Stmt s = stmt();
        match(Token.WHILE);
        match(Token.LPAREN);
        Expr e = expr();
        match(Token.RPAREN);
		match(Token.SEMICOLON);
        Stmts ss = new Stmts(s); 
        ss.stmts.add(new While(e, s));
        return ss;
    }

    private Let forStmt () {
    // <forStmt> -> for (<type> id = <expr>; <expr>; id = <expr>) <stmt>
        match(Token.FOR);
        match(Token.LPAREN);
        Decl d = decl();
        Decls ds = new Decls(d); 
	    Expr e1 = expr();
        match(Token.SEMICOLON);
        Identifier id = new Identifier(match(Token.ID));
        match(Token.ASSIGN);
	    Expr e2 = expr();
	    Assignment assign = new Assignment(id, e2);
        match(Token.RPAREN);
        Stmt s = stmt();
        Stmts s1 = new Stmts(s); 
	    s1.stmts.add(assign);
	    Stmts s2 = new Stmts(new While(e1,s1));
        return new Let(ds, null, s2);
    }

    private Expr expr () {
    // <expr> -> <bexp> {& <bexp> | '|'<bexp>} | !<expr> | true | false
        switch (token) {
	    case NOT:
	        Operator op = new Operator(match(token));
	    Expr e = expr();
            return new Unary(op, e);
        case TRUE:
            match(Token.TRUE);
            return new Value(true);
        case FALSE:
            match(Token.FALSE);
            return new Value(false);
        }

        Expr e = bexp();
        // parse logical operations
        while (token == Token.AND || token == Token.OR) {
            Operator op = new Operator(match(token));
            Expr b = bexp();
            e = new Binary(op, e, b);
        }
        return e;
    }

    private Expr bexp() {
        // <bexp> -> <aexp> [ (< | <= | > | >= | == | !=) <aexp> ]
        Expr e = aexp();
        switch(token) {
        case LT: case LTEQ: case GT: case GTEQ: case EQUAL: case NOTEQ:
            Operator op = new Operator(match(token));
            Expr a = aexp();
            e = new Binary(op, e, a);
        }
        return e;
    }
  
    private Expr aexp () {
        // <aexp> -> <term> { + <term> | - <term> }
        Expr e = term();
        while (token == Token.PLUS || token == Token.MINUS) {
            Operator op = new Operator(match(token));
            Expr t = term();
            e = new Binary(op, e, t);
        }
        return e;
    }
  
    private Expr term () {
        // <term> -> <factor> { * <factor> | / <factor>}
        Expr t = factor();
        while (token == Token.MULTIPLY || token == Token.DIVIDE) {
            Operator op = new Operator(match(token));
            Expr f = factor();
            t = new Binary(op, t, f);
        }
        return t;
    }
  
    private Expr factor() {
        // <factor> -> [-](id | <call> | literal | '('<aexp> ')')
        Operator op = null;
        if (token == Token.MINUS) 
            op = new Operator(match(Token.MINUS));

        Expr e = null;
        switch(token) {
        case ID:
            Identifier v = new Identifier(match(Token.ID));
            e = v;
            if (token == Token.LPAREN) {  // function call
                match(Token.LPAREN); 
                Call c = new Call(v, arguments());
                match(Token.RPAREN);
                e = c;
            } else if (token == Token.LBRACKET) {  
                match(Token.LBRACKET); 
                Array a = new Array(v,expr());
                match(Token.RBRACKET);
                e = a;
            }
            break;
        case NUMBER: case STRLITERAL: 
            e = literal();
            break; 
        case LPAREN: 
            match(Token.LPAREN); 
            e = aexp();       
            match(Token.RPAREN);
            break; 
        default: 
            error("Identifier | Literal"); 
        }

        if (op != null)
            return new Unary(op, e);
        else return e;
    }
  
    private Exprs arguments() {
    // arguments -> [ <expr> {, <expr> } ]
        Exprs es = new Exprs();
        while (token != Token.RPAREN) {
            es.add(expr());
            if (token == Token.COMMA)
                match(Token.COMMA);
            else if (token != Token.RPAREN)
                error("Exprs");
        }  
        return es;  
    }

    private Value literal( ) {
        String s = null;
        switch (token) {
        case NUMBER:
            s = match(Token.NUMBER);
            return new Value(Integer.parseInt(s));
        case STRLITERAL:
            s = match(Token.STRLITERAL);
            return new Value(s);
        }
        throw new IllegalArgumentException( "no literal");
    }
 
    private boolean isType( ) {
        switch(token) {
        case INT: case BOOL: case STRING: 
            return true;
        default: 
            return false;
        }
    }
    
    public static void main(String args[]) {
	    Parser parser;
        Command command = null;
	    if (args.length == 0) {
	        System.out.print(">> ");
	        Lexer.interactive = true;
	        parser  = new Parser(new Lexer());
	        do {
	            if (parser.token == Token.EOF) 
		            parser.token = parser.lexer.getToken();

                try {
                    command = parser.command();
//		            if (command != null) command.display(0);    // display AST 
                } catch (Exception e) {
                    System.err.println(e);
                }
		        System.out.print("\n>> ");
	        } while(true);
	    }
    	else {
	        System.out.println("Begin parsing... " + args[0]);
	        parser  = new Parser(new Lexer(args[0]));
	        do {
	            if (parser.token == Token.EOF) 
                    break;

                try {
		             command = parser.command();
//		             if (command != null) command.display(0);      // display AST
                } catch (Exception e) {
                    System.err.println(e); 
                }
	        } while (command != null);
	    }
    } //main
} // Parser