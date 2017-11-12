package fr.quatrevieux.araknemu.network.realm.out;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Send list of used server (with characters count)
 */
final public class ServerList {
    final static public class Server {
        final private int id;
        final private int count;

        public Server(int id, int count) {
            this.id = id;
            this.count = count;
        }
    }

    final static public long ONE_YEAR = 31536000000L;
    
    final private long aboTime;
    final private Collection<Server> servers = new ArrayList<>();

    public ServerList(long aboTime) {
        this.aboTime = aboTime;
    }

    /**
     * Add a new server entry on the list
     * @param server
     */
    public void add(Server server){
        servers.add(server);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(64);
        
        ret.append("AxK").append(aboTime);
        
        for (Server server : servers) {
            ret.append('|').append(server.id).append(',').append(server.count);
        }
        
        return ret.toString();
    }
}
