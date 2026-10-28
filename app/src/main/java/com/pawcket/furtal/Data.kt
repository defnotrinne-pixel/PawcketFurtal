package com.pawcket.furtal

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.pawcket.furtal.R

data class Pet(val id: Int, val name: String, val breed: String, val age: String, val gender: String,
               val size: String, val type: String, val vaccinated: Boolean, val emoji: String, val img: Int? = null)
data class Report(val id: Int, val name: String, val breed: String, val desc: String, val place: String,
                  val status: String, val contact: String, val emoji: String, val img: Int? = null)
data class Clinic(val name: String, val tag: String, val address: String, val phone: String,
                  val hours: String, val services: List<String>)
data class Profile(val name: String = "PawCKet Friend", val email: String = "", val phone: String = "",
                   val facebook: String = "", val address: String = "")

class Vm : ViewModel() {
    var user by mutableStateOf<Profile?>(null)
    var guest by mutableStateOf(false)
    var filipino by mutableStateOf(false)
    val favorites = mutableStateListOf<Int>()
    val applications = mutableStateListOf<String>()
    val donations = mutableStateListOf<String>()
    val appointments = mutableStateListOf<String>()
    var totalDonated by mutableStateOf(0)

    val pets = listOf(
        Pet(1, "Cookie", "Aspin (Asong Pinoy)", "2 years", "Male", "Medium", "Dog", true, "🐕", R.drawable.cookie),
        Pet(2, "Luna", "Persian", "1 year", "Female", "Small", "Cat", false, "🐈", R.drawable.luna),
        Pet(3, "Max", "Aspin", "7 years", "Male", "Large", "Dog", true, "🐕‍🦺", R.drawable.max),
        Pet(4, "Peanut", "Puspin (Persian mix)", "3 years", "Male", "Medium", "Cat", true, "🐈", R.drawable.peanut))
    val reports = mutableStateListOf(
        Report(1, "Max", "Aspin", "Male · Blue collar", "Session Road, Baguio City", "Lost", "+63 917 000 0001", "🐕‍🦺", R.drawable.max),
        Report(2, "Luna", "Persian", "Female · Very friendly", "Burnham Park, Baguio City", "Found", "+63 917 000 0002", "🐈", R.drawable.luna),
        Report(3, "Milo", "Puspin", "Male · Calico, white and black", "Baguio Cathedral", "Found", "+63 917 000 0003", "🐱", R.drawable.milo),
        Report(4, "Oreo", "Shih Tzu", "Gray and white · Very fluffy", "SM Baguio", "Lost", "+63 917 000 0004", "🐩", R.drawable.oreo))
    val clinics = listOf(
        Clinic("City Veterinary and Agriculture Office", "Government Office", "Slaughterhouse Road, Baguio City",
            "+63 74 443 5332", "Mon-Fri · 8:00 AM-5:00 PM", listOf("Free rabies vaccination", "Spay/neuter programs", "Pet registration")),
        Clinic("Cordillera Pet Care Center", "Private Clinic", "Naguilian Road, Baguio City",
            "+63 917 555 0142", "Mon-Sat · 9:00 AM-6:00 PM", listOf("General veterinary care", "Diagnostics", "Pet pharmacy")),
        Clinic("PetTreasure Veterinary Clinic", "Open 24/7", "Marcos Highway, Baguio City",
            "+63 920 555 0188", "Open 24 hours", listOf("Emergency care", "Surgery", "Laboratory")))
}
