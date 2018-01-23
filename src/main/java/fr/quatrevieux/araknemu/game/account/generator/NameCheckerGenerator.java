package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;

/**
 * Check the generated name to be available
 */
final public class NameCheckerGenerator implements NameGenerator {
    final private NameGenerator generator;
    final private PlayerRepository repository;
    final private GameConfiguration configuration;

    public NameCheckerGenerator(NameGenerator generator, PlayerRepository repository, GameConfiguration configuration) {
        this.generator = generator;
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public String generate() throws NameGenerationException {
        for (int i = 0; i < 15; ++i) {
            String generated = generator.generate();

            if (!repository.nameExists(Player.forCreation(0, configuration.id(), generated, null, null, null))) {
                return generated;
            }
        }

        throw new NameGenerationException("Reach the maximum try number");
    }
}
