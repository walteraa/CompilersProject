package com.ccompiler.core;

import com.ccompiler.util.SemanticException;

public class Case {
	
	private Expression expression;
	
	public Case(Expression expression) {
		this.expression = expression;
		checkExpression(expression);
	}

	public Expression getExpression() {
		return expression;
	}
	
	private void checkExpression(Expression e) {
		if (!e.getType().equals(new Type("int")) && !e.getType().equals(new Type("char")))
			throw new SemanticException("Case expression not 'int' or 'char', instead is " + e.getType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Case other = (Case) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Case [expression=" + expression + "]";
	}
}
