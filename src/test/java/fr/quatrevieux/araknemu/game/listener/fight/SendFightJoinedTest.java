package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SendFightJoinedTest extends FightBaseCase {
    private SendFightJoined listener;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = createFight();

        requestStack.clear();
        fighter = player.fighter();
        listener = new SendFightJoined(fighter);
    }

    @Test
    void onFightJoined() {
        listener.on(new FightJoined(fight, fighter));

        requestStack.assertAll(
            new JoinFight(fight),
            new AddSprites(
                Arrays.asList(
                    fighter.sprite(),
                    new ArrayList<>(fight.team(1).fighters()).get(0).sprite()
                )
            ),
            new FightStartPositions(
                new List[]{
                    fight.team(0).startPlaces(),
                    fight.team(1).startPlaces(),
                },
                0
            )
        );
    }
}
