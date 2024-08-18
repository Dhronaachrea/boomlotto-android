package com.skilrock.boomlotto.utility

import android.util.Log
import com.skilrock.boomlotto.models.response.PaymentOptionsResponse
import org.json.JSONObject
import java.util.*

class PaymentOptionsResponseParser {

    fun handleResponse(jsonResponse: JSONObject) : PaymentOptionsResponse? {
        //val jsonResponse = JSONObject("{'errorCode':0,'payTypeMap':{'1':{'payTypeId':1,'payTypeCode':'CREDIT_CARD','payTypeDispCode':'byCard','imagePath':'visa.gif','subTypeMap':{'1':'VISA_CARD'},'payAccReqMap':{'1':'NO'},'payAccKycType':{'1':'NONE'},'subTypeMapPortal':{'1':'VISA_CARD#0'},'providerMap':{'14':'DialUp56k'},'currencyMap':{'8':'INR'},'uiOrder':2,'minValue':50.0,'maxValue':10000.0},'7':{'payTypeId':7,'payTypeCode':'NET_BANKING','payTypeDispCode':'NETBANKING','subTypeMap':{'58':'HDFCBank','52':'BankofIndia','59':'ICICIBank','62':'IndusindBank','67':'KotakBank','69':'StateBankofIndia','82':'SARASWATCOOPERATIVEBANK','81':'PUNJABANDSINDBANK','80':'StandardCharteredBank','79':'PunjabNationalBank','78':'LaxmiVilasBank-CorporateNetBanking','77':'DHANALAXMIBANKCORPORATE','76':'AllahabadBank','75':'UCOBank','74':'CatholicSyrianBank','73':'BankofBarodaRetailAccounts','72':'AndhraBank','71':'YesBank','70':'UnionBankofIndia','68':'SouthIndianBank','66':'KarurVysyaBank','65':'KarnatakaBankLTD','64':'JammuandKashmirBank','63':'IndustrialDevelopmentBankofIndia','61':'IndianOverseasBank','60':'IndianBank','57':'FederalBank','56':'CityUnionBank','55':'CentralBankofIndia','54':'CanaraBank','53':'BankofMaharashtra'},'payAccReqMap':{'64':'NO','65':'NO','66':'NO','67':'NO','68':'NO','69':'NO','70':'NO','71':'NO','72':'NO','73':'NO','74':'NO','75':'NO','76':'NO','77':'NO','78':'NO','79':'NO','80':'NO','81':'NO','82':'NO','52':'NO','53':'NO','54':'NO','55':'NO','56':'NO','57':'NO','58':'NO','59':'NO','60':'NO','61':'NO','62':'NO','63':'NO'},'payAccKycType':{'64':'MANUAL','65':'MANUAL','66':'MANUAL','67':'MANUAL','68':'MANUAL','69':'MANUAL','70':'MANUAL','71':'MANUAL','72':'MANUAL','73':'MANUAL','74':'MANUAL','75':'MANUAL','76':'MANUAL','77':'MANUAL','78':'MANUAL','79':'MANUAL','80':'MANUAL','81':'MANUAL','82':'MANUAL','52':'MANUAL','53':'MANUAL','54':'MANUAL','55':'MANUAL','56':'MANUAL','57':'MANUAL','58':'MANUAL','59':'MANUAL','60':'MANUAL','61':'MANUAL','62':'MANUAL','63':'MANUAL'},'subTypeMapPortal':{},'providerMap':{'14':'DialUp56k'},'currencyMap':{'8':'INR'},'uiOrder':5,'minValue':1.0,'maxValue':200000.0},'11':{'payTypeId':11,'payTypeCode':'WALLET','payTypeDispCode':'Wallet','subTypeMap':{'47':'FreechargeWallet','45':'RelianceJioWallet','43':'AirtelMoneyWallet','50':'SBIBuddyWallet','48':'PhonePeWallet','46':'OLAMoneyWallet','44':'VodafoneMpesaWallet','42':'MobikwikWallet','51':'AmazonWallet','49':'OxigenWallet'},'payAccReqMap':{'48':'NO','49':'NO','50':'NO','51':'NO','42':'NO','43':'NO','44':'NO','45':'NO','46':'NO','47':'NO'},'payAccKycType':{'48':'MANUAL','49':'MANUAL','50':'MANUAL','51':'MANUAL','42':'MANUAL','43':'MANUAL','44':'MANUAL','45':'MANUAL','46':'MANUAL','47':'MANUAL'},'subTypeMapPortal':{},'providerMap':{'14':'DialUp56k'},'currencyMap':{'8':'INR'},'uiOrder':7,'minValue':1.0,'maxValue':100000.0},'12':{'payTypeId':12,'payTypeCode':'UPI','payTypeDispCode':'UPI','subTypeMap':{'83':'UPI'},'payAccReqMap':{'83':'YES'},'payAccKycType':{'83':'MANUAL'},'subTypeMapPortal':{},'providerMap':{'14':'DialUp56k'},'currencyMap':{'8':'INR'},'uiOrder':8,'minValue':1.0,'maxValue':100000.0},'13':{'payTypeId':13,'payTypeCode':'DEBIT_CARD','payTypeDispCode':'byCard','imagePath':'visa.gif','subTypeMap':{'85':'DEBIT_CARD'},'payAccReqMap':{'85':null},'payAccKycType':{'85':'MANUAL'},'subTypeMapPortal':{'85':'DEBIT_CARD#0'},'providerMap':{'14':'DialUp56k'},'currencyMap':{'8':'INR'},'uiOrder':2,'minValue':1.0,'maxValue':100000.0}},'paymentAccounts':{'1008':{'paymentAccId':1008,'paymentTypeId':12,'paymentType':'UPI','merchantId':1,'domainId':5,'subTypeId':83,'userId':7363,'accHolderName':'Shivani','accNum':'null76857474745745','accType':'SAVING','status':'ACTIVE','verifiedByUser':0,'verificationStatus':'INITIATED','currencyId':8,'otpVerified':'YES','createdAt':1634537600000,'updatedAt':1634537613000},'1011':{'paymentAccId':1011,'paymentTypeId':12,'paymentType':'UPI','merchantId':1,'domainId':5,'subTypeId':83,'userId':7363,'accHolderName':'Sudha','accNum':'null13353464363','accType':'SAVING','status':'ACTIVE','verifiedByUser':0,'verificationStatus':'INITIATED','currencyId':8,'otpVerified':'YES','createdAt':1634538925000,'updatedAt':1634538937000},'1319':{'paymentAccId':1319,'paymentTypeId':12,'paymentType':'UPI','merchantId':1,'domainId':5,'subTypeId':83,'userId':7363,'accHolderName':'Shivani','accNum':'null57484454774','accType':'SAVING','status':'ACTIVE','verifiedByUser':0,'verificationStatus':'INITIATED','currencyId':8,'otpVerified':'YES','createdAt':1634718885000,'updatedAt':1634718895000},'1004':{'paymentAccId':1004,'paymentTypeId':12,'paymentType':'UPI','merchantId':1,'domainId':5,'subTypeId':83,'userId':7363,'accHolderName':'Shivani','accNum':'null78454634636','accType':'SAVING','status':'ACTIVE','verifiedByUser':0,'verificationStatus':'INITIATED','currencyId':8,'otpVerified':'YES','createdAt':1634536195000,'updatedAt':1634536204000}}}")
        val uiData = PaymentOptionsResponse()

        val payTypeMapKeys = jsonResponse.optJSONObject("payTypeMap")?.keys()
        val payTypeMapObject = jsonResponse.optJSONObject("payTypeMap")

        payTypeMapKeys?.let { payTypeMapKeysNonNull ->
            payTypeMapObject?.let { payTypeMapObjectNonNull ->
                val listPaymentTypes = ArrayList<PaymentOptionsResponse.PayTypeMap>()

                for (key in payTypeMapKeysNonNull) {
                    val payTypeObject = payTypeMapObjectNonNull.optJSONObject(key.toString())

                    payTypeObject?.let { payTypeObjectNonNull ->
                        val subTypeMapObject = payTypeObjectNonNull.optJSONObject("subTypeMap")
                        subTypeMapObject?.let { subTypeMapObjectNonNull ->
                            val subTypeKeys = subTypeMapObjectNonNull.keys()

                            while (subTypeKeys.hasNext()) {
                                val subTypeKey = subTypeKeys.next()
                                val depositUiData = PaymentOptionsResponse().PayTypeMap()
                                depositUiData.arrayId = key
                                depositUiData.payTypeId = payTypeObjectNonNull.optInt("payTypeId")
                                depositUiData.payTypeCode = payTypeObjectNonNull.optString("payTypeCode")
                                depositUiData.payTypeDispCode = payTypeObjectNonNull.optString("payTypeDispCode")

                                depositUiData.uiOrder = payTypeObjectNonNull.optInt("uiOrder")
                                depositUiData.minValue = payTypeObjectNonNull.optDouble("minValue")
                                depositUiData.maxValue = payTypeObjectNonNull.optDouble("maxValue")

                                depositUiData.subTypeCode = subTypeKey
                                depositUiData.subTypeValue = subTypeMapObjectNonNull.optString(subTypeKey)

                                val currencyMapObject = payTypeObjectNonNull.optJSONObject("currencyMap")
                                val listCurrency = ArrayList<PaymentOptionsResponse.PayTypeMap.CurrencyMap>()
                                currencyMapObject?.let { currencyMapObjectNonNull ->
                                    setCurrencyDetails(currencyMapObjectNonNull, listCurrency)
                                }

                                depositUiData.currencyMap = listCurrency
                                val paymentAccountsKeys = jsonResponse.optJSONObject("paymentAccounts")?.keys()
                                val paymentAccountsObject = jsonResponse.optJSONObject("paymentAccounts")
                                val listAccounts = ArrayList<PaymentOptionsResponse.PayTypeMap.AccountDetail>()
                                paymentAccountsKeys?.let { paymentAccountsKeysNonNull ->
                                    paymentAccountsObject?.let { paymentAccountsObjectNonNull ->
                                        setPaymentAccounts(paymentAccountsKeysNonNull, paymentAccountsObjectNonNull,
                                            depositUiData, listAccounts)
                                    }
                                }

                                val payAccReqMap = payTypeObjectNonNull.optJSONObject("payAccReqMap")

                                payAccReqMap?.let { payAccReqMapNonNull ->
                                    val status = payAccReqMapNonNull.optString(subTypeKey)
                                    depositUiData.showAddNewAccBtn = status.equals("YES", true)
                                } ?: run {
                                    depositUiData.showAddNewAccBtn = false
                                }

                                val payAccKycType = payTypeObjectNonNull.optJSONObject("payAccKycType")
                                payAccKycType?.let { payAccKycTypeNonNull ->
                                    val type = payAccKycTypeNonNull.optString(subTypeKey)
                                    depositUiData.kycType = type
                                } ?: run {
                                    depositUiData.kycType = ""
                                }

                                depositUiData.accountDetail = listAccounts
                                listPaymentTypes.add(depositUiData)
                            }
                        }
                    }
                }

                uiData.listPayTypeMap = listPaymentTypes
                return uiData
            }
        } ?: run {
            return null
        }
    }

