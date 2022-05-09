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


}
