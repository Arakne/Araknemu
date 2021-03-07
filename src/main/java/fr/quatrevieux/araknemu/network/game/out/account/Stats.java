/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.player.CharacterProperties;

/**
 * Send to client current player stats
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L797
 */
public final class Stats {
    private final CharacterProperties player;

    public Stats(CharacterProperties player) {
        this.player = player;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("As");

        sb
            .append(player.experience().current()).append(',').append(player.experience().min()).append(',').append(player.experience().max()).append('|')
            .append(player.kamas()).append("|") // Kamas
            .append(player.characteristics().boostPoints()).append("|")
            .append(player.spells().upgradePoints()).append("|")
            .append('|') // Align
            .append(player.life().current()).append(',').append(player.life().max()).append('|')
            .append("10000,10000|") // Energy, Energy Max
            .append(player.characteristics().initiative()).append('|')
            .append(player.characteristics().discernment()).append('|')
        ;

        for (Characteristic characteristic : Characteristic.values()) {
            sb
                .append(player.characteristics().base().get(characteristic)).append(',')
                .append(player.characteristics().stuff().get(characteristic)).append(',')
                .append(player.characteristics().feats().get(characteristic)).append(',')
                .append(player.characteristics().boost().get(characteristic)).append('|')
            ;
        }

        return sb.toString();
    }
}
