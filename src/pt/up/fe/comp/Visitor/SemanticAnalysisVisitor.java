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

        Type type;

        if(child.getKind().equals("EETrue") || child.getKind().equals("EEFalse")){
            type = new Type("boolean", false);
        }
        else if(child.getKind().equals("EEIdentifier")){
            if(!SemanticAnalysisUtils.EEIdentifierExists(method,child)){
                analysis.newReport(child, "Variable for return value not defined.");
                return;
            }
            else{
                type =  method.getLocalVariable(child.get("name")).getType();
            }
        }
        else if(child.getKind().equals("EEInt")){
            type = new Type("int", false);
        }
        else {
            type = SemanticAnalysisUtils.evaluateExpression(method, child, analysis, true);
        }

        if (type == null) {
            analysis.newReport(child, "Return type doesn't match expectation.");
            return;
        }
        else if(type.equals(new Type("Accept", false))) return;
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
        JmmNode assignee = node.getJmmChild(0);

        System.out.println("assign visit lasflksjf");

        List<JmmNode> children = node.getChildren();

        if (children.size() != 2) return; //check if there are two children
        if(children.get(0).getKind().equals("EEIdentifier")){  //if assigning to EEIdentifier
            if(!SemanticAnalysisUtils.EEIdentifierExists(method, assignee)){
                analysis.newReport(assignee, "Variable is not defined.");
            }
            //check if type matches
        }
        else if(children.get(0).getKind().equals("Array")){  //if assigning to Array
            //check for array and identifier
        }
        if(!children.get(1).getKind().equals("BinOp") && !children.get(1).getKind().equals("EENew")
        && !children.get(1).getKind().equals("Call") && !children.get(1).getKind().equals("Array") && !children.get(1).getKind().equals("EEIdentifier")
        && !children.get(1).getKind().equals("EEInt") && !children.get(1).getKind().equals("EETrue") && !children.get(1).getKind().equals("EEFalse"))
            return;


        Type leftOperandType = null;
        if(SemanticAnalysisUtils.EEIdentifierExists(method, children.get(0))){
            leftOperandType = method.getLocalVariable(children.get(0).get("name")).getType();
        }

        if (leftOperandType == null) {
            analysis.newReport(children.get(0),"Unexpected type: Left Operand.");
            return;
        }

        Type rightOperandType;
        if(children.get(1).getKind().equals("EEInt")) rightOperandType = new Type("int",false);
        else if(children.get(1).getKind().equals("EETrue") || children.get(1).getKind().equals("EEFalse"))
            rightOperandType = new Type("boolean",false);
        else if(children.get(1).getKind().equals("EEIdentifier") &&SemanticAnalysisUtils.EEIdentifierExists(method, children.get(1))){
            rightOperandType = method.getLocalVariable(children.get(1).get("name")).getType();
        }
        else {
            rightOperandType = SemanticAnalysisUtils.evaluateExpression(method, children.get(1), analysis, true);
        }

        if(rightOperandType != null) {
            List<String> imports = analysis.getSymbolTable().getImports();
            if (imports.contains(leftOperandType.getName()) && imports.contains(rightOperandType.getName())) {
                return;
            }
        }

        if (rightOperandType == null)
            analysis.newReport(children.get(1),"Unexpected type: Right Operand.");
        else if (!leftOperandType.getName().equals(rightOperandType.getName()))
            analysis.newReport(children.get(1),"Unexpected type: Right Operand should be "+leftOperandType.getName());
        else if (leftOperandType.isArray())
            if (!rightOperandType.isArray())
                analysis.newReport(children.get(1),"Right type expected to be an array.");
    }


}
