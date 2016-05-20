package com.ccompiler.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.ccompiler.core.Case;
import com.ccompiler.core.Default;
import com.ccompiler.core.Expression;
import com.ccompiler.core.Function;
import com.ccompiler.core.Identifier;
import com.ccompiler.core.Operation;
import com.ccompiler.core.Program;
import com.ccompiler.core.Register;
import com.ccompiler.core.ScopedEntity;
import com.ccompiler.core.Switch;
import com.ccompiler.core.SwitchCase;
import com.ccompiler.core.Type;
import com.ccompiler.core.Variable;
import com.ccompiler.util.SemanticException;

public class Semantic {

	public static Syntatic parser;

	private static final Type[] BASIC_TYPES = new Type[] { new Type("int"), new Type("float"), new Type("double"),
			new Type("long"), new Type("char"), new Type("void"), new Type("string"), new Type("bool") };

	public static ArrayList<String> variaveis = new ArrayList<String>();
	public static ArrayList<String> valores = new ArrayList<String>();
	private static Semantic sAnalysis;

	
	private CodeGenerator codeGenerator = new CodeGenerator();

	public static Semantic getInstance() {
		if (sAnalysis == null)
			sAnalysis = new Semantic();
		return sAnalysis;
	}

	// Object Attributes

	private Program cProgram; // ...

	private Stack<ScopedEntity> scopeStack;

	private Semantic() {
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
		if(scoped instanceof Case){
			
			
		}
	}
	
	public void putBreakInCase(){
		if(scopeStack.peek() instanceof SwitchCase){
			
			System.out.println("Break find in case scope");
			Semantic.getInstance().getCodeGenerator().addOutSwitch();
			
		}else{
			System.out.println("Break find out of case scope");
		}
	}
	
	public void exitCaseScope(){
		ScopedEntity scoped = scopeStack.pop();
		if(scoped != null && scoped instanceof Case){
			System.out.println("GET OUT OF CASE");
			//getCodeGenerator().addCase((Case) scoped);
		}
	}
	
	public void incrementAtualScope(){
		if (scopeStack.peek() != null) {
			if(scopeStack.peek() instanceof Case){
				((Case)scopeStack.peek()).incrementSize();
			}
		}
	}

	public ScopedEntity getCurrentScope() {
		return scopeStack.peek();
	}

	public void addFunctionAndNewScope(Function f) {
		cProgram.checkOverload(f);
		cProgram.addFunction(f);
		createNewScope(f);
		getCodeGenerator().addFunctionAddress(f.getName());
	}

	
	public void createSwitch(Expression e){
		createNewScope(new Switch(e,  getCodeGenerator().allocateRegister()));
		Semantic.getInstance().getCodeGenerator().addSwitch((Switch)scopeStack.peek());
	}
	
	
	public void createCase(Expression e){
		System.out.println("CASE CREATED");
//		if(!(scopeStack.peek() instanceof SwitchCase)){
//			Register sentinel = getCodeGenerator().allocateRegister();
//			createNewScope(new Case(e, sentinel));
//		}else{
			createNewScope(new Case(e, ((SwitchCase) scopeStack.peek()).getSentinel() ));
		//}
		getCodeGenerator().addCase((Case) scopeStack.peek());
	}
	
	public void createDefault(Expression e){
//		if(!(scopeStack.peek() instanceof SwitchCase)){
//			Register sentinel = getCodeGenerator().allocateRegister();
//			createNewScope(new Default(e, sentinel));
//		}else{
			createNewScope(new Default(e, ((SwitchCase) scopeStack.peek()).getSentinel() ));
		//}
		getCodeGenerator().addCase((Case) scopeStack.peek());
	}
	
		
	/** Switch Case Semantic */
//	private Switch switchCase = new Switch();
//
//	public void checkSwitchCase(Expression e) {
//		
//		
//		this.switchCase.setExpression(e);
//		// TODO Ruan: resetar o switch depois de analisar e gerar código
//	}
//
//	public void addCaseToSwitch(Expression e) {
//		this.switchCase.addCase(new Case(e));
//	}

