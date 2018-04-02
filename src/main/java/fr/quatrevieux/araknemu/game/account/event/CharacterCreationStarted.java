package fr.quatrevieux.araknemu.game.account.event;

import fr.quatrevieux.araknemu.game.account.AccountCharacter;

/**
 * Event dispatched when the character creation request is validated
 * and the character will be created
 */
final public class CharacterCreationStarted {
    final private AccountCharacter character;

    public CharacterCreationStarted(AccountCharacter character) {
        this.character = character;
    }

    public AccountCharacter character() {
        return character;
    }
}
