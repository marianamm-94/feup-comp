package pt.up.fe.comp.SymbolTable;

import java.util.*;


import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;


public class JmmSymbolTable implements SymbolTable {
    private final List<String> imports = new ArrayList<>();
    private String className;
    private String superClassName;
    private final Map<String, Symbol> fields = new HashMap<>();
    private final Map<String, JmmMethod> methods = new HashMap<>();


    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superClassName;
    }

    @Override
    public List<Symbol> getFields() {
        return new ArrayList<>(fields.values());
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(methods.keySet());
    }

    @Override
    public Type getReturnType(String methodSignature) {
        return methods.get(methodSignature).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodSignature) {
        return methods.get(methodSignature).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodSignature) {
        return methods.get(methodSignature).getLocalVariables();
    }

    public boolean hasMethod(String methodSignature){
        return methods.containsKey(methodSignature);
    }

    public void addImport(String newImport){
        imports.add(newImport);
    }

    public void setClassName(String className){
        this.className=className;
    }

    public void setSuper(String superClassName){
        this.superClassName=superClassName;
    }

    public void addField(Type type, String name){
        fields.put(name, new Symbol(type,name));
    }

    public void addMethod(String name,Type returnType, List<Symbol> parameters){
        JmmMethod method = new JmmMethod(name,returnType,parameters);
        methods.put(name,method);
    }

    public JmmMethod getMethod(JmmMethod method){
        return methods.getOrDefault(method.getIdentifier(), null);
    }

    public JmmMethod getMethodById(String id){
        return methods.get(id);
    }

    //HELP

    private List<String> parseMethodInfo(String info) {
        List<String> list = new ArrayList<>();

        int indexBeg = info.indexOf('(');
        list.add(info.substring(0, indexBeg));

        int indexEnd = info.indexOf(')');
        String param = info.substring(indexBeg + 1, indexEnd).trim();

        if (!param.equals("")) {
            String[] params = param.split(",");
            list.addAll(Arrays.asList(params));
        }

        return list;
    }

    public JmmMethod getMethodByInfo(String methodInfo) {
        return methods.get(methodInfo);
    }

    public JmmType returnFieldTypeIfExists(String field) {
        return (JmmType) fields.get(field).getType();
    }

    public JmmType hasImport(String identifierName) {
        for (String importName : getImports()) {
            String[] imports = importName.split("\\.");
            if (imports[imports.length - 1].equals(identifierName)) return new JmmType("Accepted", false);
        }
        return null;
    }

    public boolean fieldExists(Symbol symbol){
        return fields.getOrDefault(symbol.getName(), null) != null;
    }

    public void setSuperClassName(String super_class_name) {
        this.superClassName = super_class_name;
    }
}
