package assignment_s_interpreter.Lab3;// Parser.java
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
    // <command> ->  <decl> | <stmt>
	    if (isType()) {
	        Decl d = decl();
	        return d;
	    }
	    if (token != Token.EOF) {
	        Stmt s = stmt();
            return s;
	    }
	    return null;
    }

    private Decl decl() { 
    // <decl>  -> <type> id [=<expr>]; 
        Type t = type();
	    String id = match(Token.ID);
	    Decl d = null;
	    if (token == Token.ASSIGN) {
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

    private Type type () {
    // <type>  ->  int | bool | string 
        Type t = null;
        switch (token) {
	    case INT:
            t = Type.INT; break;
        case BOOL:
            t = Type.BOOL; break;
        case STRING:
            t = Type.STRING; break;
        default:
	        error("int | bool | string");
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
        
        // TODO: [case DO, FOR]
        // student exercise
        case DO:
            s = doWhileStmt(); return s;
        case FOR:
            s = forStmt(); return s;
        case ID:	// assignment
            s = assignment(); return s;
	    case LET:	// let statement 
            s = letStmt(); return s;
	    case READ:	// read statement 
            s = readStmt(); return s;
	    case PRINT:	// print statment 
            s = printStmt(); return s;
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
    // <letStmt> -> let <decls> in <block> end
	    match(Token.LET);	
        Decls ds = decls();
	    match(Token.IN);
        Stmts ss = stmts();
        match(Token.END);	
        match(Token.SEMICOLON);
        return new Let(ds, ss);
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

    private Stmt assignment() {
    // <assignment> -> id = <expr>;   
        Identifier id = new Identifier(match(Token.ID));
        match(Token.ASSIGN);
        Expr e = expr();
        match(Token.SEMICOLON);
        return new Assignment(id, e);
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
    
    // TODO: [Implement dowhileStmt]
    private Stmts doWhileStmt() {
    	// check syntax <dowhileStmt> -> do <stmt> while (<expr>);
    	// ==> generate AST of [<stmt> <whileStmt>]
    	// student exercise
        match(Token.DO); // 'do' 키워드를 파싱
        Stmt s = stmt(); // 'do' 뒤에 오는 문장을 파싱하여 AST 노드로 가져옴
        match(Token.WHILE); // 'while' 키워드를 파싱
        match(Token.LPAREN); // 'while' 키워드 뒤에 오는 '('를 파싱
        Expr e = expr(); // '(' 뒤에 오는 식을 파싱하여 AST 노드로 가져옴, do-while문의 조건식
        match(Token.RPAREN); // 조건식 뒤에 오는 ')'를 파싱
        match(Token.SEMICOLON); // ;

        Stmts stmts = new Stmts(); // // Stmt* 객체를 생성(do-while 문의 body(1)과 조건식(1))
        stmts.stmts.add(s);  // do-while 문의 'do' 부분을 추가

        While whileStmt = new While(e, s);  // 'while'객체 생성
        stmts.stmts.add(whileStmt);  // do에 이어서, body와 조건문까지 추가, ex) do {print(i); i++;} while(i<5)

        return stmts;
    }

    // TODO: [Implement forStmt]
    private Let forStmt() {
        // check syntax <forStmt> -> for (<type> id = <expr>; <expr>; id = <expr>) <stmt>
        // ==> generate AST of [let <type> id = <expr> in while(<expr>) <stmt> end]
        // student exercise

        match(Token.FOR); // for 키워드 가져오기, for
        match(Token.LPAREN); // (
        Type t = type(); // for 루프 반복 변수의 타입 파싱, ex) int
        String id = match(Token.ID); // 반복 변수의 이름 파싱(for루프의 반복 변수의 이름을 'id'라는 문자열 변수에 저장, ex) i
        match(Token.ASSIGN); // 초기값 할당, =
        Expr initExpr = expr();  // 초기화, 0
        match(Token.SEMICOLON); // ;
        Expr condition = expr();  // 조건 검사, ex) i < 10
        match(Token.SEMICOLON); // ;
        Identifier varId = new Identifier(match(Token.ID));  // 반복 변수의 이름 가져와 Identifier생성(현재 토큰 스트림에서 ID토큰 가져와 변수명을 추출함) ex) i++
        match(Token.ASSIGN); // 할당
        Expr update = expr();  // 업데이트 식(보통 증감식, ex. i + 1 => varId부터해서 i += 1이 완성됨)
        match(Token.RPAREN); // )

        Stmt body = stmt();  // for 루프의 body

        Stmts combinedStmts = new Stmts(); //for 루프를 while루프로 바꿔주기 위해서는 Stmt* 필요 why? for문 body(1개 stmt) + i +=1(1개 Assignment식(stmt 상속)
        combinedStmts.stmts.add(body); //for문 body 추가
        combinedStmts.stmts.add(new Assignment(varId, update)); //for문의 업데이트 식, Assignment이기에 i(varId) =(ASSIGN) i + 1(update)

        // while 루프를 생성
        While whileLoop = new While(condition, combinedStmts); //while(i<10) {for문 body, assignment(증감식)}

        // 변수 선언과 초기화를 포함하는 let 문을 생성
        Decl decl = new Decl(id, t, initExpr); //for 루프의 반복변수의 이름, 타입, 초기식 저장(int i = 0)
        Decls decls = new Decls(); // 여러개의 반복변수 선언을 담을 수 있는 decls(int i = 0, int j = 0,...복수형 일수도 있음)
        decls.add(decl); //decl객체 추가

        return new Let(decls, new Stmts(whileLoop));  // let 문(반복변수 초기식 + while문)을 반환
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
		            if (command != null) command.display(0);    // display AST 
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
		             if (command != null) command.display(0);      // display AST
                } catch (Exception e) {
                    System.err.println(e); 
                }
	        } while (command != null);
	    }
    } //main
} // Parser