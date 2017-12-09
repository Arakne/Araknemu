package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreated;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreationError;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle character creation {@link AddCharacterRequest}
 */
final public class CreateCharacter implements PacketHandler<GameSession, AddCharacterRequest> {
    final private CharactersService service;

    public CreateCharacter(CharactersService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AddCharacterRequest packet) throws Exception {
        try {
            service.create(
                AccountCharacter.fromRequest(
                    session.account(),
                    packet
                )
            );
        } catch (CharacterCreationException e) {
            throw new ErrorPacket(
                new CharacterCreationError(e.error()),
                e.getCause()
            );
        }

        session.write(new CharacterCreated());
        session.write(
            new CharactersList(
                session.account().remainingTime(),
                service.list(session.account())
            )
        );
    }

    @Override
    public Class<AddCharacterRequest> packet() {
        return AddCharacterRequest.class;
    }
}
