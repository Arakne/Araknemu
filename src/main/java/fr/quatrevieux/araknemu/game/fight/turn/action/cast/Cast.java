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

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.SendPacket;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.SpellCasted;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Cast a spell
 */
final public class Cast implements Action {
    final private FightTurn turn;
    final private Fighter caster;
    final private Spell spell;
    final private FightCell target;
    final private CastConstraintValidator<Spell> validator;
    final private CriticalityStrategy criticalityStrategy;

    private CastSuccess result;

    public Cast(FightTurn turn, Fighter caster, Spell spell, FightCell target) {
        this(turn, caster, spell, target, new SpellConstraintsValidator(), new BaseCriticalityStrategy(caster));
    }

    public Cast(FightTurn turn, Fighter caster, Spell spell, FightCell target, CastConstraintValidator<Spell> validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.caster = caster;
        this.spell = spell;
        this.target = target;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate() {
        Error error = spell == null
            ? Error.cantCastNotFound()
            : validator.validate(turn, spell, target)
        ;

        if (error != null) {
            caster.apply(new SendPacket(error));

            return false;
        }

        return true;
    }

    @Override
    public ActionResult start() {
        if (!target.equals(caster.cell())) {
            caster.setOrientation(
                new CoordinateCell<>(caster.cell())
                    .directionTo(new CoordinateCell<>(target))
            );
        }

        if (criticalityStrategy.failed(spell.criticalFailure())) {
            return new CastFailed(caster, spell);
        }

        return result = new CastSuccess(
            caster,
            spell,
            target,
            criticalityStrategy.hit(spell.criticalHit())
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
    public void end() {
        if (result.critical()) {
            caster.fight().send(ActionEffect.criticalHitSpell(caster, spell));
        }

        turn.points().useActionPoints(spell.apCost());
        turn.fight().effects().apply(new CastScope(spell, caster, target).withRandomEffects(result.effects()));

        turn.fight().dispatch(new SpellCasted(this));
    }

    @Override
    public void failed() {
        turn.points().useActionPoints(spell.apCost());

        if (spell.endsTurnOnFailure()) {
            turn.stop();
        }
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
}
