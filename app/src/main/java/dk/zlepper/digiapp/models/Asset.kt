package dk.zlepper.digiapp.models

import com.google.gson.annotations.SerializedName

// Brehhhh
enum class AssetType(val value: Int) {
    @SerializedName("0")
    All(0),
    @SerializedName("1")
    Video(1),
    @SerializedName("2")
    Audio(2),
    @SerializedName("4")
    Image(4),
    @SerializedName("5")
    PowerPoint(5),
    @SerializedName("6")
    Html(6),
    @SerializedName("7")
    Text(7),
    @SerializedName("8")
    Word(8),
    @SerializedName("9")
    Excel(9),
    @SerializedName("10")
    InDesign(10),
    @SerializedName("11")
    Zip(11),
    @SerializedName("15")
    Archive(15),
    @SerializedName("1000")
    Live(1000),
    @SerializedName("12")
    META(12),
    @SerializedName("14")
    PDF(14),
    @SerializedName("110")
    ODF(110),
    @SerializedName("107")
    ODG(107),
    @SerializedName("109")
    ODB(109),
    @SerializedName("111")
    ODM(111),
    @SerializedName("105")
    ODP(105),
    @SerializedName("102")
    ODS(102),
    @SerializedName("112")
    OTH(112),
    @SerializedName("106")
    OTP(106),
    @SerializedName("100")
    ODT(100),
    @SerializedName("108")
    OTG(108),
    @SerializedName("103")
    OTS(103),
    @SerializedName("101")
    OTT(101),
    @SerializedName("16")
    Photoshop(16),
    @SerializedName("17")
    Illustrator(17),
}

data class Asset(
    val assetId: Int,
    val assetType: AssetType,
    val description: String,
    val itemId: Int,
    val name: String,
    val thumb: String,
    val image1080p: String,
    val uploadedByName: String,
    val desktopH264: String
)