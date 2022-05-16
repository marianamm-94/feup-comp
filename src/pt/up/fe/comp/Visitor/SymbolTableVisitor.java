package pt.up.fe.comp.Visitor;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class SymbolTableVisitor extends PreorderJmmVisitor<Boolean, Boolean> {
    private final JmmSymbolTable symbolTable;
    private final List<Report> reports;

    public SymbolTableVisitor() {
        this.symbolTable = new JmmSymbolTable();
        this.reports = new ArrayList<>();
        addVisit("ClassDeclaration", this::visitClass);
        addVisit("ImportDeclaration", this::visitImport);
        addVisit("MainDeclaration", this::visitMainDeclaration);
        addVisit("OtherMethodDeclaration", this::visitOtherMethodDeclaration);
    }

    public JmmSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Report> getReports() {
        return reports;
    }

    public boolean visitClass(JmmNode node, Boolean dummy) {
        String className = node.get("name");
        symbolTable.setClassName(className);
        JmmNode child = node.getJmmChild(0);
        if (child.getKind().equals("ClassExtend"))
            symbolTable.setSuper(child.get("name"));

        for (JmmNode varNode : node.getChildren()) {
            if (varNode.getKind().equals("VarDeclaration")) {
                JmmNode typeNode = varNode.getJmmChild(0);
                Type type = new Type(typeNode.get("name"), Boolean.parseBoolean(typeNode.get("isArray")));
                symbolTable.addField(type, varNode.get("name"));
            }
        }
        return true;
    }

    public boolean visitImport(JmmNode node, Boolean dummy) {
        StringBuilder importString = new StringBuilder(node.get("name"));
        for (JmmNode child : node.getChildren()) {
            importString.append(child.get("name"));
            for (JmmNode importNode : child.getChildren()) {
                importString.append(".");
                importString.append(importNode.get("name"));
            }
        }
        symbolTable.addImport(importString.toString());
        return true;
    }

    public boolean visitMainDeclaration(JmmNode node, Boolean dummy) {
        String paramName = node.get("name");
        List<Symbol> param = new ArrayList<Symbol>();
        JmmMethod method;

        param.add(new Symbol(new Type("String", true), paramName));
        Type returnType = new Type("void", false);

        method = new JmmMethod("main",returnType, param);
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("MethodBody")) {

                for (JmmNode childMethod : child.getChildren()) {

                    if (childMethod.getKind().equals("VarDeclaration")) {
                        Type type = new Type(childMethod.getJmmChild(0).get("name"), Boolean.parseBoolean(childMethod.getJmmChild(0).get("isArray")));
                        method.addLocalVariables(type, childMethod.get("name"));
                    }
                }
            }
        }
        symbolTable.addMethod(method);

        return true;
    }

    public boolean visitOtherMethodDeclaration(JmmNode node, Boolean dummy) {
        List<Symbol> param = new ArrayList<Symbol>();
        String methodName = node.get("name");
        Type returnType;
        JmmMethod method;
        if (node.getJmmChild(0).getKind().equals("Type"))
            returnType = new Type(node.getJmmChild(0).get("name"), Boolean.parseBoolean(node.getJmmChild(0).get("isArray")));
        else
            returnType = new Type("void", false);

        for (JmmNode child : node.getChildren()) {

            if (child.getKind().equals("Argument")) {
                String paramName = child.get("name");
                Type paramType = new Type(child.getJmmChild(0).get("name"), Boolean.parseBoolean(child.getJmmChild(0).get("isArray")));
                param.add(new Symbol(paramType, paramName));

            }
        }
        method = new JmmMethod(methodName, returnType, param);
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("MethodBody")) {

                for (JmmNode childMethod : child.getChildren()) {

                    if (childMethod.getKind().equals("VarDeclaration")) {
                        Type type = new Type(childMethod.getJmmChild(0).get("name"), Boolean.parseBoolean(childMethod.getJmmChild(0).get("isArray")));
                        method.addLocalVariables(type, childMethod.get("name"));
                    }
                }
            }
        }
        symbolTable.addMethod(method);
        return true;
    }
}
