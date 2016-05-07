package com.ccompiler.main;

import java.io.BufferedReader;
import java.io.FileReader;

import com.ccompiler.analyzer.lexical.LexicalAnalyzer;
import com.ccompiler.analyzer.syntactical.Parser;

import java_cup.runtime.Symbol;

public class Main {

	/**
	 * Minimum number of input
	 */
	private static final int MIN_INPUT_FILES = 1;

	public static void main(String[] args) {
		if (args.length < MIN_INPUT_FILES) {
			displayHelpMessage();

		} else {
			for (String filePath : args) {
				startCompilationFor(filePath);
			}
		}
	}

	private static void displayHelpMessage() {
		System.out
				.println("Usage: java -jar compiler.jar file [file2 file3...]");
	}

	private static void startCompilationFor(String filePath) {
		try {
			System.out.println("Start compiling \"" + filePath + "\"...");
			LexicalAnalyzer scanner = new LexicalAnalyzer(new BufferedReader(new FileReader(filePath)));

			Parser parser = new Parser(scanner);
			Symbol s = parser.parse();
			
			if (s.toString().equals("#0"))
				System.out.println("> SUCCESSFULL COMPILATION: " + filePath);
			else
				System.out.println(s);
			
		} catch (Exception e) {
			System.err.println("Failed to compile \"" + filePath + "\":");
			System.err.println(e.getMessage());
		}
	}

}
