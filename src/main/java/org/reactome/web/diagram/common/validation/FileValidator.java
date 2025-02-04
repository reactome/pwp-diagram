package org.reactome.web.diagram.common.validation;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FileValidator extends AbstractValidator {
    @Override
    public boolean validate(String input) {
        boolean rtn = true;
        String aux = input.trim();
        if(aux.isEmpty()) {
            rtn = false;
            setTooltip("Please select a file and try again.");
        }
        showIcon(rtn);
        return rtn;
    }
}
