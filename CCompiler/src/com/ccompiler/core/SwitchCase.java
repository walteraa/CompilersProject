package com.ccompiler.core;

public class SwitchCase extends ScopedEntity{
	public SwitchCase(String name, Register sentinel) {
		super(name);
		this.sentinelRegister = sentinel;
		// TODO Auto-generated constructor stub
	}

	Register sentinelRegister;

	public Register getSentinel(){
		return this.sentinelRegister;
	}
	
}
