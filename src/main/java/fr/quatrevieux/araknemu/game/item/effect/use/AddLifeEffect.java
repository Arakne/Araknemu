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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

/**
 * Add life to the use target
 */
public final class AddLifeEffect implements UseEffectHandler {
    private final RandomUtil random = new RandomUtil();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        final int value = random.rand(effect.arguments());

        caster.player().properties().life().add(value);
    }

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        apply(effect, target);
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return !caster.properties().life().isFull();
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return target != null;
    }

    @Override
    public boolean checkFighter(UseEffect effect, PlayerFighter fighter) {
        return !fighter.life().isFull();
    }

    @Override
    public void applyToFighter(UseEffect effect, PlayerFighter fighter) {
        final int value = random.rand(effect.arguments());

        fighter.player().properties().life().add(value);
    }
}
