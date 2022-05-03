package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.ClassUnit;

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
            jasminCode.append(" ");
            if (field.isStaticField())
                jasminCode.append("static ");
            if (field.isFinalField())
                jasminCode.append("final ");
            jasminCode.append("'").append(field.getFieldName()).append("' ");
            String jasminType = JasminUtils.getJasminType(field.getFieldType());
            jasminCode.append(jasminType).append(" ");
            if (field.isInitialized()) {
                jasminCode.append(" = ");
                jasminCode.append(field.getInitialValue());
            }
            jasminCode.append("\n");

        }

    }

    private void addMethods() {

    }

}
