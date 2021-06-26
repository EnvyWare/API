package com.envyful.api.type.requirement;

/**
 *
 * Interface providing an implementation for validating data against a requirement
 *
 * @param <T> The type of data to be validated
 */
public interface Requirement<T> {

    /**
     *
     * Gets the basic requirement data
     *
     * @return The basic requirement
     */
    T get();

    /**
     *
     * Determines if the passed data fits the requirement.
     *
     * @param data The data to validate
     * @return False if it doesn't fit the requirement(s)
     */
    boolean fits(T data);

}
