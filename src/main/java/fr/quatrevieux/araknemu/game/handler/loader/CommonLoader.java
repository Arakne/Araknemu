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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.game.handler.*;
import fr.quatrevieux.araknemu.game.handler.account.GenerateName;
import fr.quatrevieux.araknemu.game.handler.account.Login;
import fr.quatrevieux.araknemu.game.handler.account.SendRegionalVersion;
import fr.quatrevieux.araknemu.game.handler.basic.SendDateAndTime;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for common packets
 */
final public class CommonLoader implements Loader {
    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new StartSession(),
            new StopSession(),
            new CloseInactiveSession(),
            new CheckQueuePosition(),
            new Login(
                container.get(TokenService.class),
                container.get(AccountService.class),
                container.get(SessionLogService.class)
            ),
            new SendRegionalVersion(),
            new PongResponse(),
            new SendDateAndTime(),
            new SendPong(),
            new GenerateName(container.get(NameGenerator.class)),
            new SendQuickPong()
        };
    }
}
