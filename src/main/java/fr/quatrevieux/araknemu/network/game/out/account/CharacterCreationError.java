package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;

/**
 * Packet for error during character creation
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L571
 */
final public class CharacterCreationError {
    final private PlayerConstraints.Error error;

    public CharacterCreationError(PlayerConstraints.Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AAE" + error.code();
    }
}
