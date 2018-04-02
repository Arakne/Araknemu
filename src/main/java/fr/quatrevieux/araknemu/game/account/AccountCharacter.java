package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.EmptyAccessories;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;

/**
 * Character for game account
 */
final public class AccountCharacter {
    final private GameAccount account;
    final private Player entity;
    final private Accessories accessories;

    public AccountCharacter(GameAccount account, Player entity, Accessories accessories) {
        this.account = account;
        this.entity = entity;
        this.accessories = accessories;
    }

    public AccountCharacter(GameAccount account, Player character) {
        this(account, character, new EmptyAccessories());
    }

    /**
     * Get the layer account
     */
    public GameAccount account() {
        return account;
    }

    /**
     * Get the player id
     */
    public int id() {
        return entity.id();
    }

    /**
     * Get the character entity
     */
    public Player character() {
        return entity;
    }

    /**
     * Get the character level
     */
    public int level() {
        return entity.level();
    }

    /**
     * Get sprite info for character
     */
    public SpriteInfo spriteInfo() {
        return new AccountCharacterSpriteInfo(entity, accessories);
    }

    /**
     * Get the player server ID
     */
    public int serverId() {
        return entity.serverId();
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
