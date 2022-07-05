package de.mindtastic.albatrouz;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.serializer.SerializerUtils;
import org.openapitools.codegen.templating.mustache.OnChangeLambda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.serializer.Serializer;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class TiraGenerator extends DefaultCodegen implements CodegenConfig {
    public final String OUTPUT_NAME = "outputFile";
    private final Logger LOGGER = LoggerFactory.getLogger(TiraGenerator.class);

    protected String outputFile = "openapi.yaml";

    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    public String getName() {
        return "tira";
    }

    public String getHelp() {
        return "Generates an openapi-documentation with tira extension suport";
    }

    public TiraGenerator() {
        super();

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        // Support full open api specification feature set
        modifyFeatureSet(features -> features
                .documentationFeatures(EnumSet.allOf(DocumentationFeature.class))
                .dataTypeFeatures(EnumSet.allOf(DataTypeFeature.class))
                .wireFormatFeatures(EnumSet.allOf(WireFormatFeature.class))
                .securityFeatures(EnumSet.allOf(SecurityFeature.class))
                .globalFeatures(EnumSet.allOf(GlobalFeature.class))
                .parameterFeatures(EnumSet.allOf(ParameterFeature.class))
                .schemaSupportFeatures(EnumSet.allOf(SchemaSupportFeature.class))
        );

        // Subspecs
        outputFolder = "generated-code" + File.separator + "tira";
        apiTemplateFiles.put("api.mustache", ".yaml");
        embeddedTemplateDir = templateDir = "tira";
        apiPackage = File.separator + "Apis";

        cliOptions.add(CliOption.newString(OUTPUT_NAME, "Merged spec output filename").defaultValue(outputFile));
    }

    /**
     * Method that processes a Map of operations, enhanced with more attributes depending on the values in this
     * generator. The result will be used to populate the individual API template files.
     * @see DefaultGenerator
     * @param objs
     * @param allModels
     * @return
     */
    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        SingleSpecBuilder builder = SingleSpecBuilder.buildSpecFromServiceProps(objs, allModels);
        return super.postProcessOperationsWithModels(objs, allModels);
    }

    @Override
    public void processOpts() {
        super.processOpts();
        if (additionalProperties.containsKey(OUTPUT_NAME)) {
            outputFile = additionalProperties.get(OUTPUT_NAME).toString();
        }
        LOGGER.info("Output file [outputFile={}]", outputFile);
        supportingFiles.add(new SupportingFile("openapi.mustache", outputFile));
    }

    @Override
    protected ImmutableMap.Builder<String, Mustache.Lambda> addMustacheLambdas() {
        return super.addMustacheLambdas()
                .put("onchange", new OnChangeLambda());
    }

    @Override
    public Map<String, Object> postProcessSupportingFileData(Map<String, Object> objs) {
        generateYAMLSpecFile(objs);
        return super.postProcessSupportingFileData(objs);
    }

    @Override
    public String escapeQuotationMark(String input) {
        return input;
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input;
    }
}
