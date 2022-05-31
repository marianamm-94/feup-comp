package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

import java.util.List;

public class JasminLoops {

    public static String gotoInstruction(GotoInstruction instruction){
        return "goto " + instruction.getLabel() + "\n";
    }

    public static String branchInstruction(CondBranchInstruction instruction, Method method){

        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();

        Element leftElement = instruction.getOperands().get(0);
        Element leftRight = instruction.getOperands().get(1);




        return jasminCode.toString();
    }

    public static String evaluateBranchCondition(String leftInstruction, String rightInstruction, OperationType operationType, String label){
        StringBuilder jasminCode= new StringBuilder();

        //TODO
        if (operationType == OperationType.LTH) jasminCode.append();
        else if (operationType == OperationType.ANDB) jasminCode.append();
        else if (operationType == OperationType.NOTB) jasminCode.append();
        else if (operationType == OperationType.GTE) jasminCode.append();


        return jasminCode.toString();
    }

}
