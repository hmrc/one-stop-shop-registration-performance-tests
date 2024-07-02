# one-stop-shop-registration-performance-tests
Performance test suite for `one-stop-shop-registration-frontend`, using [performance-test-runner](https://github.com/hmrc/performance-test-runner) under the hood.

## Local setup - MongoDB and Service Manager

Run at least 4.0 with a replica set: `docker run --restart unless-stopped -d -p 27017-27019:27017-27019 --name mongo4 mongo:4.0 --replSet rs0`
Connect to said replica set: `docker exec -it mongo4 mongo`
When that console is there: `rs.initiate()`
You then should be running 4.0 with a replica set. You may have to re-run the rs.initiate() after you've restarted

sm --start ONE_STOP_SHOP_ALL -r

## Running the tests

### Run one-stop-shop-registration-frontend locally to turn off email verification

Use "sm --stop ONE_STOP_SHOP_REGISTRATION_FRONTEND" to stop the frontend services in service manager then in the terminal for each service run:
sbt run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes

Amend application.conf in ONE_STOP_SHOP_REGISTRATION_FRONTEND repo so that email-verification-enabled is false.

### Rejoin tests will require returns data

In order to use VRN 600000050 for a rejoin, it requires the Q1 2024 return to be submitted in the environment.

This can be added locally via the one-stop-shop-registration-journey tests or manually do a nil return.

This should already be set up on staging, however can also be done manually as a nil return on there if it becomes deleted 
in the future.

#### Smoke test

It might be useful to try the journey with one user to check that everything works fine before running the full performance test
```
sbt -Dperftest.runSmokeTest=true -DrunLocal=true gatling:test
```

#### Running the performance test
```
sbt -DrunLocal=true gatling:test
```
### Run the example test against staging environment

#### Smoke test
```
sbt -Dperftest.runSmokeTest=true -DrunLocal=false gatling:test
```

#### Run the performance test on staging

To run a full performance test against staging environment, see Jenkins [https://performance.tools.staging.tax.service.gov.uk/job/one-stop-shop-registration-performance-tests/](https://performance.tools.staging.tax.service.gov.uk/job/one-stop-shop-registration-performance-tests/)

##### app-config-staging:
app-config-staging has been set up to use the testOnly routes so that service manager will run the equivalent of
"sbt run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes" on staging.


### Scalafmt
 This repository uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf).

 To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

 ```
 sbt scalafmtAll
 ```

 To check files have been formatted as expected execute:

 ```
 sbt scalafmtCheckAll scalafmtSbtCheck
 ```

[Visit the official Scalafmt documentation to view a complete list of tasks which can be run.](https://scalameta.org/scalafmt/docs/installation.html#task-keys)
