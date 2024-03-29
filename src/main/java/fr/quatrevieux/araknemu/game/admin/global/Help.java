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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import fr.quatrevieux.araknemu.game.admin.help.DefaultHelpRenderer;

/**
 * Show help about the console usage
 */
public final class Help extends AbstractCommand<CommandParser.Arguments> {
    private final ArgumentsHydrator hydrator;

    public Help(ArgumentsHydrator hydrator) {
        this.hydrator = hydrator;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(formatter -> formatter
                .description("Show help for use the console commands")
                .synopsis("help [COMMAND NAME]")
                .example("help", "List all available commands")
                .example("help echo", "Show the help for the echo command")
            )
        ;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public void execute(AdminPerformer performer, CommandParser.Arguments arguments) throws AdminException {
        if (arguments.arguments().size() > 1) {
            command(performer, arguments, arguments.arguments().get(1));
            return;
        }

        global(performer, arguments);
    }

    /**
     * Show help for the given command
     */
    private void command(AdminPerformer performer, CommandParser.Arguments arguments, String commandName) throws AdminException {
        final Command command = arguments.context().command(commandName);
        final CommandHelp help = hydrator.help(command, command.createArguments(), command.help());

        performer.success("<b>Help for {}</b>", command.name());
        performer.info(help.render(new DefaultHelpRenderer()));
    }

    /**
     * Show the global help page
     */
    private void global(AdminPerformer performer, CommandParser.Arguments arguments) {
        performer.success("<b>Admin console usage</b>");
        performer.info("For execute a command, simply type the command name, and add arguments separated by a single space.");
        performer.error("<i>(!) The command and arguments are case sensitive, i.e. HELP and help are not the same !</i>");
        performer.info(
            "Some commands needs a context to works. The context should be set in front of the command : [context] [command] [arguments...].\n" +
            "The context can define the target user or account. The contexts syntax are :\n" +
            "!                 - The current user\n" +
            "@John             - The player 'John'\n" +
            "#Foo              - The account 'Foo'\n" +
            "*                 - The server context\n" +
            ":                 - Contains debug commands"
        );

        performer.success("<b>List of available commands :</b>");
        performer.info("<i>(i) You can click on the command name for open its help page</i>");

        for (Command command : arguments.context().commands()) {
            if (!performer.isGranted(command.permissions())) {
                continue;
            }

            performer.info(
                "{} - {}",
                new Link()
                    .text(command.name())
                    .execute((arguments.contextPath().isEmpty() ? "" : arguments.contextPath() + " ") + arguments.command() + " " + command.name()),
                command.help().description()
            );
        }
    }
}
