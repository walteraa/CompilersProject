package com.ccompiler.core;

/**
 * C Type<br>
 * These types could be primitives (char, int, float, ...) and user-defined
 * using structs, unions, etc
 */
public class Type extends NamedEntity implements Parameter {

	public Type(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "{ Type " + getName() + " }";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Type))
			return false;
		return getName().equals(((Type) obj).getName())
				|| ((Type) obj).getName().equals("char") && this.getType().getName().equals("int")
				|| ((Type) obj).getName().equals("int") && this.getType().getName().equals("char");
	}

	public Type getType() {
		return this;
	}
}
