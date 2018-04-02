package me.thesquadmc.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Completer {

    /**
     * The command that this completer completes. If it is a sub command then
     * its values would be separated by periods. ie. a command that would be a
     * subcommand of test would be 'command subcommand'
     */
    String name();

    /**
     * A list of alternate names that the completer is executed under. See
     * name() for details on how names work
     */
    String[] aliases() default {};

}