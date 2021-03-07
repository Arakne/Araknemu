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
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.event.FighterPlaceChanged;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.util.PlacementCellsGenerator;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterPositions;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterReadyState;
import fr.quatrevieux.araknemu.game.listener.fight.SendNewFighter;
import fr.quatrevieux.araknemu.game.listener.fight.StartFightWhenAllReady;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.ClearFighter;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterRemoved;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Placement before start fight
 */
public final class PlacementState implements LeavableState, EventsSubscriber {
    private long startTime;
    private Fight fight;
    private Listener[] listeners;
    private Map<FightTeam, PlacementCellsGenerator> cellsGenerators;

    private final boolean randomize;

    public PlacementState() {
        this(true);
    }

    public PlacementState(boolean randomize) {
        this.randomize = randomize;
    }

    @Override
    public void start(Fight fight) {
        this.fight = fight;
        this.cellsGenerators = new HashMap<>();

        for (FightTeam team : fight.teams()) {
            cellsGenerators.put(
                team,
                randomize
                    ? PlacementCellsGenerator.randomized(fight.map(), team.startPlaces())
                    : new PlacementCellsGenerator(fight.map(), team.startPlaces())
            );
        }

        fight.dispatcher().register(this);
        startTime = System.currentTimeMillis();
        addFighters(fight.fighters(false));

        if (fight.type().hasPlacementTimeLimit()) {
            fight.schedule(this::startFight, Duration.ofMillis(remainingTime()));
        }
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public Listener[] listeners() {
        if (listeners != null) {
            return listeners;
        }

        return listeners = new Listener[] {
            new SendFighterPositions(fight),
            new SendFighterReadyState(fight),
            new StartFightWhenAllReady(fight, this),
            new SendNewFighter(fight),
            new ClearFighter(),
            new SendFighterRemoved(fight),
        };
    }

    /**
     * Get the remaining placement time, in milliseconds
     */
    public long remainingTime() {
        return (fight.type().placementTime() * 1000 + startTime) - System.currentTimeMillis();
    }

    /**
     * Try to change fighter start place
     *
     * @param fighter Fighter to move
     * @param cell The target cell
     */
    public synchronized void changePlace(Fighter fighter, FightCell cell) {
        if (fighter.ready()) {
            throw new FightException("The fighter is ready");
        }

        if (!cell.walkable()) {
            throw new FightMapException("Not walkable");
        }

        if (!fighter.team().startPlaces().contains(cell.id())) {
            throw new FightException("Bad start cell");
        }

        fighter.move(cell);
        fight.dispatch(new FighterPlaceChanged(fighter));
    }

    /**
     * Try to join a team
     *
     * @param fighter The fighter to add
     * @param team The team
     *
     * @throws JoinFightException When cannot join the team
     */
    public synchronized void joinTeam(Fighter fighter, FightTeam team) throws JoinFightException {
        if (fight.state() != this) {
            throw new JoinFightException(JoinFightError.CANT_DO_TOO_LATE);
        }

        team.join(fighter);
        addFighters(Collections.singleton(fighter));
    }

    @Override
    public synchronized void leave(Fighter fighter) {
        if (fight.state() != this) {
            throw new InvalidFightStateException(getClass());
        }

        // Not allowed to leave the fight : punish the fighter
        if (!fight.type().canCancel()) {
            punishDeserter(fighter);
        }

        // Remove fighter
        leaveFromFight(fighter);
    }

    /**
     * Start the fight
     */
    public synchronized void startFight() {
        if (fight.state() != this) {
            return;
        }

        fight.dispatcher().unregister(this);
        fight.nextState();
    }

    /**
     * Leave from a fight :
     * - If the fighter is the team leader, the team is dissolved
     * - The fighter is removed
     * - Check if the fight is valid (has at least two teams)
     */
    private void leaveFromFight(Fighter fighter) {
        // The team leader quit the fight => Dissolve team
        if (fighter.isTeamLeader()) {
            fight.teams().remove(fighter.team());
            fighter.team().fighters().forEach(this::removeFighter);
        } else {
            fighter.team().kick(fighter);
            removeFighter(fighter);
        }

        checkFightValid();
    }

    /**
     * Punish the deserter fighter
     */
    private void punishDeserter(Fighter fighter) {
        final FightRewardsSheet rewardsSheet = fight.type().rewards().generate(
            new EndFightResults(
                fight,
                Collections.emptyList(),
                Collections.singletonList(fighter)
            )
        );

        fighter.dispatch(new FightLeaved(rewardsSheet.rewards().get(0)));
    }

    private void addFighters(Collection<Fighter> fighters) {
        for (Fighter fighter : fighters) {
            fighter.joinFight(fight, cellsGenerators.get(fighter.team()).next());
        }

        for (Fighter fighter : fighters) {
            fighter.dispatch(new FightJoined(fight, fighter));
        }

        for (Fighter fighter : fighters) {
            fight.dispatch(new FighterAdded(fighter));
        }
    }

    /**
     * Notify fight that a fighter has leave
     */
    private void removeFighter(Fighter fighter) {
        fighter.dispatch(new FightLeaved());
        fight.dispatch(new FighterRemoved(fighter, fight));
    }

    /**
     * Check if the fight is valid after fighter leaved
     */
    private void checkFightValid() {
        if (fight.teams().stream().filter(FightTeam::alive).count() <= 1) {
            fight.cancel();
        }
    }
}
