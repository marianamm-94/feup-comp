package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

public class JasminMethodAssignment extends JasminBuilder{

    public JasminMethodAssignment (ClassUnit classUnit) {
        super(classUnit);
    }
    public static String getInstructionsAssign(AssignInstruction assignInstruction, Method method){
        Instruction rhs =assignInstruction.getRhs();
        Element lhs = assignInstruction.getDest();
        Operand o = (Operand) assignInstruction.getDest();
        var table = method.getVarTable();
        int reg= table.get(o.getName()).getVirtualReg();

        String rhsOperand = new JasminOperand().getOperand(rhs);
        
    return "";
    }

}
