package fr.quatrevieux.araknemu.game.fight.fighter.operation;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

class SendPacketTest extends FightBaseCase {
    @Test
    void onPlayer() throws Exception {
        createFight();

        player.fighter().apply(new SendPacket("my packet"));

        requestStack.assertLast("my packet");
    }

    @Test
    void notPlayer() throws Exception {
        Fight fight = createPvmFight();

        requestStack.clear();
        fight.team(1).fighters().stream().findFirst().get().apply(new SendPacket("my packet"));

        requestStack.assertEmpty();
    }
}
