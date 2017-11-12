//package fr.quatrevieux.arknemu.network.realm.out;
//
//import java.util.Collection;
//
///**
// * Send list of game servers
// */
//final public class HostList {
//    static public class Host{
//        private int id;
//        private World.State state;
//        private int completion;
//        private boolean canLog;
//
//        public Host(int id, World.State state, int completion, boolean canLog) {
//            this.id = id;
//            this.state = state;
//            this.completion = completion;
//            this.canLog = canLog;
//        }
//
//        @Override
//        public String toString() {
//            return id + ";" + state.getStateId() + ";" + completion + ";" + canLog;
//        }
//    }
//
//    final private Collection<Host> list = new ArrayList<>();
//
//    public void addHost(Host host){
//        list.add(host);
//    }
//
//    @Override
//    public String toString() {
//        return "AH" + StringUtil.join(list, "|");
//    }
//
//    static public HostList createByWorld(World world){
//        HostList hl = new HostList();
//        hl.addHost(new Host(1, world.getState(), 110, true));
//        return hl;
//    }
//}
