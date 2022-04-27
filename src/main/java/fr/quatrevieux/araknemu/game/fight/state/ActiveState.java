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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.DispelDeadFighterBuff;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshBuffs;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RemoveDeadFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterDie;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterLifeChanged;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterMaxLifeChanged;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStopped;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightersInformation;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendTurnList;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendUsedActionPoints;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendUsedMovementPoints;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightAction;
import fr.quatrevieux.araknemu.game.listener.fight.turn.action.SendFightActionTerminated;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.time.Duration;
import java.util.Collections;

/**
 * State for active fight
 */
public final class ActiveState implements LeavableState, EventsSubscriber {
    private final FighterOrderStrategy orderStrategy;

    private @MonotonicNonNull Fight fight;
    private Listener @MonotonicNonNull[] listeners;

    public ActiveState(FighterOrderStrategy orderStrategy) {
        this.orderStrategy = orderStrategy;
    }

    public ActiveState() {
        this(new AlternateTeamFighterOrder());
    }

    @Override
    public void start(Fight fight) {
        this.fight = fight;
        fight.dispatcher().register(this);

        fight.fighters().forEach(Fighter::init);
        fight.turnList().init(orderStrategy);

        fight.start();
    }

    @Override
    public Listener[] listeners() {
        if (listeners != null) {
            return listeners;
        }

        final Fight fight = this.fight;

        if (fight == null) {
            throw new IllegalStateException("State must be started");
        }

        return listeners = new Listener[] {
            new SendFightStarted(fight),
            new SendFightersInformation(fight),
            new SendFightTurnStarted(fight),
            new SendFightTurnStopped(fight),
            new SendFightAction(fight),
            new SendFightActionTerminated(),
            new SendUsedMovementPoints(fight),
            new SendUsedActionPoints(fight),
            new SendFighterLifeChanged(fight),
            new SendFighterMaxLifeChanged(fight),
            new SendFighterDie(fight),
            new RemoveDeadFighter(fight),
            new DispelDeadFighterBuff(fight),
            new CheckFightTerminated(fight),
            new SendTurnList(fight),
            new RefreshBuffs(),
        };
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public synchronized void leave(Fighter fighter) {
        final Fight fight = this.fight;

        if (fight == null) {
            throw new IllegalStateException("State must be started");
        }

        // nextState is performed 1.5s after fight stop
        // So leave can occurs on terminated fight
        if (!fight.active()) {
            return;
        }

        fighter.life().kill(fighter);

        // Fight terminated
        if (!fight.active()) {
            return;
        }

        fighter.team().kick(fighter);
        fight.turnList().remove(fighter);

        final FightRewardsSheet rewardsSheet = fight.type().rewards().generate(
            new EndFightResults(
                fight,
                Collections.emptyList(),
                Collections.singletonList(fighter)
            )
        );

        fighter.dispatch(new FightLeaved(rewardsSheet.rewards().get(0)));
    }

    /**
     * Terminate the fight
     */
    public void terminate() {
        if (fight == null || fight.state() != this) {
            return;
        }

        fight.dispatcher().unregister(this);
        fight.stop();

        fight.schedule(fight::nextState, Duration.ofMillis(1500));
    }
}
