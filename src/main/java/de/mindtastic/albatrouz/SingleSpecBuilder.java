package de.mindtastic.albatrouz;

import de.mindtastic.albatrouz.openapi.PathsFilterer;
import de.mindtastic.albatrouz.serialization.Serializer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleSpecBuilder {

    /**
     * The full OpenAPI spec where the single specs shall be built from
     */
    protected OpenAPI baseSpec;

    protected OperationMap operations;

    protected List<String> usedImports;

    protected SingleSpecBuilder(OpenAPI spec) {
        baseSpec = spec;
    }


    public OpenAPI buildSpec() {
        OpenAPI spec = new OpenAPI();
        spec.openapi(baseSpec.getOpenapi());
        spec.info(baseSpec.getInfo().title(operations.getClassname()));

        List<String> operationIds = operations.getOperation().stream()
                .map(op -> op.operationIdOriginal)
                .toList();

        // Filter all operations from base spec that do not belong to the currently request single spec
        spec.paths(PathsFilterer.forPaths(baseSpec.getPaths()).filterOperationIds(operationIds));
        spec.setComponents(getComponents());

        return spec;
    }

    protected Components getComponents() {
        Components c = new Components();
        c.schemas(getSchemas());
        return c;
    }

    protected Map<String, Schema> getSchemas() {
        return baseSpec.getComponents().getSchemas().entrySet().stream()
                .filter(entry -> usedImports.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String buildYaml() {
        return Serializer.forSpec(buildSpec())
                .withOpenApiField()
                .withoutNullValues()
                .asYAML();
    }

     public SingleSpecBuilder forOperations(OperationsMap objs) {
        operations = objs.getOperations();
        usedImports = objs.getImports().stream()
                .map(i -> i.get("import"))
                .toList();
        return this;
    }

    public static SingleSpecBuilder fromSpec(OpenAPI spec) {
        return new SingleSpecBuilder(spec);
    }

}
