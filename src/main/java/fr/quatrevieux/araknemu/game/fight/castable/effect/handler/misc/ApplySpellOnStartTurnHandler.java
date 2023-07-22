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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.util.Asserter;

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
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            final Buff buff = new Buff(effect.effect(), cast.action(), cast.caster(), target, this);

            // The duration must be at least 1 to ensure that the effect will be applied at the next start turn
            buff.incrementRemainingTurns();
            target.buffs().add(buff);
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        final Fighter target = buff.target();
        final Spell spell = spellService.get(buff.effect().min())
            .level(Asserter.assertPositive(buff.effect().max()))
        ;

        final FightCastScope castScope = FightCastScope.probable(spell, target, target.cell(), spell.effects());

        fight.send(ActionEffect.launchVisualEffect(target, target.cell(), spell));
        fight.effects().apply(castScope);

        return !target.dead();
    }
}
