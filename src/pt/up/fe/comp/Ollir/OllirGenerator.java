package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.stream.Collectors;

public class OllirGenerator extends AJmmVisitor<Integer,Integer> {

    private final StringBuilder ollirCode;
    public final SymbolTable symbolTable;

    public OllirGenerator(SymbolTable symbolTable) {
        this.ollirCode = new StringBuilder();
        this.symbolTable=symbolTable;
        addVisit("Program",this::programVisit);
        addVisit("ClassDeclaration",this::classDeclarationVisit);
        addVisit("MainDeclaration", this::mainDeclarationVisit);
        addVisit("OtherMethodDeclaration", this::otherMethodDeclarationVisit);
        //addVisit("MethodDeclaration",this::methodDeclVisit);
        addVisit("MethodBody",this::methodBodyVisit);
        addVisit("VarDeclaration",this::varDeclarationVisit);

    }

    public String getCode() {
        return ollirCode.toString();
    }

    private Integer programVisit(JmmNode program, Integer dummy){
        //TODO:: IMPORTS WITH .
        for(var importString: symbolTable.getImports()){
            ollirCode.append("import ").append(importString).append(";\n");
        }
        for(var child : program.getChildren())
            visit(child);

        return 0;
    }

    private Integer classDeclarationVisit(JmmNode classDecl, Integer dummy){
        ollirCode.append("public ").append(symbolTable.getClassName());

        var superClass = symbolTable.getSuper();

        if(superClass!=null)
            ollirCode.append(" extends ").append(superClass);

        ollirCode.append("{\n");

        for(var child : classDecl.getChildren()){
            if(child.getKind().equals("VarDeclaration")){
                visit(child);
            }
        }

        ollirCode.append(".construct ").append(symbolTable.getClassName());
        ollirCode.append("().V{\n");
        ollirCode.append("invokespecial(this, \"<init>\").V;\n");
        ollirCode.append("}\n");

        for(var child : classDecl.getChildren()){
            if(!child.getKind().equals("VarDeclaration")){
                visit(child);
            }
        }

        ollirCode.append("}\n");
        return 0;
    }
    private Integer mainDeclarationVisit(JmmNode mainMethodDecl, Integer dummy){
        var main="main";

        ollirCode.append(".method public ");
        ollirCode.append("static ");
        ollirCode.append("main(");

        var params =symbolTable.getParameters(main);

        var paramCode= params.stream()
                .map(symbol -> OllirUtils.getCode(symbol))
                .collect(Collectors.joining(", "));

        ollirCode.append(paramCode);
        ollirCode.append(").");
        ollirCode.append(OllirUtils.getCode(symbolTable.getReturnType(main)));

        ollirCode.append("{\n");

        int lastParam =OllirUtils.getLastParamIndex(mainMethodDecl);

        var stmts=mainMethodDecl.getChildren().subList(lastParam+1,mainMethodDecl.getNumChildren());

        for(var stmt : stmts)
            visit(stmt);

        ollirCode.append("}\n");

        return 0;
    }

    private Integer otherMethodDeclarationVisit(JmmNode otherMethodDecl, Integer dummy){
        var methodName=otherMethodDecl.get("name");

        ollirCode.append(".method public ");
        ollirCode.append( methodName);
        ollirCode.append("(");

        var params =symbolTable.getParameters( methodName);

        var paramCode= params.stream()
                .map(symbol -> OllirUtils.getCode(symbol))
                .collect(Collectors.joining(", "));

        ollirCode.append(paramCode);
        ollirCode.append(").");
        ollirCode.append(OllirUtils.getCode(symbolTable.getReturnType( methodName)));

        ollirCode.append("{\n");
        //int lastParam =OllirUtils.getLastParamIndex(otherMethodDecl);
        for(JmmNode child: otherMethodDecl.getChildren()){
            System.out.println(child.getKind());
            if(child.getKind().equals("MethodBody")){
                visit(child);
            }
        }

        //var stmts=otherMethodDecl.getChildren().subList(lastParam+1,otherMethodDecl.getNumChildren());



        ollirCode.append("}\n");

        return 0;
    }

    private Integer varDeclarationVisit(JmmNode varDecl, Integer dummy){
        String name=varDecl.get("name");
        String type= varDecl.getJmmChild(0).get("name");
        if(varDecl.getJmmParent().getKind().equals("ClassDeclaration")){
            ollirCode.append(".field ");
            ollirCode.append(name).append(".");

            if(varDecl.getJmmChild(0).get("isArray").equals("True")){
                ollirCode.append("array.");
                ollirCode.append(OllirUtils.getOllirType(type));
            }else{
                ollirCode.append(OllirUtils.getOllirType(type));
            }
            ollirCode.append(";").append("\n");
        }

        return 0;
    }
    private Integer methodBodyVisit(JmmNode methodBody, Integer dummy){
    //TODO::

       String methodName= methodBody.getJmmParent().get("name");
/*
       for(var child: methodBody.getChildren()){
           if(child.getKind().equals("VarDeclaration")){
               visit(child);
           }
       }
*/
        for (JmmNode statement : methodBody.getChildren()) {
            switch (statement.getKind()) {
                case ("Assignment"):
                    OllirAssignment.assignmentStatement(methodName, statement,symbolTable);
                    break;
                case ("Call"):
                    OllirCall.callStatement();
                    break;
                case ("VarDeclaration"):
                    visit(statement);
                    break;
            }
           ollirCode.append("\n");
        }
        return 0;
    }



}
