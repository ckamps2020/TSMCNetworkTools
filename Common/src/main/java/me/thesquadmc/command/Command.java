package me.thesquadmc.command;

import me.thesquadmc.utils.enums.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * A list of  names that the command is executed under. If it is a sub command then its values would be
     * separated by periods. ie. a command that would be a subcommand of test
     * would be 'command subcommand'
     */
    String[] name();

    /**
     * Gets the required permission of the command
     */
    String permission() default "";

    //TODO Add rank support
    //Rank rank() default Rank.;

    /**
     * The message sent to the player when they do not have permission to
     * execute it
     */
    String noPermission() default "&cYou do not have permission to perform that command";

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