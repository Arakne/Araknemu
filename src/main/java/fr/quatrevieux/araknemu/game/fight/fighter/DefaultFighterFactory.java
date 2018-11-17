package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.*;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Default implementation of the fighter factory
 */
final public class DefaultFighterFactory implements FighterFactory {
    final private Dispatcher dispatcher;

    public DefaultFighterFactory(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public PlayerFighter create(GamePlayer player) {
        PlayerFighter fighter = new PlayerFighter(player);

        fighter.dispatcher().add(new SendFightJoined(fighter));
        fighter.dispatcher().add(new ApplyEndFightReward(fighter));
        fighter.dispatcher().add(new StopFightSession(fighter));
        fighter.dispatcher().add(new SendFightLeaved(fighter));
        fighter.dispatcher().add(new LeaveOnDisconnect(fighter));
        fighter.dispatcher().add(new ApplyLeaveReward(fighter));
        fighter.dispatcher().add(new SendStats(fighter));

        dispatcher.dispatch(new PlayerFighterCreated(fighter));

        return fighter;
    }
}
