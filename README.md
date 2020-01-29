
# Cleanenger


Cleanenger is a mobile application that allows people to send and receive both instant messages and photos.
Photos are captured by device's camera and deleted from database when all the users to whom it was sent opened the picture.


## Getting Started

### Prerequisites

- Android SDK
- Latest Android Build Tools
- Android Support Repository

### Installation

#### 1.  Android Studio - clone from URL

1. In Github click the "Clone or download" button of the project and copy the URL link.
2. Navigate to File > New > Project from Version Control > Git. 
3. Next, enter your GitHub account credentials and click Login. 
4. Click Clone to clone the repo to your local machine inside the already selected parent directory.  

#### 2. Android Studio - import from ZIP
1. In Github click the "Clone or download" button of the project and download the ZIP file and unzip it.
2.  In Android Studio go to File > New Project > Import Project and select the unzipped folder.


## Running the Cleanenger

### Android Studio

1.  Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
2. Select the device you wish to run the app on and click 'OK'

## Using the Cleanenger

#### Login Screen

It has support for **signing in with Facebook**, as well as the classic email & password form. You can create new account by clicking **+ Create an account**.

#### Main Screen

![presentation11](https://user-images.githubusercontent.com/41000632/73372182-152de880-42b7-11ea-9d9d-07c40dedb0a1.jpg)

The main screen contains recycler views of both photos and messages.

Message text color is black if you haven't opened them yet.
After opening text color of the message changes to dark grey.
If your message was read, **Tick Icon**  will appear on the left side of the message.

When you open a photo, it will be deleted from a database and will be no longer available on your main screen.
You can send one by tapping **Camera Icon** on the left side of the photo list.

#### Account Screen
You can change your username, profile photo as well as your email and password to authorization. 
If you are logged in with your facebook account all Edit Text fields and a profile photo chooser are disabled.

#### Chat Screen

![presentationchat101](https://user-images.githubusercontent.com/41000632/73372048-e748a400-42b6-11ea-9b33-2d5b0c48e2e5.jpg)

The chat screen allows you to send and receive instant messages.
Tap a message to show the time it was sent.
Customize your chat by clicking **three dots  menu icon** on top-right corner of the screen.
Personalization of chat appearance include text size and chat color. 

#### Find Friends Screen

![presentationfindfriends1](https://user-images.githubusercontent.com/41000632/73371836-96d14680-42b6-11ea-8ab2-ab34b3aa1253.jpg)


When opened, the Recycler View is filled with all the users of the app.
Tap **Search Icon** to search for users by their usernames and 
swipe **Only Friends Switch** to show list of your friends.
In **Only Friends Mode** you can remove users from your friends list.

#### Send Photo Screen

Tap **Rotation Buttons** in the top-right corner to rotate the photo you've taken and **Send Button** to select users that will receive the picture.
Then click **Send Button** to upload it to the Cloud Storage.

## Built With

````
*Firebase:
	-Analytics (16.0.3)
	-Core (16.0.8)
	-Auth (16.1.0)
	-Database (16.1.0)
	-Storage (16.1.0)
	-Messaging (17.3.4)
	 Firebase is a crucial dependency for authorizing users, storing photos,the database of messages and users and sending notifications via messaging service.

*Facebook Login [5,6) for signing in users with a facebook account.

*Android:
	-Material (1.0.0) for a material toolbar in chat activity.
	-Flexbox (2.0.1) for color picker adjustable to screen size in chat options
 
*Squareup:
	-Picasso (2.5.2) for showing pictures directly from a database.
	-Retrofit2 (2.5.0)
	-Retrofit2 Converter Gson (2.5.0) for serialization to and from JSON in Notofication Services

*Hdodenhof Circleimageview (2.2.0) for rounded Image Views of profile photos

*AndroidX Exif Interface (1.0.0) for obtaining captured photo info to rotate it properly

````



## Support

If you have any question, please send me an email:
jacob.kielar@gmail.com
