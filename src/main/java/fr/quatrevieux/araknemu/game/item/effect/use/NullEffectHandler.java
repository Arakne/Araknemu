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

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Null object for {@link UseEffectHandler}
 */
public final class NullEffectHandler implements UseEffectHandler {
    public static final UseEffectHandler INSTANCE = new NullEffectHandler();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        // No-op method
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        return true;
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return true;
    }

    @Override
    public boolean checkFighter(UseEffect effect, PlayerFighter fighter) {
        return true;
    }
}
