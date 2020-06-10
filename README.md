# Trik

## Running the program:
1. From the terminal, navigate to the root folder
2. Build the project: `gradle build`
3. Start the metrics gathering daemon: `gradle run`
4. Finally, run `node .` to start the CLI program

## Build process:
The application utilizes Gradle to create its build. The `settings.gradle` file in the MeTrik folder specifies the build script and contains all of the Java dependencies. The `package.json` file in the MeTrik folder specifies the Node dependencies for the CLI program.

## Testing:
The unit testing for the metric collecting program is done with JUnit and is incorporated in the build process. The test suite is run during every build and will cause the build to fail if one or more test cases do not pass.
