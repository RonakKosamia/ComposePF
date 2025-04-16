import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectorySearchResponse(
    val people: List<DirectoryPerson> = emptyList(),
    val totalSize: Int? = null
)

@Serializable
data class DirectoryPerson(
    val resourceName: String? = null,
    val etag: String? = null,
    val metadata: Metadata? = null,
    val names: List<Name>? = null,
    val emailAddresses: List<EmailAddress>? = null,
    val phoneNumbers: List<PhoneNumber>? = null,
    val photos: List<Photo>? = null,
    val coverPhotos: List<Photo>? = null,
    val addresses: List<Address>? = null,
    val organizations: List<Organization>? = null,
    val relations: List<Relation>? = null,
    val externalIds: List<ExternalId>? = null,
    val locations: List<Location>? = null
)

@Serializable
data class Metadata(
    val sources: List<Source>? = null,
    val primary: Boolean? = null,
    val verified: Boolean? = null
)

@Serializable
data class Source(
    val type: String? = null,
    val id: String? = null
)

@Serializable
data class Name(
    val displayName: String? = null,
    val familyName: String? = null,
    val givenName: String? = null,
    val displayNameLastFirst: String? = null,
    val unstructuredName: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class EmailAddress(
    val value: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class PhoneNumber(
    val value: String? = null,
    val canonicalForm: String? = null,
    val type: String? = null,
    val formattedType: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class Address(
    val formattedValue: String? = null,
    val type: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class Photo(
    val url: String? = null,
    val default: Boolean? = null,
    val metadata: Metadata? = null
)

@Serializable
data class Organization(
    val department: String? = null,
    val title: String? = null,
    val location: String? = null,
    val type: String? = null,
    val formattedType: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class Relation(
    val person: String? = null,
    val type: String? = null,
    val formattedType: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class ExternalId(
    val value: String? = null,
    val type: String? = null,
    val formattedType: String? = null,
    val metadata: Metadata? = null
)

@Serializable
data class Location(
    val value: String? = null,
    val type: String? = null,
    val buildingId: String? = null,
    val floor: String? = null,
    val metadata: Metadata? = null
)