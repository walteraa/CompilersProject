package com.ccompiler.util;

import com.ccompiler.analysis.Lexical;

public class SemanticException extends IllegalArgumentException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7961006974649568226L;

	public SemanticException(String message) {
		super("Error at " + Lexical.curLine + "\n" + message);
	}

}
