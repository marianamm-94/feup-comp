package pt.up.fe.comp.Jasmin;

import java.util.Collections;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

public class JasminEmitter implements JasminBackend {

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        String jasminCode="";
        return new JasminResult(ollirResult, jasminCode, Collections.emptyList());
    }
    
}
