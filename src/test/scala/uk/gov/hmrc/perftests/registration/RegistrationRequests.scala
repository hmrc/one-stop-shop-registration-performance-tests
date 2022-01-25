/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.registration

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import java.time.LocalDate

object RegistrationRequests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("one-stop-shop-registration-frontend")
  val ossUrl: String  = "/pay-vat-on-goods-sold-to-eu/northern-ireland-register"
  val fullUrl: String = baseUrl + ossUrl

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def goToAuthLoginPage =
    http("Go to Auth login page")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .check(status.in(200, 303))

  def upFrontAuthLogin =
    http("Enter Auth login credentials ")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("redirectionUrl", fullUrl)
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "${vrn}")
      .formParam("enrolment[0].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def getAlreadyRegistered =
    http("Get Already Registered in EU page")
      .get(fullUrl + "/already-eu-registered")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAlreadyRegistered =
    http("Post Already Registered in EU pagte")
      .post(fullUrl + "/already-eu-registered")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(303))

  def getSellsGoodsFromNi =
    http("Get Sells Goods from NI page")
      .get(fullUrl + "/sell-from-northern-ireland")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSellsGoodsFromNi =
    http("Post Sells Goods From NI")
      .post(fullUrl + "/sell-from-northern-ireland")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(303))

  def getBusinessBasedInNi =
    http("Get Business Based in NI page")
      .get(fullUrl + "/northern-ireland-business")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postBusinessBasedInNi =
    http("Post Business Based In NI")
      .post(fullUrl + "/northern-ireland-business")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(303))

  def getBusinessPay =
    http("Get Report and Pay VAT on Sales page")
      .get(fullUrl + "/business-pay")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def getAlreadyMadeSales =
    http("Get Already Made Sales page")
      .get(fullUrl + "/already-made-sales")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAlreadyMadeSales =
    http("Post Already Made Sales")
      .post(fullUrl + "/already-made-sales")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(303))

  def getDateOfFirstSale =
    http("Get Date Of First Sale page")
      .get(fullUrl + "/date-of-first-sale")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postDateOfFirstSale =
    http("Post Date Of First Sale")
      .post(fullUrl + "/date-of-first-sale")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value.day", s"${LocalDate.now().getDayOfMonth}")
      .formParam("value.month", s"${LocalDate.now().getMonthValue}")
      .formParam("value.year", s"${LocalDate.now().getYear}")
      .check(status.in(303))

  def getStartDate =
    http("Get Commencement Date page")
      .get(fullUrl + "/start-date")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postStartDate =
    http("Post Commencement Date")
      .post(fullUrl + "/start-date")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(303))

  def resumeJourney =
    http("Resume journey")
      .get(fullUrl + "/on-sign-in")
      .check(status.in(303))

  def getCheckVatDetails =
    http("Get Check VAT Details page")
      .get(fullUrl + "/confirm-vat-details")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckVatDetails =
    http("Enter Check VAT Details")
      .post(fullUrl + "/confirm-vat-details")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "yes")
      .check(status.in(200, 303))

  def getRegisteredCompanyName =
    http("Get Registered Company Name page")
      .get(fullUrl + "/registeredCompanyName")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postRegisteredCompanyName =
    http("Enter Registered Company Name")
      .post(fullUrl + "/registeredCompanyName")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "Foo Ltd")
      .check(status.in(200, 303))

  def getHasTradingName =
    http("Get Has Trading Name page")
      .get(fullUrl + "/have-uk-trading-name")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postHasTradingName =
    http("Answer Has Trading Name")
      .post(fullUrl + "/have-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))

  def getTradingName(index: Int) =
    http("Get Trading Name page")
      .get(fullUrl + s"/uk-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postTradingName(index: Int, tradingName: String) =
    http("Enter Trading Name")
      .post(fullUrl + s"/uk-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200, 303))

  def getAddTradingName =
    http("Get Add Trading Name page")
      .get(fullUrl + "/add-uk-trading-name")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddTradingName(answer: Boolean) =
    http("Add Trading Name")
      .post(fullUrl + "/add-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getIsTaxRegisteredInEu =
    http("Get Is Tax Registered in EU page")
      .get(fullUrl + "/tax-in-eu")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postIsTaxRegisteredInEu =
    http("Answer Is Tax Registered in EU")
      .post(fullUrl + "/tax-in-eu")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))

  def getVatRegisteredInEuMemberState(index: Int) =
    http("Get VAT Registered in EU Member State page")
      .get(fullUrl + s"/eu-tax/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRegisteredInEuMemberState(index: Int, countryCode: String) =
    http("Enter VAT Registered in EU Member State")
      .post(fullUrl + s"/eu-tax/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))

  def getVatRegistered(index: Int) =
    http("Get VAT Registered page")
      .get(fullUrl + s"/eu-vat/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRegistered(index: Int, answer: Boolean) =
    http("Answer Vat Registered")
      .post(fullUrl + s"/eu-vat/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getEuVatNumber(index: Int) =
    http("Get EU VAT Number page")
      .get(fullUrl + s"/eu-vat-number/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postEuVatNumber(index: Int) =
    http("Enter EU VAT Number")
      .post(fullUrl + s"/eu-vat-number/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "ES852369")
      .check(status.in(200, 303))

  def getHasFixedEstablishment(index: Int) =
    http("Get Has Fixed Establishment page")
      .get(fullUrl + s"/eu-fixed-establishment/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postHasFixedEstablishment(index: Int, value: Boolean) =
    http("Answer Has Fixed Establishment")
      .post(fullUrl + s"/eu-fixed-establishment/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", value)
      .check(status.in(200, 303))

  def getFixedEstablishmentTradingName(index: Int) =
    http("Get Fixed Establishment Trading Name page")
      .get(fullUrl + s"/eu-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def getEuTaxReference(index: Int) =
    http("Get EU Tax Reference page")
      .get(fullUrl + s"/euTaxReference/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postEuTaxReference(index: Int) =
    http("Enter EU Tax Reference")
      .post(fullUrl + s"/euTaxReference/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "123456789")
      .check(status.in(200, 303))

  def postFixedEstablishmentTradingName(index: Int) =
    http("Enter Fixed Establishment Trading Name")
      .post(fullUrl + s"/eu-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "foo")
      .check(status.in(200, 303))

  def getFixedEstablishmentAddress(index: Int) =
    http("Get Fixed Establishment Address page")
      .get(fullUrl + s"/eu-fixed-establishment-address/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postFixedEstablishmentAddress(index: Int) =
    http("Enter Fixed Establishment Address")
      .post(fullUrl + s"/eu-fixed-establishment-address/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("line1", "line1")
      .formParam("line2", "line2")
      .formParam("townOrCity", "townOrCity")
      .formParam("postCode", "ABC")
      .check(status.in(200, 303))

  def getCheckEuVatDetails(index: Int) =
    http("Get Check EU VAT Details page")
      .get(fullUrl + s"/check-tax-details/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckEuVatDetails(index: Int) =
    http("Submit Check EU VAT Details")
      .post(fullUrl + s"/check-tax-details/$index")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))

  def getAddEuVatDetails =
    http("Get Add EU VAT Details page")
      .get(fullUrl + "/add-tax-details")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddEuVatDetails(answer: Boolean) =
    http("Answer Add EU VAT Details")
      .post(fullUrl + "/add-tax-details")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getPreviouslyRegistered =
    http("Get Previously Registered page")
      .get(fullUrl + "/deregistered")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviouslyRegistered(answer: Boolean) =
    http("Answer Previously Registered")
      .post(fullUrl + "/deregistered")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getPreviousEuCountry(index: Int) =
    http("Get Previous EU Country page")
      .get(fullUrl + s"/deregistered-country/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousEuCountry(index: Int, countryCode: String) =
    http("Enter Previous EU Country")
      .post(fullUrl + s"/deregistered-country/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))

  def getPreviousEuVatNumber(index: Int) =
    http("Get Previous EU VAT Number page")
      .get(fullUrl + s"/deregistered-eu-vat-number/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousEuVatNumber(index: Int, answer: String) =
    http("Enter Previous EU VAT Number")
      .post(fullUrl + s"/deregistered-eu-vat-number/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getAddPreviousRegistration =
    http("Get Add Previous Registration page")
      .get(fullUrl + "/add-deregistration")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddPreviousRegistration(answer: Boolean) =
    http("Add Previous Registration")
      .post(fullUrl + "/add-deregistration")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getBusinessAddress =
    http("Get Business Address page")
      .get(fullUrl + "/businessAddress")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postBusinessAddress =
    http("Enter Business Address")
      .post(fullUrl + "/businessAddress")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("line1", "1 The Avenue")
      .formParam("line2", "Amazonville")
      .formParam("townOrCity", "Amazontown")
      .formParam("county", "Amazonian")
      .formParam("postCode", "AM1 1AM")
      .check(status.in(200, 303))

  def getHasWebsite =
    http(s"Get Has Website page")
      .get(fullUrl + s"/give-website-address")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postHasWebsite(answer: Boolean) =
    http(s"Answer has website")
      .post(fullUrl + s"/give-website-address")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(303))

  def getIsOnlineMarketplace =
    http(s"Get Is Online Marketplace page")
      .get(fullUrl + s"/online-marketplace")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postIsOnlineMarketplace =
    http(s"Answer Is Online Marketplace")
      .post(fullUrl + s"/online-marketplace")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(303))

  def getWebsite(index: Int) =
    http(s"Get Website page $index")
      .get(fullUrl + s"/website-address/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postWebsite(index: Int, website: String) =
    http(s"Enter website $index")
      .post(fullUrl + s"/website-address/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", website)
      .check(status.in(303))

  def getAddWebsite =
    http("Get Add Website page")
      .get(fullUrl + "/add-website-address")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddWebsite(answer: Boolean) =
    http("Add Website")
      .post(fullUrl + "/add-website-address")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getBusinessContactDetails =
    http("Get Business Contact Details page")
      .get(fullUrl + "/business-contact-details")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postBusinessContactDetails =
    http("Enter Business Contact Details")
      .post(fullUrl + "/business-contact-details")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("fullName", "Jane Smith")
      .formParam("telephoneNumber", "01478523691")
      .formParam("emailAddress", "jane@email.com")
      .check(status.in(200, 303))

  def getBankDetails =
    http("Get Bank Details page")
      .get(fullUrl + "/bank-details")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postBankDetails =
    http("Enter Bank Details")
      .post(fullUrl + "/bank-details")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("accountName", "Account name")
      .formParam("bic", "ABCDEF2A")
      .formParam("iban", "GB33BUKB20201555555555")
      .check(status.in(200, 303))

  def getCheckYourAnswers =
    http("Get Check Your Answers page")
      .get(fullUrl + "/check-answers")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckYourAnswers =
    http("Post Check Your Answers page")
      .post(fullUrl + "/check-answers")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))

  def getApplicationComplete =
    http("Get Application Complete page")
      .get(fullUrl + "/successful")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))
}
