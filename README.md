# Alfresco Mobile SDK for Android

You have arrived at the source repository for the Alfresco Mobile SDK for Android. Welcome! There are two ways you can choose to work with the Mobile SDK:

- If you'd like to work with the source code of the SDK itself, you've come to the right place! You can browse sample app source code and debug down through the layers to get a feel for how everything works under the covers. Read on for instructions on how to get started with the SDK in your development environment.

- If you just want to start using the SDK in your own project, the quickest way is to import the SDK into your project by pasting the below sample into your build.gradle file.

```groovy
compile 'org.alfresco.mobile.android.sdk:alfresco-mobile-android-client-api:1.5'
```
or Maven:
```xml
<dependency>
  <groupId>org.alfresco.mobile.android.sdk</groupId>
  <artifactId>alfresco-mobile-android-client-api</artifactId>
  <version>1.5</version>
</dependency>
```


# Building Native Apps?

The Alfresco Mobile SDK provides the essential libraries for quickly building native mobile apps that interact with the Alfresco platform. The Auth library abstracts away the complexity of implementing OpenID Connect from scratch, there is an example of how to use this in the sample app. The SDK also provides wrappers for the Alfresco REST API that you can use from both Kotlin and Java.
