package org.reactome.web.diagram.context.popups;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.carrousel.client.CarrouselPanel;
import org.reactome.web.carrousel.client.Slide;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.InputPanel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.common.validation.ContentValidator;
import org.reactome.web.diagram.common.validation.FileValidator;
import org.reactome.web.diagram.common.validation.NameValidator;
import org.reactome.web.diagram.common.validation.UrlValidator;
import org.reactome.web.diagram.data.interactors.custom.raw.RawInteractorError;
import org.reactome.web.diagram.data.interactors.custom.raw.RawSummary;
import org.reactome.web.diagram.data.interactors.custom.raw.RawUploadResponse;
import org.reactome.web.diagram.util.Console;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class InsertItemDialog extends PopupPanel implements CustomResourceSubmitter.Handler,
        ValueChangeHandler, ClickHandler, SelectionHandler<Integer>, OpenHandler<DisclosurePanel>, CloseHandler<DisclosurePanel> {

    public interface Handler {
        void onResourceAdded(RawSummary summary);
    }

    private static final String SERVICE_URL_ACTION = "/ContentService/interactors/upload/psicquic/url?name=";
    private static final String TUPLE_URL_ACTION = "/ContentService/interactors/upload/tuple/url?name=";
    private static final String TUPLE_CONTENT_ACTION = "/ContentService/interactors/upload/tuple/content?name=";
    private static final String TUPLE_FILE_ACTION = "/ContentService/interactors/upload/tuple/form?name=";

    private static final String URL = "url";
    private static final String FILE = "file";
    private static final String CONTENT = "content";

    private CustomResourceSubmitter submitter;

    private InputPanel nameInput;
    private InputPanel urlInput;
    private InputPanel fileInput;
    private InputPanel copyPasteInput;
    private InputPanel urlServiceInput;

    private FileUpload fileUpload;
    private FormPanel formPanel;
    private TabLayoutPanel tabPanel;

    private List<Button> tabButtons = new LinkedList<>();
    private Button addDataTabBtn;
    private Button addServiceTabBtn;

    private Button submitBtn;
    private Button cancelBtn;

    private StatusReport statusReport;
    private String selectedOption;
    private Handler handler;

    public InsertItemDialog(Handler handler) {
        super();
        this.handler = handler;
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);
        this.setStyleName(RESOURCES.getCSS().popupPanel());

        submitter = new CustomResourceSubmitter(this);
        statusReport = new StatusReport();

        initUI();
    }

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
    }

    @Override
    public void show() {
        super.show();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                center();
            }
        });
    }

    @Override
    public void onClick(ClickEvent event) {
        IconButton btn = (IconButton) event.getSource();
        int selectedIndex = tabPanel.getSelectedIndex();
        boolean validationResult;
        if (btn.equals(submitBtn)) {
            validationResult = nameInput.validate();
            String name = nameInput.getText().trim();
            if (selectedIndex == 0) {        //Tuple tab
                switch (selectedOption) {
                    case URL:
                        validationResult = validationResult && urlInput.validate();
                        if (validationResult) submitter.submit(urlInput.getText(), TUPLE_URL_ACTION + name);
                        break;
                    case FILE:
                        validationResult = validationResult && fileInput.validate();
                        if (validationResult) submitter.submit(formPanel, TUPLE_FILE_ACTION + name);
                        break;
                    case CONTENT:
                        validationResult = validationResult && copyPasteInput.validate();
                        if (validationResult) submitter.submit(copyPasteInput.getText(), TUPLE_CONTENT_ACTION + name);
                        break;
                }
            } else {                        //Service tab
                validationResult = validationResult && urlServiceInput.validate();
                if (validationResult) submitter.submit(urlServiceInput.getText(), SERVICE_URL_ACTION + name);
            }

        } else if (btn.equals(cancelBtn)) {
            submitter.cancel();
            this.hide();
        }
    }

    @Override
    public void onSelection(SelectionEvent event) {
        Integer index = (Integer) event.getSelectedItem();
        for (int i = 0; i < tabButtons.size(); i++) {
            Button btn = tabButtons.get(i);
            if (index.equals(i)) {
                btn.addStyleName(RESOURCES.getCSS().tabButtonSelected());
            } else {
                btn.removeStyleName(RESOURCES.getCSS().tabButtonSelected());
            }
        }
    }

    @Override
    public void onSubmission() {
        showLoading(true);
    }

    @Override
    public void onSubmissionCompleted(RawUploadResponse response, long time) {
        showLoading(false);
        this.hide();
        List<String> warnings = response.getWarningMessages();
        if (warnings != null && !warnings.isEmpty()) {
            Console.info(warnings);
            statusReport.showWarnings("Successful submission with warnings (" + warnings.size() + ")", warnings);
        } else {
            statusReport.showSuccess("Successful submission", response.getSummary());
            Console.info("New Token:" + response.getSummary().getToken());
        }
        handler.onResourceAdded(response.getSummary());
    }

    @Override
    public void onSubmissionException(String message) {
        showLoading(false);
        statusReport.showErrors("Error on submission", Collections.singletonList(message));
    }

    @Override
    public void onSubmissionError(RawInteractorError error) {
        showLoading(false);
        statusReport.showErrors(error.getReason() + "(" + error.getCode() + ")", error.getMessages());
    }

    @Override
    public void onOpen(OpenEvent<DisclosurePanel> openEvent) {
        smoothCenter();
    }

    @Override
    public void onClose(CloseEvent<DisclosurePanel> closeEvent) {
        smoothCenter();
    }

    private void smoothCenter(){
        AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
            final long start = System.currentTimeMillis();
            @Override
            public void execute(double timestamp) {
                center();
                long t = System.currentTimeMillis() - start;
                //The animation time for the disclosure panel is 350
                if(t < 400)  AnimationScheduler.get().requestAnimationFrame(this); // Call it again.
            }
        });
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        selectedOption = selectedBtn.getFormValue(); // Keep current selection
        urlInput.setVisible(false);
        formPanel.setVisible(false);
        fileInput.setVisible(false);
        copyPasteInput.setVisible(false);
        switch (selectedOption) {
            case URL:
                urlInput.setVisible(true);
                break;
            case FILE:
                formPanel.setVisible(true);
                fileInput.setVisible(true);
                break;
            case CONTENT:
                copyPasteInput.setVisible(true);
                break;
        }
    }

    private void initUI() {
        ResourceCSS css = RESOURCES.getCSS();
        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(css.containerPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        nameInput = new InputPanel("Name:", new TextBox(), new NameValidator(), css.namePanel(), css.infoLabel(), css.inputTB(), css.explanation());
        nameInput.setHintMessage("Enter the name of your resource");

        RadioButton urlBtn = new RadioButton("UploadOption", "URL");
        urlBtn.setFormValue(URL); //use FormValue to keep the value
        urlBtn.setTitle("Provide the URL of your data");
        urlBtn.setStyleName(css.uploadOptionBtn());
        urlBtn.addValueChangeHandler(this);
        RadioButton fileBtn = new RadioButton("UploadOption", "File");
        fileBtn.setFormValue(FILE); //use FormValue to keep the value
        fileBtn.setTitle("Provide the file of your data");
        fileBtn.setStyleName(css.uploadOptionBtn());
        fileBtn.addValueChangeHandler(this);
        fileBtn.setValue(true); selectedOption = FILE;
        RadioButton pasteBtn = new RadioButton("UploadOption", "Copy & Paste");
        pasteBtn.setFormValue(CONTENT); //use FormValue to keep the value
        pasteBtn.setTitle("Copy and paste your data directly");
        pasteBtn.setStyleName(css.uploadOptionBtn());
        pasteBtn.addValueChangeHandler(this);
        FlowPanel uploadOptionsPanel = new FlowPanel();
        uploadOptionsPanel.setStyleName(css.rowPanel());
        uploadOptionsPanel.add(fileBtn);
        uploadOptionsPanel.add(pasteBtn);
        uploadOptionsPanel.add(urlBtn);

        urlInput = new InputPanel("URL:", new TextBox(), new UrlValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        urlInput.setHintMessage("Enter the URL of your data");
        urlInput.setExplanation("Use this option to upload a publicly accessible file stored on the network, though its URL.");
        urlInput.setVisible(false);

        fileInput = new InputPanel("File:", new TextBox(), new FileValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        fileInput.setHintMessage("Click here to choose your file");
        fileInput.setExplanation("Use this option to upload a file stored locally on your computer.");
        fileInput.setReaOnly(true);
        fileInput.setVisible(true);

        formPanel = getFormPanel();
        formPanel.setVisible(true);

        copyPasteInput = new InputPanel("Paste:", new TextArea(), new ContentValidator(), css.rowPanel(), css.infoLabel(), css.textArea(), css.explanation());
        copyPasteInput.setHintMessage("Copy & paste your data here");
        copyPasteInput.setExplanation("Use this option to copy and paste your data in columns. ");
        copyPasteInput.setVisible(false);

        FlowPanel addDataFP = new FlowPanel();
        addDataFP.setStyleName(css.addDataPanel());
        addDataFP.add(uploadOptionsPanel);
        addDataFP.add(urlInput);
        addDataFP.add(formPanel);
        addDataFP.add(fileInput);
        addDataFP.add(copyPasteInput);

        urlServiceInput = new InputPanel("URL:", new TextBox(), new UrlValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        urlServiceInput.setHintMessage("Enter the URL of your PSICQUIC service");
        urlServiceInput.setExplanation("Use this option to add your custom PSICQUIC service through its URL.");

        FlowPanel addServiceFP = new FlowPanel();
        addServiceFP.setStyleName(css.addServicePanel());
        addServiceFP.add(urlServiceInput);

        DisclosurePanel dp = new DisclosurePanel(RESOURCES.close(), RESOURCES.open(), "Click here to learn more about the custom resources data upload");
        dp.addStyleName(css.optionsPanelDisclosure());
        dp.add(getTuplesCarousel());
        dp.getContent().addStyleName(css.optionsPanelDisclosureContent());
        dp.addOpenHandler(this);
        dp.addCloseHandler(this);
        dp.setAnimationEnabled(true);

        tabPanel = new TabLayoutPanel(4, Style.Unit.EM);
        tabPanel.setStyleName(css.tabPanel());
        tabPanel.add(addDataFP, addDataTabBtn = getButton("Add your data", RESOURCES.newDataIcon()));
        tabPanel.add(addServiceFP, addServiceTabBtn = getButton("Add your PSICQUIC service", RESOURCES.newServiceIcon()));
        tabPanel.addSelectionHandler(this);
        tabPanel.selectTab(0);
        addDataTabBtn.addStyleName(css.tabButtonSelected());

        submitBtn = new IconButton("Submit", RESOURCES.submitIcon());
        submitBtn.setStyleName(css.submitBtn());
        submitBtn.addClickHandler(this);
        cancelBtn = new IconButton("Cancel", RESOURCES.cancelIcon());
        cancelBtn.setStyleName(css.submitBtn());
        cancelBtn.addClickHandler(this);

        FlowPanel actionPanel = new FlowPanel();
        actionPanel.setStyleName(css.actionPanel());
        actionPanel.setVisible(true);
        actionPanel.add(submitBtn);
        actionPanel.add(cancelBtn);

        vp.add(nameInput);
        vp.add(tabPanel);
        vp.add(dp);
        vp.add(actionPanel);
        this.add(vp);

        showLoading(false);
    }

    private Widget setTitlePanel() {
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        Label title = new Label("Add a new resource");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new PwpButton("Close this dialog", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                InsertItemDialog.this.hide();
            }
        });
        header.add(image);
        header.add(title);
        header.add(closeBtn);
        return header;
    }

    public Button getButton(String text, ImageResource imageResource) {
        Image buttonImg = new Image(imageResource);
        Label buttonLbl = new Label(text);

        FlowPanel fp = new FlowPanel();
        fp.add(buttonImg);
        fp.add(buttonLbl);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml);
        btn.setStyleName(RESOURCES.getCSS().tabButton());
        tabButtons.add(btn);
        return btn;
    }

    private FormPanel getFormPanel() {
        fileUpload = new FileUpload();
        fileUpload.setStyleName(RESOURCES.getCSS().fileUpload());
        fileUpload.setName("file");
        fileUpload.setTitle("Select a file to analyse");
        fileUpload.setEnabled(true);
        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String filename = fileUpload.getFilename();
                if (filename != null && !filename.isEmpty()) {
                    if (filename.contains("\\")) {
                        fileInput.setText(filename.substring(fileUpload.getFilename().lastIndexOf("\\") + 1));
                    } else {
                        fileInput.setText(filename);
                    }
                } else {
                    fileInput.setText("");
                }
            }
        });
