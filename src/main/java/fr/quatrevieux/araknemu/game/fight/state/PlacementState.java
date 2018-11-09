package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.event.*;
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

/**
 * Placement before start fight
 */
final public class PlacementState implements LeavableState, EventsSubscriber {
    private long startTime;
    private Fight fight;
    private Listener[] listeners;

    @Override
    public void start(Fight fight) {
        this.fight = fight;

        fight.dispatcher().register(this);
        startTime = System.currentTimeMillis();

        fight.fighters().forEach(fighter -> fighter.dispatch(new FightJoined(fight, fighter)));
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
    synchronized public void changePlace(Fighter fighter, FightCell cell) {
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
    synchronized public void joinTeam(Fighter fighter, FightTeam team) throws JoinFightException {
        if (fight.state() != this) {
            throw new JoinFightException(JoinFightError.CANT_DO_TOO_LATE);
        }

        team.join(fighter);

        FightCell cell = PlacementCellsGenerator.randomized(fight.map(), team.startPlaces()).next();

        fighter.move(cell);
        fighter.setFight(fight);

        fighter.dispatch(new FightJoined(fight, fighter));
        fight.dispatch(new FighterAdded(fighter));
    }

    @Override
    synchronized public void leave(Fighter fighter) {
        if (fight.state() != this) {
            throw new InvalidFightStateException(getClass());
        }

        // @todo leave not cancellable
        if (fight.type().canCancel()) {
            leaveOnCancellableFight(fighter);
        }
    }

    /**
     * Start the fight
     */
    synchronized public void startFight() {
        if (fight.state() != this) {
            return;
        }

        fight.dispatcher().unregister(this);
        fight.nextState();
    }

    /**
     * Leave from a cancellable fight
     */
    private void leaveOnCancellableFight(Fighter fighter) {
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
     *
     */
    private void removeFighter(Fighter fighter) {
        fighter.dispatch(new FightLeaved());
        fight.dispatch(new FighterRemoved(fighter, fight));
    }

    /**
     * Check if the fight is valid after fighter leaved
     */
    private void checkFightValid() {
        if (fight.teams().size() <= 1) {
            fight.cancel();
        }
    }
}
