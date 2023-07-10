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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Resurrect the last dead teammate with a percentage of his life
 *
 * The fighter will be resurrected on the targeted cell
 * After the resurrection, the fighter will be considered as invoked by the caster,
 * so if the caster is dead, the fighter resurrected die too, and it will be considered on the maximum number of invocations.
 *
 * But unlike the other invocations, the fighter will behave like a normal fighter. So in case of {@link fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter} :
 * - The fighter will be controlled by the actual player and not by AI
 * - It will not be considered as an invocation on effect target (e.g. whip will not hit it)
 *
 * Params :
 * - min: minimum percentage of life
 * - max: maximum percentage of life
 */
public final class InvokeLastDeadFighterHandler implements EffectHandler {
    private final Fight fight;
    private final DeadFighterResolver module;
    private final InvocationCountValidator validator;

    public InvokeLastDeadFighterHandler(Fight fight, DeadFighterResolver module) {
        this.fight = fight;
        this.module = module;
        this.validator = new InvocationCountValidator(fight);
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use InvokeLastDeadFighter as buff");
    }

    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final PlayableFighter fighter = module.getLastDeadFighter(cast.caster().team());
        final Fighter caster = cast.caster();

        // Should not happen : fighter presence is checked in check() method
        if (fighter == null) {
            return;
        }

        final EffectValue value = EffectValue.create(effect.effect(), cast.caster(), fighter);
        final @Positive int life = Math.max(value.value() * fighter.life().max() / 100, 1);

        fighter.life().resuscitate(cast.caster(), life);
        fighter.setInvoker(cast.caster());

        fighter.move(cast.target());

        if (!fight.turnList().fighters().contains(fighter)) {
            fight.turnList().add(fighter);
        }

        fight.send(ActionEffect.addFighter(caster, fighter));
        fight.send(ActionEffect.invokeDeadFighter(caster, fighter));
        fight.send(ActionEffect.packet(caster, new FighterTurnOrder(fight.turnList())));
    }

    @Override
    public boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        final ActiveFighter fighter = turn.fighter();

        if (!module.hasDeadFighter(fighter.team())) {
            return false;
        }

        return validator.check(turn, castable, target);
    }

    @Override
    public @Nullable Error validate(Turn turn, Castable castable, BattlefieldCell target) {
        if (!module.hasDeadFighter(turn.fighter().team())) {
            return Error.cantCast();
        }

        return validator.validate(turn, castable, target);
    }

    public interface DeadFighterResolver {
        /**
         * Get the last dead fighter of the team
         * May return null if no dead fighter yet
         *
         * @param team the team
         */
        public @Nullable PlayableFighter getLastDeadFighter(Team<?> team);

        /**
         * Check if the team has at least one dead fighter
         *
         * @param team the team
         */
        public boolean hasDeadFighter(Team<?> team);
    }
}
