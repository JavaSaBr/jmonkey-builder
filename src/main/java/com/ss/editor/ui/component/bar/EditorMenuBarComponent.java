package com.ss.editor.ui.component.bar;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.*;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.jetbrains.annotations.NotNull;

/**
 * The menu bar of the Editor.
 *
 * @author JavaSaBr
 */
public class EditorMenuBarComponent extends MenuBar implements ScreenComponent {

    /**
     * The constant COMPONENT_ID.
     */
    @NotNull
    private static final String COMPONENT_ID = "EditorMenuBarComponent";

    /**
     * Instantiates a new Editor bar component.
     */
    public EditorMenuBarComponent() {
        super();
        setId(CSSIds.EDITOR_MENU_BAR_COMPONENT);
        createComponents();
    }

    private void createComponents() {
        final ObservableList<Menu> menus = getMenus();
        menus.addAll(createFileMenu(),
                createOtherMenu(),
                createHelpMenu());
    }

    @NotNull
    private Menu createFileMenu() {

        final Menu menu = new Menu(Messages.EDITOR_MENU_FILE);
        menu.getItems().addAll(new OpenAssetAction(),
                new ReopenAssetMenu(),
                new ExitAction());

        return menu;
    }

    @NotNull
    private Menu createOtherMenu() {

        final Menu menu = new Menu(Messages.EDITOR_MENU_OTHER);
        menu.getItems().addAll(new OpenSettingsAction(),
                new OpenPluginsAction(),
                new ClearAssetCacheAction());

        return menu;
    }

    @NotNull
    private Menu createHelpMenu() {

        final Menu menu = new Menu(Messages.EDITOR_MENU_HELP);
        menu.getItems().addAll(new AboutAction());

        return menu;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}