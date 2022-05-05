package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminOperand {
    public  String getOperand(Instruction instruction){
        switch (instruction.getInstType()){
            case BINARYOPER:
                return addBinaryOper((BinaryOpInstruction) instruction);
            case NOPER:
                return addNoOper((SingleOpInstruction) instruction);
            case GETFIELD:
                return addGetField((GetFieldInstruction) instruction);
            case CALL:
                return addCall((CallInstruction) instruction);

        }
        throw new NotImplementedException(instruction.getInstType());
    }

    private String addCall(CallInstruction instruction) {
        return "";
    }

    private String addGetField(GetFieldInstruction instruction) {
        return "";
    }

    private String addNoOper(SingleOpInstruction instruction) {
        return "";
    }

    private String addBinaryOper(BinaryOpInstruction instruction) {
        Element leftElem = instruction.getLeftOperand();
        Element rightElem = instruction.getRightOperand();
        return "";
    }

}
