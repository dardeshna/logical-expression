import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;


/**
 * <code>LogicalExpression</code> implements <code>LogicalExpressionInterface</code>
 * and represents a logical expression.  It takes a string, converts it to a evaluateable
 * list of tokens, and has methods to check for validity, satisfiability, contingency,
 * entailment and equivalence.
 * 
 * @author dardeshna
 */
public class LogicalExpression implements LogicalExpressionInterface {

	private String expression;
	private ArrayList<String> parsedExpression;
	private ArrayList<String> variables;

	/**
	 * Constructs a <code>LogicalExpression</code> from a string
	 * @param expression the string representing the expression
	 */
	public LogicalExpression(String expression) {
		if (expression.equals("")) throw new RuntimeException("Null expression");
		this.expression = expression;
		parsedExpression = parse(expression);
		parsedExpression = shunt(parsedExpression);
		variables = determineVariables(parsedExpression);
	}

	/**
	 * Constructs a <code>LogicalExpression</code> by joining two expressions with an operator
	 * @param l the first <code>LogicalExpression</code>
	 * @param m the second <code>LogicalExpression</code>
	 * @param operator the operator to join the expressions
	 */
	public LogicalExpression(LogicalExpression l, LogicalExpression m, String operator) {
		if (getTokenType(operator) != TokenType.OPERATOR) throw new RuntimeException("Invalid operator");
		parsedExpression = new ArrayList<String>();
		for (String s: l.parsedExpression)
			parsedExpression.add(s);
		for (String s: m.parsedExpression)
			parsedExpression.add(s);
		parsedExpression.add(operator);
		variables = determineVariables(parsedExpression);
	}

	/**
	 * Parses a string expression into an array of tokens
	 * @param expression a string representing the expression
	 * @return an <code>ArrayList</code> containing the tokens in the order they appeared in the string
	 */
	public ArrayList<String> parse(String expression) {

		ArrayList<String> tokens = new ArrayList<String>();
		char[] chars = expression.toCharArray();
		
		String nextToken = " ";
		for (int i = 0; i < chars.length; i++) {
			char character = chars[i];
			if (getCharType(character) == CharType.INVALID) {
				throw new RuntimeException("Invalid character");
			}
			switch (getCharType(nextToken.charAt(nextToken.length()-1))) {
			case SPLIT:
			case SINGLE_CHAR_OPERATOR:
				tokens.add(nextToken);
				nextToken = Character.toString(character);
				break;
			case VARIABLE_CHAR:
				switch (getCharType(character)) {
				case VARIABLE_CHAR:
					nextToken = nextToken + Character.toString(character);
					break;
				case SINGLE_CHAR_OPERATOR:
				case MULTI_CHAR_OPERATOR:
				case SPLIT:
					tokens.add(nextToken);
					nextToken = Character.toString(character);
					break;
				default:
					break;
				}
				break;
			case MULTI_CHAR_OPERATOR:
				switch (getCharType(character)) {
				case MULTI_CHAR_OPERATOR:
					nextToken = nextToken + Character.toString(character);
					break;
				case VARIABLE_CHAR:
				case SINGLE_CHAR_OPERATOR:
				case SPLIT:
					tokens.add(nextToken);
					nextToken = Character.toString(character);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		tokens.add(nextToken);

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equals(" ")) {
				tokens.remove(i);
				i--;
			}
		}
		
		if (tokens.size() == 0)
			throw new RuntimeException("Null expression");

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equals(">=") || tokens.get(i).equals("=<") || tokens.get(i).equals("<=")) {
				tokens.set(i, "=>");
			}
		}

