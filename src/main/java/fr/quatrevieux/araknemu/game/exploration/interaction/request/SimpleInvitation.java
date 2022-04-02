/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Implementation of {@link Invitation} using {@link InvitationHandler}
 */
public final class SimpleInvitation implements Invitation {
    private final ExplorationPlayer initiator;
    private final ExplorationPlayer target;
    private final InvitationHandler handler;

    public SimpleInvitation(InvitationHandler handler, ExplorationPlayer initiator, ExplorationPlayer target) {
        this.handler = handler;
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public ExplorationPlayer initiator() {
        return initiator;
    }

    @Override
    public ExplorationPlayer target() {
        return target;
    }

    @Override
    public @Nullable Interaction start() {
        if (!handler.check(this)) {
            return null;
        }

        try {
            target.interactions().start(handler.targetDialog(this));

            return handler.initiatorDialog(this);
        } finally {
            handler.acknowledge(this);
        }
    }

    @Override
    public void stop() {
        initiator.interactions().remove();
        target.interactions().remove();
    }

    @Override
    public void cancel(RequestDialog dialog) {
        stop();

        handler.refuse(this, dialog);
    }

    @Override
    public void accept(TargetRequestDialog dialog) {
        stop();

        handler.accept(this, dialog);
    }

    /**
     * Send packet to all interlocutors
     */
    @Override
    public void send(Object packet) {
        initiator.send(packet);
        target.send(packet);
    }
}
