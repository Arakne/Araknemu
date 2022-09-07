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

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.SendPacket;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Cast a spell
 */
public final class Cast implements Action {
    private final Fighter caster;
    private final Spell spell;
    private final FightCell target;
    private final CastConstraintValidator<Spell> validator;
    private final CriticalityStrategy criticalityStrategy;

    public Cast(Fighter caster, Spell spell, FightCell target) {
        this(caster, spell, target, new SpellConstraintsValidator(), new BaseCriticalityStrategy());
    }

    public Cast(Fighter caster, Spell spell, FightCell target, CastConstraintValidator<Spell> validator, CriticalityStrategy criticalityStrategy) {
        this.caster = caster;
        this.spell = spell;
        this.target = target;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate(Turn turn) {
        final Error error = validator.validate(turn, spell, target);

        if (error != null) {
            caster.apply(new SendPacket(error));

            return false;
        }

        return true;
    }

    @Override
    public ActionResult start() {
        if (!target.equals(caster.cell())) {
            caster.setOrientation(caster.cell().coordinate().directionTo(target));
        }

        if (criticalityStrategy.failed(caster, spell.criticalFailure())) {
            return new CastFailed(caster, spell);
        }

        return new CastSuccess(
            this,
            caster,
            spell,
            target,
            criticalityStrategy.hit(caster, spell.criticalHit())
        );
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(500);
    }

    public Fighter caster() {
        return caster;
    }

    public Spell spell() {
        return spell;
    }

    public FightCell target() {
        return target;
    }

    @Override
    public String toString() {
        return "Cast{spell=" + spell.id() + ", target=" + target.id() + '}';
    }
}
