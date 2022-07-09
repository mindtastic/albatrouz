package de.mindtastic.albatrouz.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.core.jackson.mixin.MediaTypeMixin;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.core.jackson.mixin.SchemaMixin;
import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.config.GlobalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import static java.util.Map.entry;
import java.util.Optional;
import java.util.stream.Stream;


public class Serializer {
    private final Logger LOGGER = LoggerFactory.getLogger(Serializer.class);
    private static final String YAML_MINIMIZE_QUOTES_PROPERTY = "org.openapitools.codegen.utils.yaml.minimize.quotes";
    private boolean minimizeYamlQuotes = Boolean.parseBoolean(GlobalSettings.getProperty(
            YAML_MINIMIZE_QUOTES_PROPERTY, "true"
    ));
    private boolean dropOpenApiField = false;
    private boolean includeNullValues = true;
    private JsonInclude.Include inclusionRule = JsonInclude.Include.USE_DEFAULTS;

    private Map<YAMLGenerator.Feature, Boolean> yamlFeatures = Map.ofEntries(
            entry(YAMLGenerator.Feature.MINIMIZE_QUOTES, Boolean.parseBoolean(GlobalSettings.getProperty(
                    YAML_MINIMIZE_QUOTES_PROPERTY, "true"
            ))),
            entry(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false),
            entry(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE, true)
    );

    private OpenAPI spec;

    public String asYAML() {
        try {
            return Stream.of(YAMLMapper.builder())
                    .map(b -> b.addModule(serializerModule()))
                    .map(b -> b.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true))
                    .map(this::applyYamlSerializationConfig)
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

    public Serializer withNullValues() {
        inclusionRule = JsonInclude.Include.ALWAYS;
        return this;
    }

    public Serializer withoutNullValues() {
        inclusionRule = JsonInclude.Include.NON_NULL;
        return this;
    }

    public Serializer minimizeQuotes() {
        yamlFeatures.put(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        return this;
    }

    public Serializer withoutMinimizingQuotes() {
        yamlFeatures.put(YAMLGenerator.Feature.MINIMIZE_QUOTES, false);
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

    protected YAMLMapper.Builder applyYamlSettings(YAMLMapper.Builder b) {
        yamlFeatures.forEach((key, value) -> setFeatureActive(b, key, value));
        return b;
    }

    protected YAMLMapper.Builder applyYamlSerializationConfig(YAMLMapper.Builder b) {
        //b.serializationInclusion(inclusionRule);
        b.serializationInclusion(JsonInclude.Include.CUSTOM);
        b.addMixIn(Schema.class, SchemaMixin.class);
        b.addMixIn(MediaType.class, MediaTypeMixin.class);
        return b;
    }

    protected void setFeatureActive(YAMLMapper.Builder b, YAMLGenerator.Feature f, boolean enable) {
        Optional.of(enable)
                .filter(Boolean::booleanValue)
                .map(v -> b.enable(f))
                .orElseGet(() -> b.disable(f));
    }
}
