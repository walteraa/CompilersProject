
package com.ccompiler.core;

/**
 *	A C Operation... Nodes are the parameters of the operation...
 */
public enum Register {
	 R1("R1"), R2("R2"), R3("R3"), R4("R4"), R5("R5"), R6("R6"), R7("R7"), R8("R8"), R9("R9");
	
	private String value;

	Register(String value) {

        this.value = value;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
