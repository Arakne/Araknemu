package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.data.value.ServerCharacters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Send list of used server (with characters count)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L372
 */
final public class ServerList {
    final static public long ONE_YEAR = 31536000000L;
    
    final private long aboTime;
    final private Collection<ServerCharacters> servers;

    public ServerList(long aboTime, Collection<ServerCharacters> servers) {
        this.aboTime = aboTime;
        this.servers = servers;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(64);
        
        ret.append("AxK").append(aboTime);
        
        for (ServerCharacters server : servers) {
            ret.append('|').append(server.serverId()).append(',').append(server.charactersCount());
        }
        
        return ret.toString();
    }
}
