package com.ccompiler.core;

import java.util.ArrayList;
import java.util.List;

import com.ccompiler.util.SemanticException;

public class Switch extends ScopedEntity{

	private List<Case> cases;
	private Expression expression;
	
	public Switch() {
		super("switch");
		this.cases = new ArrayList<Case>();
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
			throw new SemanticException("Switch expression not 'int' or 'char', instead is " + e.getType());
	}

	public void addCase(Case c) {
		if(this.cases.contains(c)){
			throw new SemanticException("Case clause already exists: " + c.getExpression());
		}

		this.cases.add(c);
	}

	public void checkCases() {
		Type switchType = this.expression.getType();
		
		for(Case c : cases){
			List<Type> types = getPossibleTypes(c.getExpression());
			if(!types.contains(switchType)){
				throw new SemanticException("Switch expressions and Case expressions should be the same type");
			}
		}
	}
	
	private List<Type> getPossibleTypes(Expression exp){
		List<Type> result = new ArrayList<Type>();
		result.add(exp.getType());
		
		String literal = exp.getValue().replace("'", "");
		if(literal.length() == 1){
			result.add(new Type("char"));
		}
		
		return result;
	}
}
