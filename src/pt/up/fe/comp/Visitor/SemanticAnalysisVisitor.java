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

        Type type = null;

        if(child.getKind().equals("EETrue") || child.getKind().equals("EEFalse")){
            type = new Type("boolean", false);
        }
        else if(child.getKind().equals("EEIdentifier")){
            if(!SemanticAnalysisUtils.EEIdentifierExists(method,child,analysis)){
                analysis.newReport(child, "Variable for return value not defined.");
                return;
            }
            else{
                Type typeTemp =  method.getLocalVariable(child.get("name")).getType();
                type = new Type(typeTemp.getName(),typeTemp.isArray());
            }
        }
        else if(child.getKind().equals("EEInt")){
            type = new Type("int", false);
        }
        else {
            type = SemanticAnalysisUtils.evaluateExpression(method, child, analysis);
        }

        if (type == null) {
            analysis.newReport(child, "Return type doesn't match expectation.");
            return;
        }
        if ( !(type.getName().equals(method.getReturnType().getName()) && (type.isArray() == method.getReturnType().isArray()))) {
            analysis.newReport(child, "Return type doesn't match expectation. Should be" + method.getReturnType());
        }

    }

    public void visitMethodBody(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        for (JmmNode child : children) {
            if(child.getKind().equals("VarDeclaration")) visitVarDeclaration(method, child, analysis); //OK but
            if(child.getKind().equals("Assignment")) visitAssignment(method, child, analysis);
            if(child.getKind().equals("WhileStatement")) visitWhileStatement(method, child, analysis);
            if(child.getKind().equals("IfStatement")) visitIfStatement(method, child, analysis);
            if(child.getKind().equals("Call")) SemanticAnalysisUtils.evaluateCall(method, child, analysis);
        }
    }

    public void visitVarDeclaration(JmmMethod method, JmmNode node, Analysis analysis){
        System.out.println("visiting var dec");
        System.out.println(method);
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

        //evaluate condition
        SemanticAnalysisUtils.evaluatesToBoolean(method, leftChild, analysis);
        //evaluate body
        visitMethodBody(method, rightChild, analysis);
    }

    public void visitIfStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        for (JmmNode child : children) {
            if (child.getKind().equals("IfCondition"))
                SemanticAnalysisUtils.evaluatesToBoolean(method, child, analysis);
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
        if(leftType==null || rightType == null)
            return;

        //Left é super e right é a propria classe symbol table gets super e class

        //left e right sao ambas importadas getImports na symbbol tabel . contains left and right name

        //tem de facto o mesmo tipo(ver se o nome do type é igual e se o is array tambem é igual

        //senao erro


    }


}
