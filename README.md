# Portfolio Management Application

![JDK11](https://img.shields.io/badge/jdk-11-green.svg?label=min.%20JDK)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a4ea8a78a3d0461a8493cd52f96e09e8)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=simonsymhoven/pm&amp;utm_campaign=Badge_Grade)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/simonsymhoven/pm.svg)](https://github.com/simonsymhoven/pm/pulls)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

If you want to have a look to the generated database, you need to install DBBrowser for SQLite (or something similar).


### Installing

A step by step series of examples that tell you how to get a development env running

`git clone https://github.com/simonsymhoven/pm.git`

`mvn clean install`

### Running

All the run configurations are checked in and are available on runnning this project on IntelliJ IDEA.
Preferred artifact for development:
 
`mvn javafx:run`

## Todo

*   check license of icons

*   rework of audit log for clients, stocks and join tables (client_stock & alternative_stock)

*   setup codecov for jenkins pipeline

*   release MVP 

## Testing

Small description

### JUnit test
[![codecov](https://codecov.io/gh/simonsymhoven/pm/branch/master/graph/badge.svg?token=C0WFTF0tHU)](https://codecov.io/gh/simonsymhoven/pm)

Explain what these tests test and why

`mvn test jacoco:report`

JUnit tests aren't implemented yet. 
<img src="https://codecov.io/gh/simonsymhoven/pm/commit/f38bcaa809edb8cc881a5484cd6d28e368c9d7bc/graphs/sunburst.svg?token=C0WFTF0tHU">

### And coding style tests

Explain what these tests test and why

`mvn checkstyle:check`

## Database schema
![Image description](https://github.com/simonsymhoven/pm/blob/master/img/schema.png)

## Deployment

Not yet available

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

* [Jenkins](https://jenkins.io) - CI

* [Codacy](https://app.codacy.com) - Code Review

* [Codecov](https://codecov.io) - Code Coverage

## Authors

[**Simon Symhoven**](https://github.com/simonsymhoven) 

See also the list of [contributors](https://github.com/simonsymhoven/pm/contributors) who participated in this project.
