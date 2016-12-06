
/**
 * <code>LogicalExpressionInterface</code> specifies a class that represents a LogicalExpression.
 * 
 * @author dardeshna
 */
public interface LogicalExpressionInterface {

	/**
	 * Checks whether the expression is valid (all values will make it true)
	 * @return whether the expression is valid
	 */
	public boolean valid();
	
	/**
	 * Checks whether the expression is satisfiable (some values will make it true)
	 * @return whether the expression is satisfiable
	 */
	public boolean satisfiable();

	/**
	 * Checks whether the expression is contingent (some values will make it true and some will make it false)
	 * @return whether the expression is contingent
	 */
	public boolean contingent();
	
	/**
	 * Checks whether the expression is equivalent to another expression (this <=> l)
	 * @return whether the expression is equivalent the other expression
	 */
	public boolean equivalent(LogicalExpression l);
	
	/**
	 * Checks whether the expression entails another expression (this => l)
	 * @return whether the expression is entails the other expression
	 */
	public boolean entails(LogicalExpression l);
	
}

