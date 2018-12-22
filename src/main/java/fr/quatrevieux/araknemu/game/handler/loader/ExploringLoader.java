package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.game.handler.emote.ChangeOrientation;
import fr.quatrevieux.araknemu.game.handler.fight.ListFights;
import fr.quatrevieux.araknemu.game.handler.fight.ShowFightDetails;
import fr.quatrevieux.araknemu.game.handler.game.CancelGameAction;
import fr.quatrevieux.araknemu.game.handler.game.LoadExtraInfo;
import fr.quatrevieux.araknemu.game.handler.object.UseObject;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for exploration packets
 */
final public class ExploringLoader extends AbstractLoader {
    public ExploringLoader() {
        super(EnsureExploring::new);
    }

    @Override
    public PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new LoadExtraInfo(container.get(FightService.class)),
            new CancelGameAction(),
            new ListFights(container.get(FightService.class)),
            new ShowFightDetails(container.get(FightService.class)),
            new ChangeOrientation(),
        };
    }
}
