package com.ccompiler.analyzer.lexical;

public class MainLexical {
	
	private static final int MIN_INPUT_FILES = 1;

	public static void main(String[] args) {
		
		if (args.length < MIN_INPUT_FILES) {
			displayHelpMessage();

		} else {
			LexicalAnalyzer.main(args);
		}
	}
	
	private static void displayHelpMessage() {
		System.out.println("Usage: java -jar compiler.jar file [file2 file3...]");
	}
}
