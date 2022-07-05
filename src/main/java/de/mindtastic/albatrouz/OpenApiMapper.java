package de.mindtastic.albatrouz;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import org.apache.commons.lang3.tuple.Pair;
import org.openapitools.codegen.CodegenServer;

public class OpenApiMapper {

    public Server mapServer(CodegenServer server) {
        ServerVariables vars = new ServerVariables();
        server.variables.stream()
                .map(var -> {
                    ServerVariable serverVar = new ServerVariable();

                    serverVar.setEnum(var.enumValues);
                    serverVar.setDefault(var.defaultValue);
                    serverVar.description(var.description);

                    return Pair.of(var.name, serverVar);
                })
                .forEach(p -> vars.addServerVariable(p.getLeft(), p.getRight()));

        return new Server()
                .url(server.url)
                .description(server.description)
                .variables(vars);
    }

}
