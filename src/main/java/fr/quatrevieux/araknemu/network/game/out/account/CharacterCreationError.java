package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;

/**
 * Packet for error during character creation
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L571
 */
final public class CharacterCreationError {
    final private CharacterCreationException.Error error;

    public CharacterCreationError(CharacterCreationException.Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AAE" + error.code();
    }
}
