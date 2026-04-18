package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;

public class AddFriend implements Listener<AddFriend> {

    @Override
    public void on(AddFriend event) {

    }

    @Override
    public Class<AddFriend> event() {
        return AddFriend.class;
    }
}
