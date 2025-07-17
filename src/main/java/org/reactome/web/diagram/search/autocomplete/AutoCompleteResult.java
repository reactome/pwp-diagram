package org.reactome.web.diagram.search.autocomplete;

import com.google.gwt.regexp.shared.RegExp;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class AutoCompleteResult {

    private String result;
    private String displayResult;

    public AutoCompleteResult(String result) {
        this.result = result;
        this.displayResult = result;
    }

    public String getResult() {
        return result;
    }

    public String getDisplayResult() {
        return displayResult;
    }

    public void setResultDisplay(RegExp regExp) {
        if(regExp!=null) {
            this.displayResult = regExp.replace(this.displayResult, "<u style=\"color:#1e94d0\"><strong>$1</strong></u>");
        }
    }
}
