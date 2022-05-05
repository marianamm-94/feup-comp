package pt.up.fe.comp.SymbolTable;  

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.up.fe.comp.jmm.analysis.JmmAnalysis;  

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;  

import pt.up.fe.comp.jmm.analysis.table.SymbolTable;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;


public class JmmAnalyser implements JmmAnalysis {
    JmmSymbolTable symbolTable;
    List<Report> symbolTableReports;

    public JmmAnalyser(){
        this.symbolTable = new JmmSymbolTable();
        this.symbolTableReports = new ArrayList<>();
    }

    public JmmSymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Report> getSymbolTableReports() {
        return symbolTableReports;
    }

    public void newReport(JmmNode node, String msg){
        Report report = new Report(ReportType.ERROR, Stage.SEMANTIC,
                Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), msg);
        symbolTableReports.add(report);
    }

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {         
        //SymbolTable symbolTable = null;

        return new JmmSemanticsResult(parserResult, symbolTable, Collections.emptyList());  

    }  

} 

 