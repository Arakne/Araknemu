package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.player.PlayerData;

/**
 * Send to client current player stats
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L797
 */
final public class Stats {
    final private PlayerData player;

    public Stats(PlayerData player) {
        this.player = player;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("As");

        sb
            .append("0,0,110").append('|') // XP,XPLow,XPHigh
            .append("1000|") // Kamas
            .append("15|") // Boot points
            .append("10|") // Spell points
            .append('|') // Align
            .append("100,150|") // Life point, LPMax
            .append("0,10000|") // Energy, Energy Max
        ;

        for (Characteristic characteristic : Characteristic.values()) {
            sb.append(player.characteristics().base().get(characteristic));

            if (characteristic.ordinal() >= Characteristic.ACTION_POINT.ordinal()) {
                sb
                    .append(',')
                    .append(player.characteristics().stuff().get(characteristic)).append(',')
                    .append(player.characteristics().feats().get(characteristic)).append(',')
                    .append(player.characteristics().boost().get(characteristic))
                ;
            }

            sb.append('|');
        }

        return sb.toString();
    }
}
