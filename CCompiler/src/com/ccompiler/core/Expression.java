package com.ccompiler.core;

import com.ccompiler.util.SemanticException;

public class Expression extends NamedEntity {
	
	private Type type;
	private String value;
	
	public Expression(String name) {
		super(name);
		type = new Type("UNKNOWN");
	}
	
	public Expression(Type t) {
		super(null);
		this.type = t;
	}
	
	public Expression(Type t, String value) {
		super(null);
		this.type = t;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	
	
	public void setType(Type type) {
		if (!getType().equals(new Type("UNKNOWN")) && !type.equals(getType()))
			throw new SemanticException("Illegal Type Assignment " + type + " and " + getType());
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getAssemblyValue() {
		return value == null ? getName() : value;
	}
	
	public void setValue(Expression exp) {
		if (!exp.getType().equals(new Type("UNKNOWN")))
			setType(exp.getType());
		this.value = exp.getValue();
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Expression other = (Expression) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toString() {
		if (getValue() == null) {
			return "{ Expression: " + getType() + "}";
		}
		return "{ Expression: " + getType() + " " + getValue() + "  }";
	}
}
