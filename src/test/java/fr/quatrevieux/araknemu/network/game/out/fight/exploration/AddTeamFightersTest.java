package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class AddTeamFightersTest extends FightBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        FightTeam team = new SimpleTeam(makePlayerFighter(gamePlayer(true)), new ArrayList<>(), 1);

        assertEquals("Gt1|+1;Bob;50", new AddTeamFighters(team).toString());
    }

    @Test
    void generateWithMultipleFighters() throws SQLException, ContainerException, JoinFightException {
    FightTeam team = new SimpleTeam(makePlayerFighter(gamePlayer(true)), Arrays.asList(123, 456), 1);
        team.join(makePlayerFighter(makeSimpleGamePlayer(10)));

        assertEquals("Gt1|+1;Bob;50|+10;PLAYER_10;1", new AddTeamFighters(team).toString());
    }
}
