package com.ccompiler.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.ccompiler.core.Case;
import com.ccompiler.core.Expression;
import com.ccompiler.core.Function;
import com.ccompiler.core.Identifier;
import com.ccompiler.core.Operation;
import com.ccompiler.core.Program;
import com.ccompiler.core.Register;
import com.ccompiler.core.ScopedEntity;
import com.ccompiler.core.Switch;
import com.ccompiler.core.Type;
import com.ccompiler.core.Variable;
import com.ccompiler.util.SemanticException;


public class Semantic {

	public static Syntatic parser;
	
	private static final Type[] BASIC_TYPES = new Type[] {
		new Type("int"),
		new Type("float"),
		new Type("double"),
		new Type("long"),
		new Type("char"),
		new Type("void"),
		new Type("string"),
		new Type("bool")
		};

	public static ArrayList<String> variaveis = new ArrayList<String>();
	public static ArrayList<String> valores = new ArrayList<String>();
	private static Semantic sAnalysis;
	
	public static Semantic getInstance() {
		if (sAnalysis == null)
			sAnalysis = new Semantic();
		return sAnalysis;
	}
	
	// Object Attributes
	
	private Program cProgram; // ...
	
	private Stack<ScopedEntity> scopeStack;

	private Semantic(){
		scopeStack = new Stack<ScopedEntity>();
		cProgram = new Program();
	}
	
	private void createNewScope(ScopedEntity scope) {
		scopeStack.push(scope);
	}
	
	public void exitCurrentScope() {
		ScopedEntity scoped = scopeStack.pop();
		
		if (scoped instanceof Function)
			((Function) scoped).validateReturnedType();
	}
	
	public ScopedEntity getCurrentScope() {
		return scopeStack.peek();
	}
	
	public void addFunctionAndNewScope(Function f) {
		cProgram.checkOverload(f);
		cProgram.addFunction(f);
		createNewScope(f);
	}
	
	/** Switch Case Semantic */
	private Switch switchCase = new Switch();
	
	public void checkSwitchCase(Expression e) {
		this.switchCase.setExpression(e);
		//TODO Ruan: resetar o switch depois de analisar e gerar código
	}
	
	public void addCaseToSwitch(Expression e){
		this.switchCase.addCase(new Case(e));
	}
	
	public void addVariable(Variable v) {
		if (checkVariableNameCurrentScope(v.getName()))
			throw new SemanticException("Variable "+ v.getName() +  " already exists");
		
		if (scopeStack.peek() != null) {
			scopeStack.peek().addVariable(v);
		} else {
			cProgram.addVariable(v);
		}
	}
	
	public Identifier getIdentifier(String name) {
		if (!checkVariableNameAllScopes(name) && !checkFunctionName(name))
			throw new SemanticException("Identifier name doesn't exists: " + name);
		
		if (cProgram.getFunctions().get(name) != null)
			return cProgram.getFunctions().get(name);
		
		for (int i = scopeStack.size() - 1 ; i >= 0 ; i--)
			if (scopeStack.get(i).getVariable().get(name) != null)
				return scopeStack.get(i).getVariable().get(name);
		
		return cProgram.getVariable().get(name);
	}
	
	// Check Operations
	
	public void isFunction(Object o) {
		if (!(o instanceof Function))
			throw new SemanticException("Sorry, but " + o.toString() + " is not a function");
	}
	
	public boolean checkVariableNameCurrentScope(String name) {
		
		Set<String> variablesName;
		if (scopeStack.isEmpty())
			variablesName = cProgram.getVariable().keySet();
		else
			variablesName = scopeStack.peek().getVariable().keySet();
		
		return variablesName.contains(name);
	}
	
	public boolean checkVariableNameAllScopes(String name) {
		HashSet<String> variablesName = new HashSet<String>();
		if (scopeStack.isEmpty())
			variablesName.addAll(cProgram.getVariable().keySet());
		else {
			variablesName.addAll(scopeStack.peek().getVariable().keySet());
			
			for (int i = 0 ; i < scopeStack.size() - 1 ; i++) {
				for (String vName : scopeStack.get(i).getVariable().keySet()) {
					variablesName.add(vName);
				}
			}
		}
		return variablesName.contains(name);
	}
	
	public boolean checkTypeExists(Type type) {
		for (int i = 0 ; i < BASIC_TYPES.length ; i++)
			if (BASIC_TYPES[i].getName().equals(type.getName()))
				return true;
		
		for (int i = 0 ; i < scopeStack.size() ; i++) {
			if (scopeStack.get(i).getTypes().containsKey(type.getName())) {
				return true;
			}
		}
		return false;
	}