		return tokens;	
	}

	/**
	 * Returns the type of a character
	 * @param c the character
	 * @return the <code>CharType</code> of the character
	 */
	private static CharType getCharType(char c) {
		if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '$' || c == '_')
			return CharType.VARIABLE_CHAR;
		else if ( c == '~'  || c == '&' || c == '|' || c == '(' || c == ')')
			return CharType.SINGLE_CHAR_OPERATOR;
		else if (c == '=' || c == '<' || c == '>')
			return CharType.MULTI_CHAR_OPERATOR;
		else if (c == ' ')
			return CharType.SPLIT;
		else
			return CharType.INVALID;
	}
	private static enum CharType {
		VARIABLE_CHAR, SINGLE_CHAR_OPERATOR, MULTI_CHAR_OPERATOR, SPLIT, INVALID
	}

	/**
	 * Reorders an array of tokens into reverse polish notation using the shunting yard algorithm
	 * @param tokens an <code>ArrayList</code> containing the tokens of the expression
	 * @return a reordered <code>ArrayList</code>
	 */
	public ArrayList<String> shunt(ArrayList<String> tokens) {

		ArrayList<String> queue = new ArrayList<String>();
		Stack<String> operatorStack = new Stack<String>();

		while (tokens.size() > 0) {
			String currentToken = tokens.get(0);
			switch (getTokenType(currentToken)) {
			case VARIABLE:
				queue.add(currentToken);
				break;
			case OPERATOR:
				while (!operatorStack.isEmpty() && getTokenType(operatorStack.peek()) == TokenType.OPERATOR && precedence(operatorStack.peek(), currentToken) > 0) {
					queue.add(operatorStack.pop());
				}
				operatorStack.add(currentToken);
				break;
			case LEFT_PARENTHESIS:
				operatorStack.add(currentToken);
				break;
			case RIGHT_PARENTHESIS:
				while (!operatorStack.isEmpty() && getTokenType(operatorStack.peek()) != TokenType.LEFT_PARENTHESIS) {
					queue.add(operatorStack.pop());
				}
				if (operatorStack.isEmpty())
					throw new RuntimeException("Mismatched parentheses");
				operatorStack.pop();
				break;
			}
			tokens.remove(0);
		}
		
		while (!operatorStack.isEmpty()) {
			TokenType t = getTokenType(operatorStack.peek());
			if (t == TokenType.LEFT_PARENTHESIS || t == TokenType.RIGHT_PARENTHESIS)
				throw new RuntimeException("Mismatched parentheses");
			queue.add(operatorStack.pop());
		}

		return queue;
	}

	/**
	 * Returns the type of a token
	 * @param s the token
	 * @return the <code>TokenType</code> of the character
	 */
	private static TokenType getTokenType(String s) {
		switch (s) {
		case "~":
		case "&":
		case "|":
		case "=>":
		case "<=>":
			return TokenType.OPERATOR;
		case "(":
			return TokenType.LEFT_PARENTHESIS;
		case ")":
			return TokenType.RIGHT_PARENTHESIS;
		default:
			return TokenType.VARIABLE;
		}
	}
	private static enum TokenType {
		VARIABLE, OPERATOR, LEFT_PARENTHESIS, RIGHT_PARENTHESIS
	}

	/**
	 * Determines the variables from <code>ArrayList</code> of tokens 
	 * @param tokens an <code>ArrayList</code> containing the tokens of the expression
	 * @return a sorted <code>ArrayList</code> containing the variables in the expression
	 */
	public ArrayList<String> determineVariables(ArrayList<String> tokens) {
		ArrayList<String> variables = new ArrayList<String>(tokens);
		for (int i = 0; i < variables.size(); i++) {
			if (getTokenType(variables.get(i)) != TokenType.VARIABLE) {
				variables.remove(i);
				i--;
			}
		}

		for (int i = 0; i < variables.size(); i++) {
			int removedBefore = 0;
			for (int j = 0; j < variables.size(); j++) {
				if (i != j && variables.get(j).equals(variables.get(i))) {
					variables.remove(j);
					j--;
					if (j < i)
						removedBefore++;
				}
			}
			i -= removedBefore;
		}

		Collections.sort(variables);
		return variables;
	}
	/**
	 * Returns the variables in the expression as an <code>ArrayList</code>
	 * @return an <code>ArrayList</code> of the variables in the expression
	 */
	public ArrayList<String> getVariables() {
		return new ArrayList<String>(variables);
	}

	private static String[] operators = new String[]{"~", "&", "|", "=>", "<=>"};
	/**
	 * Determines which operator has higher precedence
	 */
	private static int precedence(String op1, String op2) {
		int p1 = Math.min(indexOf(operators, op1), 3);
		int p2 = Math.min(indexOf(operators, op2), 3);
		return Integer.compare(p2, p1);
	}
	/**
	 * Returns the index of a value in an array
	 * @param list the array to be searched
	 * @param value the value to search for
	 * @return the index of the value if it exists, otherwise -1
	 */
	private static int indexOf(String[] list, String value) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(value))
				return i;
		}
		return -1;
	}
	/**
	 * Returns the index of a value in an <code>ArrayList</code>
	 * @param list the <code>ArrayList</code> to be searched
	 * @param value the value to search for
	 * @return the index of the value if it exists, otherwise -1
	 */
	private static int indexOf(ArrayList<String> list, String value) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(value))
				return i;
		}
		return -1;
	}

	/**
	 * Evaluates the expression for a given set of truth values
	 * @param truthVals the boolean array containing the truth values, must be the same size as the number of variables in the expression
	 * @return whether the sentence is true or false with the given set of truth values
	 */
	public boolean evaluate(boolean[] truthVals) {
		if (truthVals.length != variables.size()) {
			throw new RuntimeException("Invalid array size");
		}

		Stack<String> stack = new Stack<String>();
		ArrayList<String> expression = new ArrayList<String>(parsedExpression);

		while (expression.size() > 0) {

			String currentToken = expression.get(0);
			if (getTokenType(currentToken) == TokenType.VARIABLE) {
				stack.push(Boolean.toString(truthVals[indexOf(variables, currentToken)]));
			}
			else {
				String arg1, arg2;
				switch (currentToken) {
				case "~":
					stack.push(Boolean.toString(!Boolean.parseBoolean(stack.pop())));
					break;
				case "&":
					arg2 = stack.pop();
					arg1 = stack.pop();
					stack.push(Boolean.toString(Boolean.parseBoolean(arg1) && Boolean.parseBoolean(arg2)));
					break;
				case "|":
					arg2 = stack.pop();
					arg1 = stack.pop();
					stack.push(Boolean.toString(Boolean.parseBoolean(arg1) || Boolean.parseBoolean(arg2)));
					break;
				case "=>":
					arg2 = stack.pop();
					arg1 = stack.pop();
					boolean arg1b = Boolean.parseBoolean(arg1);
					boolean arg2b = Boolean.parseBoolean(arg2);

					if (arg1b == true && arg2b == false) {
						stack.push("false");
					}
					else {
						stack.push("true");
					}
					break;
				case "<=>":
					arg2 = stack.pop();
					arg1 = stack.pop();
					stack.push(Boolean.toString(Boolean.parseBoolean(arg1) == Boolean.parseBoolean(arg2)));
					break;
				}
			}

			expression.remove(0);

		}

		String value = stack.pop();
		return Boolean.parseBoolean(value);
	}

	/**
	 * Checks whether the expression is valid
	 * @return whether the expression is valid
	 */
	@Override
	public boolean valid() {
		for (int i = 0; i < 1<<variables.size(); i++) {
			boolean[] values = new boolean[variables.size()];
			for (int j = 0; j < variables.size(); j++) {
				values[j] = ((i >> j) & 1) != 0;
			}
			if (evaluate(values) == false)
				return false;
		}
		return true;
	}

	/**
	 * Checks whether the expression is satisfiable
	 * @return whether the expression is satisfiable
	 */
	@Override
	public boolean satisfiable() {
		return valid() || contingent();
	}

	/**
	 * Checks whether the expression is contingent
	 * @return whether the expression is contingent
	 */
	@Override
	public boolean contingent() {
		boolean existsTrue = false;
		boolean existsFalse = false;
		for (int i = 0; i < 1<<variables.size(); i++) {
			boolean[] values = new boolean[variables.size()];
			for (int j = 0; j < variables.size(); j++) {
				values[j] = ((i >> j) & 1) != 0;
			}

			boolean evaluated = evaluate(values);
			if (!existsTrue && evaluated)
				existsTrue = true;
			if (!existsFalse && !evaluated)
				existsFalse = true;
			if (existsFalse && existsTrue)
				return true;
		}
		return false;
	}

	/**
	 * Checks whether the expression is equivalent to another expression (this <=> l)
	 * @return whether the expression is equivalent the other expression
	 */
	@Override
	public boolean equivalent(LogicalExpression l) {
		LogicalExpression m = new LogicalExpression(this, l, "<=>");
		return m.valid();
	}

	/**
	 * Checks whether the expression entails another expression (this => l)
	 * @return whether the expression is entails the other expression
	 */
	@Override
	public boolean entails(LogicalExpression l) {
		LogicalExpression m = new LogicalExpression(this, l, "=>");
		return m.valid();
	}

	/**
	 * Returns the string representation of the <code>LogicalExpression</code>
	 * @return the string representation of the <code>LogicalExpression</code>
	 */
	public String toString() {
		return expression;
	}

	/**
	 * Tests the <code>LogicalExpression</code> class
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);

		System.out.print("Enter a logical expression: ");
		LogicalExpression l = new LogicalExpression(input.nextLine());
		System.out.println();
		
		System.out.println("Expression (E1): " + l);
		
		System.out.println("Valid: " + l.valid());
		System.out.println("Satisfiable: " + l.satisfiable());
		System.out.println("Contingent: " + l.contingent());
		System.out.println();

		System.out.print("Enter a second logical expression (if you want): ");
		LogicalExpression m = new LogicalExpression(input.nextLine());
		System.out.println();
		
		System.out.println("Second Expression (E2): " + m);
		
		System.out.println("E1 entails E2: " + l.entails(m));
		System.out.println("E2 entails E1: " + m.entails(l));
		System.out.println("E1 is equivalent to E2: " + l.equivalent(m));
		System.out.println();
		
		input.close();
	}

}
