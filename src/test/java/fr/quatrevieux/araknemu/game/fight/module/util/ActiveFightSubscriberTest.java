package fr.quatrevieux.araknemu.game.fight.module.util;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActiveFightSubscriberTest extends FightBaseCase {
    class MyListener implements Listener<FighterChangePlace> {
        @Override
        public void on(FighterChangePlace event) {

        }

        @Override
        public Class<FighterChangePlace> event() {
            return FighterChangePlace.class;
        }
    }

    @Test
    void listeners() throws Exception {
        Fight fight = createFight(false);

        fight.dispatcher().register(new ActiveFightSubscriber(new Listener[] {new MyListener()}));

        fight.start();
        assertTrue(fight.dispatcher().has(MyListener.class));

        fight.stop();
        assertFalse(fight.dispatcher().has(MyListener.class));
    }
}
