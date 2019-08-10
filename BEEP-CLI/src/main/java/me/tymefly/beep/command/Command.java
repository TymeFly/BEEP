package me.tymefly.beep.command;


/**
 * Interface implemented by all of the programmer commands
 */
public interface Command {
    /**
     * Execute the implementing command
     * @return {@literal true} only if the command succeeded
     */
    boolean execute();
}
