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
        addVisit("MethodDeclaration",this::methodDeclVisit);
        addVisit("MethodBody",this::methodBodyVisit);
        addVisit("VarDeclaration",this::varDeclarationVisit);

    }

    public String getCode() {
        return ollirCode.toString();
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
        var methodSignature=methodDecl.get("name");
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

    private Integer varDeclarationVisit(JmmNode varDecl, Integer dummy){
        String name=varDecl.get("name");
        String type= varDecl.getJmmChild(0).get("name");
        if(varDecl.getJmmParent().getKind().equals("ClassDeclaration")){
            ollirCode.append(".field ");
            ollirCode.append(name).append(".");

            OllirUtils.varDeclaration(varDecl,type);
        }
        else{
            ollirCode.append(name).append(".");
            OllirUtils.varDeclaration(varDecl,type);
        }

        ollirCode.append(";");
        return 0;
    }
    private Integer methodBodyVisit(JmmNode methodBody, Integer dummy){
    //TODO::
       String methodName= methodBody.getJmmParent().get("name");

        for (JmmNode statement : methodBody.getChildren()) {
            switch (statement.getKind()) {
                //childrens name of methodBody
                //for this checkpoint we dont need to implement if and else and while statement
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
