package de.kopfsachen.openapi.tira;

import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.templating.mustache.OnChangeLambda;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap.Builder;
import com.samskivert.mustache.Mustache.Lambda;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.model.OperationsMap;
import io.swagger.v3.oas.models.Operation;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

        // Merged main specification
        cliOptions.add(CliOption.newString(OUTPUT_NAME, "Merged spec output filename").defaultValue(outputFile));
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
    protected Builder<String, Lambda> addMustacheLambdas() {
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

/*    @Override
    public File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename, boolean shouldGenerate, String skippedByOption) throws IOException {
        // Do we write an API file?
        if (skippedByOption.equals(CodegenConstants.APIS)) {
            Map<String, Object> yamlSpecObj = buildSingleApiSpec();
            String yaml = SerializerUtils.toYamlString(yamlSpecObj);
            if (yaml != null) {
                templateData.put("api-openapi-yaml", yaml);
            } else {
                LOGGER.warn("Failed to generate individual API spec");
            }
        }   

        return super.processTemplateToFile(templateData, templateName, outputFilename, shouldGenerate, skippedByOption, this.config.getOutputDir());
    }*/

    private Map<String, Object> buildSingleApiSpec(Map<String, Object> inputData) {
        Map<String, Object> apiSpec = new HashMap<String, Object>();

        // Create info section
        apiSpec.put("info", Map.of(
            "title", inputData.get("baseName"),
            "contact", Map.of(
                "name", inputData.get("infoName"),
                "url", inputData.get("infoUrl"),
                "email", inputData.get("infoEmail")
            ),
            "description", inputData.get("appDescription"),
            "license", Map.of(
                "name", inputData.get("licenseInfo"),
                "url", inputData.get("licenseUrl")
            ),
            "version", inputData.get("version")
        ));

        return apiSpec;
    }
}
