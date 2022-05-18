package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import java.util.List;

public class SemanticAnalysisUtils {

    public static boolean evaluatesToBoolean(JmmMethod method, JmmNode node, Analysis analysis) {

        Type type = new Type("boolean", false);
        switch (node.getKind()) {
            case "EETrue":
            case "EEFalse":
                return true;
            case "EEIdentifier":
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
                if (type.equals(evaluateMethodCall(method, node, analysis))) return true;
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

        Type type = new Type("int", false);
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
                if (type.equals(evaluateMethodCall(method, node, analysis))) return true;
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
            if (!isIdentifier(method, children.get(0), analysis, true)) {
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

        char operation = switch (node.get("op")) {
            case "add" -> '+';
            case "sub" -> '-';
            case "mult" -> '*';
            case "div" -> '/';
            case "less" -> '<';
            default -> ' ';
        };

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

    public static Type checkIfIdentifierExists(JmmMethod method, JmmNode node, Analysis analysis) {
        System.out.println("checking identifier");

        Type identType = null;
        if(SemanticAnalysisUtils.EEIdentifierExists(method, node)){
            identType = method.getLocalVariable(node.get("name")).getType();
        }
        return identType;
    }

    public static boolean isIdentifier(JmmMethod method, JmmNode node, Analysis analysis, boolean isArray) {

        Type typeIdentifier = checkIfIdentifierExists(method, node, analysis);
        if (typeIdentifier == null)
            return false;
        if(typeIdentifier.isArray() != isArray) return false;
        return true;
    }


    private static boolean evaluateArray(JmmMethod method, JmmNode node, Analysis analysis) {

        if (node.getKind().equals("Call")) {
            Type type = evaluateCall(method, node, analysis);
            if (type == null) return false;
            if (type.equals(new Type("int", true))) return true;
        }

        List<JmmNode> children = node.getChildren();
        if (children.size() == 1) {
            JmmNode child = children.get(0);
            if (child.getKind().contains("EEIdentifier")) {
                try {
                    Type typeTemp =  method.getLocalVariable(node.get("name")).getType();
                    Type type = new Type(typeTemp.getName(),typeTemp.isArray());
                    if(!type.isArray()) analysis.newReport(child,"Trying to access array on int.");
                }
                catch(Exception e){
                    analysis.newReport(node, "Variable not defined.");
                }
                String identifier = child.get("name");
                if ( analysis.getSymbolTable().returnFieldTypeIfExists(identifier) != null)
                    analysis.newReport(child,"non-static variable '" + identifier + "' cannot be referenced from a static context");
                if ( (!isIdentifier(method, child, analysis, true)) || (!isIdentifier(method, child, analysis, true)))
                    return true;
            }

        }

        analysis.newReport(children.get(0),"length can only be used for arrays");
        return false;
    }

    public static Type evaluateExpression(JmmMethod method, JmmNode node, Analysis analysis, boolean rightOperand) {

        System.out.println("expression eval aslkjasdlkasjd");
        System.out.println(node);

        if(node.getKind().equals("BinOp")){

            String op = node.get("op");
            switch (op) {
                case "and":
                    if (evaluateOperationWithBooleans(method, node, analysis)) {
                        return new Type("boolean", false);
                    }
                    break;
                case "less":
                    if (evaluateOperationWithIntegers(method, node, analysis))
                        return new Type("boolean", false);
                    break;
                case "add":
                case "sub":
                case "div":
                case "mult":
                    if (evaluateOperationWithIntegers(method, node, analysis))
                        return new Type("int", false);
                    break;
            }
        }
        else if(node.getKind().equals("Array")){

            System.out.println("expression eval array");

            if (evaluateArrayAccess(method, node, analysis))
                return new Type("int", false);
        }
        else if(node.getKind().equals("Call")){

            System.out.println("expression eval call");
            return evaluateCall(method, node, analysis);
        }
        else if(node.getKind().equals("EENew")){
            if(node.getJmmChild(0).getKind().equals("Array")){
                return evaluateExpression(method, node.getJmmChild(0), analysis, true);
            }
            else if(node.getJmmChild(0).getKind().equals("EEObject")){ //&& SemanticAnalysisUtils.EEIdentifierExists(method, children.get(0),analysis)){

                System.out.println("Got to expression object");
                System.out.println(node.getJmmChild(0));
                System.out.println(method.getName());

                List<String> imports = analysis.getSymbolTable().getImports();
                String extendClass = analysis.getSymbolTable().getSuper();
                if(imports.contains(node.getJmmChild(0).get("name"))) {
                    return new Type(node.getJmmChild(0).get("name"), false);
                }
                else if(node.getJmmChild(0).get("name").equals(extendClass)){
                    //idk what to do here
                }
            }
        }

        return null;
    }

    public static Type evaluateCall(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        System.out.println("evaluate call function");
        System.out.println(children.get(0));
        System.out.println(method.getLocalVariables());
         //node              // children: EEThis EEIdentifier call
                             //children 2: ExpressionMethodCall, ArrayLength
        Symbol varSymbol = null;

        if (children.size() != 2) return null;
        if (children.get(0).getKind().equals("Call")) {
            if(evaluateCall(method, children.get(0), analysis) == null)
                return null;
        }
        else if(children.get(0).getKind().equals("EEIdentifier")){
            if(!EEIdentifierExists(method, children.get(0)))
                return null;
            List<Symbol> variablesList = method.getLocalVariables();
            List<String> imports = analysis.getSymbolTable().getImports();
            for(Symbol symbol: variablesList){
                if(symbol.getName().equals(children.get(0).get("name")))
                    varSymbol = symbol;
                    break;
            }
            for(String impt: imports) {
                if(varSymbol.getType().getName().equals(impt)){
                    return new Type("Accept", false);
                }
            }
        }

        if(children.get(1).getKind().equals("ArrayLength")){
            if(evaluateArray(method, children.get(0), analysis)){
                if(varSymbol.getName().equals(children.get(0).get("name")))
                    return new Type(varSymbol.getType().getName(), false);
            }
        }
        else if(children.get(1).getKind().equals("ExpressionMethodCall")){
            return evaluateMethodCall(method, children.get(1), analysis);
        }

        return null;
    }

    public static Type evaluateMethodCall(JmmMethod method, JmmNode node, Analysis analysis){

        List<JmmNode> children = node.getChildren();

        System.out.println("Entered evaluate method call");
        System.out.println(node);
        JmmMethod methodFunc = analysis.getSymbolTable().getMethodById(node.get("name"));
        if(methodFunc == null) return null;
        List<Symbol> methodParams = methodFunc.getParameters();

        boolean hasReports = false;
        List<String> methodsDefined = analysis.getSymbolTable().getMethods();
        if(methodsDefined.contains(methodFunc.getName())){
            for(int i=0; i< children.size();i++){

                Symbol symbolNow = method.getLocalVariable(children.get(i).get("name"));

                if(methodParams.get(i).getType().getName().equals(symbolNow.getType().getName())){
                    if(methodParams.get(i).getType().isArray() == symbolNow.getType().isArray()){
                        continue;
                    }
                    else{
                        analysis.newReport(children.get(i),"Parameter is not of the same type");
                        hasReports = true;
                    }
                }
                else{
                    analysis.newReport(children.get(i),"Parameter is not of the same type");
                    hasReports = true;

                }
            }
        }

        if(!hasReports) return methodFunc.getReturnType();
        else return null;
    }

    public static boolean EEIdentifierExists(JmmMethod method, JmmNode assignee){
        try {
            Type typeTemp =  method.getLocalVariable(assignee.get("name")).getType();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}