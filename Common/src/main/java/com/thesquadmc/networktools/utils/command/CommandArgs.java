package com.thesquadmc.networktools.utils.command;

import com.thesquadmc.networktools.player.TSMCUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Framework - CommandArgs <br>
 * This class is passed to the command methods and contains various utilities as
 * well as the command info.
 *
 * @author minnymin3
 */
public class CommandArgs {

    private CommandSender sender;
    private org.bukkit.command.Command command;
    private String label;
    private String[] args;

    protected CommandArgs(CommandSender sender, org.bukkit.command.Command command, String label, String[] args,
                          int subCommand) {
        String[] modArgs = new String[args.length - subCommand];
        System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);

        StringBuilder buffer = new StringBuilder();
        buffer.append(label);
        for (int x = 0; x < subCommand; x++) {
            buffer.append(".").append(args[x]);
        }
        String cmdLabel = buffer.toString();
        this.sender = sender;
        this.command = command;
        this.label = cmdLabel;
        this.args = modArgs;
    }

    /**
     * Gets the command sender
     *
     * @return
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the original command object
     *
     * @return
     */
    public org.bukkit.command.Command getCommand() {
        return command;
    }

    /**
     * Gets the label including sub command labels of this command
     *
     * @return Something like 'test.subcommand'
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets all the arguments after the command's label. ie. if the command
     * label was test.subcommand and the arguments were subcommand foo foo, it
     * would only return 'foo foo' because 'subcommand' is part of the command
     *
     * @return
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets the argument at the specified index
     *
     * @param index The index to get
     * @return The string at the specified index
     */
    public String getArg(int index) {
        return args[index];
    }

    /**
     * Returns the length of the command arguments
     *
     * @return int length of args
     */
    public int length() {
        return args.length;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            return null;
        }
    }

    public TSMCUser getUser() {
        if (isPlayer()) {
            return TSMCUser.fromPlayer(getPlayer());
        }

        return null;
    }

}
