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

package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.handler.AbstractPlayingPacketHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellUpgradeError;

/**
 * Upgrade the spell
 */
public final class UpgradeSpell extends AbstractPlayingPacketHandler<SpellUpgrade> {
    @Override
    public void handle(GameSession session, GamePlayer player, SpellUpgrade packet) throws Exception {
        try {
            player.properties().spells()
                .entry(packet.spellId())
                .upgrade()
            ;
        } catch (Exception e) {
            throw new ErrorPacket(new SpellUpgradeError(), e);
        }
    }

    @Override
    public Class<SpellUpgrade> packet() {
        return SpellUpgrade.class;
    }
}
