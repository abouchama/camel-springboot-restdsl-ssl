package com.aboucham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@SpringBootApplication
// load regular Spring XML file from the class path that contains the Camel XML DSL
@ImportResource(locations = {"classpath:spring/camel-context.xml"})
//@PropertySource(value = {"classpath:application.properties"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
            restConfiguration()
                    .component("restlet")
                    //.bindingMode(off)
                    .port("5117")
                    .scheme("https")
                    .endpointProperty("sslContextParameters", "#MySslContextParameter");

            rest("/books").description("Books REST service")
                    .get("/").description("The list of all the books")
                    .route().routeId("books-api")
                    .to("direct:myExample")
                    .endRest();
        }
    }
}

