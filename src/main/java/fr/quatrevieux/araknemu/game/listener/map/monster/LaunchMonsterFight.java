package fr.quatrevieux.araknemu.game.listener.map.monster;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;

/**
 * Start the PvM fight if the player reach a monster group cell
 */
final public class LaunchMonsterFight implements Listener<PlayerMoveFinished> {
    @Override
    public void on(PlayerMoveFinished event) {
        event.cell().apply(new Operation() {
            boolean found = false;

            @Override
            public void onMonsterGroup(MonsterGroup monsterGroup) {
                if (!found) {
                    monsterGroup.startFight(event.player());
                    found = true;
                }
            }
        });
    }

    @Override
    public Class<PlayerMoveFinished> event() {
        return PlayerMoveFinished.class;
    }
}
