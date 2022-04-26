package pt.up.fe.comp.Visitor;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class SymbolTableVisitor extends PreorderJmmVisitor<Boolean, Boolean>  {
    private final JmmSymbolTable symbolTable;
    private final List<Report> reports;
    
    public SymbolTableVisitor() {
        this.symbolTable = new JmmSymbolTable();
        this.reports = new ArrayList<>();
        addVisit("ClassDeclaration", this::visitClass);
        addVisit("ImportDeclaration", this::visitImport);
        addVisit("MainDeclaration", this::visitMainDeclaration);
        addVisit("OtherMethodDeclaration", this::visitOtherMethodDeclaration);
    }
    public JmmSymbolTable getSymbolTable(){
        return symbolTable;
    }
    public List<Report> getReports(){
        return reports;
    }

    public boolean visitClass(JmmNode node, Boolean dummy){
        String className=node.get("name");
        symbolTable.setClassName(className);
        //como ver o extends?? nome superclasse
        //node.getOptional("super_name");
        return true;
    }
    public boolean visitImport(JmmNode node, Boolean dummy){
        StringBuilder importString = new StringBuilder(node.get("name"));
        for (JmmNode child : node.getChildren())
            importString.append(child.get("name"));
        symbolTable.addImport(importString.toString());
        return true;
    }
    public boolean visitMainDeclaration(JmmNode node, Boolean dummy){
        
        return dummy;
    }
    public boolean visitOtherMethodDeclaration(JmmNode node, Boolean dummy){
        return dummy;
    }
}
