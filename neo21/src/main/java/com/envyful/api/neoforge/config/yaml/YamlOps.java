package com.envyful.api.neoforge.config.yaml;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class YamlOps implements DynamicOps<CommentedConfigurationNode> {

    public static final YamlOps INSTANCE = new YamlOps();

    protected YamlOps() {}

    @Override
    public CommentedConfigurationNode empty() {
        return CommentedConfigurationNode.root();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, CommentedConfigurationNode input) {
        if (input.isMap()) {
            return convertMap(outOps, input);
        }

        if (input.isList()) {
            return convertList(outOps, input);
        }

        var raw = input.raw();

        if (raw == null) {
            return outOps.empty();
        }

        if (raw instanceof String string) {
            return outOps.createString(string);
        }

        if (raw instanceof Number number) {
            return outOps.createNumeric(number);
        }

        if (raw instanceof Boolean bool) {
            return outOps.createBoolean(bool);
        }

        if (raw instanceof CommentedConfigurationNode node) {
            return convertTo(outOps, node);
        }

        return outOps.empty();
    }

    @Override
    public DataResult<Number> getNumberValue(CommentedConfigurationNode input) {
        var raw = input.raw();

        if (raw instanceof Number number) {
            return DataResult.success(number);
        }

        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public CommentedConfigurationNode createNumeric(Number i) {
        var empty = this.empty();
        try {
            return empty.set(i);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataResult<Boolean> getBooleanValue(CommentedConfigurationNode input) {
        var raw = input.raw();

        if (raw instanceof Boolean bool) {
            return DataResult.success(bool);
        }

        return DataResult.error(() -> "Not a boolean: " + input);
    }

    @Override
    public CommentedConfigurationNode createBoolean(boolean value) {
        var empty = this.empty();
        try {
            return empty.set(value);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataResult<String> getStringValue(CommentedConfigurationNode input) {
        var raw = input.raw();

        if (raw instanceof String string) {
            return DataResult.success(string);
        }

        return DataResult.error(() -> "Not a string: " + input);
    }

    @Override
    public CommentedConfigurationNode createString(String value) {
        var empty = this.empty();
        try {
            return empty.set(value);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataResult<CommentedConfigurationNode> mergeToList(CommentedConfigurationNode list, CommentedConfigurationNode value) {
        if (!list.isList() && list != empty()) {
            return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
        }

        var newList = empty();

        newList.from(list);

        try {
            newList.appendListNode().set(value);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        return DataResult.success(newList);
    }

    @Override
    public DataResult<CommentedConfigurationNode> mergeToList(CommentedConfigurationNode list, List<CommentedConfigurationNode> values) {
        if (!list.isList() && list != empty()) {
            return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
        }

        var newList = empty();
        newList.from(list);

        try {
            for (var value : values) {
                newList.appendListNode().set(value);
            }
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        return DataResult.success(newList);
    }

    @Override
    public DataResult<CommentedConfigurationNode> mergeToMap(CommentedConfigurationNode map, CommentedConfigurationNode key, CommentedConfigurationNode value) {
        var rawKey = key.raw();

        if (!(rawKey instanceof String)) {
            return DataResult.error(() -> "key is not a string: " + key, map);
        }

        var newMap = empty();
        newMap.from(map);

        try {
            newMap.node(key.getString()).set(value);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        return DataResult.success(newMap);
    }

    @Override
    public DataResult<CommentedConfigurationNode> mergeToMap(CommentedConfigurationNode map, MapLike<CommentedConfigurationNode> values) {
        var newMap = empty();
        newMap.from(map);

        for (var entry : values.entries().toList()) {
            var key = entry.getFirst();
            var value = entry.getSecond();

            var rawKey = key.raw();

            if (!(rawKey instanceof String)) {
                return DataResult.error(() -> "key is not a string: " + key, map);
            }

            try {
                newMap.node(rawKey).set(value);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }

        return DataResult.success(newMap);
    }

    @Override
    public DataResult<Stream<Pair<CommentedConfigurationNode, CommentedConfigurationNode>>> getMapValues(CommentedConfigurationNode input) {
        if (!input.isMap()) {
            return DataResult.error(() -> "Not a JSON object: " + input);
        }

        var childrenMap = input.childrenMap();

        if (childrenMap.isEmpty()) {
            return DataResult.success(Stream.empty());
        }

        List<Pair<CommentedConfigurationNode, CommentedConfigurationNode>> pairs = new ArrayList<>();

        for (var entry : childrenMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            var commentedKey = empty();

            try {
                commentedKey.set(key);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

            pairs.add(Pair.of(commentedKey, value));
        }

        return DataResult.success(pairs.stream());
    }

    @Override
    public DataResult<MapLike<CommentedConfigurationNode>> getMap(CommentedConfigurationNode input) {
        if (!input.isMap()) {
            return DataResult.error(() -> "Not a JSON object: " + input);
        }

        var map = input.childrenMap();

        return DataResult.success(new MapLike<>() {
            @Nullable
            @Override
            public CommentedConfigurationNode get(CommentedConfigurationNode key) {
                var rawKey = key.raw();

                if (!(rawKey instanceof String)) {
                    return null;
                }

                return map.get(key.getString());
            }

            @Nullable
            @Override
            public CommentedConfigurationNode get(String key) {
                return map.get(key);
            }

            @Override
            public Stream<Pair<CommentedConfigurationNode, CommentedConfigurationNode>> entries() {
                List<Pair<CommentedConfigurationNode, CommentedConfigurationNode>> pairs = new ArrayList<>();

                for (var entry : map.entrySet()) {
                    var key = entry.getKey();
                    var value = entry.getValue();

                    var commentedKey = empty();

                    try {
                        commentedKey.set(key);
                    } catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }

                    pairs.add(Pair.of(commentedKey, value));
                }

                return pairs.stream();
            }

            @Override
            public String toString() {
                return "MapLike[" + map + "]";
            }
        });
    }

    @Override
    public CommentedConfigurationNode createMap(Stream<Pair<CommentedConfigurationNode, CommentedConfigurationNode>> map) {
        var output = this.empty();

        for (var pair : map.toList()) {
            try {
                output.node(pair.getFirst()).set(pair.getSecond());
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }

        return output;
    }

    @Override
    public DataResult<Stream<CommentedConfigurationNode>> getStream(CommentedConfigurationNode input) {
        if (!input.isList()) {
            return DataResult.error(() -> "Not a list: " + input);
        }

        return DataResult.success(StreamSupport.stream(input.childrenList().spliterator(), false));
    }

    @Override
    public DataResult<Consumer<Consumer<CommentedConfigurationNode>>> getList(CommentedConfigurationNode input) {
        if (!input.isList()) {
            return DataResult.error(() -> "Not a list: " + input);
        }

        return DataResult.success(c -> {
            for (final CommentedConfigurationNode element : input.childrenList()) {
                c.accept(element);
            }
        });
    }

    @Override
    public CommentedConfigurationNode createList(Stream<CommentedConfigurationNode> input) {
        var output = this.empty();

        input.forEach(commentedConfigurationNode -> {
            try {
                output.appendListNode().set(commentedConfigurationNode);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        });

        return output;
    }

    @Override
    public CommentedConfigurationNode remove(CommentedConfigurationNode input, String key) {
        input.removeChild(key);
        return input;
    }

    @Override
    public String toString() {
        return "YAML";
    }

    @Override
    public ListBuilder<CommentedConfigurationNode> listBuilder() {
        return new ArrayBuilder();
    }

    private static final class ArrayBuilder implements ListBuilder<CommentedConfigurationNode> {
        private DataResult<CommentedConfigurationNode> builder = DataResult.success(CommentedConfigurationNode.root(), Lifecycle.stable());

        @Override
        public DynamicOps<CommentedConfigurationNode> ops() {
            return INSTANCE;
        }

        @Override
        public ListBuilder<CommentedConfigurationNode> add(CommentedConfigurationNode value) {
            builder = builder.map(b -> {
                try {
                    b.appendListNode().set(value);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
                return b;
            });
            return this;
        }

        @Override
        public ListBuilder<CommentedConfigurationNode> add(DataResult<CommentedConfigurationNode> value) {
            builder = builder.apply2stable((b, element) -> {
                try {
                    b.appendListNode().set(element);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
                return b;
            }, value);
            return this;
        }

        @Override
        public ListBuilder<CommentedConfigurationNode> withErrorsFrom(final DataResult<?> result) {
            builder = builder.flatMap(r -> result.map(v -> r));
            return this;
        }

        @Override
        public ListBuilder<CommentedConfigurationNode> mapError(final UnaryOperator<String> onError) {
            builder = builder.mapError(onError);
            return this;
        }

        @Override
        public DataResult<CommentedConfigurationNode> build(CommentedConfigurationNode prefix) {
            var result = builder.flatMap(b -> {
                if (!prefix.isList() && prefix != ops().empty()) {
                    return DataResult.error(() -> "Cannot append a list to not a list: " + prefix, prefix);
                }

                var output = ops().empty();

                for (CommentedConfigurationNode commentedConfigurationNode : prefix.childrenList()) {
                    try {
                        output.appendListNode().set(commentedConfigurationNode);
                    } catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }
                }

                for (CommentedConfigurationNode commentedConfigurationNode : b.childrenList()) {
                    try {
                        output.appendListNode().set(commentedConfigurationNode);
                    } catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }
                }

                return DataResult.success(output, Lifecycle.stable());
            });

            builder = DataResult.success(ops().empty(), Lifecycle.stable());
            return result;
        }
    }

    @Override
    public RecordBuilder<CommentedConfigurationNode> mapBuilder() {
        return new YamlRecordBuilder();
    }

    private class YamlRecordBuilder extends RecordBuilder.AbstractStringBuilder<CommentedConfigurationNode, CommentedConfigurationNode> {
        protected YamlRecordBuilder() {
            super(YamlOps.this);
        }

        @Override
        protected CommentedConfigurationNode initBuilder() {
            return CommentedConfigurationNode.root();
        }

        @Override
        protected CommentedConfigurationNode append(String key, CommentedConfigurationNode value, CommentedConfigurationNode builder) {
            builder.node(key).from(value);
            return builder;
        }

        @Override
        protected DataResult<CommentedConfigurationNode> build(CommentedConfigurationNode builder, CommentedConfigurationNode prefix) {
            if (prefix == null) {
                return DataResult.success(builder);
            }

            if (prefix.empty()) {
                return DataResult.success(builder);
            }

            if (prefix.isMap()) {
                var output = ops().empty();

                for (var entry : prefix.childrenMap().entrySet()) {
                    output.node(entry.getKey(), entry.getValue());
                }

                for (var entry : builder.childrenMap().entrySet()) {
                    output.node(entry.getKey(), entry.getValue());
                }

                return DataResult.success(output);
            }

            var raw = prefix.raw();

            if (raw instanceof String prefixString && prefixString.isEmpty()) {
                return DataResult.success(builder);
            }

            var empty = ops().empty();
            empty.node(prefix).mergeFrom(builder);
            return DataResult.success(empty);
        }
    }
}
