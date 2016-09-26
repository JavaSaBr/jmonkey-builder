package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.SpriteInfluencer;

/**
 * The action for creating the {@link SpriteInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateSpriteParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateSpriteParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Sprite influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new SpriteInfluencer();
    }
}
