package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Acknowledge for character deletion
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L214
 */
final public class CharacterDeleted {
    final private boolean success;

    public CharacterDeleted(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AD" + (success ? "K" : "E");
    }
}
