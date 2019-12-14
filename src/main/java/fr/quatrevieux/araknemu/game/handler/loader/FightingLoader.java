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
import fr.quatrevieux.araknemu.game.handler.EnsureFighting;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterReadyState;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterStartPlace;
import fr.quatrevieux.araknemu.game.handler.fight.EndFighterTurn;
import fr.quatrevieux.araknemu.game.handler.fight.LeaveFight;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Loader for fight packets
 */
final public class FightingLoader extends AbstractLoader {
    public FightingLoader() {
        super(EnsureFighting::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] handlers(Container container) {
        return new PacketHandler[] {
            new ChangeFighterStartPlace(),
            new ChangeFighterReadyState(),
            new EndFighterTurn(),
            new LeaveFight()
        };
    }
}
