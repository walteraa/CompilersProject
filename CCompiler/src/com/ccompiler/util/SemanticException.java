package com.ccompiler.util;

import com.ccompiler.analyzer.lexical.LexicalAnalyzer;

public class SemanticException extends IllegalArgumentException {
	
	public SemanticException(String message) {
		super("Error at " + LexicalAnalyzer.curLine + "\n" + message);
	}

}
