package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.anim.AnimationTreeNode;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to stop an animation.
 *
 * @author JavaSaBr
 */
public class StopAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Stop animation action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public StopAnimationAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_STOP;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.STOP_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AnimationTreeNode modelNode = (AnimationTreeNode) getNode();
        if (modelNode.getChannel() < 0) return;

        final AnimControl control = modelNode.getControl();
        if (control == null || control.getNumChannels() <= 0) return;

        final AnimChannel channel = control.getChannel(modelNode.getChannel());
        channel.setLoopMode(LoopMode.DontLoop);

        EXECUTOR_MANAGER.addJMETask(control::clearChannels);

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }
}