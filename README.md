# MoveoApp
Android appointment management app for doctors and patients.

## Screenshots
### Login and Sign Up screens
Authentication was performed using firebase authentication and the personal data was saved using firebase firestore.

<img src = "assets/login_screen.jpeg" height = 450> <img src = "assets/sign_up_screen.jpeg" height = 450>

## Doctor UI
### Doctor Waiting List Screen
A screen that shows patients waiting list with all pending appointments. The appointments are sorted by arrival time (the time they added to firebase).

<img src = "assets/doctor_waiting_list_screen.jpeg" height = 450>

## Patient UI
### Doctors List Screen
A screen that shows doctors list. There is an option to filter by availability.

<img src = "assets/doctors_list_screen.jpeg" height = 450> <img src = "assets/filtered_doctors_list_screen.jpeg" height = 450>

### Patient Waiting List Screen
A screen that shows patients waiting list with all pending appointments to the same doctor. The appointments are sorted by arrival time (the time they added to firebase). There is an option to cancel an appointment.

<img src = "assets/patient_waiting_list_screen.jpeg" height = 450>