    private fun setCurrencyDetails(currencyMapObjectNonNull: JSONObject, listCurrency: ArrayList<PaymentOptionsResponse.PayTypeMap.CurrencyMap>) {
        val currencyKeys = currencyMapObjectNonNull.keys()
        while (currencyKeys.hasNext()) {
            val currencyKey = currencyKeys.next()
            val depositUiDataCurrency   = PaymentOptionsResponse().PayTypeMap().CurrencyMap()
            depositUiDataCurrency.code  = currencyKey
            depositUiDataCurrency.value = currencyMapObjectNonNull.optString(currencyKey)
            listCurrency.add(depositUiDataCurrency)
        }
    }

    private fun setPaymentAccounts(paymentAccountsKeysNonNull: MutableIterator<String>, paymentAccountsObjectNonNull: JSONObject,
                                   uiData: PaymentOptionsResponse.PayTypeMap, listAccounts: ArrayList<PaymentOptionsResponse.PayTypeMap.AccountDetail>) {
        while (paymentAccountsKeysNonNull.hasNext()) {
            val paymentAccountsKey      = paymentAccountsKeysNonNull.next()
            val paymentAccountObject    = paymentAccountsObjectNonNull.optJSONObject(paymentAccountsKey)
            paymentAccountObject?.let { paymentAccountObjectNonNull ->
                try {
                    val subTypeId = paymentAccountObjectNonNull.optInt("subTypeId")
                    val subTypeCode = uiData.subTypeCode.toInt()

                    if (subTypeId == subTypeCode) {
                        val account                 = PaymentOptionsResponse().PayTypeMap().AccountDetail()
                        account.accNum              = paymentAccountObjectNonNull.optString("accNum")
                        account.paymentAccId        = paymentAccountObjectNonNull.optInt("paymentAccId")
                        account.accountHolderName   = paymentAccountObjectNonNull.optString("accHolderName")
                        account.verificationStatus  = paymentAccountObjectNonNull.optString("verificationStatus")
                        listAccounts.add(account)
                    } else {
                        Log.e("log", "subTypeId != subTypeCode")
                    }
                } catch (e: Exception) {
                    Log.e("log", e.message ?: "ERROR")
                }
            }
        }
    }

}