package com.ss.editor.ui;

import com.ss.editor.manager.IconManager;

import javafx.scene.image.Image;

/**
 * Интерфейс для перечисление иконок.
 *
 * @author Ronn
 */
public interface Icons {

    public static final IconManager ICON_MANAGER = IconManager.getInstance();

    public static final Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/cube.png");
    public static final Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/sphere.png");
    public static final Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/actions/16/plane.png");

    public static final Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/refresh.png");
    public static final Image REMOVE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/remove.png");
    public static final Image ADD_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/add.png");
    public static final Image CLOSE_18 = ICON_MANAGER.getImage("/ui/icons/actions/18/close.png");

    public static final Image SAVE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/save.png");
    public static final Image ADD_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add.png");
    public static final Image LIGHT_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/light.png");
    public static final Image CUBE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/cube.png");
    public static final Image SPHERE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/sphere.png");
    public static final Image PLANE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/plane.png");
    public static final Image ADD_CIRCLE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/add_circle.png");
    public static final Image IMAGE_24 = ICON_MANAGER.getImage("/ui/icons/actions/24/image.png");
}