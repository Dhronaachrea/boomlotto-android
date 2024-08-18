package com.skilrock.boomlotto.network

import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.models.request.*
import com.skilrock.boomlotto.models.response.*
import com.skilrock.boomlotto.utility.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiCallInterface {

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @GET(LOGIN_OTP_URL)
    suspend fun callLoginOtpApi(
        @Query("aliasName") aliasName: String = BuildConfig.DOMAIN_NAME,
        @Query("mobileNo") mobileNo: String
    ): Response<LoginOtpResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @GET(COUNTRY_LIST_URL)
    suspend fun callCountryListApi(): Response<CountryListResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @GET(STATE_URL)
    suspend fun callStateListApi(
        @Header("merchantId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("Authorization") playerToken: String = "Bearer " + PlayerInfo.getPlayerToken(),
        @Query("countryCode") countryCode: String
    ): Response<StateResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @GET(CITY_URL)
    suspend fun callCityListApi(
        @Header("merchantId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("Authorization") playerToken: String = "Bearer " + PlayerInfo.getPlayerToken(),
        @Query("stateCode") stateCode: String
    ): Response<CityResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(LOGIN_URL)
    suspend fun callLoginApi(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @Headers(HEADER_DMS_USERNAME, HEADER_DMS_PASSWORD)
    @PUT(BOOM_LOTTO_GAME_URL)
    suspend fun callBoomLottoGameApi(
        @Body boomLottoGameRequest: BoomLottoGameRequest
    ): Response<BoomLottoGameResponse>

    @Headers(HEADER_DMS_USERNAME, HEADER_DMS_PASSWORD)
    @POST(MY_TICKET_URL)
    suspend fun callTicketDetailApi(
        @Body myTicketDetailRequest: MyTicketDetailRequest
    ): Response<MyTicketDetailResponse>

    @Headers(HEADER_DMS_USERNAME, HEADER_DMS_PASSWORD)
    @GET(TICKET_DETAIL_URL)
    suspend fun callTicketListApi(
        @Query("merchantCode") merchantCode: String = WEAVER,
        @Query("gameCode") gameCode: String = BOOM_GAME_CODE,
        @Query("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Query("sessionId") sessionId: String =  PlayerInfo.getPlayerToken(),
        @Query("orderBy") orderBy: String = "desc",
        @Query("pageSize") pageSize: Int = 1000,
        @Query("pageIndex") pageIndex: Int = 0,
    ): Response<MyTicketsListResponse>

    @POST(LOGOUT_URL)
    suspend fun callLogOutApi(
        @Body logoutRequest: LogOutRequest
    ): Response<LogOutResponse>

    @POST(INSTANT_GAME_LIST_URL)
    suspend fun callGameListApi(
        @Body instantGameListRequest: InstantGameListRequest
    ): Response<InstantGameListResponse>

     @POST(REFER_CODE_URL)
    suspend fun callReferCodeApi(
        @Body referCodeRequest: ReferCodeRequest
    ): Response<ReferCodeResponse>

    @POST(VERSION_CONTROL_URL)
    suspend fun callVersionControlAPi(
        @Body versionControlRequest: VersionControlRequest
    ): Response<VersionControlResponse>

    @Headers(HEADER_DMS_USERNAME, HEADER_DMS_PASSWORD)
    @POST(BOOM_LOTTO_BUY_TICKET_URL)
    suspend fun callBoomLottoBuyTicketApi(
        @Body boomLottoBuyTicketRequest: BoomLottoBuyTicketRequest
    ): Response<BoomLottoBuyTicketResponse>

    @POST(WINNER_LIST_URL)
    suspend fun callWinnerListApi(
        @Body winnerListRequest: WinnerListRequest
    ): Response<WinnerListResponse>

    @POST(BANNER_URL)
    suspend fun callBannerApi(
        @Body bannerRequest: BannerRequest
    ): Response<BannerResponse>

    @POST(GAME_INFO)
    suspend fun callGameInfoApi(
        @Body boomGameInfoRequest: BoomGameInfoRequest
    ): Response<BoomGameInfoResponse>

    @POST(SERVER_TIME_URL)
    suspend fun callServerTimeApi(
        @Body serverTimeRequest: ServerTimeRequest
    ): Response<ServerTimeResponse>

    @POST(MY_TRANSACTION)
    suspend fun callMyTransactionApi(
        @Body myTransactionsRequest: MyTransactionsRequest
    ): Response<MyTransactionsResponse>

    @GET(INSTANT_GAME_TICKET_LIST)
    suspend fun callInstantGameTicketList(
        @Query("merchantCode") merchantCode: String = WEAVER,
        @Query("gameCode") gameCode: String = "ALL",
        @Query("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Query("orderBy") orderBy: String = "desc",
        @Query("pageSize") pageSize: Int = 1000,
        @Query("pageIndex") pageIndex: Int = 0,
    ): Response<InstantGameTicketListResponse>

    @POST(PLAYER_INBOX_URL)
    suspend fun callPlayerInboxApi(
        @Body playerInboxRequest: PlayerInboxRequest
    ): Response<PlayerInboxResponse>

    @POST(PLAYER_INBOX_ACTIVITY_URL)
    suspend fun callInboxReadApi(
        @Body playerInboxReadRequestData: PlayerInboxReadRequest
    ) : Response<PlayerInboxReadResponse>

    @POST(PLAYER_INBOX_ACTIVITY_URL)
    suspend fun callInboxDeleteApi(
        @Body playerInboxDeleteRequestData: PlayerInboxDeleteRequest
    ) : Response<PlayerInboxResponse>

    @Headers(HEADER_DMS_USERNAME, HEADER_DMS_PASSWORD)
    @POST(RESULT_URL)
    suspend fun callResultApi(
        @Body resultRequest: ResultRequest
    ): Response<ResultResponse>

    @Multipart
    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(ID_VERIFICATION_URL)
    suspend fun callIdVerificationApi(
            @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
            @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
            @PartMap paramsMap: HashMap<String, RequestBody>
    ) : Response<IdVerificationResponse>

    @Multipart
    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(ID_VERIFICATION_URL)
    suspend fun callIdVerificationWithdrawalApi(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @PartMap paramsMap: HashMap<String, RequestBody>,
        @Part imageFileList: ArrayList<MultipartBody.Part>
    ) : Response<IdVerificationResponse>

    @Multipart
    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(PLAYER_DOC_UPLOAD_URL)
    suspend fun callPlayerDocUploadApi(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @PartMap paramsMap: HashMap<String, RequestBody>,
        @Part imageFileList: ArrayList<MultipartBody.Part>
    ) : Response<BankDocUploadResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(EMAIL_OTP_URL)
    suspend fun callEmailOtpApi(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body emailOtpRequest: EmailOtpRequest
    ) : Response<EmailOtpResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(EMAIL_VERIFICATION_URL)
    suspend fun callEmailVerificationApi(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body emailVerificationRequest: EmailVerificationRequest
    ) : Response<EmailOtpResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(PLAYER_PROFILE_URL)
    suspend fun callPlayerProfileApi(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body playerProfileRequest: PlayerProfileRequest
    ) : Response<PlayerProfileResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE)
    @POST(PLAYER_PROFILE_URL)
    suspend fun callProfileApiForPlayer(
        @Header("playerId") playerId: Int = PlayerInfo.getPlayerId(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body playerProfileRequest: PlayerProfileRequest
    ) : Response<ProfileInfoForPlayerResponse>

    @Headers(HEADER_CONTENT_TYPE, HEADER_RAM_MERCHANT_CODE)
    @POST(PAYMENT_OPTIONS_URL)
    suspend fun callPaymentOptionsApi(
            @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
            @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
            @Body paymentOptionsRequestData: PaymentOptionsRequest
    ) : Response<ResponseBody>

    @Headers(HEADER_CONTENT_TYPE, HEADER_RAM_MERCHANT_CODE)
    @POST(PENDING_TRANSACTIONS_URL)
    suspend fun callDepositPendingApi(
            @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
            @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
            @Body depositPendingRequestData: DepositPendingRequest
    ) : Response<DepositPendingResponse>

    @Headers(HEADER_CONTENT_TYPE, HEADER_RAM_MERCHANT_CODE)
    @POST(WITHDRAWAL_REQUEST_URL)
    suspend fun callWithdrawRequestApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body withdrawalRequest: WithdrawalRequest
    ) : Response<WithdrawResponse>

    @Headers(HEADER_CONTENT_TYPE, HEADER_RAM_MERCHANT_CODE)
    @POST(PENDING_TRANSACTIONS_URL)
    suspend fun callWithdrawalPendingApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body withdrawalPendingRequest: WithdrawalPendingRequest
    ) : Response<WithdrawalPendingResponse>

    @POST(HEADER_INFO_URL)
    suspend fun callHeaderInfoApi(
        @Body headerInfoRequest: HeaderInfoRequest
    ) : Response<HeaderInfoResponse>

    @Headers(HEADER_CONTENT_TYPE, HEADER_RAM_MERCHANT_CODE)
    @POST(ADD_NEW_ACCOUNT_URL)
    suspend fun callAddNewAccountApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body addNewAccountRequest: AddNewAccountRequest
    ) : Response<AddNewAccountResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE, MERCHANT_PASSWORD)
    @POST(CHECK_BONUS_STATUS_URL)
    suspend fun callCheckBonusStatusPlayerApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body bonusInfoRequest: BonusRequest
    ) : Response<BonusResponse>

    @Headers(HEADER_RAM_MERCHANT_CODE, MERCHANT_PASSWORD)
    @POST(ANALYTICS_URL)
    suspend fun callAppAnalyticsApi(
        @Body appAnalyticsRequest: AppAnalyticsRequest
    ) : Response<AppAnalyticsResponse>

    @GET(TEST_APP_VERSION_URL)
    suspend fun callTestVersionApi(): Response<TestAppVersion>
}