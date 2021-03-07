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

package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.spell.UpdateSpell;

/**
 * Send upgraded spell success
 */
public final class SendUpgradedSpell implements Listener<SpellUpgraded> {
    private final GamePlayer player;

    public SendUpgradedSpell(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(SpellUpgraded event) {
        player.send(new UpdateSpell(event.entry()));
        player.send(new Stats(player.scope().properties()));
    }

    @Override
    public Class<SpellUpgraded> event() {
        return SpellUpgraded.class;
    }
}
