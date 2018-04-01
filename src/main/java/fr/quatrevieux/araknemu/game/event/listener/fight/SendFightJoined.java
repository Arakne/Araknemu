package fr.quatrevieux.araknemu.game.event.listener.fight;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.fight.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Send fight joined packets
 */
final public class SendFightJoined implements Listener<FightJoined> {
    final private PlayerFighter fighter;

    public SendFightJoined(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightJoined event) {
        fighter.send(new JoinFight(event.fight()));

        fighter.send(
            new AddSprites(
                event.fight().fighters()
                    .stream()
                    .map(Fighter::sprite)
                    .collect(Collectors.toList())
            )
        );

        fighter.send(
            new FightStartPositions(
                new List[] {
                    event.fight().team(0).startPlaces(),
                    event.fight().team(1).startPlaces()
                },
                fighter.team().number()
            )
        );
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}
