package com.envyful.api.neoforge.command.injector;

import com.envyful.api.command.injector.ArgumentInjector;
import net.minecraft.commands.CommandSource;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * The forge implementation of the {@link ArgumentInjector} interface
 *
 * @param <T> The type being converted to
 */
public class ForgeFunctionInjector<T> implements ArgumentInjector<T, CommandSource> {

    private final Class<T> superClass;
    private final boolean multipleArgs;
    private final BiFunction<CommandSource, String[], T> function;

    public ForgeFunctionInjector(Class<T> superClass, boolean multipleArgs, BiFunction<CommandSource, String[], T> function) {
        this.superClass = superClass;
        this.multipleArgs = multipleArgs;
        this.function = function;
    }

    @Override
    public Class<T> getConvertedClass() {
        return this.superClass;
    }

    @Override
    public boolean doesRequireMultipleArgs() {
        return this.multipleArgs;
    }

    @Override
    public T instantiateClass(CommandSource sender, List<Annotation> annotations, String... args) {
        return this.function.apply(sender, args);
    }
}
