package org.reactome.web.diagram.common.validation;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.reactome.web.diagram.data.interactors.custom.ResourcesManager;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class NameValidator extends AbstractValidator {

    private List<String> existingNames;

    public NameValidator() {
        existingNames = ResourcesManager.get().getResourceNames();
    }

    public void addExtraNames(List<String> extraNames){
        existingNames.addAll(extraNames);
    }

    @Override
    public boolean validate(String input) {
        boolean rtn = true;
        String aux = input.trim();
        if (aux.isEmpty()) {
            rtn = false;
            setTooltip("Name cannot be empty. Please provide one.");
        } else if(containsSpaces(aux)) {
            rtn = false;
            setTooltip("Name cannot contain spaces. Please remove them and try again");
        } else if(nameExists(aux)) {
            rtn = false;
            setTooltip("The name '" + aux + "' is already used. Please select another one.");
        }
        showIcon(rtn);
        return rtn;
    }

    private boolean nameExists(String name) {
        for (String existingName : existingNames) {
            if(existingName.toLowerCase().equals(name.toLowerCase())) return true;
        }
        return false;
    }

    private boolean containsSpaces(String name) {
        RegExp regExp = RegExp.compile("\\s");
        MatchResult matcher = regExp.exec(name);
        return matcher != null;
    }

}
