package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.SymbolTable.JmmAnalyser;

public class ArrayVisitor extends PreorderJmmVisitor<JmmAnalyser, Boolean> {

    public ArrayVisitor(){
        addVisit("ArrayAccess",this::visitArrayAccess);
        addVisit("NewIntArray", this::visitNewExpression);
        addVisit("ArrayAssignment", this::visitArrayAssignment);
    }

    public Boolean visitArrayAccess(JmmNode arrayNode, JmmAnalyser symbolTableReport){
        JmmNode accessNode = arrayNode.getChildren().get(1);

        AccessedArray(arrayNode, symbolTableReport);
        InsideBrackets(arrayNode, symbolTableReport ,accessNode,"access");

        return true;
    }

    public Boolean visitArrayAssignment(JmmNode arrayNode, JmmAnalyser symbolTableReport){
        JmmNode accessNode = arrayNode.getChildren().get(1).getChildren().get(0);

        AccessedArray(arrayNode, symbolTableReport);
        InsideBrackets(arrayNode, symbolTableReport ,accessNode,"access");

        return true;
    }

    public Boolean visitNewExpression(JmmNode arrayNode, JmmAnalyser symbolTableReport){
        JmmNode sizeNode = arrayNode.getChildren().get(0);

        InsideBrackets(arrayNode, symbolTableReport ,sizeNode,"size");

        return true;
    }


    public void AccessedArray(JmmNode ExpresionNode, JmmAnalyser symbolTableReport){
        JmmNode arrayNode = ExpresionNode.getChildren().get(0);
        String kind = arrayNode.getKind();

        // Check if the Identifier is an array
        if(kind.equals("Identifier")){
            String parentMethodName = Utils.getParentMethod(ExpresionNode);
            String type = Utils.getTypeVar(arrayNode, symbolTableReport, parentMethodName);
            if(!type.equals("int[]") && !type.equals("String[]")){
                symbolTableReport.newReport(arrayNode, "Invalid identifier, must be an int[].");
            }
        }

        // Check if the array is not the return type of a method
        else if(!isReturnArray(arrayNode, symbolTableReport)) {
            symbolTableReport.newReport(arrayNode, "Invalid array access operation. This operation is only valid for int[].");
        }

        return;
    }

    private void InsideBrackets(JmmNode arrayNode, JmmAnalyser symbolTableReport, JmmNode node, String context) {
        String kind = node.getKind();
        // Check if it the index is an identifier
        if(kind.equals("Identifier")){
            String parentMethod = Utils.getParentMethod(arrayNode);
            String type = Utils.getTypeVar(node, symbolTableReport, parentMethod);

            // If it is an identifier, it must be an integer
            if(!type.equals("int")){
                symbolTableReport.newReport(node, "Invalid array " + context + ", identifier must be an integer. Provided: " + type);
            }
        }
        // Check if the index is a number, an expression that returns a numeric value,
        // a function that returns an int or another array access
        else if(!kind.equals("Number") && !Utils.isMathOp(kind) &&
                !kind.equals("ArrayAccess") && !isReturnInt(node,symbolTableReport)) {
                    symbolTableReport.newReport(node, "Invalid array " + context + ". Must be an integer.");
        }
        return;
    }

    private boolean isReturnInt(JmmNode node, JmmAnalyser symbolTableReport){
        if(!node.getKind().equals("fullstop")) return false;

        String returnValue = Utils.getReturnType(node,symbolTableReport); 
        return returnValue.equals("undefined") || returnValue.equals("int");
    }

    private boolean isReturnArray(JmmNode node, JmmAnalyser symbolTableReport){
        if(!node.getKind().equals("fullstop")) return false;

        String returnValue = Utils.getReturnType(node,symbolTableReport); 
        return returnValue.equals("undefined") || returnValue.equals("int[]");
    }
    
}
