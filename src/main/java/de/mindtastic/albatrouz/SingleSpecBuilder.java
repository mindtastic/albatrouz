package de.mindtastic.albatrouz;

import de.mindtastic.albatrouz.serialization.Serializer;
import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenServer;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;

import java.util.*;

public class SingleSpecBuilder {
    private final OpenApiMapper mapper = new OpenApiMapper();

    private OperationMap operations;
    private List<String> importPaths;

    private List<CodegenModel> usedModels;

    protected SingleSpecBuilder(OperationsMap operations) {
        this.operations = operations.getOperations();
        this.importPaths = operations.getImports().stream()
                .map(importMap -> importMap.get("import"))
                .toList();
    }

    public OpenAPI buildSpec() {
        OpenAPI spec = new OpenAPI();

        getServers().stream()
                .map(mapper::mapServer)
                .forEach(spec::addServersItem);

        return spec;
    }

    public String buildYaml() {
        return Serializer.forSpec(buildSpec())
                .withoutOpenApiField()
                .asYAML();
    }

     public static SingleSpecBuilder buildSpecFromServiceProps(OperationsMap objs, List<ModelMap> models) {
        SingleSpecBuilder builder = new SingleSpecBuilder(objs);
        builder.usedModels = models.stream()
                .filter(builder::usesModel)
                .map(ModelMap::getModel)
                .toList();

        return builder;
    }

    public boolean usesModel(ModelMap model) {
        return Optional.ofNullable(model.get("importPath"))
                .isPresent();
    }

    public List<CodegenServer> getServers() {
        return operations.getOperation().stream()
                .map(op -> op.servers)
                .distinct()
                .min(Comparator.comparing(List::size))
                .orElse(Collections.emptyList());
    }

}