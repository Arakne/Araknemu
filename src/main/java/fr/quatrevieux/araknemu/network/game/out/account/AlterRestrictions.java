package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.player.Restrictions;

/**
 * Change local player restrictions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1109
 */
final public class AlterRestrictions {
    final private Restrictions restrictions;

    public AlterRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    @Override
    public String toString() {
        return "AR" + Integer.toString(restrictions.toInt(), 36);
    }
}
