package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

import java.util.HashMap;

public class JasminBuilder {

    private StringBuilder jasminCode;
    public static ClassUnit classUnit;

    public JasminBuilder(ClassUnit classUnit) {
        this.jasminCode = new StringBuilder();
        JasminBuilder.classUnit = classUnit;
    }

    public String build() {
        addClassName();
        addSuperClass();
        addFields();
        addMethods();

        return jasminCode.toString();
    }

    private void addClassName() {
        jasminCode.append(".class public ").append(classUnit.getClassName()).append("\n");
    }

    private void addSuperClass() {
        String superName;
        if (classUnit.getSuperClass() == null)
            superName = "java/lang/Object";
        else
            superName = classUnit.getSuperClass();
        jasminCode.append(".super ").append(superName).append("\n");
    }

    private void addFields() {
        for (var field : classUnit.getFields()) {
            jasminCode.append(".field ");
            String accessModifiers = JasminUtils.getAccessModifiers(field.getFieldAccessModifier());
            jasminCode.append(accessModifiers);
            if (field.isStaticField())
                jasminCode.append(" static");
            if (field.isFinalField())
                jasminCode.append(" final");
            jasminCode.append(" ").append(field.getFieldName()).append(" ");
            String jasminType = JasminUtils.getJasminType(field.getFieldType());
            jasminCode.append(jasminType);
            if (field.isInitialized()) {
                jasminCode.append(" = ").append(field.getInitialValue());
            }
            jasminCode.append("\n");
        }

    }

    private void addMethods() {

        for (var method : classUnit.getMethods()) {
            jasminCode.append("\n.method public ");
            if (method.isStaticMethod())
                jasminCode.append("static ");
            if (method.isFinalMethod())
                jasminCode.append("final ");

            if (method.isConstructMethod())
                jasminCode.append("<init>");
            else
                jasminCode.append(method.getMethodName());

            jasminCode.append("(");
            for (var param : method.getParams())
                jasminCode.append(JasminUtils.getJasminType(param.getType()));
            jasminCode.append(")").append(JasminUtils.getJasminType(method.getReturnType())).append("\n");

            jasminCode.append(".limit stack 99\n");
            jasminCode.append(".limit locals 99\n");
            for(var instruction : method.getInstructions()){
                 for(var label : method.getLabels(instruction))
                     jasminCode.append(label).append(":\n");
                 jasminCode.append(JasminUtils.addInstructions(instruction,method));
            }
            if(method.getReturnType().getTypeOfElement()==ElementType.VOID)
                jasminCode.append("return\n");
            jasminCode.append(".end method\n");
        }
    }


}
