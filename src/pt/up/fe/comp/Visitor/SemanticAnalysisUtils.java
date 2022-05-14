package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.SymbolTable.JmmType;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalysisUtils {

    public static boolean evaluatesToBoolean(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmType type = new JmmType("Boolean", false);

        switch (node.getKind()) {
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
            case "FinalTerms":
                if (type.equals(evaluateFinalTerms(method, node, analysis, true))) return true;
            default:
                break;
        }

        analysis.newReport(node,"Expression should return a boolean.");
        return false;
    }

    public static boolean evaluatesToInteger(JmmMethod method, JmmNode node, Analysis analysis) {

        JmmType type = new JmmType("Int", false);
        switch (node.getKind()) {
            case "Call":
                if (type.equals(evaluateCall(method, node, analysis))) return true;
            case "ExpressionMethodCall":
                if (type.equals(evaluateExpression(method, node, analysis, true))) return true;
            case "Array":
                if (evaluateArrayAccess(method, node, analysis)) return true;
            case "FinalTerms":
                if (type.equals(evaluateFinalTerms(method, node, analysis, true))) return true;
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
        List<JmmNode> children = node.getChildren();
        if (!children.get(0).getKind().contains("FinalTerms") && !children.get(0).getChildren().get(0).getKind().contains("Identifier")) {
            analysis.newReport(children.get(0), "\""+children.get(0)+" is not an array");
            return false;
        } else {
            Report report = isIdentifier(method, children.get(0).getChildren().get(0), analysis, true, true);
            if (report != null) {
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
            analysis.newReport(children.get(0),"left operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }

        if (!evaluatesToBoolean(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"right operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }
        return !hasReport;
    }

    public static boolean evaluateOperationWithIntegers(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

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
            analysis.newReport(children.get(0),"left operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }
        if (!evaluatesToInteger(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"right operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }

        return !hasReport;
    }

    public static boolean evaluateNotOperation(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        if (!evaluatesToBoolean( method, children.get(0), analysis)) {
            analysis.newReport(children.get(0), "bad operand type for binary operator '!': boolean expected");
            return false;
        }
        return true;
    }

    private static Report checkTypeIdentifier(boolean isInt, boolean isArray, JmmType type, String line, String col) {
        if (!(type.getName().equals("Int") == isInt)) {
            if (isInt)
                return new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(line), Integer.parseInt(col), "Identifier is expected to be of type int");
            else
                return new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(line), Integer.parseInt(col), "Identifier is expected to be type different of int");
        }

        if (type.isArray() != isArray) {
            if (isArray)
                return new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(line), Integer.parseInt(col), "Identifier is expected to be of type int array");
            return new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(line), Integer.parseInt(col), "Identifier is expected to be of type int, int array found");
        }
        return null;
    }

    public static JmmType checkIfIdentifierExists(JmmMethod method, String identifier, Analysis analysis) {
        JmmType identifierTypeLocal = method.returnTypeIfExists(identifier);
        JmmType identifierTypeClass = analysis.getSymbolTable().returnFieldTypeIfExists(identifier);

        if (identifierTypeLocal == null && identifierTypeClass == null) return null;
        return (identifierTypeClass != null ? identifierTypeClass : identifierTypeLocal);
    }

    public static Report isIdentifier(JmmMethod method, JmmNode node, Analysis analysis, boolean isInt, boolean isArray) {
        String identifier = node.getKind().replaceAll("'", "").replace("Identifier ", "").trim();

        JmmType typeIdentifier = checkIfIdentifierExists(method, identifier, analysis);
        if (typeIdentifier == null)
            return new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "identifier '" + identifier + "' is not declared");

        return checkTypeIdentifier(isInt, isArray, typeIdentifier, node.get("line"), node.get("col"));
    }

    private static JmmType evaluateFinalTerms(JmmMethod method, JmmNode node, Analysis analysis, boolean rightOperand) {

        List<JmmNode> children = node.getChildren();
        JmmNode firstChild = children.get(0);

        if (firstChild.getKind().contains("Number")) return new JmmType("Int", false);
        else if (firstChild.getKind().equals("NewIntArrayExpression") && rightOperand) {
            if (evaluatesToInteger(method, firstChild.getChildren().get(0), analysis))
                return new JmmType("Int", true);
            analysis.newReport(firstChild.getChildren().get(0),"bad array access: integer expected");
        } else if (firstChild.getKind().contains("NewIdentifier") && rightOperand) {
            String newIdentifier = firstChild.getKind().replaceAll("'", "").replace("NewIdentifier ", "");
            return new JmmType(newIdentifier, false);
        } else if (firstChild.getKind().contains("Identifier")) {
            String identifier = firstChild.getKind().replaceAll("'", "").replace("Identifier ", "");
            JmmType type = checkIfIdentifierExists(method, identifier, analysis);
            if (type == null)
                analysis.newReport(firstChild,"identifier '" + identifier + "' is not declared");
            else if (//method.isMain() &&
                    analysis.getSymbolTable().returnFieldTypeIfExists(identifier) != null)
                analysis.newReport(firstChild,"non-static variable '" + identifier + "' cannot be referenced from a static context");

            return type;
        } else if (rightOperand && (firstChild.getKind().equals("True") || firstChild.getKind().equals("False")))
            return new JmmType("Boolean", false);
        else if (rightOperand && firstChild.getKind().equals("Expression"))
            return evaluateExpression(method, firstChild, analysis, true);

        return null;
    }


    private static boolean evaluateArray(JmmMethod method, JmmNode node, Analysis analysis) {

        if (node.getKind().equals("Call")) {
            JmmType type = evaluateCall(method, node, analysis);
            if (type == null) return false;
            if (type.equals(new JmmType("String", true)) || type.equals(new JmmType("Int", true))) return true;
        }

        List<JmmNode> children = node.getChildren();
        if (children.size() == 1) {
            JmmNode child = children.get(0);
            if (child.getKind().contains("Identifier")) {
                String identifier = child.getKind().replaceAll("'", "").replace("Identifier ", "");
                if (//method.isMain() &&
                        analysis.getSymbolTable().returnFieldTypeIfExists(identifier) != null)
                    analysis.newReport(child,"non-static variable '" + identifier + "' cannot be referenced from a static context");
                if ((isIdentifier(method, child, analysis, true, true) == null) || (isIdentifier(method, child, analysis, false, true) == null))
                    return true;
            }

        }

        analysis.newReport(children.get(0),"length can only be used for int[] or String[]");
        return false;
    }

    public static JmmType evaluateExpression(JmmMethod method, JmmNode node, Analysis analysis, boolean rightOperand) {
        List<JmmNode> children = node.getChildren();

        if (children.size() == 1) {
            JmmNode child = children.get(0);
            if(child.getKind().equals("BinOp") && rightOperand) {
                String op = child.get("op");
                if(op.equals("and"))
                    if (evaluateOperationWithBooleans(method, child, analysis))
                        return new JmmType("boolean", false);
                else if(op.equals("less"))
                    if (evaluateOperationWithIntegers(method, child, analysis))
                        return new JmmType("boolean", false);
                else if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult"))
                    if (evaluateOperationWithIntegers(method, child, analysis))
                        return new JmmType("int", false);
            }
            else if (child.getKind().equals("Not") && rightOperand) {
                if (evaluateNotOperation(method, child, analysis)) return new JmmType("boolean", false);

            } else if (child.getKind().equals("Array")) {
                if (evaluateArrayAccess(method, child, analysis))
                    return new JmmType("int", false);
            } else if (child.getKind().equals("ArrayLength") && rightOperand) {
                if (evaluateArray(method, child, analysis))
                    return new JmmType("int", false);
            } else if (child.getKind().equals("Call")) {
                return evaluateCall(method, child, analysis);
            }
        }

        return null;
    }

    private static JmmType evaluateCall(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        System.out.println("evaluate call");
        System.out.println(node);
        if (children.size() != 2) return null;
        if (children.get(0).getKind().equals("FinalTerms") || children.get(0).getKind().equals("Call")) {
            if (children.get(1).getKind().equals("MethodCall")) {
                return evaluateMethodCall(method, children, analysis);
            } else if (children.get(1).getKind().equals("Length") && evaluateArray(method, children.get(0), analysis))
                return new JmmType("Int", false);
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
        } else if (!identifierKind.contains("EEIdentifier") && !identifierKind.equals("This")) {
            analysis.newReport(identifier.getChildren().get(0),"not a valid identifier");
            return null;
        }


        if (!hasNestedCall) {
            Boolean isNew = false;
            if (!identifierKind.equals("This")) {
                if (identifierKind.contains("NewIdentifier")) isNew = true;
                String identifierName = identifierKind.replaceAll("'", "").replace("Identifier ", "").replace("NewIdentifier ", "");
                JmmType res = analysis.getSymbolTable().hasImport(identifierName);
                if (res != null) return res;

                if (!isNew) {
                    JmmType identifierType = checkIfIdentifierExists(method, identifierName, analysis);

                    if (identifierType == null) {
                        analysis.newReport(identifier.getChildren().get(0),"identifier '" + identifierName + "' is not declared");
                        return null;
                    } else if (//method.isMain() &&
                            analysis.getSymbolTable().returnFieldTypeIfExists(identifierName) != null) {
                        analysis.newReport(identifier.getChildren().get(0), "non-static variable '" + identifierName + "' cannot be referenced from a static context");
                    }


                    identifierN = identifierType.getName();
                } else identifierN = identifierName;

                if (identifierN.equals(analysis.getSymbolTable().getSuper())) return new JmmType("Accepted", false);
                if (identifierN.equals("Int") || identifierN.equals("Boolean") || identifierN.equals("String")) {
                    analysis.newReport(identifier.getChildren().get(0), "identifier cannot be string, int or boolean");
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


    public static String getTypeParameters(List<JmmNode> parameters) {
        List<String> types = new ArrayList<>();

        if ((parameters.size() == 0) || (parameters.size() % 2 != 0)) return "";

        for (int i = 0; i < parameters.size(); i++) {
            JmmNode nodeType = parameters.get(i++);
            JmmType type = new JmmType(nodeType);
            types.add(type.printType());
        }

        return String.join(",", types);
    }
}