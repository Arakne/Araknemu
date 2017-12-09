package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle {@link AskCharacterList}
 */
final public class ListCharacters implements PacketHandler<GameSession, AskCharacterList> {
    final private CharactersService service;

    public ListCharacters(CharactersService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AskCharacterList packet) throws Exception {
        session.write(
            new CharactersList(
                session.account().remainingTime(),
                service.list(session.account())
            )
        );
    }

    @Override
    public Class<AskCharacterList> packet() {
        return AskCharacterList.class;
    }
}
