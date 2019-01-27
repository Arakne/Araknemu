package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.game.handler.emote.ChangeOrientation;
import fr.quatrevieux.araknemu.network.game.in.dialog.ChosenResponse;
import fr.quatrevieux.araknemu.network.game.in.dialog.CreateDialogRequest;
import fr.quatrevieux.araknemu.network.game.in.dialog.LeaveDialogRequest;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.AskFightDetails;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExploringLoaderTest extends LoaderTestCase {
    private ExploringLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new ExploringLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsOnly(EnsureExploring.class, handlers);

        assertHandlePacket(AskExtraInfo.class, handlers);
        assertHandlePacket(GameActionCancel.class, handlers);
        assertHandlePacket(ListFightsRequest.class, handlers);
        assertHandlePacket(AskFightDetails.class, handlers);
        assertHandlePacket(SetOrientationRequest.class, handlers);
        assertHandlePacket(CreateDialogRequest.class, handlers);
        assertHandlePacket(LeaveDialogRequest.class, handlers);
        assertHandlePacket(ChosenResponse.class, handlers);
    }
}
