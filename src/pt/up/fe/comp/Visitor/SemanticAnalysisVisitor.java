package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.SymbolTable.JmmType;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
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

        for (int i = 0; i < children.size(); i++) {

            JmmNode child = children.get(i);
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

        JmmType type = null;

        if(child.getKind().equals("EETrue") || child.getKind().equals("EEFalse")){
            type = new JmmType("boolean", false);
        }
        else if(child.getKind().equals("EEIdentifier")){
            if(!SemanticAnalysisUtils.EEIdentifierExists(method,child,analysis)){
                analysis.newReport(child, "Variable for return value not defined.");
                return;
            }
            else{
                Type typeTemp =  method.getLocalVariable(child.get("name")).getType();
                type = new JmmType(typeTemp.getName(),typeTemp.isArray());
            }
        }
        else if(child.getKind().equals("EEInt")){
            type = new JmmType("int", false);
        }
        else {
            type = SemanticAnalysisUtils.evaluateExpression(method, child, analysis, true);
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
            if(child.getKind().equals("Call")) visitCall(method, child, analysis);
        }
    }

    public void visitVarDeclaration(JmmMethod method, JmmNode node, Analysis analysis){
        String varName = node.get("name");
        JmmNode typeNode = node.getJmmChild(0);
        String varTypeStr = typeNode.get("name");
        boolean isArray = Boolean.parseBoolean(typeNode.get("isArray"));

        Type varType = new Type(varTypeStr, isArray);
        Symbol symbol = new Symbol(varType, varName );

        if(method == null){
            if(!analysis.getSymbolTable().fieldExists(symbol)){
                analysis.getSymbolTable().addField(varType, varName);
                return;
            }
            else{
                analysis.newReport(node, "Variable " + symbol + " is already defined in the scope");
            }
        }
        else{
            if(method.localVariableExists(symbol)){
                analysis.newReport(node, "Variable " + symbol + " is already defined in the scope");
                return;
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
        JmmNode Expression = node.getJmmChild(1);

        System.out.println("assign visit lasflksjf");
        JmmType identifierType;

        List<JmmNode> children = node.getChildren();
        if (children.size() != 2) return; //check if there are two children
        if(children.get(0).getKind().equals("EEIdentifier")){  //if assigning to EEIdentifier
            /*
            try {
                Type typeTemp =  method.getLocalVariable(assignee.get("name")).getType();
                identifierType = new JmmType(typeTemp.getName(),typeTemp.isArray());
            }
            catch(Exception e){
                analysis.newReport(assignee, "Variable is not defined.");
                return;
            }
             */
            if(!SemanticAnalysisUtils.EEIdentifierExists(method, assignee, analysis)){
                analysis.newReport(assignee, "Variable is not defined.");
            }

        }
        else if(children.get(0).getKind().equals("Array")){  //if assigning to Array
            //check for array and identifier
        }
        if(!children.get(1).getKind().equals("BinOp") || !children.get(1).equals("EENew")
        || !children.get(1).equals("Call") || !children.get(1).equals("Array") || !children.get(1).equals("EEIdentifier"))
            return;

        JmmType leftOperandType = SemanticAnalysisUtils.evaluateExpression(method, children.get(0), analysis, false);
        if (leftOperandType == null) {
            analysis.newReport(children.get(0),"Unexpected type: Left Operand.");
            return;
        }

        JmmType rightOperandType = SemanticAnalysisUtils.evaluateExpression(method, children.get(1), analysis, true);

        if (rightOperandType == null)
            analysis.newReport(children.get(1),"Unexpected type: Right Operand.");
        else if (rightOperandType.getName().equals("Accepted")) return;
        else if (!leftOperandType.getName().equals(rightOperandType.getName()))
            analysis.newReport(children.get(1),"Unexpected type: Right Operand should be "+leftOperandType.getName());
        else if (leftOperandType.isArray())
            if (!rightOperandType.isArray())
                analysis.newReport(children.get(1),"Right type expected to be an array.");
    }

    public void visitCall(JmmMethod method, JmmNode node, Analysis analysis){

    }

}
