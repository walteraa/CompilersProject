package com.ccompiler.analysis;

import java.util.HashMap;
import java.util.Map;

import com.ccompiler.core.Case;
import com.ccompiler.core.Expression;
import com.ccompiler.core.Function;
import com.ccompiler.core.Register;
import com.ccompiler.core.Type;
import com.ccompiler.core.Variable;

public class CodeGenerator {

	private int labels;
	private int register;
	private String assemblyCode;
	private Register[] registers;
	private Map<String, Integer> functionAddres;

	public CodeGenerator() {
		this.labels = 100;
		this.register = -1;
		this.registers = Register.values();
		this.assemblyCode = initAssemblyCode();
		this.functionAddres = new HashMap<String, Integer>();
	}

	private String initAssemblyCode() {
		return "100: LD SP, 2000\n";
	}

	public void assignmentDeclararion(Variable var, Object obj) {
		if (obj instanceof Expression) {
			generateLDCode((Expression) obj);
			generateSTCode(var);
		}

		if (obj instanceof Function) {
			Function f = (Function) obj;
			generateLDCode(new Expression(f.getName()));
			generateSTCode(var);
		}
	}

	public void generateADDCode() {
		labels += 8;

		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}

	public void generateADDCode(Register result, Register one, Register two) {
		labels += 8;
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}

	public void generateADDCode(Register result, Register one, Expression exp) {
		labels += 8;
		addCode(labels + ": ADD " + result + ", " + one + ", #" + exp.getAssemblyValue());
	}

	public void generateSUBCode() {
		labels += 8;

		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": SUB " + result + ", " + one + ", " + two);
	}

	public void generateSUBCode(Register result, Register one, Expression exp) {
		labels += 8;
		addCode(labels + ": SUB " + result + ", " + one + ", #" + exp.getAssemblyValue());
	}

	public void generateMULCode() {
		labels += 8;

		Register one = registers[register - 1];
		Register two = allocateRegister();

		register++;
		Register result = allocateRegister();
		addCode(labels + ": MUL " + result + ", " + one + ", " + two);
	}

	public void generateMULCode(Register result, Register one, Expression exp) {
		labels += 8;
		addCode(labels + ": MUL " + result + ", " + one + ", #" + exp.getValue());
	}

	public void generateNOTCode() {
		generateBEQZCode(4);
		generateMULCode(Register.R2, Register.R1, new Expression(new Type("int"), "-1"));
		generateADDCode(Register.R1, Register.R1, Register.R2);
		generateBRCode(2);
		generateADDCode(Register.R1, Register.R1, new Expression(new Type("int"), "1"));
	}

	public void generateBEQZCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;

		Register current = allocateRegister();
		addCode(labels + ": BEQZ " + current + ", " + jump);
	}

	public void generateBRCode(int br) {
		labels += 8;
		int jump = (br * 8) + labels;
		addCode(labels + ": BR " + jump);
	}

	public void generateLDCode(Expression expression) {
		if (expression.getAssemblyValue() != null) {
			register++;
			labels += 8;
			addCode(labels + ": LD " + allocateRegister() + ", " + expression.getAssemblyValue());
		}
	}

	public void generateSTCode(Variable variable) {
		labels += 8;
		addCode(labels + ": ST " + variable.getName() + ", " + allocateRegister());
		this.register = -1;
	}

	public void generateSTCode(Register one, Expression exp) {
		labels += 8;
		addCode(labels + ": ST " + one + ", " + exp.getAssemblyValue());
		this.register = -1;
	}

	public void generateSTCode(Expression exp) {
		labels += 8;
		addCode(labels + ": ST " + exp.getAssemblyValue() + ", " + allocateRegister());
		this.register = -1;
	}

	public void addCode(String assemblyString) {
		assemblyCode += assemblyString + "\n";

		System.out.println("\n ############################################### \n");
		System.out.println(getAssemblyCode());
		System.out.println("\n ############################################### \n");
	}

	public void addSwitch(Expression e) {
		if (e.getAssemblyValue() != null) {
			register++;
			labels += 8;
			addCode(labels + ": LD " + allocateRegister() + ", " + e.getAssemblyValue());
		}
	}

	public void addCase(Case c) {
		System.out.println("Size of Case:" + c.getSize());
		register++;
		// int startLabel = labels;
		labels += 8;
		addCode(labels + ": LD " + allocateRegister() + ", " + c.getExpression().getAssemblyValue());
		labels += 8;
		addCode(labels + ": SUB " + allocateRegister() + ", " + allocateRegister() + ", " + registers[register - 1]);
		labels += 8;
		addCode(labels + ": BLTZ " + allocateRegister() + ", " + (labels + 8 + (8 * c.getSize())));
		labels += 8;
		addCode(labels + ": BGTZ " + allocateRegister() + ", " + (labels + c.getSize() * 8));
		register--;
	}

	public void generateCallFunction(String functionName) {
		Expression blockSize = new Expression("size");
		Integer addressFunction = functionAddres.get(functionName);

		generateADDCode(Register.SP, Register.SP, blockSize);

		int jump = (3 * 8) + labels;
		generateSTCode(Register._SP, new Expression(new Type("int"), Integer.toString(jump)));
		generateBRCode(addressFunction);
		generateSUBCode(Register._SP, Register.SP, blockSize);
	}

	public void generateBRCode(Integer address) {
		labels += 8;
		addCode(labels + ": BR " + address);
	}

	public void generateBRCode(Register register) {
		labels += 8;
		addCode(labels + ": BR " + register);
	}

	public void generateHalt() {
		labels += 8;
		addCode(labels + ": halt");
	}

	public String getAssemblyCode() {
		return assemblyCode;
	}

	public void addFunctionAddress(String name) {
		labels += 300;
		functionAddres.put(name, labels + 8);
		addCode("\n");
	}

	public void StorageReturnedType(Function function, Expression returnedExpression) {
		if (returnedExpression.getValue() != null) {
			generateLDCode(returnedExpression);
			generateSTCode(new Expression(function.getName()));
			generateHalt();
		} else {
			if (returnedExpression.getName() != null) {
				generateLDCode(returnedExpression);
				generateSTCode(new Expression(function.getName()));
			} else {
				generateSTCode(new Expression(function.getName()));
			}
			generateBRCode(Register._SP);
		}
	}
	
	private Register allocateRegister(){
		try {
			Register allocated = registers[register]; 
			return allocated;
		} catch (Exception e) {
			register++; 
			return allocateRegister();
		}
	}
}
