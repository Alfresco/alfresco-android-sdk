Samples App Readme
==================

The samples app is provided as a demonstration of how to use the SDK and should not be used in a production scenario.

Known Issues
------------

- The samples app does not provide any handling for expired access tokens when running against the Cloud, if you experience
  persistent error message navigate back to the Server Type page and re-authenticate.
- Rotating the device whilst entering a comment can lose the entered text.
- Files uploaded to an on-premise server will have two versions to be created, if tags are applied three version will be present.
- Thumbnails will not be shown for documents uploaded to Alfresco in the Cloud until they have been viewed in Share.
- When connecting to Alfresco in the Cloud only the users home network will be accessed.


For an up to date list of known issues with the samples app please use the following URL:

https://issues.alfresco.com/jira/secure/IssueNavigator.jspa?mode=hide&requestId=15094