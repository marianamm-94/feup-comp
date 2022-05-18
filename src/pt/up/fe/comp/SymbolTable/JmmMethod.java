package pt.up.fe.comp.SymbolTable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class JmmMethod {
    private String name;
    private Type returnType;
    private List<Symbol> parameters;
    private final HashMap<String, Symbol> localVariables = new HashMap<>();

    public JmmMethod(String name, Type returnType, List<Symbol> parameters){
        this.name=name;
        this.returnType=returnType;
        this.parameters=parameters;
        for(Symbol parameter: parameters)
            localVariables.put(parameter.getName(),parameter);
    }
    public String getName(){
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public List<Symbol> getLocalVariables() {
        return new ArrayList<>(localVariables.values());
    }

    private Type createType(String t) {

        boolean isArray = t.contains("[]");
        String type = t.replace("[]", "");

        return new Type(type, isArray);
    }

    public boolean equalsMethod(List<String> info) {
        if (info == null || info.size() < 1) return false;

        if ((info.size() - 1) != this.parameters.size()) return false;

        String name = info.get(0);
        if (!name.equals(this.name)) return false;

        int n_param = 0;
        for (int i = 1; i < info.size(); i++) {
            Type type = createType(info.get(i));
            if (!type.equals(this.parameters.get(n_param).getType())) return false;
            n_param++;
        }

        return true;
    }

    public Type returnTypeIfExists(String identifier) {
        return returnType;
    }

    public String getIdentifier() {

        List<String> parameter_types = new ArrayList<>();
        for(Symbol i: parameters){
            String parameterId = i.getType().getName() + (i.getType().isArray() ? "[]" : "");
            parameter_types.add(parameterId);
        }

        return String.join("-", name, String.join("-", parameter_types));
    }

    public void addLocalVariable(Type type, String name) {
        localVariables.put(name, new Symbol(type, name));
    }

    public Symbol getLocalVariable(String varName) {
        return localVariables.getOrDefault(varName, null);}

    public boolean localVariableExists(Symbol symbol){
        if(localVariables.getOrDefault(symbol, null) == null){
            return false;
        }
        return true;
    }

}
