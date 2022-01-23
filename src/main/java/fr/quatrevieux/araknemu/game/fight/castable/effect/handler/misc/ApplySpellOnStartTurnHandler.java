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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Apply related spell effects on start turn of the target fighter
 *
 * The spell effect value is :
 * - min = spell id
 * - max = spell level
 * - special = ?
 *
 * Note: applied effects are considered as direct effect, not indirect / poison effects, so armor are taken in account
 */
public final class ApplySpellOnStartTurnHandler implements EffectHandler, BuffHook {
    private final SpellService spellService;
    private final Fight fight;

    public ApplySpellOnStartTurnHandler(Fight fight, SpellService spellService) {
        this.fight = fight;
        this.spellService = spellService;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        buff(cast, effect);
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        final PassiveFighter target = buff.target();
        final Spell spell = spellService.get(buff.effect().min()).level(buff.effect().max());

        final CastScope castScope = new CastScope(spell, (ActiveFighter) target, target.cell());

        fight.send(ActionEffect.launchVisualEffect(target, target.cell(), spell));
        fight.effects().apply(castScope.withRandomEffects(spell.effects()));

        return !target.dead();
    }
}
