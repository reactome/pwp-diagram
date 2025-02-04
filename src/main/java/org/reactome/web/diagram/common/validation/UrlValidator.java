package org.reactome.web.diagram.common.validation;

import com.google.gwt.regexp.shared.RegExp;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class UrlValidator extends AbstractValidator {

    private static final String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static RegExp regExp;

    public UrlValidator() {
        regExp = RegExp.compile(regex);
    }

    @Override
    public boolean validate(String input) {
        boolean rtn = true;
        String aux = input.trim();
        if(aux.isEmpty()) {
            rtn = false;
            setTooltip("URL cannot be empty. Please provide one.");
        } else if(regExp.exec(aux) == null) {
            rtn = false;
            setTooltip("URL is not valid.");
        }
        showIcon(rtn);
        return rtn;
    }

}
