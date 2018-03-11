package fr.quatrevieux.araknemu.game.event.manage;

import fr.quatrevieux.araknemu.game.account.AccountCharacter;

/**
 * Event dispatched when the character is created
 */
final public class CharacterCreated {
    final private AccountCharacter character;

    public CharacterCreated(AccountCharacter character) {
        this.character = character;
    }

    public AccountCharacter character() {
        return character;
    }
}
