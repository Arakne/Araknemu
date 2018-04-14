package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStopped;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightersInformation;

import java.time.Duration;

/**
 * State for active fight
 */
final public class ActiveState implements FightState, EventsSubscriber {
    final private FighterOrderStrategy orderStrategy;

    private Fight fight;

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

        fight.dispatch(new FightStarted());

        fight.schedule(
            () -> fight.turnList().start(),
            Duration.ofMillis(200)
        );
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new SendFightStarted(fight),
            new SendFightersInformation(fight),
            new SendFightTurnStarted(fight),
            new SendFightTurnStopped(fight)
        };
    }

    @Override
    public int id() {
        return 3;
    }
}
