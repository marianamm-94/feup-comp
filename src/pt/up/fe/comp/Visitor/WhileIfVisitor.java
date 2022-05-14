package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmAnalyser;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

/*
public class WhileIfVisitor extends PreorderJmmVisitor<Analysis, Boolean>{
    public WhileIfVisitor(){
        addVisit("WhileStatment", this::visitWhileIf);
        addVisit("IfStatement", this::visitWhileIf);
    }

    public Boolean visitWhileIf(JmmNode node, Analysis analysis){
        JmmNode conditionNode = node.getChildren().get(0);
        String parentMethodName = Utils.getParentMethodName(node);

        if (Utils.isBoolOp(conditionNode.getKind())) return true;
        if (Utils.isMathOp(conditionNode.getKind()))
            analysis.newReport(conditionNode,"\"" + conditionNode+ "\" expecting a boolean expression.");
        else if (conditionNode.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(conditionNode, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("boolean"))
                analysis.newReport(conditionNode,"\"" + conditionNode + "\" invalid type: expecting an boolean.");
        }
        else if (!Utils.getVariableType(conditionNode, analysis, parentMethodName).equals("boolean"))
            analysis.newReport(conditionNode,"\"" + conditionNode+ "\" expecting a boolean expression.");

        return true;
    }
}

 */