package com.skilrock.boomlotto.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstantGameListResponse(

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    override val errorCode: Int?,

    @SerializedName("message")
    @Expose
    override val errorMessage: String?,

    @SerializedName("success")
    @Expose
    val success: Boolean?
) : AppResponse {
    data class Data(

        @SerializedName("ige")
        @Expose
        val ige: Ige?,

        @SerializedName("ipAddress")
        @Expose
        val ipAddress: String?
    ) {
        data class Ige(

            @SerializedName("engines")
            @Expose
            val engines: Engines?
        ) {
            data class Engines(

                @SerializedName("DUBAI")
                @Expose
                val dUBAI: DUBAI?
            ) {
                data class DUBAI(

                    @SerializedName("games")
                    @Expose
                    val games: ArrayList<Game?>?,

                    @SerializedName("params")
                    @Expose
                    val params: Params?
                ) {
                    data class Game(

                        @SerializedName("betList")
                        @Expose
                        val betList: List<Int?>?,

                        @SerializedName("bonusMultiplier")
                        @Expose
                        val bonusMultiplier: Any?,

                        @SerializedName("currencyCode")
                        @Expose
                        val currencyCode: String?,

                        @SerializedName("gameCategory")
                        @Expose
                        val gameCategory: String?,

                        @SerializedName("gameDescription")
                        @Expose
                        val gameDescription: String?,

                        @SerializedName("gameName")
                        @Expose
                        val gameName: String?,

                        @SerializedName("gameNumber")
                        @Expose
                        val gameNumber: Int?,

                        @SerializedName("gameVersion")
                        @Expose
                        val gameVersion: String?,

                        @SerializedName("gameWinUpto")
                        @Expose
                        val gameWinUpto: String?,

                        @SerializedName("imagePath")
                        @Expose
                        val imagePath: String?,

                        var imagePathLarge: String?,

                        var amount: String?,

                        @SerializedName("isFlash")
                        @Expose
                        val isFlash: String?,

                        @SerializedName("isHTML5")
                        @Expose
                        val isHTML5: String?,

                        @SerializedName("isImageGeneration")
                        @Expose
                        val isImageGeneration: Any?,

                        @SerializedName("isKeyboard")
                        @Expose
                        val isKeyboard: String?,

                        @SerializedName("isTablet")
                        @Expose
                        val isTablet: Any?,

                        @SerializedName("jackpotStatus")
                        @Expose
                        val jackpotStatus: String?,

                        @SerializedName("orderId")
                        @Expose
                        val orderId: Int?,

                        @SerializedName("prizeSchemes")
                        @Expose
                        val prizeSchemes: LinkedHashMap<String, String>?,

                        @SerializedName("productInfo")
                        @Expose
                        val productInfo: ProductInfo?,

                        @SerializedName("setId")
                        @Expose
                        val setId: Any?,

                        @SerializedName("setName")
                        @Expose
                        val setName: Any?,

                        @SerializedName("status")
                        @Expose
                        val status: String?,

                        @SerializedName("windowHeight")
                        @Expose
                        val windowHeight: Int?,

                        @SerializedName("windowWidth")
                        @Expose
                        val windowWidth: Int?,

                        @SerializedName("loaderImage")
                        @Expose
                        val loaderImage: LinkedHashMap<String, String>?,
                    ) {
                        data class ProductInfo(

                            @SerializedName("donation")
                            @Expose
                            val donation: List<Donation?>?
                        ) {
                            data class Donation(

                                @SerializedName("image")
                                @Expose
                                val image: String?,

                                @SerializedName("title")
                                @Expose
                                val title: String?
                            )
                        }
                    }

                    data class Params(

                        @SerializedName("currencyCode")
                        @Expose
                        val currencyCode: List<String?>?,

                        @SerializedName("domainName")
                        @Expose
                        val domainName: String?,

                        @SerializedName("lang")
                        @Expose
                        val lang: String?,

                        @SerializedName("merchantCode")
                        @Expose
                        val merchantCode: String?,

                        @SerializedName("merchantKey")
                        @Expose
                        val merchantKey: Int?,

                        @SerializedName("repo")
                        @Expose
                        val repo: String?,

                        @SerializedName("root")
                        @Expose
                        val root: String?,

                        @SerializedName("secureKey")
                        @Expose
                        val secureKey: String?,

                        @SerializedName("vendorType")
                        @Expose
                        val vendorType: String?
                    )
                }
            }
        }
    }
}