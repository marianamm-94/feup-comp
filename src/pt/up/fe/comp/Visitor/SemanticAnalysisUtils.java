package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.SymbolTable.JmmType;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalysisUtils {

    public static boolean evaluatesToBoolean(JmmMethod method, JmmNode node, Analysis analysis) {

        JmmType type = new JmmType("boolean", false);
        switch (node.getKind()) {
            case "EETrue":
            case "EEFalse":
                return true;
            case "EEIndentifier":
                String typeVar = node.get("name");
                List<Symbol> checkExist = analysis.getSymbolTable().getLocalVariables(method.getName());
                for(Symbol symbol: checkExist){
                    if(symbol.getName().equals(typeVar))
                        if (symbol.getType().getName().equals("boolean") && !symbol.getType().isArray())
                            return true;
                }
                break;
            case "Call":
                if (type.equals(evaluateCall(method, node, analysis))) return true;
            case "ExpressionMethodCall":
            case "IfExpression":
                if (type.equals(evaluateExpression(method, node, analysis, true))) return true;
            case "Not":
                if (evaluateNotOperation(method, node, analysis)) return true;
            case "BinOp":
                String op = node.get("op");
                if(op.equals("and"))
                    if (evaluateOperationWithBooleans(method, node, analysis)) return true;
                else if(op.equals("less"))
                    if (evaluateOperationWithIntegers(method, node, analysis)) return true;
            default:
                break;
        }

        analysis.newReport(node,"Expression should return a boolean.");
        return false;
    }

    public static boolean evaluatesToInteger(JmmMethod method, JmmNode node, Analysis analysis) {

        System.out.println("evaluate to integer entered");
        System.out.println(node);

        JmmType type = new JmmType("int", false);
        switch (node.getKind()) {
            case "EEInt":
                return true;
            case "EEIdentifier":
                String typeVar = node.get("name");
                List<Symbol> checkExist = analysis.getSymbolTable().getLocalVariables(method.getName());
                for(Symbol symbol: checkExist){
                    if(symbol.getName().equals(typeVar))
                        if (symbol.getType().getName().equals("int") && !symbol.getType().isArray())
                            return true;
                }
                break;
            case "Call":
                if (type.equals(evaluateCall(method, node, analysis))) return true;
            case "ExpressionMethodCall":
                if (type.equals(evaluateExpression(method, node, analysis, true))) return true;
            case "Array":
                if (evaluateArrayAccess(method, node, analysis)) return true;
            case "BinOp":
                String op = node.get("op");
                if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult"))
                    if (evaluateOperationWithIntegers(method, node, analysis)) return true;
            default:
                break;
        }

        analysis.newReport(node,"Expression should return an int.");
        return false;
    }

    private static boolean evaluateArrayAccess(JmmMethod method, JmmNode node, Analysis analysis) {

        System.out.println("evaluating array access");

        List<JmmNode> children = node.getChildren();

        if (!children.get(0).getKind().equals("EEIdentifier")) {


            analysis.newReport(children.get(0), "\""+children.get(0)+" is not an array");
            return false;
        } else {
            if (!isIdentifier(method, children.get(0), analysis, true, true)) {
                analysis.newReport(children.get(0),"\""+children.get(0)+" is not an array");
                return false;
            }
        }
        if (!evaluatesToInteger(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"Bad array access: expected int.");
            return false;
        }
        return true;
    }

    public static boolean evaluateOperationWithBooleans(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        if (children.size() != 2) return false;

        boolean hasReport = false;

        if (!evaluatesToBoolean(method, children.get(0), analysis)) {
            analysis.newReport(children.get(0),"Left operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }

        if (!evaluatesToBoolean(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"Right operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }
        return !hasReport;
    }

    public static boolean evaluateOperationWithIntegers(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        System.out.println("evaluateOpWithInt");

        if (children.size() != 2) return false;

        char operation = ' ';

        switch (node.get("op")) {
            case "add":
                operation = '+';
                break;
            case "sub":
                operation = '-';
                break;
            case "mult":
                operation = '*';
                break;
            case "div":
                operation = '/';
                break;
            case "less":
                operation = '<';
                break;
        }

        boolean hasReport = false;
        if (!evaluatesToInteger(method, children.get(0), analysis)) {
            analysis.newReport(children.get(0),"Left operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }
        if (!evaluatesToInteger(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"Right operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }

        return !hasReport;
    }

    public static boolean evaluateNotOperation(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        System.out.println("evaluateNotOp");

        if (!evaluatesToBoolean( method, children.get(0), analysis)) {
            analysis.newReport(children.get(0), "bad operand type for binary operator '!': boolean expected");
            return false;
        }
        return true;
    }

    public static JmmType checkIfIdentifierExists(JmmMethod method, JmmNode node, Analysis analysis) {
        System.out.println("checking identifier");

        JmmType identType = null;
        if(SemanticAnalysisUtils.EEIdentifierExists(method, node,analysis)){
            Type typeTemp =  method.getLocalVariable(node.get("name")).getType();
            identType = new JmmType(typeTemp.getName(),typeTemp.isArray());
        }
        return identType;
    }

    public static boolean isIdentifier(JmmMethod method, JmmNode node, Analysis analysis, boolean isInt, boolean isArray) {
        String identifier = node.get("name");

        JmmType typeIdentifier = checkIfIdentifierExists(method, node, analysis);
        if (typeIdentifier == null)
            return false;
        if(typeIdentifier.isArray() != isArray) return false;
        return true;
    }


    private static boolean evaluateArray(JmmMethod method, JmmNode node, Analysis analysis) {

        if (node.getKind().equals("Call")) {
            JmmType type = evaluateCall(method, node, analysis);
            if (type == null) return false;
            if (type.equals(new JmmType("int", true))) return true;
        }

        List<JmmNode> children = node.getChildren();
        if (children.size() == 1) {
            JmmNode child = children.get(0);
            if (child.getKind().contains("EEIdentifier")) {
                try {
                    Type typeTemp =  method.getLocalVariable(node.get("name")).getType();
                    JmmType type = new JmmType(typeTemp.getName(),typeTemp.isArray());
                    if(!type.isArray()) analysis.newReport(child,"Trying to access array on int.");
                }
                catch(Exception e){
                    analysis.newReport(node, "Variable not defined.");
                }
                String identifier = child.get("name");
                if ( analysis.getSymbolTable().returnFieldTypeIfExists(identifier) != null)
                    analysis.newReport(child,"non-static variable '" + identifier + "' cannot be referenced from a static context");
                if ( (!isIdentifier(method, child, analysis, true, true)) || (!isIdentifier(method, child, analysis, false, true)))
                    return true;
            }

        }

        analysis.newReport(children.get(0),"length can only be used for arrays");
        return false;
    }

    public static JmmType evaluateExpression(JmmMethod method, JmmNode node, Analysis analysis, boolean rightOperand) {
        List<JmmNode> children = node.getChildren();

        System.out.println("expression eval aslkjasdlkasjd");

        if(node.getKind().equals("BinOp")){

            String op = node.get("op");
            if(op.equals("and")) {
                if (evaluateOperationWithBooleans(method, node, analysis)) {
                    return new JmmType("boolean", false);
                }
            }
            else if(op.equals("less")) {
                if (evaluateOperationWithIntegers(method, node, analysis))
                    return new JmmType("boolean", false);
            }
            else if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult")) {
                if (evaluateOperationWithIntegers(method, node, analysis))
                    return new JmmType("int", false);
            }
        }
        else if(node.getKind().equals("Array")){

            System.out.println("expression eval array");

            if (evaluateArrayAccess(method, node, analysis))
                return new JmmType("int", false);
        }
        else if(node.getKind().equals("Call")){

            System.out.println("expression eval call");


            return evaluateCall(method, node, analysis);
        }
        else if(node.getKind().equals("EENew")){
            if(node.getJmmChild(0).getKind().equals("Array")){
                return evaluateExpression(method, node.getJmmChild(0), analysis, true);
            }
            else if(node.getJmmChild(0).getKind().equals("EEObject") && SemanticAnalysisUtils.EEIdentifierExists(method, children.get(0),analysis)){
                System.out.println("Got to expression object");
                Type typeTemp =  method.getLocalVariable(node.getJmmChild(0).get("name")).getType();
                return new JmmType(typeTemp.getName(), typeTemp.isArray());
            }
        }

        return null;
    }

    public static JmmType evaluateCall(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        System.out.println("evaluate call function");
         //node              // children: EEThis EEIdentifier call
                             //children 2: ExpressionMethodCall, ArrayLength
        if (children.size() != 2) return null;
        if (children.get(0).getKind().equals("Call")) {
            //evaluate if exisits
            //evaluatecall(JmmMethod method, JmmNode children.get(0., analysis);
        }
        else if(children.get(0).equals("EETHis")){

        }
        else if(children.get(0).equals("EEIdentifier")){

        }

        if(children.get(1).equals("ArrayLength")){
            if(evaluateArray(method, children.get(0), analysis)){
                return new JmmType("int", false);
            }
        }
        else if(children.get(1).equals("ExpressionMethodCall")){
            return evaluateMethodCall(method, children, analysis);
        }

        return null;
    }

    private static JmmType evaluateMethodCall(JmmMethod method, List<JmmNode> nodes, Analysis analysis) {

        JmmNode identifier = nodes.get(0);
        JmmNode methodNode = nodes.get(1);

        String identifierKind = identifier.getChildren().get(0).getKind();
        Boolean hasNestedCall = false;
        String identifierN = "this";

        if (identifier.getKind().equals("Call")) {
            hasNestedCall = true;
            JmmType t = evaluateCall(method, identifier, analysis);
            if (t != null) {
                JmmType res = analysis.getSymbolTable().hasImport(t.getName());
                if (t.equals(res)) return t;
                else if (t.getName().equals(analysis.getSymbolTable().getSuper())) return new JmmType("Accepted", false);
                else if (t.getName().equals(analysis.getSymbolTable().getClassName())) identifierN = t.getName();
                else {
                    analysis.newReport(identifier.getChildren().get(0),"method does not exist or is being invoked with the wrong arguments");
                    return null;
                }
            }
        } else if (!identifierKind.contains("EEIdentifier") && !identifierKind.equals("EEThis")) {
            analysis.newReport(identifier.getChildren().get(0),"not a valid identifier");
            return null;
        }


        if (!hasNestedCall) {
            Boolean isNew = false;
            if (!identifierKind.equals("EEThis")) {
                if (identifierKind.contains("EEIdentifier")) isNew = true;
                String identifierName = identifierKind;
                JmmType res = analysis.getSymbolTable().hasImport(identifierName);
                if (res != null) return res;

                if (!isNew) {
                    JmmType identifierType = checkIfIdentifierExists(method, identifier.getChildren().get(0), analysis);

                    if (identifierType == null) {
                        analysis.newReport(identifier.getChildren().get(0),"identifier '" + identifierName + "' is not declared");
                        return null;
                    } else if (analysis.getSymbolTable().returnFieldTypeIfExists(identifierName) != null) {
                        analysis.newReport(identifier.getChildren().get(0), "non-static variable '" + identifierName + "' cannot be referenced from a static context");
                    }


                    identifierN = identifierType.getName();
                } else identifierN = identifierName;

                if (identifierN.equals(analysis.getSymbolTable().getSuper())) return new JmmType("Accepted", false);
                if (identifierN.equals("int") || identifierN.equals("boolean")) {
                    analysis.newReport(identifier.getChildren().get(0), "identifier cannot be int or boolean");
                    return null;
                }
            }
        }

        String methodName = methodNode.getChildren().get(0).getKind().replaceAll("'", "").replace("Identifier ", "");

        List<JmmNode> parameters = new ArrayList<>(methodNode.getChildren());
        parameters.remove(0);

        List<String> p = new ArrayList<>();
        for (JmmNode parameter : parameters) {
            JmmType type = evaluateExpression(method, parameter, analysis, true);

            if (type == null) {
                analysis.newReport(parameter,"parameter is not valid");
                return null;
            }
            p.add(type.printType());
        }

        String methodInfo = methodName + "(" + String.join(",", p) + ")";

        JmmType type = (JmmType) analysis.getSymbolTable().getReturnType(methodInfo);

        if (type != null) return type;

        if (identifierN.equals("this") || identifierN.equals(analysis.getSymbolTable().getClassName())) {
            if (!analysis.getSymbolTable().getSuper().equals("")) return new JmmType("Accepted", false);
        }

        analysis.newReport(identifier.getChildren().get(0), "method does not exist or is being invoked with the wrong arguments");
        return null;
    }

    public static boolean EEIdentifierExists(JmmMethod method, JmmNode assignee, Analysis analysis){
        try {
            Type typeTemp =  method.getLocalVariable(assignee.get("name")).getType();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}