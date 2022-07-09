package de.mindtastic.albatrouz.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;
import java.util.Map.Entry;

/**
 * OpenApiSerializer is a class that serialized an Object representing an OpenAPI specification
 * or a subset of an OpenAPI specification to JSON.
 */
public class OpenAPISerializer extends JsonSerializer<OpenAPI> {

    private boolean ignoreVersion;

    public OpenAPISerializer withoutVersion() {
        this.ignoreVersion = true;
        return this;
    }

    @Override
    public void serialize(OpenAPI value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            return;
        }

        gen.writeStartObject();
        if (!ignoreVersion) {
            gen.writeObjectField("openapi", value.getOpenapi());
        }

        if (value.getInfo() != null) {
            gen.writeObjectField("info", value.getInfo());
        }
        if (value.getExternalDocs() != null) {
            gen.writeObjectField("externalDocs", value.getExternalDocs());
        }
        if (value.getServers() != null) {
            gen.writeObjectField("servers", value.getServers());
        }
        if (value.getSecurity() != null) {
            gen.writeObjectField("security", value.getSecurity());
        }
        if (value.getTags() != null) {
            gen.writeObjectField("tags", value.getTags());
        }
        if (value.getPaths() != null) {
            gen.writeObjectField("paths", value.getPaths());
        }
        if (value.getComponents() != null) {
            gen.writeObjectField("components", value.getComponents());
        }
        if (value.getExtensions() != null) {
            for (Entry<String, Object> e : value.getExtensions().entrySet()) {
                gen.writeObjectField(e.getKey(), e.getValue());
            }
        }
        gen.writeEndObject();
    }


}
