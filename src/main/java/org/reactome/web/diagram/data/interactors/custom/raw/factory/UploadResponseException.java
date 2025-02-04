package org.reactome.web.diagram.data.interactors.custom.raw.factory;



/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class UploadResponseException extends Exception {

    public UploadResponseException(){}

    public UploadResponseException(String message) {
        super(message);
    }

    public UploadResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}
