# Camel Spring-boot Rest+SSL application 

This example shows how to deploy a Rest SSL to Spring Boot StandAlone & OpenShift.

## Requirements

Openshift cluster up and running.

## Setup

### Building

The example can be built with

    mvn clean install

### Running the example in Spring Boot
In resources/spring/camel-context.xml, ensure to uncomment "sslContextParameters" section under 

<!-- Spring Boot StandAlone -->

And comment the "sslContextParameters" section under     

<!-- OpenShift -->

Run the application:

    mvn spring-boot:run

In the log you will see the following lines:

```
[main] INFO  o.a.camel.spring.SpringCamelContext - Route: route1 started and consuming from: https://0.0.0.0:5117/myexample/customers?restletMethods=GET
[main] INFO  o.a.camel.spring.SpringCamelContext - Route: books-api started and consuming from: https://0.0.0.0:5117/books/?restletMethods=GET
```

You can test If everything works with the following curl command:

```
$ curl --insecure https://0.0.0.0:5117/myexample/customers
Hey your rest port is working now --> Enjoy your camel #Microservices | POD : hostname
```

### Running the example in OpenShift

It is assumed that:
- OpenShift platform is already running, if not you can find details how to [Install OpenShift at your site](https://docs.openshift.com/container-platform/3.3/install_config/index.html).
- Your system is configured for Fabric8 Maven Workflow, if not you can find a [Get Started Guide](https://access.redhat.com/documentation/en/red-hat-jboss-middleware-for-openshift/3/single/red-hat-jboss-fuse-integration-services-20-for-openshift/)

The rest port https --> "5117" is defined in fabric8/deployment.yml in order to be exposed.

In order to expose the same port by the service, the following file fabric8/service.yml reference that port.

The certificate keys in the deployment/keys directory are copied to the container in the /deployments/keys path.

In resources/spring/camel-context.xml, ensure to uncomment "sslContextParameters" section under 

<!-- OpenShift -->

And comment the "sslContextParameters" section under     

<!-- Spring Boot StandAlone -->

The example can be built and run on OpenShift using a single goal:

    mvn fabric8:deploy

By running "oc get pods" your pod should be running like below:
```
camel-springboot-restdsl-1-gzgpr            1/1       Running     0          53s
```

by running oc logs "pod_name", you will see the following lines:

```
[main] INFO  o.a.camel.spring.SpringCamelContext - Route: route1 started and consuming from: https://0.0.0.0:5117/myexample/customers?restletMethods=GET
[main] INFO  o.a.camel.spring.SpringCamelContext - Route: books-api started and consuming from: https://0.0.0.0:5117/books/?restletMethods=GET
```

You can test by exposing this service via route:

```
oc create route passthrough --service camel-springboot-restdsl-ssl --port 5117
route "camel-springboot-restdsl-ssl" created
```

If everything works with the following curl command:

```
$ curl --insecure https://camel-springboot-restdsl-ssl-myproject.192.168.42.241.nip.io/myexample/customers
Hey your rest port is working now --> Enjoy your camel #Microservices | POD : camel-springboot-restdsl-1-gzgpr
```

# How to generate certificate for localhost test:
```
keytool -genkey -alias replserver \
    -keyalg RSA -keystore keystore.jks \
    -dname "cn=localhost, ou=IT, o=Continuent, c=US" \
    -storepass password -keypass password

keytool -export -alias replserver -file client.cer -keystore keystore.jks


keytool -import -trustcacerts -alias replserver -file client.cer \
    -keystore truststore.ts -storepass password -noprompt
```

# In order to avoid the following exception:
sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

```
keytool -importcert -file client.cer -alias replserver -keystore $JAVA_HOME/jre/lib/security/cacerts
enter PW: changeit
```