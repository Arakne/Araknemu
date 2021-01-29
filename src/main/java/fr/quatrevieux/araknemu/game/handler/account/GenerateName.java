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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerationException;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskRandomName;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerated;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerationError;

/**
 * Generate a random name for character creation
 */
final public class GenerateName implements PacketHandler<GameSession, AskRandomName> {
    final private NameGenerator generator;

    public GenerateName(NameGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void handle(GameSession session, AskRandomName packet) throws Exception {
        try {
            session.send(
                new RandomNameGenerated(
                    generator.generate()
                )
            );
        } catch (NameGenerationException e) {
            throw new ErrorPacket(new RandomNameGenerationError(RandomNameGenerationError.Error.UNDEFINED), e);
        }
    }

    @Override
    public Class<AskRandomName> packet() {
        return AskRandomName.class;
    }
}
