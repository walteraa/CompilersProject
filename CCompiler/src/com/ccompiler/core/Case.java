package com.ccompiler.core;

import java.util.LinkedList;
import java.util.List;

import com.ccompiler.util.SemanticException;

public class Case extends SwitchCase{
	
	private Expression expression;
	List<Expression> statements;
	int sizeOfElements = 0;
	public Case(Expression expression, Register r) {
		super("case", r);
		this.expression = expression;
		checkExpression(expression);
		statements = new LinkedList<Expression>();
	}

	public Expression getExpression() {
		return expression;
	}
	
	private void checkExpression(Expression e) {
		if (!e.getType().equals(new Type("int")) && !e.getType().equals(new Type("char")))
			throw new SemanticException("Case expression not 'int' or 'char', instead is " + e.getType());
	}

	public void incrementSize(){
		sizeOfElements++;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	public void addStatement(Expression e){
		statements.add(e);
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

	public int getSize(){
		return sizeOfElements;
	}
	public void addVariable(Variable v) {
		super.addVariable(v);
		
	};
	
	@Override
	public String toString() {
		return "Case [expression=" + expression + "]";
	}
}
