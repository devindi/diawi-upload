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
		classpath 'com.devindi:diawi-upload:1.1'
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
  comment = 'Comment on the build'
  password = 'installation password' 
  callbackEmail = 'me@company.com'
  callbackUrl = 'http://www.myintranet.com/my-diawi-callback-url'
  wallOfApps = false
}
```

###Hint
You can provide any parameter via arguments
```groovy
diawi {
  password = diawiPswrd 
}
```
And run task with argument like ```./gradlew diawiPublishMockDebug -PdiawiPswrd='SECRET_STUFF' ```


## Tasks

Plugin creates task for every build variant.
You can provide diawi token at diawi section or at local.propeties file, property key is 'diawi.token'
