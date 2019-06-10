package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterSelected;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterSelectionError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle character select for entering game
 */
final public class SelectCharacter implements PacketHandler<GameSession, ChoosePlayingCharacter> {
    final private PlayerService service;

    public SelectCharacter(PlayerService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, ChoosePlayingCharacter packet) throws Exception {
        try {
            service.load(session, packet.id()).register(session);
        } catch (EntityNotFoundException e) {
            throw new CloseWithPacket(new CharacterSelectionError());
        }

        session.write(new CharacterSelected(session.player()));
        session.player().dispatch(new GameJoined());
        session.write(Error.welcome());
    }

    @Override
    public Class<ChoosePlayingCharacter> packet() {
        return ChoosePlayingCharacter.class;
    }
}
