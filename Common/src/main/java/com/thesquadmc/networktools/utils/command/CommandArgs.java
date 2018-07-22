package com.thesquadmc.networktools.utils.command;

import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArgs {

    private CommandSender sender;
    private org.bukkit.command.Command command;
    private String label;
    private String[] args;

    protected CommandArgs(CommandSender sender, org.bukkit.command.Command command, String label, String[] args, int subCommand) {
        try {
            String[] modArgs = new String[args.length - subCommand];
            System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);

            StringBuilder builder = new StringBuilder();
            builder.append(label);
            for (int x = 0; x < subCommand; x++) {
                builder.append(" ").append(args[x]);
            }

            String cmdLabel = builder.toString();
            this.sender = sender;
            this.command = command;
            this.label = cmdLabel;
            this.args = modArgs;

        } catch (NegativeArraySizeException e) { //hacky solution
            this.sender = sender;
            this.command = command;
            this.label = command.getName();
            this.args = new String[0];

            sender.sendMessage(CC.RED + "Please use the proper command!");
        }
    }

    /**
     * Gets the command sender
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the original command object
     */
    public org.bukkit.command.Command getCommand() {
        return command;
    }

    /**
     * Gets the label including sub command labels of this command
     *
     * @return Something like 'command subcommand'
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets all the arguments after the command's label. ie. if the command
     * label was command subcommand and the arguments were subcommand foo foo, it
     * would only return 'foo foo' because 'subcommand' is part of the command
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
        return isPlayer() ? (Player) sender : null;
    }

}
