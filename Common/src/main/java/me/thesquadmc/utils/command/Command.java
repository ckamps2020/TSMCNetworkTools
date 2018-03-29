package me.thesquadmc.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The name of the command. If it is a sub command then its values would be
     * separated by periods. ie. a command that would be a subcommand of test
     * would be 'command subcommand'
     */
    String name();

    /**
     * Gets the required permission of the command
     */
    String permission() default "";

    /**
     * The message sent to the player when they do not have permission to
     * execute it
     */
    String noPermission() default "&cYou do not have permission to perform that action";

    /**
     * A list of alternate names that the command is executed under. See
     * name() for details on how names work
     */
    String[] aliases() default {};

    /**
     * The description that will appear in /help of the command
     */
    String description() default "There is no description :(";

    /**
     * The usage that will appear in /help (commandname)
     */
    String usage() default "";

    /**
     * Whether or not the command is available to players only
     */
    boolean playerOnly() default false;
}