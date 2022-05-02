package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class MathOperationVisitor extends PreorderJmmVisitor {

    public MathOperationVisitor() {
        addVisit("Add", this::MathOperationVisit);
        addVisit("Sub", this::MathOperationVisit);
        addVisit("Mul", this::MathOperationVisit);
        addVisit("Div", this::MathOperationVisit);
    }

    public Boolean MathOperationVisit(JmmNode node, JmmAnalyser symbolTableReport) {
        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        String operation = Utils.getParentMethod(node);

        checkNodeExpression(leftNode, symbolTableReport, operation);
        checkNodeExpression(rightNode, symbolTableReport, operation);

        return true;
    }

    private void checkNodeExpression(JmmNode node, JmmAnalyser symbolTableReport, String operation){

        if(Utils.isMathOp(node.getKind())) return;
        else if (Utils.isBoolOp(node.getKind()))
            symbolTableReport.newReport(node, "\"" + node + "\" invalid operator.");
        else if (node.getKind().equals("FULLSTOP")) {
            String value = Utils.getReturn(node, symbolTableReport);
            if (!(value.equals("undefined") || value.equals("int")))
                symbolTableReport.newReport(node, "\"" + node + "\" invalid operator.");
        }
        //else if (!node.getKind().equals("ArrayAccess") )
    }

}
