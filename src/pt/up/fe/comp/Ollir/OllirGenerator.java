package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.stream.Collectors;

public class OllirGenerator extends AJmmVisitor<Integer,Integer> {

    private final StringBuilder ollirCode;
    private final SymbolTable symbolTable;

    public OllirGenerator(SymbolTable symbolTable) {
        this.ollirCode = new StringBuilder();
        this.symbolTable=symbolTable;

        addVisit("Program",this::programVisit);
        addVisit("ClassDeclaration",this::classDeclarationVisit);
        addVisit("MethodDeclaration",this::methodDeclVisit);

    }

    private Integer programVisit(JmmNode program, Integer dummy){
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
            ollirCode.append(" extends").append(superClass);

        ollirCode.append("{\n");

        for(var child : classDecl.getChildren())
            visit(child);

        ollirCode.append("}\n");
        return 0;
    }


    private Integer methodDeclVisit(JmmNode methodDecl, Integer dummy){
        var methodSignature=methodDecl.getJmmChild(1).get("name");
        ollirCode.append(".method public ");
        if(methodDecl.getKind().equals("MainDeclaration")){
            ollirCode.append("static ");
            ollirCode.append("main( ");
        }
        ollirCode.append(methodSignature);

        var params =symbolTable.getParameters(methodSignature);

       var paramCode= params.stream()
               .map(symbol -> OllirUtils.getCode(symbol))
               .collect(Collectors.joining(", "));

       ollirCode.append(paramCode);
       ollirCode.append(").");
       ollirCode.append(OllirUtils.getCode(symbolTable.getReturnType(methodSignature)));

        ollirCode.append("{\n");

        int lastParam =OllirUtils.getLastParamIndex(methodDecl);

        var stmts=methodDecl.getChildren().subList(lastParam+1,methodDecl.getNumChildren());

        for(var stmt : stmts)
            visit(stmt);

        ollirCode.append("}\n");

        return 0;
    }

}
