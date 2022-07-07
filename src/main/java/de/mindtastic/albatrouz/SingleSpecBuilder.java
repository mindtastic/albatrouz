package de.mindtastic.albatrouz;

import de.mindtastic.albatrouz.openapi.PathsFilterer;
import de.mindtastic.albatrouz.serialization.Serializer;
import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;

import java.util.List;

public class SingleSpecBuilder {

    /**
     * The full OpenAPI spec where the single specs shall be built from
     */
    protected OpenAPI baseSpec;

    protected OperationMap operations;

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
        var paths = baseSpec.getPaths();
        spec.paths(PathsFilterer.forPaths(baseSpec.getPaths()).filterOperationIds(operationIds));

        return spec;
    }


    public String buildYaml() {
        return Serializer.forSpec(buildSpec())
                .withOpenApiField()
                .withoutNullValues()
                .asYAML();
    }

     public SingleSpecBuilder forOperations(OperationsMap objs) {
        operations = objs.getOperations();
        return this;
    }

    public static SingleSpecBuilder fromSpec(OpenAPI spec) {
        return new SingleSpecBuilder(spec);
    }

}
