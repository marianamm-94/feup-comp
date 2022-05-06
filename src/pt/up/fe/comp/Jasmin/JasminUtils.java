package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminUtils {
    static ClassUnit classUnit;

    public static String getAccessModifiers(AccessModifiers accessModifier) {
        switch (accessModifier) {
            case PUBLIC:
                return "public";
            case PRIVATE:
                return "private";
            case DEFAULT:
                return "private";
            case PROTECTED:
                return "protected";
            default:
                return "Error Acess Modifiers";
        }
    }

    public static String getJasminType(Type type) {

        switch (type.getTypeOfElement()) {
            case ARRAYREF:
                return getJasminArrayType((ArrayType) type);
            case INT32:
                return "I";
            case OBJECTREF:
                return getJasminObjectType((ClassType) type);
            case BOOLEAN:
                return "Z";
            case VOID:
                return "V";
            case STRING:
                return "Ljava/lang/String;";

        }
       throw new NotImplementedException(type.getTypeOfElement());
    }

    private static String getJasminObjectType(ClassType type) {
        StringBuilder jasminCode = new StringBuilder();
        String className = type.getName();
        for (var imported : classUnit.getImports()) {
            if (imported.endsWith("." + className))
                jasminCode.append("L").append(imported.replace('.', '/')).append(";");
        }
        jasminCode.append("L").append(className).append(";");
        return jasminCode.toString();
    }

    private static String getJasminArrayType(ArrayType type) {
        StringBuilder jasminCode = new StringBuilder();
        jasminCode.append("[".repeat(type.getNumDimensions()));
        if(type.getTypeOfElements()==ElementType.INT32)
            jasminCode.append("[I");
        else
            jasminCode.append("Ljava/lang/String;");
        return jasminCode.toString();
    }

    public static String addInstructions(Instruction instruction, Method method){
        switch (instruction.getInstType()){
            case ASSIGN:
                return JasminMethodAssignment.getInstructionsAssign((AssignInstruction) instruction,method);
            case PUTFIELD:
                return JasminPutField.addPutField((PutFieldInstruction) instruction);
            case RETURN:
                return JasminReturn.returnInstructions((ReturnInstruction) instruction, method);
            case BINARYOPER:
                return  addBinaryOper((BinaryOpInstruction) instruction);
            case NOPER:
                return addNoOper((SingleOpInstruction) instruction);
            case GETFIELD:
                return JasminGetField.addGetField((GetFieldInstruction) instruction,method);
            case CALL:
                return JasminCall.addCall((CallInstruction) instruction);

        }
        throw new NotImplementedException(instruction.getInstType());
    }

    private static String addNoOper(SingleOpInstruction instruction) {
        //TODO::
        return "";
    }

    private static String addBinaryOper(BinaryOpInstruction instruction) {
        //TODO::
        return "";
    }

}
