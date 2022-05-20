package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class SemanticAnalysisVisitor extends PreorderJmmVisitor<Analysis, Boolean> {

    public SemanticAnalysisVisitor(){
        addVisit("MainMethodDeclaration", this::visitMainMethod);
        addVisit("OtherMethodDeclaration",this::visitOtherMethod);
    }

    public Boolean visitMainMethod(JmmNode node, Analysis analysis){
        JmmNode child = node.getJmmChild(0);

        String methodName = node.get("name");
        JmmMethod method = analysis.getSymbolTable().getMethodById(methodName);
        visitMethodBody(method, child, analysis);
        return true;
    }

    public Boolean visitOtherMethod(JmmNode node, Analysis analysis){

        List<JmmNode> children = node.getChildren();

        String methodName = node.get("name");
        JmmMethod method = null;

        for (JmmNode child : children) {

            String childKind = child.getKind();

            if (childKind.equals("MethodBody")) { //method body (local variables)
                List<String> methodList = analysis.getSymbolTable().getMethods();
                method = analysis.getSymbolTable().getMethodById(methodName);
                if (!methodList.contains(methodName))
                    return false;
                visitMethodBody(method, child, analysis);
            } else if (child.getKind().equals("ReturnValue")) {
                visitReturnValue(method, child, analysis);
            }
        }

        return true;
    }

    public void visitReturnValue(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode child = node.getChildren().get(0);

        Type type = SemanticAnalysisUtils.getNodeType(method,child,analysis);

        if(type==null)
            return;

        String methodName=method.getName();
        Type typeMethod=analysis.getSymbolTable().getReturnType(methodName);

        if(child.getKind().equals("Call")){
            child.put("typeValue",typeMethod.getName());
            child.put("isArray",Boolean.toString(typeMethod.isArray()));
        }

        if(type.getName().equals(analysis.getSymbolTable().getClassName()) && typeMethod.getName().equals(analysis.getSymbolTable().getSuper()))
            return;
        if(analysis.getSymbolTable().getImports().contains(type.getName()) && analysis.getSymbolTable().getImports().contains(typeMethod.getName()))
            return;
        if(!SemanticAnalysisUtils.sameType(type,typeMethod))
            analysis.newReport(node,"Error on return: different types");

    }

    public void visitMethodBody(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        for (JmmNode child : children) {
            if(child.getKind().equals("VarDeclaration")) visitVarDeclaration(method, child, analysis); //OK but
            if(child.getKind().equals("Assignment")) visitAssignment(method, child, analysis);
            if(child.getKind().equals("WhileStatement")) visitWhileStatement(method, child, analysis);
            if(child.getKind().equals("IfStatement")) visitIfStatement(method, child, analysis);
            if(child.getKind().equals("Call")) visitCall(method, child, analysis);
        }
    }

    private void visitCall(JmmMethod method, JmmNode node, Analysis analysis) {
       SemanticAnalysisUtils.methodCall(method,node,analysis);
    }

    public void visitVarDeclaration(JmmMethod method, JmmNode node, Analysis analysis){
        String varName = node.get("name");
        JmmNode typeNode = node.getJmmChild(0);
        String varTypeStr = typeNode.get("name");
        boolean isArray = Boolean.parseBoolean(typeNode.get("isArray"));

        Type varType = new Type(varTypeStr, isArray);
        Symbol symbol = new Symbol(varType, varName );

        if(method == null){
            List<Symbol> symbols = analysis.getSymbolTable().getFields();
            for(Symbol symb: symbols){
                if(symb.getName().equals(varName)) {
                    analysis.newReport(node, "Variable " + symbol + " is already defined in the scope");
                    return;
                }
            }
        }
        else{
            List<Symbol> symbols = method.getLocalVariables();
            for(Symbol symb: symbols){
                if(symb.getName().equals(varName)) {
                    analysis.newReport(node, "Variable " + symbol + " is already defined in the scope");
                    return;
                }
            }
            method.addLocalVariable(varType, varName);
        }

    }

    public void visitWhileStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode leftChild = node.getJmmChild(0); //this is WhileCondition
        JmmNode rightChild = node.getJmmChild(1); //this is WhileBody

        if(leftChild.getKind().equals("Call")){
            leftChild.put("typeValue","boolean");
            leftChild.put("isArray","false");
        }

        //evaluate condition
        SemanticAnalysisUtils.evaluatesToBoolean(method, leftChild, analysis);
        //evaluate body
        visitMethodBody(method, rightChild, analysis);
    }

    public void visitIfStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        for (JmmNode child : children) {
            if (child.getKind().equals("IfCondition")){
                SemanticAnalysisUtils.evaluatesToBoolean(method, child, analysis);
                if(child.getJmmChild(0).getKind().equals("Call")){
                    child.getJmmChild(0).put("typeValue","boolean");
                    child.getJmmChild(0).put("isArray","false");
                }
            }
            else if (child.getKind().equals("IfBody")) visitMethodBody(method, child, analysis);
            else if (child.getKind().equals("ElseBody")) visitMethodBody(method, child, analysis);
        }
    }

    public void visitAssignment(JmmMethod method, JmmNode node, Analysis analysis) {
        if(node.getNumChildren()!=2){
            analysis.newReport(node,"Assignment doesn't have two children!");
        }
        JmmNode leftChild=node.getJmmChild(0);
        JmmNode rightChild=node.getJmmChild(1);

        if(!leftChild.getKind().equals("EEIdentifier")&& !leftChild.getKind().equals("Array"))
        {
            analysis.newReport(node,"Left child of assignment must be a variable or Array Index");
            return;
        }
        Type leftType = SemanticAnalysisUtils.getNodeType(method,leftChild,analysis);
        Type rightType = SemanticAnalysisUtils.getNodeType(method,rightChild,analysis);

        if(leftChild.getKind().equals("Call")){
            leftChild.put("typeValue",rightType.getName());
            leftChild.put("isArray",Boolean.toString(rightType.isArray()));
        }
        if(rightChild.getKind().equals("Call")){
            rightChild.put("typeValue",leftType.getName());
            rightChild.put("isArray",Boolean.toString(leftType.isArray()));
        }

        if(leftType==null || rightType == null)
            return;

        if(leftType.getName().equals(analysis.getSymbolTable().getSuper()) && rightType.getName().equals(analysis.getSymbolTable().getClassName())){
           return;
        }
       if(analysis.getSymbolTable().getImports().contains(leftType.getName()) && analysis.getSymbolTable().getImports().contains(rightType.getName()) ){
            return;
        }
        if(SemanticAnalysisUtils.sameType(leftType,rightType)==true){
            return;

        }else{
            analysis.newReport(node, "Invalid assignment children");
        }


    }


}