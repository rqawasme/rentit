### Rentit

Rentit is an Android application allowing user to rent their possessions to other people to other users. This is intended for people who need to rent a specific object for a temporary period, and for people who want to make some extra cash from their unused possessions. 

### Members

Danyaal Patel, Kei Nakano, Kenneth Liang, Rashid Qawasme

### Team Effort Breakdown 

Kei Nakano
1. Firebase DB/Storage
2. Booking System
   * User enters start time/date and end time/date from dialogs, after confirmation queries the information from existing bookings in Firebase DB to detect overlap. 
3. Your Bookings
   * Displays bookings made by user, as well as current status of booking. Status of booking changes according to whether booking is has been completed, in progress, awaiting, or deleted. (Improved on by Kenneth as well) 
4. DetailView
   * Uses Viewpager to display a swipeable gallery of images fetched for listing from Firebase Storage. Also displays info fetched from Listing entry in Firebase DB(Description, price, etc) 
   * Contact button which displays contact info for lender, as well as being hyperlinked to email/phone applications. 
4. User Reminder
   * On launch, queries the bookings made by the user(If logged in), and creates a reminder dialog of bookings that expire that day. 5. 
5. Presentation/Demo recording

Kenneth Liang 
1. Firebase user authentication
2. Login and Signup activities
3. Settings
   * User profile: Enable user to be able to change their profile photo, username, phone number, and postal code
4. Update email/ password
   * Have to re-authenticate user in order to change email and password
5. Create listing activity
   * Used RecyclerView and custom adapter to allow user to choose multiple images and display the selected photos in a scrollable grid
   * Nice EditText fields using TextInoutLayout
   * Uploads photos and listing data to Firebase storage and database
6. Edit listing activity
   * Allow user to make changes to their listings
7. User listings activity
   *Used custom layout items to display listings users posted. 
   *Able to make deletions and updates to listings
8. UI improvements
   * General colour scheme
   * App logo and icon
   * Activity UI improvements

Rashid Qawasme
1. Editing presentation videos
2. Rentals Fragment
   * Display listings in a gridview (was multiple columns but now only 1 so it’s like a list view)
   * Listings are sorted based on what is closest to you in distance
   * Search bar and switch buttons work together to be able to filter listings 
3. Create Listing Activity
   * Scale down images to save to speed up download speed for images
   * Fetch current location for the listing’s location and store as Gson
4. Map Activity
   * Request permissions for location
   * Upon open, the map hovers to your location
   * For each available listing, display a marker on the map
   * Clicking on marker shows a dialog of listing consisting of Title, Image, and a short snippet. Can take you to detailed view from it.
5. UI improvements

Danyaal Patel 
1. Initial implementation for Listing creation 


### Video Links

<!---(Replace the link here with the Youtube link)-->
&emsp; [Original Pitch](https://youtu.be/DCBdJQeRkQI)

&emsp; [Rentit Show and Tell 1](https://youtu.be/qP5KhkwnlbM)

&emsp; [Rentit Show and Tell 2](https://youtu.be/ohoBv5kA1M8)

&emsp; [Presentation video](https://youtu.be/4z5gA4ksUIs)
### Downloads
&emsp; <a href="Rentit.apk">Download app apk</a>
<br>
<br>
&emsp; <a href="Rent-it.zip">Final project code</a>
<br>
<br>
&emsp; <a href="README.txt.txt">README</a>

### Presentation Slides
&emsp; [Google Slides for the Presentation(Contains Thread Design Diagram)](https://docs.google.com/presentation/d/1FyLT6M-YFi7-Ah8WX57Eu5Mss1mOFEHc3xnOYGeZJ98/edit?usp=sharing)
