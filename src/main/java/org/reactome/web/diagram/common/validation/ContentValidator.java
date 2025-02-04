package org.reactome.web.diagram.common.validation;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ContentValidator extends AbstractValidator {

    @Override
    public boolean validate(String input) {
        boolean rtn = true;
        String aux = input.trim();
        if(aux.isEmpty()) {
            rtn = false;
            setTooltip("Please copy and paste your data.");
        }
        showIcon(rtn);
        return rtn;
    }
}
