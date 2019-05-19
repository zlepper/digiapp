package dk.zlepper.digiapp.models

/**
 * A wrapper for any response the digizuite can genenerate
 */
data class DigizuiteApiResponse<T>(
    /**
     * All the items in the response
     */
    val items: List<T>,
    /**
     * Indicates if the response was a success
     */
    val success: Boolean,

    /**
     * The total number of items available if the cunsumer pages over.
     *
     * Has a tendency to be undefined behavior if the api is not designed to be pages, e.g. login
     */
    val total: Int,

    // These _might_ be set, if we are lucky...
    val error: String = "",
    val warning: String = ""
) {
    /**
     * The first item in the response
     */
    val item: T
        get() = items[0]
}

data class CreateAccessKeyResponse(val accessKey: String, val languageId: Int, val memberId: Int)

data class SystemConfigResponse(
    val UploadName: String,
    val MainSearchFolderId: String
)