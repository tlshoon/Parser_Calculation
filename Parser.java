// 201720708 신재훈
package plsin;
import java.io.*;
public class Parser {
	int token;
	int value;
	int ch;
	boolean flag = false; 				// true,false를 넣어주기 위해 flag를 false로 초기화
	private PushbackInputStream input;
	final int NUMBER = 256;
	Parser(PushbackInputStream is){
		input = is;
	}
	int getToken()  					// getToken 호출
	{
		while(true) {
			try {
				ch = input.read(); //숫자 하나 읽어옴
				if(ch == ' '|| ch=='\t'|| ch=='\r');
				else if(Character.isDigit(ch)) {  //현재 입력된 문자가 숫자인지 확인
					value = number();  //숫자로 확인되면 number에서 저장
					input.unread(ch); 
					return NUMBER;  	// 숫자 토큰 반환
				}
				else return ch;
			}catch(IOException e) {
				System.err.println(e);
			}
		}
	}
	void match(int c) {
		if(token == c)
			token = getToken();
		else error();
	}
	
	
	// <expr> → <bexp> {& <bexp> | ‘|’ <bexp>} | !<expr> | true | false
	
	int expr() {
		if (token == '!') { 
			match('!');
			if (token != '=') {
				if (expr()==1)
					return 0;
				else
					return 1;
			}
		}
		int result = bexp();
		if (token =='&' || token == '|') {         // 기호와 토큰 확인
			while(token == '&' || token == '|') {  
				if (token == '&') {
					match('&'); //match 함수 호출
					if (bexp()==0) {  	
						return 0;  		
					}
				}
				else if (token =='|') {
					match('|');
					if (bexp()==1) {	
						return 1;  		
					}
				}
			}
		}
		return result;
	}
	
	//<bexp> → <aexp> [(== | != | < | > | <= | >=) <aexp>]
	
	int bexp() {
		int result = aexp();
		if (token == '=' || token == '<' || token == '>' || token == '!') {  		// 기호와 토큰 확인
			flag = true;  				// flag는 true로 초기화
			if (token == '=') {
				match('=');
				if (token == '=') {   	// '=='이므로 if문을 한 번 더 함
					match('='); 
					if (result == aexp())
						return 1; 		// 두 숫자가 같다면 true를 출력
					else
						return 0;
					}
				}
			else if (token == '<') {
				match('<');
				if (token == '=') {  
					match('=');
					if (result <= aexp())  // result가 작거나 같다
						return 1;
					else
						return 0;
					}
				else {
					if (result < aexp())
						return 1;
					else
						return 0;
					}
				}
			else if (token == '>') {
				match('>');
				if (token == '=') {
					match('=');
					if (result >= aexp())  // result가 크거나 같다
						return 1;  
					else
						return 0;
					}
				else {
					if (result > aexp())
						return 1;
					else
						return 0;
					}
				}
			else if (token == '!') {
				match('!');
				if (token == '=') {
					match('=');
					if (result != aexp())  // 값이 다를 때
						return 1;
					else
						return 0;
					}
				else   
					return bexp();
				}
			}
		return result;
		}
	
	// <aexp> -> <term> { + <term> | '-' <term> }
	
	int aexp() {
		int result = term();
		while(token == '+' || token == '-') {
			if (token == '+') {
				match('+');
				result += term();
			}
			else if (token == '-') {
				match('-');
				result -= term(); 
			}
		}
		return result;
	}
	
	// <term> -> <factor> { '*' <factor> | '/' <factor> } 
	
	int term() {
		int result = factor();
		while (token == '*' || token == '/') {
			if (token == '*') {
				match('*');
				result *= factor();
			}
			else if (token == '/') {
				match('/');
				result /= factor();
			}
		}
		return result;
	}
	// <factor> -> ( <aexp> ) | number
	
	int factor(){
		int result = 0;
		if (token =='(') {
			match('(');
			result = aexp();
			match(')');
			
		}
		else if (token == NUMBER) {
			result = value;
			match(NUMBER);
		}
		return result;
	}
	// <command> -> <expr> '\n'
	
	void command() {
		int result = expr();
		if (token == '\n') {
			if(!flag)
				System.out.println(result);				
			else {
				if(result == 0)
					System.out.println(false);
				else if(result == 1)
					System.out.println(true);
				}
			}
		else error();
	}
	
	void parse() {
		token = getToken(); //getToken 메소드 호출
		command();
	}
	
	// number -> digit { digit }
	
	int number() {
		int result = ch - '0';  	 // 0을 빼서 실제 숫자로 바꿔줌 아스키코드값을 실제 숫자로 바꾸는 과정
		try {
			ch = input.read(); // 다음 문자 읽기
			while(Character.isDigit(ch)) {
				result = 10*result + ch-'0';   // 자릿수가 두자리이기 때문에 앞에는 곱하기 10
			
			ch = input.read();	
			}
		}catch(IOException e) {
			System.err.println(e);
			
		}
		return result;
		
	}
	
	void error() {
		System.out.printf("parse error : %d\n", ch);
		System.exit(1);
	}
	public static void main(String[] args) { //메인 메소드 시작
		Parser p = new Parser(new PushbackInputStream(System.in));
		while(true) { //무한루프
			System.out.print(">>");
			p.parse(); //parse 함수 호출
			
		}

	}

}
