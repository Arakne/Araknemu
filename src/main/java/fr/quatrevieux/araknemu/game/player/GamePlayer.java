package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * GamePlayer object
 * A player is a logged character, with associated game session
 */
final public class GamePlayer extends AbstractCharacter {
    final private GameSession session;

    public GamePlayer(GameAccount account, Player entity, GameSession session) {
        super(account, entity);

        this.session = session;
    }
}
