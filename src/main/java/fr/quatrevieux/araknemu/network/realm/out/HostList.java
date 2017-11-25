package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.host.GameHost;

import java.util.Collection;

/**
 * Send list of game servers
 */
final public class HostList {
    final private Collection<GameHost> list;

    public HostList(Collection<GameHost> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AH");
        boolean first = true;

        for (GameHost host : list) {
            if (first) {
                first = false;
            } else {
                sb.append('|');
            }

            sb.append(host.id()).append(';')
                .append(host.state().ordinal()).append(';')
                .append(host.completion()).append(';')
                .append(host.canLog() ? 1 : 0)
            ;
        }

        return sb.toString();
    }
}
