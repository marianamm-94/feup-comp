package pt.up.fe.comp.SymbolTable;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.stream.Collectors;

public class JmmType extends Type {

    public JmmType(String name, boolean isArray) {
        super(name.trim(), isArray);
    }

    public JmmType(JmmNode node) {
        super(parseName(node).replaceAll("'", "").replace("Identifier ", "").trim(), parseIsArray(node));

    }

    private static String parseName(JmmNode node) {
        if (node.getKind().equals("Type")) return node.getChildren().get(0).getKind();
        return node.getKind().replace("Array", "").trim();
    }

    private static boolean parseIsArray(JmmNode node) {
        if (node.getKind().equals("Type"))
            return (node.getChildren().size() == 2) && (node.getChildren().get(1).getKind().equals("Array"));
        return node.getKind().contains("Array");
    }

    public String printType() {
        String type = getName();
        if (isArray()) type += "[]";
        return type;
    }

    public String toOLLIR() {
        StringBuilder res = new StringBuilder();
        if (isArray()) res.append(".array");
        switch (getName()) {
            case "Int":
                res.append(".i32");
                break;
            case "Boolean":
                res.append(".bool");
                break;
            case "Void":
                res.append(".V");
                break;
            default:
                res.append(".").append(getName());
                break;
        }
        return res.toString();
    }
}