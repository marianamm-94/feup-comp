package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.ReportType;

import javax.sound.midi.SysexMessage;
import java.util.List;

public class BinOperationVisitor extends PreorderJmmVisitor<Analysis, Boolean> {

    public BinOperationVisitor() {
        addVisit("BinOp", this::BinOperationVisit);
    }

    public Boolean BinOperationVisit(JmmNode node, Analysis analysis) {

        String op = node.get("op");
        //System.out.println(op);

        if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult")){
            return verifyMath(node, analysis, op);
        }
        if(op.equals("less")){
            return verifyLessThan(node, analysis, op);
        }
        if(op.equals("and")){

        }

        return true;
    }

    public Boolean verifyMath(JmmNode node, Analysis symbolTableReport, String op) {

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        checkNodeExpression(leftNode, symbolTableReport, op);
        checkNodeExpression(rightNode, symbolTableReport, op);

        return true;
    }

    private Boolean checkNodeExpression(JmmNode node, Analysis analysis, String operation){

        JmmMethod method;
        Type type = new Type("void",false);



        if(node.getKind().equals("BinOp")){
            if(node.get("op").equals("less") || node.get("op").equals("and"))
                analysis.newReport(node, "\"" + node + "\" this operator is not valid.");
        }
        else if (node.getKind().equals("EEIdentifier")) {

            String typeVar = node.get("name");
            List<Symbol> checkExist = analysis.getSymbolTable().getLocalVariables(typeVar);

            System.out.println("sfsdfd");

            if(typeVar.equals("int")){
                //if(!typeArray){}
                //else{
                    //check if on bounds
                //}
            }
            else{
                analysis.newReport(node, "\""+node+"\" is not a valid operand (type not int).");
            }
        }

        return true;
    }


    public Boolean verifyLessThan(JmmNode node, Analysis analysis, String op){

        JmmNode leftNode = node.getChildren().get(0);
        JmmNode rightNode = node.getChildren().get(1);

        if(leftNode.getKind().equals("EEInt")){}
        else if(leftNode.getKind().equals("EEIdentifier")){
            JmmNode typeNode = node.getChildren().get(0);
            String typeVar = typeNode.get("name");
            Boolean typeArray = ("true" == typeNode.get("isArray"));
            if(typeVar != "int"){
                analysis.newReport(leftNode, "\"" + leftNode + "\" invalid type: expected int.");
            }
            else if(typeArray){
                //check if on bounds
            }
        }

        if(rightNode.getKind().equals("EEInt")){}
        else if(rightNode.getKind().equals("EEIdentifier")){
            JmmNode typeNode = node.getChildren().get(0);
            String typeVar = typeNode.get("name");
            Boolean typeArray = ("true" == typeNode.get("isArray"));
            if(typeVar != "int"){
                analysis.newReport(rightNode, "\"" + rightNode + "\" invalid type: expected int.");
            }
            else if(typeArray){
                //check if on bounds
            }
        }
        else if(rightNode.getKind()=="BinOp"){
            if(rightNode.get("op") == "add" || node.get("op") == "sub" ){}
            if(node.get("op") == "div" || node.get("op") == "mult"){}
            else analysis.newReport(node, "\"" + node + "\" invalid type: expected int.");
        }

        return true;
    }

}
