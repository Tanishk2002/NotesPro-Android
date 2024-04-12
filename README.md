
# Notes Pro (Android App)

A notes application, built using Android Studio, which runs on **Android 9** or higher.


## App Look


| <img src="https://github.com/Tanishk2002/NotesPro-Android/raw/main/Screenshots/ss1.jpeg" width="300"> | <img src="https://github.com/Tanishk2002/NotesPro-Android/raw/main/Screenshots/ss2.jpeg" width="300"> |
|      :--------:       |       :-------:       |
|    Light theme        | Dark theme |




  



## Features

- App lock
- Individual Note lock
- Light and Dark themes



## App lock look

| <img src="https://github.com/Tanishk2002/NotesPro-Android/raw/main/Screenshots/ss3.jpeg" width="300"> | <img src="https://github.com/Tanishk2002/NotesPro-Android/raw/main/Screenshots/ss4.jpeg" width="300"> |         
|    :--------:       | :-------:  |
|    Light theme      | Dark theme |








## Tech stack

- Android Studio
- Kotlin
- Room Database
- MVVM Architecture Pattern
### Note:

The **Utilities** package contains kotlin file **SendMail**. This is used to send mail to the user for verification. It contains two variables :

- **stringSenderEmail** : Should contain the email id through which the emails should be sent for verification.
- **stringPasswordSenderEmail** : Should contain the password for the sender's email. This should not be your sender's email password, instead, it should be the "App password" generated for your sender's email using two factor authentication option of the email.

Specify these variables as per your choice before using the project to avoid any errors.






