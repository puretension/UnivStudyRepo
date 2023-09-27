package Lab2;// Parser.java
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
	/*
	    if (token == Token.FUN) {
	        Function f = function();
	        return f;
	    }
	*/
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

/*
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

    private Decls params() {
	    Decls params = new Decls();
        
		// parse declrations of parameters

        return params;
    }

*/

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
    // <stmt> -> <block> | <assignment> | <ifStmt> | <whileStmt> | ...
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
        case ID:	// assignment
            s = assignment(); return s;
	    case LET:	// let statement 
            s = letStmt(); return s;
	    case READ:	// read statement 
            s = readStmt(); return s;
	    case PRINT:	// print statment 
            s = printStmt(); return s;
	    case RETURN: // return statement 
            s = returnStmt(); return s;
        default:  
	        error("Illegal stmt"); return null; 
	    }
    }
  
    private Stmts stmts () {
    // <block> -> {<stmt>}
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
        return new Let(ds, null, ss);
    }

    // TODO: [Complete the code of readStmt()]
    private Read readStmt() {
    // <readStmt> -> read id;
    //
    // parse read statement
        match(Token.READ); // // 현재 토큰이 read 인지 확인
        String id = match(Token.ID); // 현재 토큰이 ID(식별자)인지 확인,ID를 문자열로 반환하여 'id' 변수에 저장
        match(Token.SEMICOLON); //read 문이 정상적으로 종료되었는지 확인하기 위해 세미콜론으로 체크
	    return new Read((new Identifier(id))); // id 문자열을 이용해 Identifier 객체를 생성하고, 그것을 매개변수로 사용해 Read 객체를 생성
    }

    // TODO: [Complete the code of printStmt()]
    private Print printStmt() {
    // <printStmt> -> print <expr>;
    //
    // parse print statement
    //
        match(Token.PRINT); //현재 토큰이 print 인지 확인
        Expr e = expr(); //expr() 메소드를 호출하여 print문에 대한 출력 표현식을 파싱
        match(Token.SEMICOLON); //print 문이 정상적으로 종료되었는지 확인하기 위해 세미콜론으로 체크
        return new Print(e); // 파싱된 표현식 'e'를 사용하여 Print 객체를 생성하고 반환. 이 객체는 파싱된 'print'문을 나타냄
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
        Identifier id = new Identifier(match(Token.ID));
	/*
	    if (token == Token.LPAREN)    // function call 
	        return call(id);
	*/

        match(Token.ASSIGN);
        Expr e = expr();
        match(Token.SEMICOLON);
        return new Assignment(id, e);
    }

/*
    private Call call(Identifier id) {
    // <call> -> id(<expr>{,<expr>});
    //
    // parse function call
    //
	return null;
    }
*/

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

    // TODO: [Complete the code of whileStmt()]
    private While whileStmt () {
        // <whileStmt> -> while (<expr>) <stmt>
        match(Token.WHILE); // 현재 토큰이 'while' 키워드인지 확인하고 매치. 매치가 성공하면 다음 토큰으로 넘어감
        match(Token.LPAREN); // (
        Expr e = expr(); //while루프내에 조건 파싱
        match(Token.RPAREN); // )
        Stmt s = stmt(); //while루프의 본문 파싱(하나의 statement 블록)
        return new While(e, s);
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
        // TODO: [Complete the code of logical operations for <expr> -> <bexp> {& <bexp> | '|'<bexp>}]
		//
		// parse logical operations
		// And와 OR 논리 연산 처리
        while (token == Token.AND || token == Token.OR) {
            Operator op = new Operator(match(token)); // 논리 연산자 토큰을 매치하고, Operator 객체를 생성
            Expr b = bexp(); //bexp() 메소드를 호출하여 연산의 오른쪽(2번째) 피연산자를 가져온다
            e = new Binary(op, e, b); // 논리 연산자와 두 피연산자를 사용하여 이진 표현식 객체를 생성
        }
        return e;
    }

    // TODO: [Complete the code of bexp()]
    private Expr bexp() {
        // <bexp> -> <aexp> [ (< | <= | > | >= | == | !=) <aexp> ]
        Expr e = aexp();
	//
	// parse relational operations
	//

        // 관계 연산자를 파싱하는 부분. 관계 연산자가 있는 경우에만 이 while 루프가 동작하며,
        // 이 루프는 관계 연산자 + 그 오른쪽의 산술 표현식까지 처리
        while (token == Token.LT || token == Token.LTEQ || token == Token.GT ||
                token == Token.GTEQ || token == Token.EQUAL || token == Token.NOTEQ) {
            //관계 연산자들 < < | <= | > | >= | == | != 확인후에 관련 표현식 파싱!
            Operator op = new Operator(match(token)); // 현재 토큰의 관계 연산자를 매치하고 Operator 객체를 생성. 매치된 토큰은 다음 토큰으로 이동됨
            Expr a = aexp(); // 관계 연산자 오른쪽의 산술 표현식을 파싱하고, 그 결과를 a에
            // 이진 연산을 나타내는 Binary 객체를 생성.
            // 이 객체는 앞서 파싱된 산술 표현식 e, 관계 연산자 op, 산술 표현식 a를 포함하며
            e = new Binary(op, e, a); //관계 연산의 결과물
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
                Call c = new Call(v,arguments());
                match(Token.RPAREN);
                e = c;
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
		             if (command != null) command.display(0);    // display AST, TODO: [Uncomment this line]
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
		              if (command != null) command.display(0);      // display AST, TODO: [Uncomment this line]
                } catch (Exception e) {
                    System.err.println(e); 
                }
	        } while (command != null);
	    }
    } //main
} // Parser