package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.AbstractCharacter;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;

/**
 * Character for game account
 */
final public class AccountCharacter extends AbstractCharacter {
    public AccountCharacter(GameAccount account, Player character) {
        super(account, character);
    }

    /**
     * Get the character entity
     */
    Player character() {
        return entity;
    }

    /**
     * Get the character level
     */
    public int level() {
        return entity.level();
    }

    /**
     * Create the character from creation request
     *
     * @param account The creator account
     * @param request The request
     */
    static public AccountCharacter fromRequest(GameAccount account, AddCharacterRequest request) {
        return new AccountCharacter(
            account,
            Player.forCreation(
                account.id(),
                account.serverId(),
                request.name(),
                request.race(),
                request.sex(),
                request.colors()
            )
        );
    }
}
