/*
 * Copyright 2024 HM Revenue & Customs
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

  val baseUrl: String       = baseUrlFor("one-stop-shop-registration-frontend")
  val ossUrl: String        = "/pay-vat-on-goods-sold-to-eu/northern-ireland-register"
  val fullUrl: String       = baseUrl + ossUrl
  val rejoinJourney: String = s"$fullUrl/start-rejoin-journey"

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"
  def selectorById(id: String): Expression[String]          = s"#$id"

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

  def upFrontAuthLoginWithOssEnrolment =
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
      .formParam("enrolment[0].taxIdentifier[0].value", "300000002")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-OSS-ORG")
      .formParam("enrolment[1].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[1].taxIdentifier[0].value", "300000002")
      .formParam("enrolment[1].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def upFrontAuthLoginWithOssEnrolmentForRejoin =
    http("Enter Auth login credentials ")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("redirectionUrl", rejoinJourney)
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "600000050")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "HMRC-OSS-ORG")
      .formParam("enrolment[1].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[1].taxIdentifier[0].value", "600000050")
      .formParam("enrolment[1].state", "Activated")
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
      .check(header("Location").is(ossUrl + "/sell-from-northern-ireland"))

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
      .check(header("Location").is(ossUrl + "/northern-ireland-business"))

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
      .check(header("Location").is(ossUrl + "/business-pay"))

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
      .check(header("Location").is(ossUrl + "/date-of-first-sale"))

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
      .check(header("Location").is(ossUrl + "/previous-oss"))

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
      .check(header("Location").is(ossUrl + "/tax-in-eu"))

  def getRejoinAlreadyMadeSales =
    http("Get Rejoin Already Made Sales page")
      .get(fullUrl + "/rejoin-already-made-sales")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postRejoinAlreadyMadeSales =
    http("Post Rejoin Already Made Sales")
      .post(fullUrl + "/rejoin-already-made-sales")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/rejoin-date-of-first-sale"))

  def getRejoinDateOfFirstSale =
    http("Get Rejoin Date Of First Sale page")
      .get(fullUrl + "/rejoin-date-of-first-sale")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postRejoinDateOfFirstSale =
    http("Post Rejoin Date Of First Sale")
      .post(fullUrl + "/rejoin-date-of-first-sale")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value.day", s"${LocalDate.now().getDayOfMonth}")
      .formParam("value.month", s"${LocalDate.now().getMonthValue}")
      .formParam("value.year", s"${LocalDate.now().getYear}")
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/rejoin-start-date"))

  def getRejoinStartDate =
    http("Get Rejoin Start Date page")
      .get(fullUrl + "/rejoin-start-date")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postRejoinStartDate =
    http("Post Rejoin Start Date")
      .post(fullUrl + "/rejoin-start-date")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/rejoin-registration"))

  def resumeJourney =
    http("Resume journey")
      .get(fullUrl + "/on-sign-in")
      .check(status.in(303))

  def getAmendJourney =
    http("Get Amend Registration Journey")
      .get(fullUrl + "/start-amend-journey")
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/change-your-registration"))

  def getRejoinRegistration =
    http("Get Rejoin Registration")
      .get(fullUrl + "/rejoin-registration")
      .check(status.in(200))

  def getRejoinJourney =
    http("Get Rejoin Registration Journey")
      .get(fullUrl + "/start-rejoin-journey")
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/rejoin-already-made-sales"))

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
      .check(header("Location").is(ossUrl + "/have-uk-trading-name"))

  def getCheckVatGroup =
    http("Get Check VAT Group page")
      .get(fullUrl + "/uk-vat-group")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckVatGroup =
    http("Enter Check VAT Group")
      .post(fullUrl + "/uk-vat-group")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "false")
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

  def postHasTradingName(index: Int) =
    http("Answer Has Trading Name")
      .post(fullUrl + "/have-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/uk-trading-name/$index"))

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
      .check(header("Location").is(ossUrl + "/add-uk-trading-name"))

  def getAmendTradingName(index: Int) =
    http("Get Trading Name page")
      .get(fullUrl + s"/amend-uk-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAmendTradingName(index: Int, tradingName: String) =
    http("Enter Trading Name")
      .post(fullUrl + s"/amend-uk-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/amend-add-uk-trading-name"))

  def getAddTradingName =
    http("Get Add Trading Name page")
      .get(fullUrl + "/add-uk-trading-name")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAddTradingName(answer: Boolean) =
    http("Add Trading Name")
      .post(fullUrl + "/add-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAddTradingName(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testAddTradingName(answer)
        .check(header("Location").is(ossUrl + s"/uk-trading-name/${index.get}"))
    } else {
      testAddTradingName(answer)
        .check(header("Location").is(ossUrl + "/already-made-sales"))
    }

  def getAmendAddTradingName =
    http("Get Amend Add Trading Name page")
      .get(fullUrl + "/amend-add-uk-trading-name")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAmendAddTradingName(answer: Boolean) =
    http("Amend Trading Name")
      .post(fullUrl + "/amend-add-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAmendAddTradingName(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testAmendAddTradingName(answer)
        .check(header("Location").is(ossUrl + s"/amend-uk-trading-name/${index.get}"))
    } else {
      testAmendAddTradingName(answer)
        .check(header("Location").is(ossUrl + "/change-your-registration"))
    }

  def getRejoinAddTradingName =
    http("Get Rejoin Add Trading Name page")
      .get(fullUrl + "/rejoin-amend-add-uk-trading-name")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testRejoinAddTradingName(answer: Boolean) =
    http("Add Rejoin Trading Name")
      .post(fullUrl + "/rejoin-amend-add-uk-trading-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postRejoinAddTradingName(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testRejoinAddTradingName(answer)
        .check(header("Location").is(ossUrl + s"/rejoin-amend-uk-trading-name/${index.get}"))
    } else {
      testRejoinAddTradingName(answer)
        .check(header("Location").is(ossUrl + "/rejoin-registration"))
    }

  def getRejoinTradingName(index: Int) =
    http("Get Rejoin Trading Name page")
      .get(fullUrl + s"/rejoin-amend-uk-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postRejoinTradingName(index: Int, tradingName: String) =
    http("Enter Rejoin Trading Name")
      .post(fullUrl + s"/rejoin-amend-uk-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/rejoin-amend-add-uk-trading-name"))

  def getIsTaxRegisteredInEu =
    http("Get Is Tax Registered in EU page")
      .get(fullUrl + "/tax-in-eu")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postIsTaxRegisteredInEu(index: Int) =
    http("Answer Is Tax Registered in EU")
      .post(fullUrl + "/tax-in-eu")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/eu-tax/$index"))

  def getPreviousOss =
    http("Get Is Previous Oss page")
      .get(fullUrl + "/previous-oss")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousOss(index: Int) =
    http("Answer Previous Oss Page")
      .post(fullUrl + "/previous-oss")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/previous-country/$index"))

  def getVatRegisteredInEuMemberState(index: Int) =
    http("Get Tax Registered in EU Member State page")
      .get(fullUrl + s"/eu-tax/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRegisteredInEuMemberState(index: Int, countryCode: String) =
    http("Enter Tax Registered in EU Member State")
      .post(fullUrl + s"/eu-tax/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/sells-goods-to-eu-consumers/$index"))

  def getPreviousCountry(index: Int) =
    http("Get previous country page")
      .get(fullUrl + s"/previous-country/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousCountry(countryIndex: Int, schemeIndex: Int, countryCode: String) =
    http("Enter previous country")
      .post(fullUrl + s"/previous-country/$countryIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/previous-scheme/$countryIndex/$schemeIndex"))

  def getPreviousOssSchemeNumber(countryIndex: Int, schemeIndex: Int) =
    http("Get Previous Oss Scheme number page")
      .get(fullUrl + s"/previous-oss-scheme-number/$countryIndex/$schemeIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousOssSchemeNumber(countryIndex: Int, schemeIndex: Int, registrationNumber: String) =
    http("Enter Previous Oss Scheme Number")
      .post(fullUrl + s"/previous-oss-scheme-number/$countryIndex/$schemeIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", registrationNumber)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/previous-scheme-answers/$countryIndex"))

  def getVatRegistered(index: Int) =
    http("Get Eu VAT Number page")
      .get(fullUrl + s"/eu-vat-number/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postVatRegistered(index: Int, countryCode: String) =
    http("Enter Eu Vat Number")
      .post(fullUrl + s"/eu-vat-number/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200, 303))

  def getSendGoods(index: Int) =
    http("Get EU Send Goods page")
      .get(fullUrl + s"/eu-send-goods/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSendGoods(index: Int, answer: Boolean) =
    http("Answer EU Send Goods")
      .post(fullUrl + s"/eu-send-goods/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getPreviousScheme(countryIndex: Int, schemeIndex: Int) =
    http("Get Previous Scheme page")
      .get(fullUrl + s"/previous-scheme/$countryIndex/$schemeIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testPreviousScheme(countryIndex: Int, schemeIndex: Int, schemeType: String) =
    http("Answer Previous Scheme")
      .post(fullUrl + s"/previous-scheme/$countryIndex/$schemeIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", schemeType)
      .check(status.in(200, 303))

  def postPreviousScheme(countryIndex: Int, schemeIndex: Int, schemeType: String) =
    if (schemeType == "oss") {
      testPreviousScheme(countryIndex, schemeIndex, schemeType)
        .check(header("Location").is(ossUrl + s"/previous-oss-scheme-number/$countryIndex/$schemeIndex"))
    } else {
      testPreviousScheme(countryIndex, schemeIndex, schemeType)
        .check(header("Location").is(ossUrl + s"/previous-ioss-scheme/$countryIndex/$schemeIndex"))
    }

  def getEuVatNumber(index: Int) =
    http("Get EU VAT Number page")
      .get(fullUrl + s"/eu-vat-number/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postEuVatNumber(index: Int, euVatNumber: String) =
    http("Enter EU VAT Number")
      .post(fullUrl + s"/eu-vat-number/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", euVatNumber)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/eu-trading-name/$index"))

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

  def getFixedEuTradingName(index: Int) =
    http("Get Fixed Establishment Trading Name page")
      .get(fullUrl + s"/eu-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postFixedEuTradingName(index: Int, tradingName: String) =
    http("Enter Fixed Eu Trading Name")
      .post(fullUrl + s"/eu-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/eu-fixed-establishment-address/$index"))

  def getSendGoodsTradingName(index: Int) =
    http("Get EU Send Goods Trading Name page")
      .get(fullUrl + s"/eu-send-goods-trading-name/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSendGoodsTradingName(index: Int, tradingName: String) =
    http("Enter EU Send Goods Trading Name")
      .post(fullUrl + s"/eu-send-goods-trading-name/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/eu-send-goods-address/$index"))

  def getEuTaxReference(index: Int) =
    http("Get EU Tax Reference page")
      .get(fullUrl + s"/eu-tax-number/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postEuTaxReference(index: Int, taxReference: String) =
    http("Enter EU Tax Reference")
      .post(fullUrl + s"/eu-tax-number/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", taxReference)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/eu-send-goods-trading-name/$index"))

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
      .check(header("Location").is(ossUrl + s"/check-tax-details/$index"))

  def getSendGoodsAddress(index: Int) =
    http("Get EU Send Goods Address page")
      .get(fullUrl + s"/eu-send-goods-address/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSendGoodsAddress(index: Int) =
    http("Enter EU Send Goods Address")
      .post(fullUrl + s"/eu-send-goods-address/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("line1", "1 The Street")
      .formParam("line2", "A Village")
      .formParam("townOrCity", "A City")
      .formParam("postCode", "ABC 123D")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/check-tax-details/$index"))

  def getCheckTaxDetails(index: Int) =
    http("Get Check Tax Details page")
      .get(fullUrl + s"/check-tax-details/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckTaxDetails(index: Int) =
    http("Submit Check EU VAT Details")
      .post(fullUrl + s"/check-tax-details/$index?incompletePromptShown=false")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/add-tax-details"))

  def getAddTaxDetails =
    http("Get Add VAT Details page")
      .get(fullUrl + "/add-tax-details")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAddTaxDetails(answer: Boolean) =
    http("Answer Add EU VAT Details")
      .post(fullUrl + "/add-tax-details?incompletePromptShown=false")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAddTaxDetails(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testAddTaxDetails(answer)
        .check(header("Location").is(ossUrl + s"/eu-tax/${index.get}"))
    } else {
      testAddTaxDetails(answer)
        .check(header("Location").is(ossUrl + "/online-marketplace"))
    }

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
      .post(fullUrl + "/add-deregistration?incompletePromptShown=false")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def getAddDeRegistration =
    http("get Deregistered page")
      .get(fullUrl + "/deregistered")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddDeRegistration(answer: Boolean) =
    http("Deregistered")
      .post(fullUrl + "/deregistered?incompletePromptShown=false")
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

  def postHasWebsite(answer: Boolean, index: Int) =
    http(s"Answer has website")
      .post(fullUrl + s"/give-website-address")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(303))
      .check(header("Location").is(ossUrl + s"/website-address/$index"))

  def getAmendHasWebsite =
    http(s"Get Amend Has Website page")
      .get(fullUrl + s"/amend-give-website-address")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAmendHasWebsite =
    http(s"Answer amend has website")
      .post(fullUrl + s"/amend-give-website-address")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", false)
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/amend-remove-all-websites"))

  def getAmendRemoveAllWebsites =
    http(s"Get Amend Remove All Websites page")
      .get(fullUrl + s"/amend-remove-all-websites")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAmendRemoveAllWebsites =
    http(s"Answer Amend Remove All Websites")
      .post(fullUrl + s"/amend-remove-all-websites")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(303))
      .check(header("Location").is(ossUrl + "/change-your-registration"))

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
      .check(header("Location").is(ossUrl + "/give-website-address"))

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
      .check(header("Location").is(ossUrl + "/add-website-address"))

  def getAddWebsite =
    http("Get Add Website page")
      .get(fullUrl + "/add-website-address")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testAddWebsite(answer: Boolean) =
    http("Add Website")
      .post(fullUrl + "/add-website-address")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postAddWebsite(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testAddWebsite(answer)
        .check(header("Location").is(ossUrl + s"/website-address/${index.get}"))
    } else {
      testAddWebsite(answer)
        .check(header("Location").is(ossUrl + "/business-contact-details"))
    }

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
      .check(header("Location").is(ossUrl + "/bank-details"))

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
      .check(header("Location").is(ossUrl + "/check-answers"))

  def getCheckYourAnswers =
    http("Get Check Your Answers page")
      .get(fullUrl + "/check-answers")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCheckYourAnswers =
    http("Post Check Your Answers page")
      .post(fullUrl + "/check-answers/false")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/successful"))

  def getChangeYourRegistration =
    http("Get Change Your Registration page")
      .get(fullUrl + "/change-your-registration")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postChangeYourRegistration =
    http("Post Change Your Registration page")
      .post(fullUrl + "/change-your-registration?incompletePrompt=false")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/successful-amend"))

  def postRejoinRegistration =
    http("Post Rejoin Registration page")
      .post(fullUrl + "/rejoin-registration?incompletePrompt=false")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/successful-rejoin"))

  def getPreviousSchemeAnswers(index: Int) =
    http("Get Previous Scheme Answers page")
      .get(fullUrl + s"/previous-scheme-answers/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousSchemeAnswers(index: Int, answer: Boolean) =
    http("Post Previous Scheme Answers page")
      .post(fullUrl + s"/previous-scheme-answers/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + "/previous-schemes-overview"))

  def getPreviousSchemesOverview =
    http("Get Previous Schemes Overview page")
      .get(fullUrl + "/previous-schemes-overview")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testPreviousSchemesOverview(answer: Boolean) =
    http("Previous Schemes Overview")
      .post(fullUrl + "/previous-schemes-overview?incompletePromptShown=false")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))

  def postPreviousSchemesOverview(answer: Boolean, index: Option[Int]) =
    if (answer) {
      testPreviousSchemesOverview(answer)
        .check(header("Location").is(ossUrl + s"/previous-country/${index.get}"))
    } else {
      testPreviousSchemesOverview(answer)
        .check(header("Location").is(ossUrl + "/start-date"))
    }

  def getPreviousIossScheme(countryIndex: Int, schemeIndex: Int) =
    http("Get Previous IOSS Scheme page")
      .get(fullUrl + s"/previous-ioss-scheme/$countryIndex/$schemeIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousIossScheme(countryIndex: Int, schemeIndex: Int, answer: Boolean) =
    http("Previous IOSS Scheme")
      .post(fullUrl + s"/previous-ioss-scheme/$countryIndex/$schemeIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/previous-ioss-number/$countryIndex/$schemeIndex"))

  def getPreviousIossNumber(countryIndex: Int, schemeIndex: Int) =
    http("Get Previous IOSS number page")
      .get(fullUrl + s"/previous-ioss-number/$countryIndex/$schemeIndex")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviousIossNumber(countryIndex: Int, schemeIndex: Int, iossNumber: String, intermediaryNumber: String) =
    http("Previous IOSS Number")
      .post(fullUrl + s"/previous-ioss-number/$countryIndex/$schemeIndex")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("previousSchemeNumber", iossNumber)
      .formParam("previousIntermediaryNumber", intermediaryNumber)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/previous-scheme-answers/$countryIndex"))

  def getSellGoodsToEuConsumers(index: Int) =
    http("Get Sell Goods To Eu Consumers page")
      .get(fullUrl + s"/sells-goods-to-eu-consumers/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSellGoodsToEuConsumers(index: Int) =
    http("Answer Sell Goods To Eu Consumers page")
      .post(fullUrl + s"/sells-goods-to-eu-consumers/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/sells-goods-to-eu-consumer-method/$index"))

  def getSellGoodsToEuConsumersMethod(index: Int) =
    http("Get Sell Goods To Eu Consumers Method Page page")
      .get(fullUrl + s"/sells-goods-to-eu-consumer-method/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postSellGoodsToEuConsumersMethod(index: Int, salesMethod: String) =
    http("Answer Sell Goods To Eu Consumers Method Page")
      .post(fullUrl + s"/sells-goods-to-eu-consumer-method/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", salesMethod)
      .check(status.in(200, 303))
      .check(header("Location").is(ossUrl + s"/registration-type/$index"))

  def getRegistrationType(index: Int) =
    http("Get Registration Type page")
      .get(fullUrl + s"/registration-type/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def testRegistrationType(index: Int, registrationType: String) =
    http("Answer Registration Type Page")
      .post(fullUrl + s"/registration-type/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", registrationType)
      .check(status.in(200, 303))

  def postRegistrationType(index: Int, registrationType: String) =
    if (registrationType == "vatNumber") {
      testRegistrationType(index, registrationType)
        .check(header("Location").is(ossUrl + s"/eu-vat-number/$index"))
    } else {
      testRegistrationType(index, registrationType)
        .check(header("Location").is(ossUrl + s"/eu-tax-number/$index"))
    }

  def getApplicationComplete =
    http("Get Application Complete page")
      .get(fullUrl + "/successful")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))

  def getSuccessfulAmend =
    http("Get Successful Amend page")
      .get(fullUrl + "/successful-amend")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))

  def getSuccessfulRejoin =
    http("Get Successful Rejoin page")
      .get(fullUrl + "/successful-rejoin")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))

}
