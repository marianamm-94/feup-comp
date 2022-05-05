package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.AccessModifiers;
import org.specs.comp.ollir.ArrayType;
import org.specs.comp.ollir.ClassType;
import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Type;

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
                return "";
        }
    }

    public static String getJasminType(Type type) {

        switch (type.getTypeOfElement().toString()) {
            case "ARRAYREF":
                return getJasminArrayType((ArrayType) type);
            case "INT32":
                return "I";
            case "OBJECTREF":
                return getJasminObjectType((ClassType) type);
            case "BOOLEAN":
                return "Z";
            case "VOID":
                return "V";
            case "String":
                return "Ljava/lang/String;";

        }
        return "Error Jasmin Type";

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
        jasminCode.append("[".repeat(type.getNumDimensions())).append("I");
        return jasminCode.toString();
    }

}
