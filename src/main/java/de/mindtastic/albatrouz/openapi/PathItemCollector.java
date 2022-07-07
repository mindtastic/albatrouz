package de.mindtastic.albatrouz.openapi;

import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PathItemCollector implements
        Collector<Map.Entry<PathItem.HttpMethod, Operation>, Map<PathItem.HttpMethod, Operation>, PathItem>
{
    @Override
    public Supplier<Map<PathItem.HttpMethod, Operation>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<PathItem.HttpMethod, Operation>, Entry<PathItem.HttpMethod, Operation>> accumulator() {
        return (map, element) -> map.put(element.getKey(), element.getValue());
    }

    @Override
    public BinaryOperator<Map<PathItem.HttpMethod, Operation>> combiner() {
        return (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        };
    }

    @Override
    public Function<Map<PathItem.HttpMethod, Operation>, PathItem> finisher() {
        return operations -> {
            PathItem pi = new PathItem();
            operations.forEach(pi::operation);
            return pi;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of(Characteristics.UNORDERED);
    }

    public static PathItemCollector toPathItem() {
        return new PathItemCollector();
    }

}
