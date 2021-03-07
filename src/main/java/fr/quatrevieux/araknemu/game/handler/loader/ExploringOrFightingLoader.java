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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.handler.EnsureFighting;
import fr.quatrevieux.araknemu.game.handler.EnsureInactiveFight;
import fr.quatrevieux.araknemu.game.handler.ExploringOrFightingSwitcher;
import fr.quatrevieux.araknemu.game.handler.chat.SendSmileyToExplorationMap;
import fr.quatrevieux.araknemu.game.handler.chat.SendSmileyToFight;
import fr.quatrevieux.araknemu.game.handler.fight.PerformTurnAction;
import fr.quatrevieux.araknemu.game.handler.fight.TerminateTurnAction;
import fr.quatrevieux.araknemu.game.handler.fight.UseObjectBeforeStart;
import fr.quatrevieux.araknemu.game.handler.game.EndGameAction;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.game.handler.object.UseObject;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for exploring or fighter switch packet handlers
 */
public final class ExploringOrFightingLoader implements Loader {
    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ExploringOrFightingSwitcher<>(
                new ValidateGameAction(container.get(ActionFactory.class)),
                new EnsureFighting<>(new PerformTurnAction())
            ),
            new ExploringOrFightingSwitcher<>(
                new EndGameAction(),
                new EnsureFighting<>(new TerminateTurnAction())
            ),
            new ExploringOrFightingSwitcher<>(
                new UseObject(),
                new EnsureInactiveFight<>(new UseObjectBeforeStart())
            ),
            new ExploringOrFightingSwitcher<>(
                new SendSmileyToExplorationMap(),
                new SendSmileyToFight()
            ),
        };
    }
}
