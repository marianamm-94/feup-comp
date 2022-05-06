package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

public class JasminGetField {
    public static String addGetField(GetFieldInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        Element first =instruction.getFirstOperand();
        Element second = instruction.getSecondOperand();
        var table = method.getVarTable();
        ClassUnit classUnit=JasminBuilder.classUnit;
        String name;
        String className=classUnit.getClassName();
        String type= JasminUtils.getJasminType(second.getType());
        if(className.equals("this"))
            name=className;
        else
            name=((Operand) first).getName();
        jasminCode.append(JasminLoadStore.loadElement(first,table)).append("getfield ").append(name).append("/").append(((Operand) second).getName()).append(" ").append(type);
        jasminCode.append("\n");
        return jasminCode.toString();
    }
}