//        fileUpload.getElement().setAttribute("accept", ".txt");
        FormPanel form = new FormPanel();
        form.addSubmitHandler(submitter);
        form.addSubmitCompleteHandler(submitter);
        form.add(fileUpload);
        return form;
    }

    private CarrouselPanel getTuplesCarousel() {
        List<Slide> slidesList = new LinkedList<>();
        slidesList.add(new Slide(RESOURCES.tuplesSlide01(), "You can import and overlay your data<br>onto pathways by defining custom resources", "white", 12));
        slidesList.add(new Slide(RESOURCES.tuplesSlide02(), "A custom resource can be defined by providing a<br>local or network-stored file or a PSICQUIC service", "white", 12));
        slidesList.add(new Slide(RESOURCES.tuplesSlide03(), "The simplest way to submit data is in a two-column file<br>(tsv/csv) with the interactors defined in columns 1 and 2", "white", 12));
        slidesList.add(new Slide(RESOURCES.tuplesSlide04(), "The extended tuple format offers more options (alias, scores, etc).<br>This information will be displayed and used in the overlay", "white", 12));

        CarrouselPanel carouselPanel = new CarrouselPanel(slidesList, 400, 240, "white");
        carouselPanel.getElement().getStyle().setMarginLeft(50, Style.Unit.PX);
        carouselPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        return carouselPanel;
    }

    private void showLoading(boolean loading) {
        if (loading) {
            ((IconButton) submitBtn).setImage(RESOURCES.loader());
        } else {
            ((IconButton) submitBtn).setImage(RESOURCES.submitIcon());
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/addNewResources.png")
        ImageResource headerIcon();

        @Source("images/addNewData.png")
        ImageResource newDataIcon();

        @Source("images/addNewService.png")
        ImageResource newServiceIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/ok.png")
        ImageResource submitIcon();

        @Source("images/cancel.png")
        ImageResource cancelIcon();

        @Source("images/loader.gif")
        ImageResource loader();

        @Source("images/plus.png")
        ImageResource open();

        @Source("images/minus.png")
        ImageResource close();

        @Source("tuples/slide_01.png")
        ImageResource tuplesSlide01();

        @Source("tuples/slide_02.png")
        ImageResource tuplesSlide02();

        @Source("tuples/slide_03.png")
        ImageResource tuplesSlide03();

        @Source("tuples/slide_04.png")
        ImageResource tuplesSlide04();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-InsertItemDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/InsertItemDialog.css";

        String popupPanel();

        String containerPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String namePanel();

        String rowPanel();

        String actionPanel();

        String tabPanel();

        String tabButton();

        String tabButtonSelected();

        String addDataPanel();

        String addServicePanel();

        String infoLabel();

        String inputTB();

        String uploadOptionBtn();

        String fileUpload();

        String textArea();

        String explanation();

        String optionsPanelDisclosure();

        String optionsPanelDisclosureContent();

        String submitBtn();
    }
}
