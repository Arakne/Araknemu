package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Send to client the generated name
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1190
 */
final public class RandomNameGenerated {
    final private String name;

    public RandomNameGenerated(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "APK" + name;
    }
}
