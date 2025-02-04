package org.reactome.web.diagram.context.popups;

import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;
import org.reactome.web.diagram.data.interactors.custom.raw.RawInteractorError;
import org.reactome.web.diagram.data.interactors.custom.raw.RawUploadResponse;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseException;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseFactory;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class CustomResourceSubmitter implements FormPanel.SubmitHandler, FormPanel.SubmitCompleteHandler, RequestCallback {

    public interface Handler {
        void onSubmission();
        void onSubmissionCompleted(RawUploadResponse response, long time);
        void onSubmissionException(String message);
        void onSubmissionError(RawInteractorError error);
    }

    private Handler handler;
    private Request request;

    public CustomResourceSubmitter(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(request!=null && request.isPending()){
            request.cancel();
        }
    }

    public void submit(String data, String url){
        handler.onSubmission();
        // Any previous request has to be canceled
        cancel();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
        requestBuilder.setHeader("Content-Type", "text/plain");
        requestBuilder.setHeader("Accept", "application/json");
        try {
            this.request = requestBuilder.sendRequest(data, this);
        } catch (RequestException e) {
            handler.onSubmissionException(e.getMessage());
        }
    }

    public void submit(FormPanel formPanel, String url){
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setAction(url);
        formPanel.submit();
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                RawUploadResponse uploadResponse;
                try {
                    uploadResponse = UploadResponseFactory.getUploadResponseObject(RawUploadResponse.class, response.getText());
                    long time = System.currentTimeMillis() - start;
                    this.handler.onSubmissionCompleted(uploadResponse, time);
                } catch (UploadResponseException e) {
                    this.handler.onSubmissionException(e.getMessage());
                    return;
                }
                break;
            default:
                try {
                    RawInteractorError error = UploadResponseFactory.getUploadResponseObject(RawInteractorError.class, response.getText());
                    this.handler.onSubmissionError(error);
                } catch (UploadResponseException e) {
                    this.handler.onSubmissionException(e.getMessage());
                }
        }
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        this.handler.onSubmissionException(throwable.getMessage());
    }

    @Override
    public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
        //Work around to extract the content in case it's included in a HTML tag
        Element label = DOM.createLabel();
        label.setInnerHTML( event.getResults() );
        long start = System.currentTimeMillis();
        RawUploadResponse uploadResponse;
        try {
            uploadResponse = UploadResponseFactory.getUploadResponseObject(RawUploadResponse.class, label.getInnerText());
            if(uploadResponse.getSummary()!=null){
                long time = System.currentTimeMillis() - start;
                this.handler.onSubmissionCompleted(uploadResponse, time);
            } else {
                RawInteractorError error = UploadResponseFactory.getUploadResponseObject(RawInteractorError.class, label.getInnerText());
                this.handler.onSubmissionError(error);
            }
        } catch (UploadResponseException e) {
            try {
                RawInteractorError error = UploadResponseFactory.getUploadResponseObject(RawInteractorError.class, label.getInnerText());
                this.handler.onSubmissionError(error);
            } catch (UploadResponseException e1) {
                this.handler.onSubmissionException("Error processing this file. Please contact our help-desk at help@reactome.org");
            }
        }
    }

    @Override
    public void onSubmit(FormPanel.SubmitEvent event) {
        handler.onSubmission();
    }

}
