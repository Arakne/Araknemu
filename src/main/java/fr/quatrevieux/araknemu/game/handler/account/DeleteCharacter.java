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

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.DeleteCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterDeleted;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Delete the character
 */
final public class DeleteCharacter implements PacketHandler<GameSession, DeleteCharacterRequest> {
    final private CharactersService service;
    final private GameConfiguration.PlayerConfiguration configuration;

    public DeleteCharacter(CharactersService service, GameConfiguration.PlayerConfiguration configuration) {
        this.service = service;
        this.configuration = configuration;
    }

    @Override
    public void handle(GameSession session, DeleteCharacterRequest packet) throws Exception {
        try {
            AccountCharacter character = service.get(
                session.account(),
                packet.id()
            );

            if (
                character.level() >= configuration.deleteAnswerLevel()
                && !session.account().checkAnswer(packet.answer())
            ) {
                throw new Exception("Bad secret answer");
            }

            service.delete(character);
        } catch (Exception e) {
            throw new ErrorPacket(new CharacterDeleted(false), e);
        }

        session.write(new CharacterDeleted(true));
        session.write(
            new CharactersList(
                session.account().remainingTime(),
                service.list(session.account())
            )
        );
    }

    @Override
    public Class<DeleteCharacterRequest> packet() {
        return DeleteCharacterRequest.class;
    }
}
