package plsin;
import java.io.*;
public class Parser {
	int token;
	int value;
	int ch;
	private PushbackInputStream input;
	final int NUMBER = 256;
	Parser(PushbackInputStream is){
		input = is;
	}
	int getToken()
	{
		while(true) {
			try {
				ch = input.read();
				if(ch == ' '|| ch=='\t'|| ch=='\r');
				else if(Character.isDigit(ch)) {
					value = number();
					input.unread(ch);
					return NUMBER;
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
	// <expr> -> <term> { + <term> }
	int expr() {
		int result = term();
		while(token == '+') {
			match('+');
			result += term();
		}
		return result;
	}
	// <term> -> <factor> { * <factor> }
	int term() {
		int result = factor();
		while (token == '*') {
			match('*');
			result *= factor();
		}
		return result;
	}
	// <factor> -> ( <expr> ) | number
	int factor(){
		int result = 0;
		if (token =='(') {
			match('(');
			result = expr();
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
		if (token == '\n')
			System.out.printf("The result is: %d\n", result);
		else error();
	}
	
	void parse() {
		token = getToken();
		command();
	}
	
	// number -> digit { digit }
	int number() {
		int result = ch - '0';
		try {
			ch = input.read();
			while(Character.isDigit(ch)) {
				result = 10*result + ch-'0';
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
	public static void main(String[] args) {
		Parser p = new Parser(new PushbackInputStream(System.in));
		while(true) {
			System.out.print(">>");
			p.parse();
		}

	}

}
