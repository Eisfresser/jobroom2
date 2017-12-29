# jobroom2

[![Build Status](https://travis-ci.org/alv-ch/jobroom2.svg?branch=develop)](https://travis-ci.org/alv-ch/jobroom2)
[![Coverage Status](https://codecov.io/github/alv-ch/jobroom2/coverage.svg?branch=develop)](https://codecov.io/github/alv-ch/jobroom2?branch=develop)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=ch.admin.seco.jobroom:jobroom)](https://sonarcloud.io/dashboard?id=ch.admin.seco.jobroom:jobroom)

This application was generated using JHipster 4.8.2, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.8.2](http://www.jhipster.tech/documentation-archive/v4.8.2).

This is a "gateway" application intended to be part of a microservice architecture, please refer to the [Doing microservices with JHipster][] page of the documentation for more information.

This application is configured for Service Discovery and Configuration with the JHipster-Registry. On launch, it will refuse to start if it is not able to connect to the JHipster-Registry at [http://localhost:8761](http://localhost:8761). For more information, read our documentation on [Service Discovery and Configuration with the JHipster-Registry][].

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
2. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install

We use yarn scripts and [Webpack][] as our build system.


Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    yarn start
    
To connect your local front-end to the development environment run

    yarn start-development
    
To run your local front-end in mixed mode e.i connect to local reference service on 8082 port and development environment run

    yarn start --env.localUrl='/referenceservice' --env.localPort=8082

[Yarn][] is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `yarn update` and `yarn install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `yarn help update`.

The `yarn run` command will list all of the scripts available to run for this project.

### Service workers

Service workers are commented by default, to enable them please uncomment the following code.

* The service worker registering script in index.html
```
<script>
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker
        .register('./sw.js')
        .then(function() { console.log('Service Worker Registered'); });
    }
</script>
```
* The copy file option in webpack-common.js
```js
{ from: './src/main/webapp/sw.js', to: 'sw.js' },
```
Note: Add the respective scripts/assets in `sw.js` that is needed to be cached.

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    yarn add --exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    yarn add --dev --exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:

Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:
~~~
import 'leaflet/dist/leaflet.js';
~~~

Edit [src/main/webapp/content/css/vendor.css](src/main/webapp/content/css/vendor.css) file:
~~~
@import '~leaflet/dist/leaflet.css';
~~~

Note: there are still few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using angular-cli

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts
    
### Export / Import translation json files to / from CSV file

We use the [ngx-translate](https://github.com/ngx-translate) module with JSON files to internationalize the application.
The JSON files can be exported to a single CSV file by executing the ``json2csv`` gradle task.

    ./gradlew json2csv    

This task generates the ``translation.csv`` file under the ``src/main/webapp/i18n/`` directory.
Where the structure of the CSV file is the following:

|  page    |  key                 | de          |  en         | fr          | it          |
|----------|----------------------|-------------|-------------|-------------|-------------|
|  health  |  health.table.status | Status      |  Status     | Etat        | Stato       |
| activate |  activate.title      | Aktivierung |  Activation | Activation  | Attivazione |
 

* page is same as the routing path that maps an url to component
* key is the translation key that can be used either by the ``jhiTranslate`` directive or by the ``translate`` pipe
* de, en, fr, it are the translations  
  
The ``csv2json`` gradle tasks generates the JSON translation files from the ``translation.csv``.
When we execute the ``./gradlew json2csv`` command, considering the example above the following file structure will be generated:
    
    src/main/webapp/i18n
        ├── de
        │   ├── activate.json
        │   └── health.json
        ├── en
        │   ├── activate.json
        │   └── health.json
        ├── fr
        │   ├── activate.json
        │   └── health.json
        ├── it
        │   ├── activate.json
        │   └── health.json
        └── translations.csv   
    
Where a single translation file is like:

    {
        "health": {      
            "table": {            
                "status": "Status"
            }
        }
    }

The tasks must be executed manually. If you change either a translation JSON or the csv file make sure that the changes are synchronized
and everything is committed into git.   


## Building for production

To optimize the jobroom application for production, run:

    ./gradlew -Pprod clean bootWar

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    yarn test

UI end-to-end tests are powered by [Protractor][], which is built on top of WebDriverJS. They're located in [src/test/javascript/e2e](src/test/javascript/e2e)
and can be run by starting Spring Boot in one terminal (`./gradlew bootRun`) and running the tests (`yarn run e2e`) in a second one.
### Other tests

Performance tests are run against the DEV enviroment by [Gatling][] and written in Scala. They're located in [src/test/gatling](src/test/gatling) and can be run with:

    baseURL=https://dev.job-room.ch:8443 ./gradlew -b e2e.gradle gatlingRunAll

or run individual test

    baseURL=https://dev.job-room.ch:8443 ./gradlew -b e2e.gradle gatlingRun -PgatlingSimulationClass=JobGatlingTest

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.
For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootWar -Pprod buildDocker

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: http://www.jhipster.tech
[JHipster 4.10.2 archive]: http://www.jhipster.tech/documentation-archive/v4.10.2
[Doing microservices with JHipster]: http://www.jhipster.tech/documentation-archive/v4.10.2/microservices-architecture/
[Using JHipster in development]: http://www.jhipster.tech/documentation-archive/v4.10.2/development/
[Service Discovery and Configuration with the JHipster-Registry]: http://www.jhipster.tech/documentation-archive/v4.10.2/microservices-architecture/#jhipster-registry
[Using Docker and Docker-Compose]: http://www.jhipster.tech/documentation-archive/v4.10.2/docker-compose
[Using JHipster in production]: http://www.jhipster.tech/documentation-archive/v4.10.2/production/
[Running tests page]: http://www.jhipster.tech/documentation-archive/v4.10.2/running-tests/
[Setting up Continuous Integration]: http://www.jhipster.tech/documentation-archive/v4.10.2/setting-up-ci/

[Gatling]: http://gatling.io/
[Node.js]: https://nodejs.org/
[Yarn]: https://yarnpkg.org/
[Webpack]: https://webpack.github.io/
[Angular CLI]: https://cli.angular.io/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
[Leaflet]: http://leafletjs.com/
[DefinitelyTyped]: http://definitelytyped.org/
