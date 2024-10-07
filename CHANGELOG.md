# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

### Added

### Changed

- Update dependencies ([#24](https://github.com/wiremock/wiremock-graphql-extension/pull/24) from @bgalek)
  - Update wiremock 3.0.3 -> 3.9.0
  - Update graphql-java 21.0 -> 22.3
  - Update mockk 1.13.8 -> 1.13.12
  - Update testcontainers 1.19.3 -> 1.20.2
  - Update junit 5.10.1 -> 5.11.1
  - Update mockk 1.13.8 -> 1.13.12

## [0.8.2] - 2024-01-04

### Added

- Added `operationName` parameter ([#18](https://github.com/wiremock/wiremock-graphql-extension/pull/18) from @kyle-winkelman)
- Added `GraphqlBodyMatcher.parameters` method which can use create `Parameters` easily ([#18](https://github.com/wiremock/wiremock-graphql-extension/pull/18) from @kyle-winkelman)

### Changed

- Remove `org.json:json` dependency in favor of reusing `com.github.tomakehurst.wiremock.common.Json` ([#18](https://github.com/wiremock/wiremock-graphql-extension/pull/18) from @kyle-winkelman)
- Deprecate `withRequestJson` and `withRequest` in favor of `parameters` ([#18](https://github.com/wiremock/wiremock-graphql-extension/pull/18) from @kyle-winkelman)

## [0.8.1] - 2023-12-12

### Changed

- Added `@JvmStatic` annotation to `GraphqlBodyMatcher.Companion.withRequest` method to allow Java clients to use the method without `Companion`. ([#16](https://github.com/wiremock/wiremock-graphql-extension/pull/16) from @kyle-winkelman)

## [0.8.0] - 2023-12-08

### Changed

- Implemented `graphql-java`'s `AstSorter` and `AstComparator` for GraphQL query normalization. This integration significantly aligns the supported GraphQL features of our extension with those of `graphql-java`. ([#14](https://github.com/wiremock/wiremock-graphql-extension/pull/14) from @kyle-winkelman)

## [0.7.1] - 2023-11-25

### Changed

- Update dev dependencies (kotlin, mockk, junit, testcontainers)

### Fixed

- Improved handling of newline characters in JSON strings. Newline characters are now removed to prevent parsing errors (`JSONException: Unterminated string`) when processing JSON data. This change ensures that JSON strings with embedded newlines are handled correctly by the `String.toJSONObject()` method. ([#11](https://github.com/wiremock/wiremock-graphql-extension/issues/11))

## [0.7.0] - 2023-09-27

### Changed

- Throws `InvalidQueryException` and `InvalidJsonException` when `withRequest` is called.
- When `match` method is called, it will not throw any exception if the request is invalid.

## [0.6.2] - 2023-09-08

### Added

- Added `withRequest` method which can used easily when using remote wiremock server.

## [0.6.1] - 2023-08-31

### Changed

- Update target jvmVersion 1.8 -> 11
- Update graphql-java 20.2 -> 21.0
- Update json 20230227 -> 20230618
- Update dev dependencies (kotlin, mockk, junit)

## [0.6.0] (deprecated) - 2023-08-31

### Changed

- Update wiremock 2.27.2 -> 3.0.0!
- `withRequestQueryAndVariables` method has been changed to deprecate.

## [0.5.0] - 2023-08-11

### Added

- Added `GraphqlBodyMatcher.extensionName` which can used easily when using remote wiremock server.

### Changed

- Change parameter key `expectedQuery` to `expectedJson` for remote wiremock server.

## [0.4.0] - 2023-05-25

### Added

- Support Remote Wiremock Server.

## [0.3.0] - 2023-04-26

### Added

- Support Graphql Variables.

### Changed

- `withRequestQuery` method has been changed to `withRequestQueryAndVariables` and now takes `expectedVariables` as argument. `expectedVariables` is Nullable.
- Update junit 5.8.1 -> 5.9.2
- Update mockk-jvm 1.13.4 -> 1.13.5

## [0.2.1] - 2023-04-21

### Added

- Support fragment normalization.

## [0.2.0] - 2023-04-21

### Changed

- Use `withRequestJson or Query` instead of constructor.

## [0.1.x]

Prerelease Version.
