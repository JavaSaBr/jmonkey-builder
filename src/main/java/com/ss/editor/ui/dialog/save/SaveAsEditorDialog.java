package com.ss.editor.ui.dialog.save;

import static com.ss.editor.Messages.SAVE_AS_EDITOR_DIALOG_FIELD_FILENAME;
import static com.ss.editor.Messages.SAVE_AS_EDITOR_DIALOG_TITLE;
import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of the {@link EditorDialog} to choose how to save an object.
 *
 * @author JavaSaBr
 */
public class SaveAsEditorDialog extends AbstractSimpleEditorDialog {

    /**
     * The constant DIALOG_SIZE.
     */
    @NotNull
    protected static final Point DIALOG_SIZE = new Point(900, -1);

    /**
     * The event manager.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    @NotNull
    private final EventHandler<Event> createdFileHandler = event -> processEvent((CreatedFileEvent) event);

    @NotNull
    private final EventHandler<Event> selectFileHandle = event -> processEvent((RequestSelectFileEvent) event);

    @NotNull
    private final EventHandler<Event> deletedFileHandler = event -> processEvent((DeletedFileEvent) event);

    /**
     * The list of waited files to select.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The function for handling the choose.
     */
    @NotNull
    protected final Consumer<@NotNull Path> consumer;

    /**
     * The file extension.
     */
    @Nullable
    private String extension;

    /**
     * The tree with all resources.
     */
    @Nullable
    private ResourceTree resourceTree;

    /**
     * The filename field.
     */
    @Nullable
    private TextField fileNameField;

    public SaveAsEditorDialog(@NotNull final Consumer<@NotNull Path> consumer) {
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
        this.consumer = consumer;
    }

    /**
     * Get the title of filename field.
     *
     * @return the title of filename field.
     */
    protected @NotNull String getFileNameLabelText() {
        return SAVE_AS_EDITOR_DIALOG_FIELD_FILENAME;
    }

    /**
     * Set the target file extension.
     *
     * @param extension the target file extension.
     */
    public void setExtension(@NotNull final String extension) {
        this.extension = extension;
        getResourceTree().setExtensionFilter(ArrayFactory.asArray(extension));
    }

    /**
     * Get the target file extension.
     *
     * @return the target file extension.
     */
    private @NotNull String getExtension() {
        return extension == null ? "" : extension;
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    public void setActionTester(@NotNull final Predicate<@NotNull Class<?>> actionTester) {
        getResourceTree().setActionTester(actionTester);
    }

    /**
     * Sets only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    public void setOnlyFolders(final boolean onlyFolders) {
        getResourceTree().setOnlyFolders(onlyFolders);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final HBox container = new HBox();
        container.prefWidthProperty().bind(widthProperty());

        final GridPane settingsContainer = new GridPane();
        settingsContainer.prefWidthProperty().bind(container.widthProperty().multiply(0.5));
        settingsContainer.prefHeightProperty().bind(container.heightProperty());

        resourceTree = new ResourceTree(null, true);
        resourceTree.prefWidthProperty().bind(container.widthProperty().multiply(0.5));

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFileName());

        createSettings(settingsContainer);

        FXUtils.addToPane(resourceTree, container);
        FXUtils.addToPane(settingsContainer, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(root, CSSClasses.SAVE_AS_DIALOG);
        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(settingsContainer, CSSClasses.DEF_GRID_PANE);
    }

    /**
     * Create settings of the creating file.
     *
     * @param root the root
     */
    @FXThread
    protected void createSettings(@NotNull final GridPane root) {

        final Label fileNameLabel = new Label(getFileNameLabelText() + ":");
        fileNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        fileNameField = new TextField();
        fileNameField.prefWidthProperty().bind(root.widthProperty());
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFileName());
        fileNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(fileNameLabel, 0, 0);
        root.add(fileNameField, 1, 0);

        FXUtils.addClassTo(fileNameLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(fileNameField, CSSClasses.DIALOG_FIELD);
    }

    /**
     * Validate the inputted name.
     */
    @FXThread
    protected void validateFileName() {

        final Button okButton = getOkButton();
        if (okButton == null) return;

        final Path fileToCreate = getFileToSave();

        if (fileToCreate == null) {
            okButton.setDisable(true);
            return;
        }

        okButton.setDisable(false);
    }

    /**
     * Gets file to create.
     *
     * @return the file to creating.
     */
    @FromAnyThread
    protected @Nullable Path getFileToSave() {

        final TextField fileNameField = getFileNameField();
        final String filename = fileNameField.getText();
        if (StringUtils.isEmpty(filename)) return null;

        final String fileExtension = getExtension();

        final Path selectedFile = getSelectedFile();
        if (selectedFile == null) return null;

        final Path directory = Files.isDirectory(selectedFile) ? selectedFile : selectedFile.getParent();

        return StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) :
                directory.resolve(filename + "." + fileExtension);
    }

    /**
     * @return the selected file in the resources tree.
     */
    @FromAnyThread
    private @Nullable Path getSelectedFile() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();
        if (selectedItem == null) return null;

        final ResourceElement element = selectedItem.getValue();
        return element.getFile();
    }

    /**
     * Get the filename field.
     *
     * @return the filename field.
     */
    protected @NotNull TextField getFileNameField() {
        return notNull(fileNameField);
    }

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ResourceTree resourceTree = getResourceTree();
        final Path currentAsset = notNull(editorConfig.getCurrentAsset());

        resourceTree.fill(currentAsset);

        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        validateFileName();

        EXECUTOR_MANAGER.addFXTask(getFileNameField()::requestFocus);
    }

    /**
     * Handle creating file event.
     */
    private void processEvent(@NotNull final CreatedFileEvent event) {

        final Path file = event.getFile();

        final Array<Path> waitedFilesToSelect = getWaitedFilesToSelect();
        final boolean waitedSelect = waitedFilesToSelect.contains(file);

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (waitedSelect) waitedFilesToSelect.fastRemove(file);
        if (waitedSelect || event.isNeedSelect()) resourceTree.expandTo(file, true);
    }

    /**
     * Handle deleting file event.
     */
    private void processEvent(@NotNull final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Handle selecting file event.
     */
    private void processEvent(@NotNull final RequestSelectFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * @return the list of waited files to select.
     */
    private @NotNull Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    @Override
    public void hide() {
        FX_EVENT_MANAGER.removeEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler);
        FX_EVENT_MANAGER.removeEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle);
        FX_EVENT_MANAGER.removeEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);
        super.hide();
    }

    /**
     * Gets consumer.
     *
     * @return the function for handling the choose.
     */
    protected @NotNull Consumer<@NotNull Path> getConsumer() {
        return consumer;
    }

    /**
     * @return the tree with all resources.
     */
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    protected @NotNull String getTitleText() {
        return SAVE_AS_EDITOR_DIALOG_TITLE;
    }

    @Override
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    @Override
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_SAVE;
    }

    @Override
    @FXThread
    protected void processOk() {
        super.processOk();
        consumer.accept(notNull(getFileToSave()));
    }
}