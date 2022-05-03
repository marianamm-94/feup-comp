package pt.up.fe.comp.Jasmin;

import org.eclipse.jgit.lib.RefRename;
import org.specs.comp.ollir.AccessModifiers;
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
                return "[I";
            case "INT32":
                return "I";
            case "OBJECTREF":
                return classUnit.getClassName();
            case "BOOLEAN":
                return "Z";
        }

        return "";

    }
}
