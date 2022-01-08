Jacotura [jacotura-gradle-plugin]
======================
[![Build](https://github.com/chirag-ji/Jacotura/actions/workflows/gradle.yml/badge.svg)](https://github.com/chirag-ji/Jacotura/actions/workflows/gradle.yml)

Jacotura is a [Gradle](https://www.gradle.org) plugin that converts [JaCoCo](http://www.eclemma.org/jacoco/) coverage
reports to [Cobertura](http://cobertura.github.io/cobertura/) coverage reports.

Usage
-----
Add the following to buildscript:

### Multi-module Project

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath("io.github.chiragji:jacotura-gradle-plugin:${latest-tag}")
  }
}

subprojects {
  apply(plugin: 'io.github.chiragji.jacotura')
	
  jacotura {
    properties {
      property("jacotura.jacoco.path", "$buildDir/reports/jacoco.xml")
      property("jacotura.cobertura.path", "$buildDir/reports/cobertura.xml")
    }
    sourceDirs = sourceSets.main.java.srcDirs
  }
}
```

### Single Module Project

```groovy
plugins {
  id 'io.github.chiragji.jacotura' version("${latest-tag}")
}

jacotura {
  properties {
    property("jacotura.jacoco.path", "$buildDir/reports/jacoco.xml")
    property("jacotura.cobertura.path", "$buildDir/reports/cobertura.xml")
  }
  sourceDirs = sourceSets.main.java.srcDirs
}
```

### Configurations
```groovy
jacotura {
  properties {
    // path of the report for jacoco
    property("jacotura.jacoco.path", "$buildDir/reports/jacoco.xml")
    
    // path of the report for cobertura
    property("jacotura.cobertura.path", "$buildDir/reports/cobertura.xml")
  }
  // the source directories
  sourceDirs = sourceSets.main.java.srcDirs
	
  // if wanting to beautify the output cobertura report 
  beautify = true
	
  // Only output coverage for selected file names. Do not set if needed for all files
  includedFileNames = ['A.java', 'B.java']
}
```


Run the `jacotura` task to convert jacoco report to cobertura report.
```bash
./gradlew jacotura
```

See [jacotura-demo](https://github.com/chirag-ji/Jacotura/tree/main/jacotura-demo) project for detailed configuration. This plugin ported over from [cover2cover](https://github.com/rix0rrr/cover2cover).

If you think this library is useful, please press star button at upside.

 ![](https://camo.githubusercontent.com/efeaf0e8044a05ab3058270a7ac59b56fb0f3579c0185db85629ceab28e3697c/68747470733a2f2f7068617365722e696f2f636f6e74656e742f6e6577732f323031352f30392f31303030302d73746172732e706e67) 

License
-------

	BSD 3-Clause License

	Copyright (c) 2022, Chirag Gupta
	All rights reserved.

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:

	1. Redistributions of source code must retain the above copyright notice, this
	   list of conditions and the following disclaimer.

	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.

	3. Neither the name of the copyright holder nor the names of its
	   contributors may be used to endorse or promote products derived from
	   this software without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
	DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
	SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
	CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
