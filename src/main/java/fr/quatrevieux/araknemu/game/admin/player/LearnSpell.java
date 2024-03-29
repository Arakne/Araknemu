/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.kohsuke.args4j.Argument;

/**
 * Learn a spell to a player
 */
public final class LearnSpell extends AbstractCommand<LearnSpell.Arguments> {
    private final GamePlayer player;
    private final SpellService service;

    public LearnSpell(GamePlayer player, SpellService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    protected void build(AbstractCommand<Arguments>.Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Add the given spell to a player")
                    .example("@John learnspell 366", "John will learn the spell Moon Hammer")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "learnspell";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final SpellLevels toLearn;

        try {
            toLearn = service.get(arguments.spellId);
        } catch (EntityNotFoundException e) {
            performer.error("Spell {} not found", arguments.spellId);
            return;
        }

        final SpellBook spells = player.properties().spells();

        if (!spells.canLearn(toLearn)) {
            performer.error("Cannot learn spell {} ({})", toLearn.name(), arguments.spellId);
            return;
        }

        spells.learn(toLearn);
        performer.success("The spell {} ({}) has been learned", toLearn.name(), arguments.spellId);
    }

    public static final class Arguments {
        @Argument(required = true, metaVar = "SPELLID", usage = "The spell ID to learn.")
        private int spellId;
    }
}
