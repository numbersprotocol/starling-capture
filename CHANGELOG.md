# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.6.3 - 2020-10-05

### Added

- Build the workflow automatically creating GitHub release with development tag.
- Build the workflow automatically signing and deploying master-release app on the internal track of Google Play Console.

## 1.6.0 - 2020-09-22

### Added

- Replace camera intent with built-in camera fragment. #81 
- Built-in picture capturing.
- Built-in video recording.
- Support video publishing. #66 

### Fixed

- Fix the incorrect name of the internal test folder.

### Changed

#### Code Quality

- Update Kotlin from 1.3.x to 1.4.0. #39 

## 1.5.0 - 2020-09-08

### Added

- Collect sensors and additional location information.

### Fixed

- Fix app crashes when the user rapidly trigger multiple navigation events. #32 
- Multiple `PublisherConfig`s might have name conflicts for `SharedPreferences`.

### Changed

#### Code Quality

- Use standard MIME type string on serialization for better compatibility.
- Change timestamp format to ISO 8601 for CAI compability.

## 1.4.2 - 2020-08-14

### Changed

#### UI/UX

- Switch the layout of the slate and live-view components on CCAPI fragment.

## 1.4.1 - 2020-08-10

## Fixed

- Publish the proof directly if there is only one publisher enabled. #20

## 1.4.0 - 2020-08-10

### Added

- Provide verification scripts in Python.
- Implement the grouped information view. #16
- Add slate display in CCAPI fragment. #19
- Implement published indicators. #20
- Implement preferences for InfoSnapshotProvider. #18
- Implement different "cold-start" splash screens for master and numbers variants. #21

### Fixed

- Media file size is no longer limited. #11
- App will no longer crash on NumbersStoragePublisherFragment when rotate. #17

### Changed

#### Code Quality

- Setup Codacy with GitHub Action.
- Modulize publisher, information provider, and signature provider.
- Update the schema of SortedProofInformation. You can find the new schema in the README. #27