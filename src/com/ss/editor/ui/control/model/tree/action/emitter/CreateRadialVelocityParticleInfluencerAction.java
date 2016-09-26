package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.RadialVelocityInfluencer;

/**
 * The action for creating the {@link RadialVelocityInfluencer} for the {@link
 * ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateRadialVelocityParticleInfluencerAction extends AbstractCreateParticleInfluencerAction {

    public CreateRadialVelocityParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Radial velocity influencer";
    }

    @NotNull
    @Override
    protected ParticleInfluencer createInfluencer() {
        return new RadialVelocityInfluencer();
    }
}
