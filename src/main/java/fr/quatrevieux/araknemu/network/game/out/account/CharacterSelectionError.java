package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * An error occurs during selecting character
 * Will close connection
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L764
 */
final public class CharacterSelectionError {
    @Override
    public String toString() {
        return "ASE";
    }
}
