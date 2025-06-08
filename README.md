# Template Spring Application

This is a Spring Boot project. It requires Java 17 or newer.

## Building

Use the Maven wrapper so that Maven does not need to be installed on the build machine. Run the command from the project root:

```bash
./mvnw clean package
```

On Windows use PowerShell or CMD from the project root. Prefix the script with `./` to run it from the current directory:

```cmd
./mvnw.cmd clean package
```

## Running locally

Set the environment variables required by `src/main/resources/application.properties` or create a profile with those values. Then start the application:

```bash
./mvnw spring-boot:run
```

On Windows use the wrapper from the project root. Prefix the script with `./`:

```cmd
./mvnw.cmd spring-boot:run
```

## Testing

The test suite includes default credentials in `src/test/resources/application.properties`, so tests can run without additional configuration:

```bash
./mvnw test
```

On Windows use the wrapper from the project root. Prefix the script with `./`:

```cmd
./mvnw.cmd test
```

