package org.reactome.web.diagram.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;
import org.reactome.web.diagram.common.validation.AbstractValidator;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InputPanel<T extends TextBoxBase, V extends AbstractValidator> extends FlowPanel {

    private Label titleLb;
    private T input;
    private V validator;
    private Label explanation;

    public InputPanel(String title, T T, V V, String style, String titleStyle, String inputStyle, String explanationStyle) {
        setStyleName(style);
        titleLb = new Label(title);
        titleLb.setStyleName(titleStyle);
        input = T;
        input.setStyleName(inputStyle);

        validator = V;
        explanation = new Label();
        explanation.setStyleName(explanationStyle);

        add(titleLb);
        add(input);
        add(validator);
        add(explanation);
    }



    public String getText() {
        return input.getText();
    }

    public void setHintMessage(String tip) {
        input.getElement().setPropertyString("placeholder", tip);
    }

    public void setExplanation(String text) {
        explanation.setText(text);
    }

    public void setText(String text) {
        input.setText(text);
    }

    public void setReaOnly(boolean isReadOnly) {
        input.setReadOnly(isReadOnly);
    }

    public boolean validate(){
        boolean rtn = false;
        if(validator != null) {
            rtn = validator.validate(input.getText());
            highlight(rtn);
        }
        return rtn;
    }

    public void highlight(boolean rtn){
        if(!rtn) {
            input.getElement().getStyle().setBackgroundColor("#FFCDD2");
        } else {
            input.getElement().getStyle().clearBackgroundColor();
        }
    }

    public void clear() {
        if(validator!=null) {
            validator.clear();
        }
    }
}
