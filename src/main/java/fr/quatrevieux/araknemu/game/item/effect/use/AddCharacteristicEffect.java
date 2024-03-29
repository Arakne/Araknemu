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
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Effect handler for add characteristic
 */
public final class AddCharacteristicEffect implements UseEffectHandler {
    private final Characteristic characteristic;

    private final RandomUtil random = new RandomUtil();

    public AddCharacteristicEffect(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        final int value = random.rand(effect.arguments());
        final Information info = Information.characteristicBoosted(characteristic, value);

        caster.player().properties().characteristics().base().add(characteristic, value);

        if (info != null) {
            caster.send(info);
        }
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return true;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @Nullable ExplorationMapCell cell) {
        return false;
    }
}