	public void checkFunctionCallException(String functionName) {
		if (!checkFunctionCall(functionName)) {
			throw new SemanticException("Calling function not declared: " + functionName + "()");
		}
		
		addCode(labels+": ADD SP, SP, #size");
		addCode(labels+8+": ST *ST, #" + labels+32);
		addCode(labels+16+": BR " + functionName);
		addCode(labels+32 + ":");
		addCode(labels+40+": SUB SP, SP, #size");
		labels= labels+56;
	}
	
	public void checkFunctionCallException(String functionName, Type[] types) {
		if (!checkFunctionCall(functionName, types)) {
			throw new SemanticException("Calling function not declared: " + functionName + " " + Arrays.toString(types));
		}
		
		addCode(labels+": ADD SP, SP, #size");
		addCode(labels+8+": ST *ST, #" + labels+32);
		addCode(labels+16+": BR " + functionName);
		addCode(labels +32+ ":");
		addCode(labels+40+": SUB SP, SP, #size");
		labels= labels+56;
	}
	
	public boolean checkFunctionName(String functionName) {
		Function f = cProgram.getFunctions().get(functionName);
		return f != null;
	}
	
	public boolean checkFunctionCall(String functionName) {
		Function f = cProgram.getFunctions().get(functionName);
		return f != null && f.getParameterTypes().length == 0;
	}
	
	public boolean checkFunctionCall(String functionName, Type[] types) {
		Function f = cProgram.getFunctions().get(functionName);
		if (f != null && f.getParameterTypes().length == types.length) {
			for (int i = 0 ; i < types.length ; i++) {
				if (!(types[i].getName().equals(f.getParameterTypes()[i].getName())))
					return false;
			}
			return true;
		}
		return false;
	}
	
	public void checkReturnedType(Object e) {
		Type typeToCheck;
		if (e instanceof Function)
			typeToCheck = ((Function)e).getReturnType();
		else
			typeToCheck = ((Expression) e).getType();
		
		
		Function f = null;
		if (scopeStack.peek() instanceof Function) {
			f = (Function) scopeStack.peek();
		} else {
			for (int i = scopeStack.size() - 1 ; i >= 0 ; i--) {
				if (scopeStack.get(i) instanceof Function) {
					f = (Function) scopeStack.get(i);
					break;
				}
			}
		}
		
		if (f == null)
			throw new SemanticException("Checking return type without function");
		
		if (!f.getReturnType().equals(typeToCheck)) {
			throw new SemanticException("Wrong return type: " + f.getReturnType() + " and " + typeToCheck);
		}
		
		f.setReturnedType(typeToCheck);
	}
	
	private boolean checkIsNumber(Type t) {
		return t.equals(new Type("int")) || t.equals(new Type("float"));
	}
	
	public Expression getExpressionForOperation(Operation op, Expression e1, Expression e2) {
		
		switch (op) {
		case AND_OP:
		case OR_OP:
			if (e1.getType().equals(new Type("int")))
				return new Expression(new Type("int")); // OK
		case EQ_OP:
		case GE_OP:
		case LE_OP:
		case LESS_THAN:
		case MORE_THAN:
		case NE_OP:
			if (checkIsNumber(e1.getType()) && checkIsNumber(e2.getType()) ||
					e1.getType().equals(e2.getType()))
				return new Expression(new Type("int"));
		case MINUS:
		case MULT:
		case PERC:
		case PLUS:
			generateADDCode();
		case DIV:
			if (checkIsNumber(e1.getType()) && checkIsNumber(e2.getType()))
				return new Expression(e1.getType());
		}
		throw new SemanticException("Illegal Operation between " + e1.getType() + " and " + e2.getType());
	}
	
	public Expression getExpressionType(Expression e1) {
		return new Expression(e1.getType());
	}
	
	//TODO RUAN REIS
	
	private int labels = 100;
	private String assemblyCode = initAssemblyCode();
	
	private int register = -1; 
	private Register[] registers = Register.values();
	
	private String initAssemblyCode(){
		return "100: LD SP, 1000\n";
	}

	public void generateADDCode(){
		labels += 8;
		
		Register one = registers[register - 1];
		Register two = registers[register];
		
		register++;
		Register result = registers[register];
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}
	
	public void generateLDCode(Expression expression){
		register++;
		labels += 8;
		addCode(labels + ": LD " + registers[register] + ", " + expression.getAssemblyValue());
	}
	
	public void generateSTCode(Variable variable){
		labels += 8;
		addCode(labels + ": ST " + variable.getName() + ", "+ registers[register]);
		this.register = -1;
	}
	
	public void addCode(String assemblyString) {
		assemblyCode += assemblyString + "\n";
	}
	
	public String getAssemblyCode(){
		return assemblyCode;
	}
}
