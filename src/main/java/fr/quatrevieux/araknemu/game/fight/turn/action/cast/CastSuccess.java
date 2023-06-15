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

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.SpellCasted;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

import java.util.List;

/**
 * Result for successfully spell cast
 */
public final class CastSuccess implements ActionResult {
    private final Cast action;
    private final PlayableFighter caster;
    private final Spell spell;
    private final FightCell target;
    private final boolean critical;

    public CastSuccess(Cast action, PlayableFighter caster, Spell spell, FightCell target, boolean critical) {
        this.action = action;
        this.caster = caster;
        this.spell = spell;
        this.target = target;
        this.critical = critical;
    }

    @Override
    public int action() {
        return ActionType.CAST.id();
    }

    @Override
    public PlayableFighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {spell.id(), target.id(), spell.spriteId(), spell.level(), spell.spriteArgs()};
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public boolean secret() {
        return false;
    }

    /**
     * Is a critical hit ?
     */
    public boolean critical() {
        return critical;
    }

    /**
     * Get the spell effects
     */
    public List<SpellEffect> effects() {
        return critical ? spell.criticalEffects() : spell.effects();
    }

    @Override
    public void apply(FightTurn turn) {
        if (critical) {
            caster.fight().send(ActionEffect.criticalHitSpell(caster, spell));
        }

        turn.points().useActionPoints(spell.apCost());
        turn.fight().effects().apply(FightCastScope.probable(spell, caster, target, effects()));

        turn.fight().dispatch(new SpellCasted(action));
    }
}
