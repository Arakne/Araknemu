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
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.handler.EnsureLogged;
import fr.quatrevieux.araknemu.game.handler.account.CreateCharacter;
import fr.quatrevieux.araknemu.game.handler.account.DeleteCharacter;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.game.handler.account.SaveClientUid;
import fr.quatrevieux.araknemu.game.handler.account.SelectCharacter;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for logged packets
 */
public final class LoggedLoader extends AbstractLoader {
    public LoggedLoader() {
        super(EnsureLogged::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ListCharacters(
                container.get(CharactersService.class)
            ),
            new CreateCharacter(
                container.get(CharactersService.class)
            ),
            new SelectCharacter(
                container.get(PlayerService.class)
            ),
            new DeleteCharacter(
                container.get(CharactersService.class),
                container.get(GameConfiguration.class).player()
            ),
            new SaveClientUid(),
        };
    }
}
