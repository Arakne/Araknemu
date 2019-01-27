package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseActionRepositoryTest extends GameBaseCase {
    private ResponseActionRepository repository;


    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushResponseAction(new ResponseAction(1, "NEXT", "3786;3787"));
        dataSet.pushResponseAction(new ResponseAction(2, "LEAVE", ""));
        dataSet.pushResponseAction(new ResponseAction(2, "ADDITEM", "1124;1"));

        repository = new ResponseActionRepository(app.database().get("game"));
    }

    @Test
    void get() {
        ResponseAction ra = repository.get(new ResponseAction(2, "ADDITEM", null));

        assertEquals(2, ra.responseId());
        assertEquals("ADDITEM", ra.action());
        assertEquals("1124;1", ra.arguments());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ResponseAction(2, "ADDITEM", null)));
        assertTrue(repository.has(new ResponseAction(2, "LEAVE", null)));
        assertTrue(repository.has(new ResponseAction(1, "NEXT", null)));
        assertFalse(repository.has(new ResponseAction(2, "NEXT", null)));
        assertFalse(repository.has(new ResponseAction(1, "LEAVE", null)));
        assertFalse(repository.has(new ResponseAction(404, "LEAVE", null)));
    }

    @Test
    void all() {
        Map<Integer, List<ResponseAction>> actions = repository.all();

        assertEquals(2, actions.size());
        assertTrue(actions.containsKey(1));
        assertTrue(actions.containsKey(2));
        assertEquals("NEXT", actions.get(1).get(0).action());
        assertEquals("LEAVE", actions.get(2).get(0).action());
        assertEquals("ADDITEM", actions.get(2).get(1).action());
    }

    @Test
    void byQuestion() {
        assertEquals(new HashMap<>(), repository.byQuestion(new Question(0, new int[0], null, null)));

        Map<Integer, List<ResponseAction>> map = repository.byQuestion(new Question(0, new int[] {2}, null, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(2));
        assertCount(2, map.get(2));

        assertEquals(2, repository.byQuestion(new Question(0, new int[] {1, 2}, null, null)).size());
        assertEquals(2, repository.byQuestion(new Question(0, new int[] {1, 2, 404}, null, null)).size());
        assertEquals(0, repository.byQuestion(new Question(0, new int[] {404}, null, null)).size());
    }
}
