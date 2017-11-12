package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerListTest {
    @Test
    void add() {
        ServerList list = new ServerList(5);
        list.add(new ServerList.Server(1, 3));

        assertEquals("AxK5|1,3", list.toString());
    }

    @Test
    void empty() {
        assertEquals("AxK31536000000", new ServerList(ServerList.ONE_YEAR).toString());
    }

}