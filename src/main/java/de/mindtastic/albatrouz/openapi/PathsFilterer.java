package de.mindtastic.albatrouz.openapi;


import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PathsFilterer {

    private Paths paths;

    protected PathsFilterer(Paths paths) {
        this.paths = paths;
    }

    public Paths filterOperationIds(List<String> operationIds) {
        Paths filtered = new Paths();
        filtered.extensions(paths.getExtensions());

        paths.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> filterOpIdsFromPathItem(entry.getValue(), operationIds)
                ))
                .entrySet().stream()
                .filter(ent -> !ent.getValue().readOperations().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .forEach(filtered::addPathItem);

        return filtered;
    }

    private PathItem filterOpIdsFromPathItem(PathItem p, List<String> operationIds) {
        return p.readOperationsMap().entrySet().stream()
                .filter(includePredicate(operationIds))
                .collect(PathItemCollector.toPathItem());
    }

    private Predicate<Map.Entry<PathItem.HttpMethod, Operation>> includePredicate(List<String> opIds) {
        return pathItemOpEntry -> opIds.contains(pathItemOpEntry.getValue().getOperationId());
    }

    public static PathsFilterer forPaths(io.swagger.v3.oas.models.Paths p) {
        return new PathsFilterer(p);
    }

}
