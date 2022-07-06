package de.mindtastic.albatrouz.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.config.GlobalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Stream;


public class Serializer {
    private final Logger LOGGER = LoggerFactory.getLogger(Serializer.class);
    private static final String YAML_MINIMIZE_QUOTES_PROPERTY = "org.openapitools.codegen.utils.yaml.minimize.quotes";
    private boolean minimizeYamlQuotes = Boolean.parseBoolean(GlobalSettings.getProperty(
            YAML_MINIMIZE_QUOTES_PROPERTY, "true"
    ));

    private boolean dropOpenApiField;

    private OpenAPI spec;

    public String asYAML() {
        try {
            return Stream.of(YAMLMapper.builder())
                    .map(b -> b.addModule(serializerModule()))
                    .map(b -> b.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true))
                    .map(this::applyYamlSettings)
                    .findFirst()
                    .orElseThrow()
                    .build()
                    .writeValueAsString(spec)
                    .replace("\r\n", "\n");

        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to write YAML:", e);
        }

        return null;
    }

    public String asJSON() {
        try {
            return JsonMapper.builder()
                    .addModule(serializerModule())
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(spec)
                    .replace("\r\n", "\n");
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to write JSON:", e);
        }

        return null;
    }

    public static Serializer forSpec(OpenAPI spec) {
        Serializer s = new Serializer();
        s.spec = spec;
        return s;
    }

    public Serializer withOpenApiField() {
        dropOpenApiField = false;
        return this;
    }

    public Serializer withoutOpenApiField() {
        dropOpenApiField = true;
        return this;
    }

    public Serializer minimizeQuotes() {
        minimizeYamlQuotes = true;
        return this;
    }

    public Serializer withoutMinimizingQuotes() {
        minimizeYamlQuotes = false;
        return this;
    }

    private SimpleModule serializerModule() {
        SimpleModule module = new SimpleModule("OpenAPIModule");
        module.addSerializer(OpenAPI.class, serializer());
        return module;
    }

    private OpenAPISerializer serializer() {
        OpenAPISerializer s = new OpenAPISerializer();
        if (dropOpenApiField) {
            return s.withoutVersion();
        }

        return s;
    }

    /*
    protected ObjectMapper yamlMapper() {
        if (yamlMapper == null) {
            yamlMapper = Yaml.mapper().copy();
            applyYamlSettings();
        }

        return yamlMapper;
    }

    protected YAMLFactory yamlFactory() {
        return (YAMLFactory) yamlMapper().getFactory();
    }
    */

    protected YAMLMapper.Builder applyYamlSettings(YAMLMapper.Builder m) {
        Optional.of(minimizeYamlQuotes)
                .filter(Boolean::booleanValue)
                .map(b -> m.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES))
                .orElseGet(() -> m.disable(YAMLGenerator.Feature.MINIMIZE_QUOTES));

        return m;
    }
}
