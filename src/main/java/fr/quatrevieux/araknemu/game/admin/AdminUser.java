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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.executor.CommandExecutor;
import fr.quatrevieux.araknemu.game.listener.admin.RemoveAdminSession;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import fr.quatrevieux.araknemu.util.LogFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Set;

/**
 * Admin user session
 */
public final class AdminUser implements AdminPerformer {
    private final AdminService service;
    private final GamePlayer player;
    private final CommandExecutor executor;
    private final Logger logger;

    private final AdminUserContext context;
    private final AdminUserCommandParser parser;
    private final ExceptionHandler errorHandler;

    public AdminUser(AdminService service, GamePlayer player, CommandExecutor executor, Logger logger) throws ContextException {
        this.service = service;
        this.player = player;
        this.executor = executor;
        this.logger = logger;

        this.context = new AdminUserContext(service, service.context("player", player));
        this.parser = new AdminUserCommandParser(this);
        this.errorHandler = new ExceptionHandler(this);

        player.dispatcher().add(new RemoveAdminSession(this));
    }

    /**
     * Get the admin user context handler
     */
    public AdminUserContext context() {
        return context;
    }

    @Override
    public Optional<GameAccount> account() {
        return Optional.of(player.account());
    }

    @Override
    public void execute(String command) throws AdminException {
        logger.log(Level.INFO, EXECUTE_MARKER, "[{}] {}", this, command);

        execute(parser.parse(command));
    }

    @Override
    public boolean isGranted(Set<Permission> permissions) {
        return player.account().isGranted(permissions);
    }

    @Override
    public void log(LogType type, String message, Object... arguments) {
        final String out = LogFormatter.format(message, arguments);

        logger.log(Level.INFO, OUTPUT_MARKER, "[{}; type={}] {}", this, type, out);
        player.send(new CommandResult(type, out));
    }

    /**
     * Send the exception error to the console
     */
    public void error(Throwable error) {
        errorHandler.handle(error);
    }

    /**
     * Destroy this user session
     */
    public void logout() {
        service.removeSession(this);
    }

    /**
     * Get the admin player id
     */
    public int id() {
        return player.id();
    }

    /**
     * Send packet to admin user
     *
     * @see AdminUser#log(LogType, String, Object...) to send a message to console
     */
    public void send(Object packet) {
        player.send(packet);
    }

    /**
     * Get the admin player
     */
    public GamePlayer player() {
        return player;
    }

    @Override
    public String toString() {
        return "account=" + player.account().id() + "; player=" + player.id();
    }

    private void execute(CommandParser.Arguments arguments) throws AdminException {
        final Command command = arguments.context().command(arguments.command());

        executor.execute(command, this, arguments);
    }

    /**
     * Factory create an AdminUser
     */
    public interface Factory {
        /**
         * Create the admin user
         *
         * @param service The admin service who handles the user
         * @param player The authorized game player
         *
         * @return The created AdminUser
         */
        public AdminUser create(AdminService service, GamePlayer player) throws AdminException;
    }
}
