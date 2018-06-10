package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RemoveTeamFightersTest extends FightBaseCase {

    @Test
    void generate() throws SQLException, ContainerException {
        PlayerFighter fighter = new PlayerFighter(gamePlayer(true));
        FightTeam team = new SimpleTeam(fighter, new ArrayList<>(), 1);

        assertEquals("Gt1|-1", new RemoveTeamFighters(team, Collections.singleton(fighter)).toString());
    }
}