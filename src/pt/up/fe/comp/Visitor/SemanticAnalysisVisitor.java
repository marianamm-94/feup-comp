package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.SymbolTable.JmmType;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

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

            if (childKind.equals("Type")) { // parameters

            } else if (childKind.contains("Argument")) {
                String methodNameArg = childKind.replaceAll("'","").replace("Argument ","");
            } else if (childKind.equals("MethodBody")) { //method body (local variables)
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


    public Boolean visitMethod(JmmNode node, Analysis analysis){

        List<JmmNode> children = node.getChildren();
        boolean alreadyInBody = false;

        StringBuilder methodInfo = new StringBuilder();
        JmmMethod method = null;

        for (int i = 0; i < children.size(); i++) {

            JmmNode child = children.get(i);
            String childKind = child.getKind();

            if (childKind.equals("LParenthesis")) { // parameters
                if (alreadyInBody) break;
                List<JmmNode> parameters = new ArrayList<>();
                while (true) {
                    i++;
                    JmmNode aux = children.get(i);
                    if (aux.getKind().equals("RParenthesis")) break;
                    parameters.add(children.get(i));
                }
            } else if (childKind.contains("Identifier")) {
                String methodName = childKind.replaceAll("'", "").replace("Identifier ", "");
                methodInfo.append(methodName).append("(");
            } else if (childKind.equals("Main")) {
                methodInfo.append("main(");
            } else if (childKind.equals("MethodBody")) { //method body (local variables)
                methodInfo.append(")");
                method = analysis.getSymbolTable().getMethodByInfo(methodInfo.toString());

                if (method == null)
                    return false;

                visitMethodBody(method, child, analysis);
                alreadyInBody = true;
            } else if (child.getKind().equals("Return")) {
                visitReturnValue(method, child, analysis);
            }
        }

        return true;
    }

    public void visitReturnValue(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode child = node.getChildren().get(0);

        JmmType type = null;

        if(child.getKind().equals("EETrue")){
            type = new JmmType("EETrue", false);
        }
        else if(child.getKind().equals("EEIdentifier")){
            try {
                Type typeTemp =  method.getLocalVariable(child.get("name")).getType();
                type = new JmmType(typeTemp.getName(),typeTemp.isArray());
            }
            catch(Exception e){
                analysis.newReport(child, "Variable not defined.");
            }
        }
        else if(child.getKind().equals("EEInt")){
            type = new JmmType("int", false);
        }
        else {
            type = SemanticAnalysisUtils.evaluateExpression(method, child, analysis, true);
        }

        if (type == null) {
            analysis.newReport(child, "Return type doesn't match specification.");
            return;
        }
        if ( !(type.getName().equals(method.getReturnType().getName()) && (type.isArray() == method.getReturnType().isArray()))) {
            System.out.println("What?");
            analysis.newReport(child, "Return type doesn't match specification. Should be" + method.getReturnType());
        }

    }

    public void visitMethodBody(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        for (JmmNode child : children) {
            if(child.getKind().equals("VarDeclaration")) visitVarDeclaration(method, child, analysis);
            if(child.getKind().equals("Assignment")) visitAssignment(method, child, analysis);
            if(child.getKind().equals("WhileStatement")) visitWhileStatement(method, child, analysis);
            if(child.getKind().equals("IfStatement")) visitIfStatement(method, child, analysis);
            //if(child.getKind().equals("Call")) visitCall();
        }
    }

    public void visitVarDeclaration(JmmMethod method, JmmNode node, Analysis analysis){
        String varName = node.get("name");
        JmmNode typeNode = node.getJmmChild(0);
        String typeString = typeNode.get("name");
        boolean isArray = Boolean.parseBoolean(typeNode.get("isArray"));

        Type varType = new Type(typeString, isArray);
        Symbol symbol = new Symbol(varType, varName );

        if(method == null){
            if(!analysis.getSymbolTable().fieldExists(symbol)){
                analysis.getSymbolTable().addField(varType, varName);
                return;
            }
            else{
                analysis.newReport(node, "VariableRedefinition: Variable " + symbol + " is already defined in the scope");
            }
        }
        else{
            JmmMethod tableMethod = analysis.getSymbolTable().getMethod(method);
            if(tableMethod == null){
                analysis.newReport(node,"Unexpected type of variable");
                System.out.println("Unexpected class");
                return;
            }
            if(tableMethod.localVariableExists(symbol)){
                analysis.newReport(node, "VariableRedefinition: Variable " + symbol + " is already defined in the scope");
                return;
            }
            tableMethod.addLocalVariable(varType, varName, false);
        }

    }

    public void visitStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        for (JmmNode child : children) {
            switch (child.getKind()) {
                case "ExpressionMethodCall":
                    SemanticAnalysisUtils.evaluateExpression(method, child, analysis, true);
                    break;
                case "Assignment":
                    visitAssignment(method, child, analysis);
                    break;
                case "IfBody":
                    visitStatement(method, child, analysis);
                    break;
                case "Call":

            }
        }
    }

    public void visitWhileStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode leftChild = node.getJmmChild(0); //this is WhileCondition
        JmmNode rightChild = node.getJmmChild(1); //this is WhileBody

        //evaluate condition
        SemanticAnalysisUtils.evaluatesToBoolean(method, leftChild, analysis);

        //evaluate body
        visitStatement(method, rightChild, analysis);
    }

    public void visitIfStatement(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        for (JmmNode child : children) {
            if (child.getKind().equals("IfCondition"))
                SemanticAnalysisUtils.evaluatesToBoolean(method, child, analysis);
            else if (child.getKind().equals("IfBody")) visitStatement(method, child, analysis);
            else if (child.getKind().equals("ElseBody")) visitStatement(method, child, analysis);
        }
    }

    public void visitAssignment(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode ident = node.getJmmChild(0);
        JmmNode Expression = node.getJmmChild(1);

        System.out.println("assign visit lasflksjf");

        List<JmmNode> children = node.getChildren();
        if (children.size() != 2) return;
        if (!children.get(0).getKind().equals("EEIdentifier")) {
            try {
                Type typeTemp =  method.getLocalVariable(node.get("name")).getType();
                JmmType type = new JmmType(typeTemp.getName(),typeTemp.isArray());
            }
            catch(Exception e){
                analysis.newReport(node, "Variable not defined.");
            }
            return;
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
