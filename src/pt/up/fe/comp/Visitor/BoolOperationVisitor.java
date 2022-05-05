package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class BoolOperationVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public BoolOperationVisitor() {
        addVisit("And", this::boolVisitAnd);
        addVisit("Exclamation_mark", this::boolVisitNot);
        addVisit("Less", this::boolVisitLess);
    }

    public Boolean boolVisitAnd(JmmNode node, JmmAnalyser symbolTableReport) {

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);
        String parentMethod = Utils.getParentMethod(node);

        if(leftNode.getKind().equals("FullStop")){
            String value = Utils.getReturnType(leftNode, symbolTableReport);
            if(!value.equals("undefined") || !value.equals("boolean"))
                symbolTableReport.newReport(leftNode, "\""+leftNode+"\" invalid type: expected boolean");
        }
        if (rightNode.getKind().equals("FullStop")){
            String value = Utils.getReturnType(rightNode, symbolTableReport);
            if(!value.equals("undefined") || !value.equals("boolean"))
                symbolTableReport.newReport(rightNode, "\""+rightNode+"\" invalid type: expected boolean");
        }
        if (!Utils.getTypeVar(leftNode, symbolTableReport, parentMethod).equals("boolean"))
            symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" invalid expression");
        if(!Utils.getTypeVar(rightNode, symbolTableReport, parentMethod).equals("boolean"))
            symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" invalid expression");
        if (leftNode.getNumChildren() > 0) {
            if(!Utils.isBoolOp(leftNode.getKind()))
                symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" invalid expression");
        }
        if (rightNode.getNumChildren() > 0) {
            if(!Utils.isBoolOp(rightNode.getKind()))
                symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" invalid expression");
        }

        return true;
    }

    public Boolean boolVisitNot(JmmNode node, JmmAnalyser symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        String parentMethod = Utils.getParentMethod(node);

        if (!Utils.isBoolOp(leftNode.getKind()))
            if (!Utils.getTypeVar(leftNode, symbolTableReport, parentMethod).equals("boolean"))
                symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" invalid expression");
        if(leftNode.getKind().equals("FullStop")){
            String value = Utils.getReturnType(leftNode, symbolTableReport);
            if(!value.equals("undefined") || !value.equals("boolean"))
                symbolTableReport.newReport(leftNode, "\""+leftNode+"\" invalid type: expected boolean");
        }

        return true;
    }

    public Boolean boolVisitLess(JmmNode node, JmmAnalyser symbolTableReport){

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        if (leftNode.getKind().equals("FullStop")){
            String returnValueMethod = Utils.getReturnType(leftNode, symbolTableReport);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("int") && !returnValueMethod.equals("int[]"))
                symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" invalid type: expecting an int or an int[].");
        }
        if (rightNode.getKind().equals("FullStop")){
            String returnValueMethod = Utils.getReturnType(rightNode, symbolTableReport);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("int") && !returnValueMethod.equals("int[]"))
                symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" invalid type: expecting an int or an int[].");
        }
        if (leftNode.getNumChildren() == 0 && rightNode.getNumChildren() == 0){

            String parentMethod = Utils.getParentMethod(node);

            String leftNodeType = Utils.getTypeVar(leftNode, symbolTableReport, parentMethod);
            String rightNodeType = Utils.getTypeVar(rightNode, symbolTableReport, parentMethod);

            if (!leftNodeType.equals("int") && !leftNodeType.equals("int[]")){
                symbolTableReport.newReport(rightNode, "\"" + leftNode + "\" invalid type");
            }
            else if (!rightNodeType.equals("int") && !rightNodeType.equals("int[]")) {
                symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" invalid type");
            }
        }
        if (leftNode.getChildren().size() > 0 && !leftNode.getKind().equals("ArrayAccess")){
            if (!Utils.isMathOp(leftNode.getKind()))
                symbolTableReport.newReport(leftNode, "\"" + leftNode + "\" unexpected operator");
        }
        if (rightNode.getChildren().size() > 0 && !rightNode.getKind().equals("ArrayAccess")){
            if (!Utils.isMathOp(rightNode.getKind()))
                symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" unexpected operator");
        }

        return true;
    }

}

