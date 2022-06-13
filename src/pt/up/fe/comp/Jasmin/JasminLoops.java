package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminLoops {

    public static String gotoInstruction(GotoInstruction instruction, Method method){
        return "goto " + instruction.getLabel() + "\n";
    }

    public static String branchInstruction(CondBranchInstruction binaryOpInstruction, Method method){

        System.out.println("entered branch instruction");

        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();

        Element firstElement = binaryOpInstruction.getLeftOperand();
        Element secondElement = binaryOpInstruction.getRightOperand();
        OperationType opType = binaryOpInstruction.getOperation().getOpType();
        String label = condBranchInstruction.getLabel();
        /String firstInstruction = JasminLoadStore.loadElement(firstElement,method.getVarTable());
        String secondInstruction = JasminLoadStore.loadElement(secondElement,method.getVarTable());

        System.out.println("GOT TO THIS POINT");

        jasminCode.append(getJasminInst(firstInstruction, secondInstruction, opType, label));

        return jasminCode.toString();
    }

    public static String getJasminInst(String leftInst, String rightInst, OperationType opType, String label) {
        StringBuilder jasminCode = new StringBuilder();

        if (opType == OperationType.LTH) {
            jasminCode.append(leftInst);
            jasminCode.append(rightInst);
            jasminCode.append("if_icmplt").append(label).append("\n");
        } else if (opType == OperationType.ANDB) {
            //TODO
        } else if (opType == OperationType.NOTB) {
            //TODO
        } else if ( opType == OperationType.GTE) {
            jasminCode.append(leftInst);
            jasminCode.append(rightInst);
            jasminCode.append("if_icmpge").append(label).append("\n");
        }

        return jasminCode.toString();
    }
}
