package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.AbstractCharacter;
import fr.quatrevieux.araknemu.game.player.PlayableCharacter;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.EmptyAccessories;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;

/**
 * Character for game account
 */
final public class AccountCharacter extends AbstractCharacter {
    final private Accessories accessories;

    public AccountCharacter(GameAccount account, Player character, Accessories accessories) {
        super(account, character);

        this.accessories = accessories;
    }

    public AccountCharacter(GameAccount account, Player character) {
        this(account, character, new EmptyAccessories());
    }

    @Override
    public void print(Printer printer) {
        super.print(printer);

        printer.accessories(accessories);
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
     * Get the character accessories
     */
    public Accessories accessories() {
        return accessories;
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
