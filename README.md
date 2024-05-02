# Tourism App 

## Description
This application offers key functionalities such as user authentication, search, and display of tourist spots using advanced technologies like Firebase and Google Places API.

### Features
- **User Authentication:** Login and registration managed through Firebase Authentication.
- **Google Places API Integration:** Users can search for tourist attractions, view details, and get personalized recommendations.
- **Additional Features:** Describe other features here.

## Setup

### Configuring Firebase
To configure Firebase and use authentication in the application, follow these steps:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project or select an existing one.
3. In the setup menu, go to "Authentication" and enable the authentication methods you require (e.g., email and password authentication, Google, etc.).
4. Add your Android application to Firebase:
   - Click on the Android icon to add a new application.
   - Register your application's package name and follow the instructions to download the `google-services.json` file.
   - Place this file in the `app/` folder of your Android project.

### Obtaining a Google Places API Key
To use the Google Places API, you need to obtain an API key from Google Cloud Platform:
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Navigate to "APIs & Services" > "Library", search for "Places API" and enable it.
4. Go to "Credentials" and click "Create credentials" > "API key". Securely save your API key.
5. Ensure that the API key usage is restricted to only the necessary APIs and limit access based on your development or production environment.
6. Create a file in the root folder called "secrets.properties" and add the value under GOOGLE_PLACE_API_KEY name.

## Contributing

Since the main branch is protected, contributors must follow the Pull Request (PR) workflow:
1. Fork the repository and clone your fork.
2. Create a new branch for your changes: `git checkout -b my-new-feature`.
3. Make your changes and commit them: `git commit -am 'Add some feature'`.
4. Push the branch to your fork: `git push origin my-new-feature`.
5. Open a Pull Request on GitHub from your branch to the main branch of the original repository.
6. Your PR will need to be reviewed and approved before it can be merged.
