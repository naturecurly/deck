Deck
=====

[![CI](https://github.com/naturecurly/deck/actions/workflows/build.yml/badge.svg)](https://github.com/naturecurly/deck/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.naturecurly.deck/deck-compose.svg)](https://central.sonatype.com/artifact/com.naturecurly.deck/deck-compose)

Deck is a pluggable UI framework for Android Jetpack Compose. It addresses the common challenge of integrating multiple sub-functionalities into a primary feature without creating direct dependencies.

_Note: This framework requires use of [KSP](https://github.com/google/ksp) and [Hilt](https://github.com/google/dagger) in your project_

Download
--------

Download [the latest JAR](https://repo1.maven.org/maven2/com/naturecurly/deck/deck-compose/0.1.0/deck-compose-0.1.0.aar) or depend via Gradle:
```kotlin
implementation("com.naturecurly.deck:deck-compose:0.1.0")
ksp("com.naturecurly.deck:deck-codegen:0.1.0")
```

Usage
--------
#### Primary Feature
#### Subfeature


License
---------

```
Copyright (c) 2025 naturecurly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
