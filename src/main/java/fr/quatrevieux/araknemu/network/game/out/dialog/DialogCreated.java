package fr.quatrevieux.araknemu.network.game.out.dialog;

import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Dialog with NPC is successfully created
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L46
 */
final public class DialogCreated {
    final private Creature interlocutor;

    public DialogCreated(Creature interlocutor) {
        this.interlocutor = interlocutor;
    }

    @Override
    public String toString() {
        return "DCK" + interlocutor.id();
    }
}
