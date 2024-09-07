package com.github.tymefly.beep.command;


/**
 * Interface implemented by all of the programmer commands
 */
public interface Command {
    /**
     * Execute the implementing command
     * @return {@literal true} only if the command succeeded
     */
    boolean execute();


    /**
     * Returns {@literal true} only if this command accesses the Programmer
     * @return {@literal true} only if this command accesses the Programmer
     */
    default boolean requiresProgrammer() {
        return true;
    }
}
