package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ServerListTest {
    @Test
    void add() {
        ServerList list = new ServerList(5, Arrays.asList(
            new ServerCharacters(1, 3),
            new ServerCharacters(5, 1)
        ));

        assertEquals("AxK5|1,3|5,1", list.toString());
    }

    @Test
    void empty() {
        assertEquals("AxK31536000000", new ServerList(ServerList.ONE_YEAR, Collections.EMPTY_LIST).toString());
    }

}