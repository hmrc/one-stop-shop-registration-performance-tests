/*
 * Copyright 2021 HM Revenue & Customs
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

object RegistrationRequests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("one-stop-shop-registration-frontend")
  val ossUrl: String = "/one-stop-shop-registration"
  val fullUrl: String = baseUrl + ossUrl

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def generateVatNumber(): Int = {
      val vatNumber = scala.util.Random
      vatNumber.nextInt(999999999)
  }

  def getIsBusinessBasedInNorthernIreland = {
    http("Get Is Business Based in Northern Ireland page")
      .get(fullUrl + "/isBusinessBasedInNorthernIreland")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postIsBusinessBasedInNorthernIreland = {
    http("Post Is Business Based in Northern Ireland")
      .post(fullUrl + "/isBusinessBasedInNorthernIreland")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(303))
  }

  def goToAuthLoginPage = {
    http("Go to Auth login page")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .check(status.in(200, 303))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
  }

  def upFrontAuthLogin = {
    http("Enter Auth login credentials ")
      .post(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("redirectionUrl", fullUrl + "/registeredCompanyName")
      .formParam("enrolment[0].name", "HMRC-MTD-VAT")
      .formParam("enrolment[0].taxIdentifier[0].name", "VRN")
      .formParam("enrolment[0].taxIdentifier[0].value", "123456789") // TODO: Needs to be fed
      .formParam("enrolment[0].state", "Activated")
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))
  }

  def getCheckVatDetails = {
    http("Get Check VAT Details page")
      .get(fullUrl + "/checkVatDetails")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postCheckVatDetails = {
    http("Enter Check VAT Details")
      .post(fullUrl + "/checkVatDetails")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", true)
      .check(status.in(200,303))
  }

  def getRegisteredCompanyName = {
    http("Get Registered Company Name page")
      .get(fullUrl + "/registeredCompanyName")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postRegisteredCompanyName = {
    http("Enter Registered Company Name")
      .post(fullUrl + "/registeredCompanyName")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "Foo Ltd")
      .check(status.in(200,303))
  }

  def getHasTradingName = {
    http("Get Has Trading Name page")
      .get(fullUrl + "/hasTradingName")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postHasTradingName = {
    http("Answer Has Trading Name")
      .post(fullUrl + "/hasTradingName")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200,303))
  }

  def getTradingName(index: Int) = {
    http("Get Trading Name page")
      .get(fullUrl + s"/tradingName/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postTradingName(index: Int, tradingName: String) = {
    http("Enter Trading Name")
      .post(fullUrl + s"/tradingName/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", tradingName)
      .check(status.in(200,303))
  }

  def getAddTradingName =
    http("Get Add Trading Name page")
      .get(fullUrl + "/addTradingName")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddTradingName(answer: Boolean) = {
    http("Add Trading Name")
      .post(fullUrl + "/addTradingName")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))
  }

  def getPartOfVatGroup = {
    http("Get Part of VAT Group page")
      .get(fullUrl + "/partOfVatGroup")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postPartOfVatGroup = {
    http("Answer Part of VAT group")
      .post(fullUrl + "/partOfVatGroup")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200,303))
  }

  def getUkVatEffectiveDate = {
    http("Get UK VAT Effective Date page")
      .get(fullUrl + "/ukVatEffectiveDate")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postUkVatEffectiveDate = {
    http("Enter UK VAT Effective Date")
      .post(fullUrl + "/ukVatEffectiveDate")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value.day", "15")
      .formParam("value.month", "05")
      .formParam("value.year", "2017")
      .check(status.in(200,303))
  }

  def getUkVatRegisteredPostcode = {
    http("Get UK VAT Registered Postcode page")
      .get(fullUrl + "/ukVatRegisteredPostcode")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postUkVatRegisteredPostcode = {
    http("Enter UK VAT Registered Postcode")
      .post(fullUrl + "/ukVatRegisteredPostcode")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "AA1 1ZZ")
      .check(status.in(200,303))
  }

  def getIsVatRegisteredInEu = {
    http("Get Is VAT Registered in EU page")
      .get(fullUrl + "/vatRegisteredInEu")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postIsVatRegisteredInEu = {
    http("Answer Is VAT Registered in EU")
      .post(fullUrl + "/vatRegisteredInEu")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.in(200,303))
  }

  def getVatRegisteredInEuMemberState(index: Int) = {
    http("Get VAT Registered in EU Member State page")
      .get(fullUrl + s"/vatRegisteredEuMemberState/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postVatRegisteredInEuMemberState(index: Int, countryCode: String) = {
    http("Enter VAT Registered in EU Member State")
      .post(fullUrl + s"/vatRegisteredEuMemberState/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200,303))
  }

  def getEuVatNumber(index: Int) = {
    http("Get EU VAT Number page")
      .get(fullUrl + s"/euVatNumber/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postEuVatNumber(index: Int) = {
    http("Enter EU VAT Number")
      .post(fullUrl + s"/euVatNumber/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "ES852369")
      .check(status.in(200,303))
  }

  def getHasFixedEstablishment(index: Int) = {
    http("Get Has Fixed Establishment page")
      .get(fullUrl + s"/hasFixedEstablishment/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postHasFixedEstablishment(index: Int, value: Boolean) = {
    http("Answer Has Fixed Establishment")
      .post(fullUrl + s"/hasFixedEstablishment/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", value)
      .check(status.in(200,303))
  }

  def getFixedEstablishmentTradingName(index: Int) = {
    http("Get Fixed Establishment Trading Name page")
      .get(fullUrl + s"/fixedEstablishmentTradingName/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postFixedEstablishmentTradingName(index: Int) = {
    http("Enter Fixed Establishment Trading Name")
      .post(fullUrl + s"/fixedEstablishmentTradingName/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "foo")
      .check(status.in(200,303))
  }

  def getFixedEstablishmentAddress(index: Int) = {
    http("Get Fixed Establishment Address page")
      .get(fullUrl + s"/fixedEstablishmentAddress/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postFixedEstablishmentAddress(index: Int) = {
    http("Enter Fixed Establishment Address")
      .post(fullUrl + s"/fixedEstablishmentAddress/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("line1", "line1")
      .formParam("line2", "line2")
      .formParam("townOrCity", "townOrCity")
      .formParam("postCode", "ABC")
      .check(status.in(200,303))
  }

  def getCheckEuVatDetails(index: Int) = {
    http("Get Check EU VAT Details page")
      .get(fullUrl + s"/checkEuVatDetails/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postCheckEuVatDetails(index: Int) = {
    http("Submit Check EU VAT Details")
      .post(fullUrl + s"/checkEuVatDetails/$index")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200,303))
  }

  def getAddEuVatDetails = {
    http("Get Add EU VAT Details page")
      .get(fullUrl + "/addAdditionalEuVatDetails")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postAddEuVatDetails(answer: Boolean) = {
    http("Answer Add EU VAT Details")
      .post(fullUrl + "/addAdditionalEuVatDetails")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))
  }

  def getCurrentlyRegisteredInEu =
    http("Get Currently Registered in EU page")
      .get(fullUrl + "/currentlyRegisteredInEu")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCurrentlyRegisteredInEu(answer: Boolean) = {
    http("Answer Currently Registered in EU")
      .post(fullUrl + "/currentlyRegisteredInEu")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))
  }

  def getCurrentCountryOfRegistration =
    http("Get Current Country of Registration page")
      .get(fullUrl + "/currentCountryOfRegistration")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postCurrentCountryOfRegistration(answer: String) = {
    http("Answer Currently Registered in EU")
      .post(fullUrl + "/currentCountryOfRegistration")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))
  }

  def getPreviouslyRegistered =
    http("Get Previously Registered page")
      .get(fullUrl + "/previouslyRegistered")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postPreviouslyRegistered(answer: Boolean) =
    http("Answer Previously Registered")
      .post(fullUrl + "/previouslyRegistered")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))

  def getPreviousEuCountry(index: Int) = {
    http("Get Previous EU Country page")
      .get(fullUrl + s"/previousEuCountry/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postPreviousEuCountry(index: Int, countryCode: String) =
    http("Enter Previous EU Country")
      .post(fullUrl + s"/previousEuCountry/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", countryCode)
      .check(status.in(200,303))

  def getPreviousEuVatNumber(index: Int) = {
    http("Get Previous EU VAT Number page")
      .get(fullUrl + s"/previousEuVatNumber/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postPreviousEuVatNumber(index: Int, answer: String) =
    http("Enter Previous EU VAT Number")
      .post(fullUrl + s"/previousEuVatNumber/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))

  def getAddPreviousRegistration =
    http("Get Add Previous Registration page")
      .get(fullUrl + "/addPreviousRegistration")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddPreviousRegistration(answer: Boolean) =
    http("Add Previous Registration")
      .post(fullUrl + "/addPreviousRegistration")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))

  def getStartDate =
    http("Get Start Date page")
      .get(fullUrl + "/startDate")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postStartDate =
    http("Answer start date")
      .post(fullUrl + "/startDate")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choice", "nextPeriod")
      .check(status.in(303))

  def getBusinessAddress = {
    http("Get Business Address page")
      .get(fullUrl + "/businessAddress")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postBusinessAddress = {
    http("Enter Business Address")
      .post(fullUrl + "/businessAddress")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("line1", "1 The Avenue")
      .formParam("line2", "Amazonville")
      .formParam("townOrCity", "Amazontown")
      .formParam("county", "Amazonian")
      .formParam("postCode", "AM1 1AM")
      .check(status.in(200,303))
  }
  
  def getWebsite(index: Int) =
    http(s"Get Website page $index")
      .get(fullUrl + s"/website/$index")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
      
  def postWebsite(index: Int, website: String) =
    http(s"Enter website $index")
      .post(fullUrl + s"/website/$index")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", website)
      .check(status.in(303))

  def getAddWebsite =
    http("Get Add Website page")
      .get(fullUrl + "/addWebsite")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))

  def postAddWebsite(answer: Boolean) =
    http("Add Website")
      .post(fullUrl + "/addWebsite")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.in(200,303))

  def getBusinessContactDetails = {
    http("Get Business Contact Details page")
      .get(fullUrl + "/businessContactDetails")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postBusinessContactDetails = {
    http("Enter Business Contact Details")
      .post(fullUrl + "/businessContactDetails")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("fullName", "Jane Smith")
      .formParam("telephoneNumber", "01478523691")
      .formParam("emailAddress", "jane@email.com")
      .check(status.in(200,303))
  }

  def getCheckYourAnswers = {
    http("Get Check Your Answers page")
      .get(fullUrl + "/check-your-answers")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(status.in(200))
  }

  def postCheckYourAnswers = {
    http("Post Check Your Answers page")
      .post(fullUrl + "/check-your-answers")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.in(200,303))
  }

  def getApplicationComplete = {
    http("Get Application Complete page")
      .get(fullUrl + "/applicationComplete")
      .header("Cookie", "mdtp=${mdtpCookie}")
      .check(status.in(200))
  }
}
