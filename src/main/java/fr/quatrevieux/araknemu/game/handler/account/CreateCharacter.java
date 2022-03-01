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
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreated;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreationError;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Handle character creation {@link AddCharacterRequest}
 */
public final class CreateCharacter implements PacketHandler<GameSession, AddCharacterRequest> {
    private final CharactersService service;

    public CreateCharacter(CharactersService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AddCharacterRequest packet) throws Exception {
        final GameAccount account = NullnessUtil.castNonNull(session.account());

        try {
            service.create(AccountCharacter.fromRequest(account, packet));
        } catch (CharacterCreationException e) {
            throw new ErrorPacket(
                new CharacterCreationError(e.error()),
                e.getCause()
            );
        }

        session.send(new CharacterCreated());
        session.send(
            new CharactersList(
                account.remainingTime(),
                service.list(account)
            )
        );
    }

    @Override
    public Class<AddCharacterRequest> packet() {
        return AddCharacterRequest.class;
    }
}
