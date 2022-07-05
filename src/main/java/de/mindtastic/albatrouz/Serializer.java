package de.mindtastic.albatrouz;

import org.openapitools.codegen.config.GlobalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Serializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Serializer.class);
    private static final String YAML_MINIMIZE_QUOTES_PROPERTY = "org.openapitools.codegen.utils.yaml.minimize.quotes";
    private static final boolean minimizeYamlQuotes = Boolean.parseBoolean(GlobalSettings.getProperty(YAML_MINIMIZE_QUOTES_PROPERTY, "true"));

}
