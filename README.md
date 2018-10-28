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

// Apply plugin after android application
apply plugin: 'diawi-upload'
```

## Configuration
### properties

| name   |      Type      |  Description |Default value|
|----------|:-------------:|------|----|
| `diawi.token` |  String, required | Diawi API access token. Token is required property. [Create new token ](https://dashboard.diawi.com/profile/api)   | null|
| `diawi.standardOutput` |java.io.OutputStream, optional| output stream in which result will be printed|null
|`diawi.resultFormat`|String, optional|output format|`{file} uploaded at {date}. Diawi hash: {hash}`|
| `diawi.comment` | String, optional | additional information to your users on this build: the comment will be displayed on the installation page |null|
| `diawi.password`| String, optional| protect your app with a password: it will be required to access the installation page|null|
|`diawi.callbackEmail`|String, optional| he email addresses Diawi will send the result to|null|
|`diawi.callbackUrl`|String, optional|the URL Diawi should call with the result|null|
|`diawi.wallOfApps`|boolean, optional|allow Diawi to display the app's icon on the wall of apps|null|

#### Output placeholders
|placeholder|value|
|-|-|
|{date}|Datetime of upload finish|
|{hash}|Diawi file hash|
|{link}|Diawi installation link|
|{file}|Original file name|
|{badge_url}|badge url|
|{badge_html}|Diawi badge as HTML-code|
|{badge_md}|Diawi badge as markdown-code|

### Example:
```groovy
diawi {
  token = 'example_token'
  standardOutput = System.out
  comment = 'New major version'
}
```

### Hint
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
