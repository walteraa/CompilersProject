package com.ccompiler.core;

import java.util.ArrayList;
import java.util.List;

import com.ccompiler.util.SemanticException;

public class Switch extends ScopedEntity{
	
	private Expression expression;
	
	private List<Case> cases;
	
	public Switch() {
		super("switch");
		
	}

	public Switch(Expression expression) {
		super("switch");
		this.expression = expression;
		this.cases = new ArrayList<Case>();
		checkExpression(expression);
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
	
	public void addCase(Case c){
		this.cases.add(c);
	}

}
