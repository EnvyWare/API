package com.envyful.api.type.requirement.impl;

import com.envyful.api.type.requirement.Requirement;

/**
 *
 * Minimum integer implementation of the {@link Requirement} interface.
 * Requires the integer being checked to be greater than the minimum value specified at instantiation.
 *
 */
public class MinimumIntegerRequirement implements Requirement<Integer> {

    private final int requirement;

    public MinimumIntegerRequirement(int requirement) {
        this.requirement = requirement;
    }

    @Override
    public Integer get() {
        return this.requirement;
    }

    @Override
    public boolean fits(Integer data) {
        if (data == null) {
            return false;
        }

        return data >= this.requirement;
    }
}
