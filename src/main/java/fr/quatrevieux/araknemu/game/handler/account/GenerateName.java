package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.account.generator.NameGenerationException;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskRandomName;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerated;
import fr.quatrevieux.araknemu.network.game.out.account.RandomNameGenerationError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Generate a random name for character creation
 */
final public class GenerateName implements PacketHandler<GameSession, AskRandomName> {
    final private NameGenerator generator;

    public GenerateName(NameGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void handle(GameSession session, AskRandomName packet) throws Exception {
        try {
            session.write(
                new RandomNameGenerated(
                    generator.generate()
                )
            );
        } catch (NameGenerationException e) {
            throw new ErrorPacket(new RandomNameGenerationError(RandomNameGenerationError.Error.UNDEFINED), e);
        }
    }

    @Override
    public Class<AskRandomName> packet() {
        return AskRandomName.class;
    }
}
