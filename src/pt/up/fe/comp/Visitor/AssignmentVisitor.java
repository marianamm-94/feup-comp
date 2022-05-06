package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.SymbolTable.JmmAnalyser;



public class AssignmentVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    
    public AssignmentVisitor() {
        addVisit("Assignment", this::visitAssigment);
    }


    public boolean visitAssigment(JmmNode node, JmmAnalyser symbolTableReport) {
        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);
        String left, right;
        String parentMethod = Utils.getParentMethod(node);

        if(leftNode.getKind().equals("Identifier")) {
            left = Utils.getTypeVar(leftNode, symbolTableReport, parentMethod);
        } else {
            JmmNode arrayIdentifier = leftNode.getChildren().get(0);
            left = Utils.getTypeVar(arrayIdentifier, symbolTableReport, parentMethod).split("\\[", 0)[0];
        }

        if (rightNode.getChildren().size() == 0) {
            right = Utils.getTypeVar(rightNode, symbolTableReport, parentMethod);
            if (!right.equals(left)) {
                symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" and \"" + leftNode + "\" incompatible types");
            }
        }
        else if(Utils.isBoolOp(rightNode.getKind()) && !left.equals("boolean")){
            symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" and \"" + leftNode + "\" incompatible types");
        }
        else if(Utils.isMathOp(rightNode.getKind()) && !left.equals("int") && !left.equals("int[]")) {
            symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" and \"" + leftNode + "\" incompatible types");
        }
        else if(rightNode.getKind().equals("FullStop")) {
            String returnType = Utils.getReturnType(rightNode, symbolTableReport);
            if (!returnType.equals("undefined") && !returnType.equals(left)) {
                symbolTableReport.newReport(rightNode, "\"" + rightNode + "\" and \"" + leftNode + "\" incompatible types");
            }
        }


        return true;
    }
}
