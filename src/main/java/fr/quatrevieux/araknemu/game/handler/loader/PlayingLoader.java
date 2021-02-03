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
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.handler.EnsureInactiveFight;
import fr.quatrevieux.araknemu.game.handler.EnsurePlaying;
import fr.quatrevieux.araknemu.game.handler.account.BoostCharacteristic;
import fr.quatrevieux.araknemu.game.handler.chat.SaveSubscription;
import fr.quatrevieux.araknemu.game.handler.chat.SendMessage;
import fr.quatrevieux.araknemu.game.handler.game.CreateGame;
import fr.quatrevieux.araknemu.game.handler.object.MoveObject;
import fr.quatrevieux.araknemu.game.handler.object.RemoveObject;
import fr.quatrevieux.araknemu.game.handler.spell.MoveSpell;
import fr.quatrevieux.araknemu.game.handler.spell.UpgradeSpell;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for playing packets
 */
public final class PlayingLoader extends AbstractLoader {
    public PlayingLoader() {
        super(EnsurePlaying::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new CreateGame(
                container.get(ExplorationService.class)
            ),
            new SendMessage(
                container.get(ChatService.class)
            ),
            new SaveSubscription(),
            new EnsureInactiveFight(new MoveObject()),
            new EnsureInactiveFight(new RemoveObject()),
            new EnsureInactiveFight(new BoostCharacteristic()),
            new EnsureInactiveFight(new UpgradeSpell()),
            new MoveSpell(),
        };
    }
}
