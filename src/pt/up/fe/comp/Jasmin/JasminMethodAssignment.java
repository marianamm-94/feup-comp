package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

public class JasminMethodAssignment extends JasminBuilder{

    public JasminMethodAssignment (ClassUnit classUnit) {
        super(classUnit);
    }
    public static String getInstructionsAssign(AssignInstruction assignInstruction, Method method){
        Instruction rhs =assignInstruction.getRhs();
        Element lhs = assignInstruction.getDest();
        var table = method.getVarTable();

        StringBuilder jasminCode = new StringBuilder();

        jasminCode.append(JasminUtils.addInstructions(rhs,method));
        jasminCode.append(JasminLoadStore.storeElement(lhs,table));

    return jasminCode.toString();
    }

}
