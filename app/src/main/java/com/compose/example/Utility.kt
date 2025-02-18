package com.compose.example

import android.os.Parcel
import android.os.Parcelable


class People(
    val name: String?,
    val mobile: String?,
    val email: String?,
    val profileUrl: String?,
    val eid: String?,
    val location: String?,
    val department: String?,
    val organization: String?,
    val jobFamily: String?,
    val manager: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(mobile)
        parcel.writeString(email)
        parcel.writeString(profileUrl)
        parcel.writeString(eid)
        parcel.writeString(location)
        parcel.writeString(department)
        parcel.writeString(organization)
        parcel.writeString(jobFamily)
        parcel.writeString(manager)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<People> {
        override fun createFromParcel(parcel: Parcel): People {
            return People(parcel)
        }

        override fun newArray(size: Int): Array<People?> {
            return arrayOfNulls(size)
        }
    }


}

val samplePeoples = listOf(
    People(
        name = "John Doe",
        mobile = "123-456-7890",
        email = "johndoe@example.com",
        profileUrl = "https://picsum.photos/200",
        eid = "E12345",
        location = "Cecilia Chapman\n" +
                "711-2880 Nulla St.\n" +
                "Mankato Mississippi 96522",
        department = "Engineering",
        organization = "TechCorp",
        jobFamily = "Software Development",
        manager = "Jane Smith"
    ),
    People(
        name = "Jane Smith",
        mobile = "987-654-3210",
        email = "janesmith@example.com",
        profileUrl = "https://picsum.photos/200",
        eid = "E12346",
        location = "Iris Watson\n" +
                "P.O. Box 283 8562 Fusce Rd.\n" +
                "Frederick Nebraska 20620",
        department = "Marketing",
        organization = "TechCorp",
        jobFamily = "Marketing",
        manager = "John Doe"
    ),
    People(
        name = "Alice Johnson",
        mobile = "555-321-2345",
        email = "alicejohnson@example.com",
        profileUrl = "https://picsum.photos/200",
        eid = "E12347",
        location = "Celeste Slater\n" +
                "606-3727 Ullamcorper. Street\n" +
                "Roseville NH 11523",
        department = "Sales",
        organization = "TechCorp",
        jobFamily = "Sales",
        manager = "Jane Smith"
    ),
    People(
        name = "Michael Brown",
        mobile = "555-432-8765",
        email = "michaelbrown@example.com",
        profileUrl = "https://picsum.photos/200",
        eid = "E12348",
        location = "Theodore Lowe\n" +
                "Ap #867-859 Sit Rd.\n" +
                "Azusa New York 39531",
        department = "HR",
        organization = "TechCorp",
        jobFamily = "Human Resources",
        manager = "John Doe"
    ),
    People(
        name = "Sarah Lee",
        mobile = "555-654-3210",
        email = "sarahlee@example.com",
        profileUrl = "https://picsum.photos/200",
        eid = "E12349",
        location = "Kyla Olsen\n" +
                "Ap #651-8679 Sodales Av.\n" +
                "Tamuning PA 10855",
        department = "Finance",
        organization = "TechCorp",
        jobFamily = "Finance",
        manager = "Jane Smith"
    )
)

sealed class LoadingStatus<out T> {
    data object Loading : LoadingStatus<Nothing>()
    data class Success<T>(val data: T) : LoadingStatus<T>()
    data class Error(val throwable: Throwable) : LoadingStatus<Nothing>()
}