package com.ccompiler.analysis;

import com.ccompiler.core.Expression;
import com.ccompiler.core.Register;
import com.ccompiler.core.Type;
import com.ccompiler.core.Variable;

public class CodeGenerator {
	
	private int labels;
	private int register;
	private String assemblyCode;
	private Register[] registers ;
	
	public CodeGenerator(){
		this.labels = 100;
		this.register = -1;
		this.registers = Register.values();
		this.assemblyCode = initAssemblyCode();
	}
	
	private String initAssemblyCode(){
		//return "100: LD SP, 1000\n";
		return "";
	}
	
	public void assignmentDeclararion(Variable var, Expression exp){
		generateLDCode(exp);
		generateSTCode(var);
	}

	public void generateADDCode(){
		labels += 8;
		
		Register one = registers[register - 1];
		Register two = registers[register];
		
		register++;
		Register result = registers[register];
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}
	
	public void generateADDCode(Register result, Register one, Register two){
		labels += 8;
		addCode(labels + ": ADD " + result + ", " + one + ", " + two);
	}
	
	public void generateADDCode(Register result, Register one, Expression exp){
		labels += 8;
		addCode(labels + ": ADD " + result + ", " + one + ", #" + exp.getValue());
	}
	
	public void generateSUBCode(){
		labels += 8;
		
		Register one = registers[register - 1];
		Register two = registers[register];
		
		register++;
		Register result = registers[register];
		addCode(labels + ": SUB " + result + ", " + one + ", " + two);
	}
	
	public void generateMULCode(){
		labels += 8;
		
		Register one = registers[register - 1];
		Register two = registers[register];
		
		register++;
		Register result = registers[register];
		addCode(labels + ": MUL " + result + ", " + one + ", " + two);
	}
	
	public void generateMULCode(Register result, Register one, Expression exp){
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
	
	public void generateBEQZCode(int br){
		labels += 8;
		int jump =(br * 8) + labels;
		
		Register current = registers[register];
		addCode(labels + ": BEQZ " + current + ", " + jump);
	}
	
	public void generateBRCode(int br){
		labels += 8;
		int jump =(br * 8) + labels;
		addCode(labels + ": BR " + jump);
	}
	
	public void generateLDCode(Expression expression){
		if (expression.getAssemblyValue() != null) {
			register++;
			labels += 8;
			addCode(labels + ": LD " + registers[register] + ", " + expression.getAssemblyValue());
		}
	}
	
	public void generateSTCode(Variable variable){
		labels += 8;
		addCode(labels + ": ST " + variable.getName() + ", "+ registers[register]);
		this.register = -1;
	}
	
	public void addCode(String assemblyString) {
		assemblyCode += assemblyString + "\n";
		
		/**
		System.out.println("\n ############################################### \n");
		System.out.println(getAssemblyCode());
		System.out.println("\n ############################################### \n");*/
	}
	
	public String getAssemblyCode(){
		return assemblyCode;
	}
}
