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
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Effect for learn a new spell
 */
public final class LearnSpellEffect implements UseEffectHandler {
    private final SpellService service;

    public LearnSpellEffect(SpellService service) {
        this.service = service;
    }

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        caster.properties().spells().learn(
            service.get(effect.arguments()[2])
        );
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        final int spellId = effect.arguments()[2];

        if (!caster.properties().spells().canLearn(service.get(spellId))) {
            caster.send(Error.cantLearnSpell(spellId));

            return false;
        }

        return true;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return false;
    }
}
