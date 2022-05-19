package pt.up.fe.comp.Visitor;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class SymbolTableVisitor extends PreorderJmmVisitor<Analysis, Boolean>  {

    public SymbolTableVisitor() {
        addVisit("ClassDeclaration", this::visitClass);
        addVisit("ImportDeclaration", this::visitImport);
        addVisit("MainDeclaration", this::visitMainDeclaration);
        addVisit("OtherMethodDeclaration", this::visitOtherMethodDeclaration);
    }

    public boolean visitClass(JmmNode node, Analysis analysis){
        String className=node.get("name");
<<<<<<< HEAD
        symbolTable.setClassName(className);
        JmmNode child=node.getJmmChild(0);
        if(child.getKind().equals("ClassExtend"))
         symbolTable.setSuper(child.get("name"));

        for(JmmNode varNode: node.getChildren()){
           if( varNode.getKind().equals("VarDeclaration")){
                JmmNode typeNode =varNode.getJmmChild(0);
                Type type = new Type(typeNode.get("name"),Boolean.parseBoolean(typeNode.get("isArray")));
                symbolTable.addField(type, varNode.get("name"));
           }
=======
        analysis.getSymbolTable().setClassName(className);
        JmmNode child=node.getJmmChild(0);
        if(child.getKind().equals("ClassExtend"))
            analysis.getSymbolTable().setSuper(child.get("name"));

        for(JmmNode varNode: node.getChildren()){
            if( varNode.getKind().equals("VarDeclaration")){
                JmmNode typeNode =varNode.getJmmChild(0);
                Type type = new Type(typeNode.get("name"),Boolean.parseBoolean(typeNode.get("isArray")));
                analysis.getSymbolTable().addField(type, varNode.get("name"));
            }
>>>>>>> visitors3
        }
        return true;
    }
    public boolean visitImport(JmmNode node, Analysis analysis){
        StringBuilder importString = new StringBuilder(node.get("name"));
        for (JmmNode child : node.getChildren()){
            importString.append(child.get("name"));
            for(JmmNode importNode: child.getChildren()){
                importString.append(".");
                importString.append(importNode.get("name"));
            }
        }
<<<<<<< HEAD
        symbolTable.addImport(importString.toString());
        return true;
    }
    public boolean visitMainDeclaration(JmmNode node, Boolean dummy){
=======
        analysis.getSymbolTable().addImport(importString.toString());
        return true;
    }
    public boolean visitMainDeclaration(JmmNode node, Analysis analysis){
>>>>>>> visitors3
        String paramName=node.get("name");
        List<Symbol> param=new ArrayList<Symbol>();
        param.add(new Symbol(new Type("String", true),paramName));
        Type type = new Type("void",false);
<<<<<<< HEAD
        symbolTable.addMethod("main",type,param);
        return true;
    }
    public boolean visitOtherMethodDeclaration(JmmNode node, Boolean dummy){
        List<Symbol> param=new ArrayList<Symbol>();
        String methodName=node.get("name");
=======
        analysis.getSymbolTable().addMethod("main",type,param);
        return true;
    }
    public boolean visitOtherMethodDeclaration(JmmNode node, Analysis analysis){
        List<Symbol> param=new ArrayList<Symbol>();
        String methodName=node.get("name");

>>>>>>> visitors3
        Type returnType;
        if( node.getJmmChild(0).getKind().equals("Type"))
            returnType = new Type(node.getJmmChild(0).get("name"),Boolean.parseBoolean( node.getJmmChild(0).get("isArray")));
        else
            returnType=new Type("void", false);
<<<<<<< HEAD
           
        for(JmmNode child : node.getChildren()){
           if(child.getKind().equals("Argument")){
                String paramName=child.get("name");
                Type paramType = new Type(child.getJmmChild(0).get("name"),Boolean.parseBoolean( child.getJmmChild(0).get("isArray")));
                param.add(new Symbol(paramType, paramName));
           }

        }
        symbolTable.addMethod(methodName, returnType, param);
=======

        for(JmmNode child : node.getChildren()){
            if(child.getKind().equals("Argument")){
                String paramName=child.get("name");
                Type paramType = new Type(child.getJmmChild(0).get("name"),Boolean.parseBoolean( child.getJmmChild(0).get("isArray")));
                param.add(new Symbol(paramType, paramName));
            }

        }
        analysis.getSymbolTable().addMethod(methodName, returnType, param);
>>>>>>> visitors3
        return true;
    }
}
