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

package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.SendPacket;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Use weapon / close combat attack
 */
public final class CloseCombat implements Action {
    private final FightTurn turn;
    private final Fighter caster;
    private final FightCell target;
    private final WeaponConstraintsValidator validator;
    private final CriticalityStrategy criticalityStrategy;

    private CloseCombatSuccess result;

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target) {
        this(turn, caster, target, new WeaponConstraintsValidator(), new BaseCriticalityStrategy(caster));
    }

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target, WeaponConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.caster = caster;
        this.target = target;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate() {
        final Error error = validator.validate(turn, caster.weapon(), target);

        if (error != null) {
            caster.apply(new SendPacket(error));

            return false;
        }

        return true;
    }

    @Override
    public ActionResult start() {
        if (criticalityStrategy.failed(caster.weapon().criticalFailure())) {
            return new CloseCombatFailed(caster);
        }

        return result = new CloseCombatSuccess(
            caster,
            caster.weapon(),
            target,
            criticalityStrategy.hit(caster.weapon().criticalHit())
        );
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public ActionType type() {
        return ActionType.CLOSE_COMBAT;
    }

    @Override
    public void end() {
        if (result.critical()) {
            caster.fight().send(ActionEffect.criticalHitCloseCombat(caster));
        }

        turn.points().useActionPoints(caster.weapon().apCost());
        turn.fight().effects().apply(new CastScope(caster.weapon(), caster, target).withEffects(result.effects()));
    }

    @Override
    public void failed() {
        turn.points().useActionPoints(caster.weapon().apCost());
        turn.stop();
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(500);
    }

    @Override
    public String toString() {
        return "CloseCombat{target=" + target.id() + '}';
    }
}
