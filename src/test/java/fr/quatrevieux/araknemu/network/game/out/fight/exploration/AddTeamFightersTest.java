package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @todo test with multiple fighters
 */
class AddTeamFightersTest extends FightBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        FightTeam team = new SimpleTeam(new PlayerFighter(gamePlayer(true)), new ArrayList<>(), 1);

        assertEquals("Gt1|+1;Bob;50", new AddTeamFighters(team).toString());
    }
}
