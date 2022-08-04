package com.cmpt362.rentit

object Constants {
    const val USERS_TABLE_NAME = "Users"
    const val INVALID_EMAIL_FORMAT_ERROR = "Invalid email format"
    const val NO_PASSWORD_ENTERED_ERROR = "Please enter a password"
    const val USERNAME_REQUIRED_ERROR = "Please enter a username"
    const val MIN_PASSWORD_LENGTH = 6
    const val PASSWORD_LENGTH_ERROR = "Password must be at least ${MIN_PASSWORD_LENGTH} characters long"
    const val PLEASE_LOGIN_MSG = "Please Login"
    const val USERNAME_PATH = "username"
    const val PHONE_PATH = "phone"
    const val POSTAL_CODE_PATH = "postalCode"
    const val ONE_MEGABYTE: Long = 1024 * 1024
    const val PICK_PROFILE_PHOTO_DIALOG_TITLE = "Pick Profile Picture"
    const val OPEN_CAMERA_DIALOG_OPTION_POSITION = 0
    const val SELECT_FROM_GALLERY_DIALOG_OPTION_POSITION = 1
    const val PROFILE_PHOTO_FILE_NAME = "rentit_profile_photo.jpg"
    const val TEMP_PROFILE_PHOTO_FILE_NAME = "rentit_temp_profile_photo.jpg"
    const val URI_AUTHORITY = "cmpt362.rentit.com"
    const val USERS_FOLDER = "Users/"
    const val USER_PROFILE_PIC_PREFIX = "profile_picture"
    const val USER_PROFILE_PIC_SUFFIX = "jpeg"
    const val FAILED_TO_RETRIEVE_PROFILE_PICTURE_ERROR = "Failed to retrieve profile picture"
    const val LISTINGS_PATH = "Listings"
    const val BOOKINGS_PATH = "Bookings"
    const val PHOTOS_PER_ROW = 4
    const val NO_LISTING_TITLE_ERROR = "Please enter a title"
    const val NO_LISTING_PRICE_ERROR = "Please enter a price"
    const val LISTING_TYPE_RENTAL = 0
    const val LISTING_TYPE_GIGS = 1
    const val LISTINGS_TABLE_NAME = "Listings"
}