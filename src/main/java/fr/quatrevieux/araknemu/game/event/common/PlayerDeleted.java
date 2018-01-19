package fr.quatrevieux.araknemu.game.event.common;

import fr.quatrevieux.araknemu.game.account.AccountCharacter;

/**
 * Event trigger when a character is deleted
 */
final public class PlayerDeleted {
    final private AccountCharacter character;

    public PlayerDeleted(AccountCharacter character) {
        this.character = character;
    }

    public AccountCharacter character() {
        return character;
    }
}