	public void addVariable(Variable v) {
		if (checkVariableNameCurrentScope(v.getName()))
			throw new SemanticException("Variable " + v.getName() + " already exists");

		if (scopeStack.peek() != null) {
			scopeStack.peek().addVariable(v);
		} else {
			cProgram.addVariable(v);
		}
	}
	
	public Variable getVariable(String name){
		
		if (scopeStack.isEmpty())
			return cProgram.getVariable().get(name);
		else
			return scopeStack.peek().getVariable().get(name);
	}
	
	public Identifier getIdentifier(String name) {
		if (!checkVariableNameAllScopes(name) && !checkFunctionName(name))
			throw new SemanticException("Identifier name doesn't exists: " + name);

		if (cProgram.getFunctions().get(name) != null)
			return cProgram.getFunctions().get(name);

		for (int i = scopeStack.size() - 1; i >= 0; i--)
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

			for (int i = 0; i < scopeStack.size() - 1; i++) {
				for (String vName : scopeStack.get(i).getVariable().keySet()) {
					variablesName.add(vName);
				}
			}
		}
		return variablesName.contains(name);
	}

	public boolean checkTypeExists(Type type) {
		for (int i = 0; i < BASIC_TYPES.length; i++)
			if (BASIC_TYPES[i].getName().equals(type.getName()))
				return true;

		for (int i = 0; i < scopeStack.size(); i++) {
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
		getCodeGenerator().generateCallFunction(functionName);
	}

	public void checkFunctionCallException(String functionName, Type[] types) {
		if (!checkFunctionCall(functionName, types)) {
			throw new SemanticException(
					"Calling function not declared: " + functionName + " " + Arrays.toString(types));
		}
		getCodeGenerator().generateCallFunction(functionName);
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
			for (int i = 0; i < types.length; i++) {
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
			typeToCheck = ((Function) e).getReturnType();
		else
			typeToCheck = ((Expression) e).getType();

		Function f = null;
		if (scopeStack.peek() instanceof Function) {
			f = (Function) scopeStack.peek();
		} else {
			for (int i = scopeStack.size() - 1; i >= 0; i--) {
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
		getCodeGenerator().StorageReturnedType(f, (Expression) e);
	}
	
	public Expression getExpressionForOperation(Operation op, Expression e1, Expression e2) {

		switch (op) {
		case AND_OP:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateMULCode();
			return new Expression(new Type("int"));
		case OR_OP:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateADDCode();
			return new Expression(new Type("int"));
		case EQ_OP:
			break;
		case GE_OP:
			break;
		case LE_OP:
			break;
		case LESS_THAN:
			break;
		case MORE_THAN:
			break;
		case NOT_OP:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateNOTCode();
			return new Expression(new Type("int"));
		case MINUS:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateSUBCode();
			return new Expression(new Type("int"));
		case MULT:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateMULCode();
			return new Expression(new Type("int"));
		case PERC:
			break;
		case PLUS:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateADDCode();
			return new Expression(new Type("int"));
		case DIV:
			getCodeGenerator().generateLDCode(e1);
			getCodeGenerator().generateLDCode(e2);
			getCodeGenerator().generateDIVCode();
			return new Expression(new Type("int"));
		}
		
		throw new SemanticException("Illegal Operation between " + e1.getType() + " and " + e2.getType());
	}

	public Expression getExpressionType(Expression e1) {
		return new Expression(e1.getType());
	}

	public CodeGenerator getCodeGenerator() {
		return codeGenerator;	
	}

	public void checkType(Variable v, Expression e){
		if(!v.getType().equals(e.getType()) && !( "int".equals(v.getType().getName()) && "char".equals(e.getType().getName()) ||  "int".equals(v.getType().getName()) && "char".equals(e.getType().getName()) ) )
			throw new SemanticException("Type error");
	}
}
