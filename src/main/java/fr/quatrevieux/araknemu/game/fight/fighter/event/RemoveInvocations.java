package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

final public class RemoveInvocations {
    final private PassiveFighter invoker;

    public RemoveInvocations(PassiveFighter invoker) {
        this.invoker = invoker;
    }

    public PassiveFighter invoker() {
        return invoker;
    }
}
