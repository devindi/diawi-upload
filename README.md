# diawi-upload
[ ![Download](https://api.bintray.com/packages/devindi/maven/diawi-upload/images/download.svg) ](https://bintray.com/devindi/maven/diawi-upload/_latestVersion)

Gradle plugin for publishing android builds on [diawi.com](https://www.diawi.com/).

## Usage

Add the following to your build.gradle

```groovy
buildscript {
	repositories{
	  jcenter()
	}
	dependencies{
		classpath 'com.devindi:diawi-upload:1.0'
	}
}

//Place this line after android{} section
apply plugin: 'diawi-upload'
```

## Configuration

```groovy
diawi {
  token //diawi token.  
  standardOutput //OutputStream in which links will be printed  
}
```

## Tasks

Plugin creates task for every build variant.
You can provide diawi token at diawi section or at local.propeties file, property key is 'diawi.token'

## FAQ

This plugin requires to add diawi's ssl certificate.
1. Go to [https://upload.diawi.com](upload.diawi.com) and save crtificate to your hdd
2. Run 'sudo keytool -import -alias diawi -keystore %PATH_TO_JAVA_HOME%/jre/lib/security/cacerts -file %PATH_TO_CERTIFICATE%'