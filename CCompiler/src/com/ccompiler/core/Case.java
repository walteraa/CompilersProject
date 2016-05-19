package com.ccompiler.core;

import com.ccompiler.util.SemanticException;

public class Case extends ScopedEntity{
	
	private Expression expression;
	
	public Case() {
		super("case");
		
	}

	public Case(Expression expression) {
		super("case");
		checkExpression(expression);
		this.expression = expression;
	}

	public void setExpression(Expression e) {
		checkExpression(e);
		this.expression = e;
	}

	public Expression getExpression() {
		return expression;
	}
	
	private void checkExpression(Expression e) {
		if (!e.getType().equals(new Type("int")) && !e.getType().equals(new Type("char")))
			throw new SemanticException("If expression not 'int' or 'char', instead is " + e.getType());
	}

}
