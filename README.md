# Pexel Photo Search

Android App, uses Pexels API to help user search for a Photo

###### How to use the app - 

Once you download the app. Run the app, you can search for a photo, get the details of it by clicking on the individual photo. You can pinch, zoom on the photo on the details of Photo page.


###### Features
- It caches the paged data in-memory for better usage of the system resources which not only gives fast response but also helps in data loading without hiccups.
- It handles the network request duplication very elegantly hence saving the user's bandwidth and system resources.
- It has inbuilt support for error handling, retry and refresh use cases.

###### Android Tech Stack

- Using MVVM architecture
- This app follows  one activity, multi fragment pattern
- Paging 3 for Pagination
- Glide to load images in Recycler Grid Layout Manager
- Navigation lib for page navigation
- Built a TouchImageView with the help of this https://stackoverflow.com/a/54474455/2470900, Thanks to Zain
- Room DB for caching data
- Flow and livedata types  for Data exchange on different layers
- ViewBinding and DatingBinding
- SavedStateHandle to store the content of the searched text

###### Packaging

- data package has the repository, mediator and pagingSource ( which is not used in this flow)
- db package has all the Room related classes
- models package has the data class objects that are interacting with UI
- network package has the data class objects and network layer interaction
- ui package has the rest of the classes that helps you to draw UI for the user with sub packages in it



###### How to run the app

The app is targeted to Android sdk 31. You will need Pexels API key to get this app up and running. You can get the api key from here https://www.pexels.com/api/. Once you get the api key, place it in your local.properties and you should be good to run the app on your Android studio
