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
    val success: Boolean
) {
    /**
     * The first item in the response
     */
    val item: T
        get() = items[0]
}

data class CreateAccessKeyResponse(val accessKey: String, val languageId: Int, val memberId: Int)