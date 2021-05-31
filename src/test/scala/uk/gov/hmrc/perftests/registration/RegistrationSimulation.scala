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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.registration.RegistrationRequests._

class RegistrationSimulation extends PerformanceTestRunner {

  setup("registration", "Registration Journey") withRequests(
    getIsBusinessBasedInNorthernIreland,
    postIsBusinessBasedInNorthernIreland,
    goToAuthLoginPage,
    upFrontAuthLogin,
    getCheckVatDetails,
    postCheckVatDetails,
    getRegisteredCompanyName,
    postRegisteredCompanyName,
    getHasTradingName,
    postHasTradingName,
    getTradingName(1),
    postTradingName(1, "Company 1"),
    getAddTradingName,
    postAddTradingName(true),
    getTradingName(2),
    postTradingName(2, "Company 2"),
    getAddTradingName,
    postAddTradingName(false),
    getPartOfVatGroup,
    postPartOfVatGroup,
    getUkVatEffectiveDate,
    postUkVatEffectiveDate,
    getUkVatRegisteredPostcode,
    postUkVatRegisteredPostcode,
    getIsVatRegisteredInEu,
    postIsVatRegisteredInEu,
    getVatRegisteredInEuMemberState(1),
    postVatRegisteredInEuMemberState(1, "ES"),
    getEuVatNumber(1),
    postEuVatNumber(1),
    getHasFixedEstablishment(1),
    postHasFixedEstablishment(1, true),
    getFixedEstablishmentTradingName(1),
    postFixedEstablishmentTradingName(1),
    getFixedEstablishmentAddress(1),
    postFixedEstablishmentAddress(1),
    getCheckEuVatDetails(1),
    postCheckEuVatDetails(1),
    getAddEuVatDetails,
    postAddEuVatDetails(true),
    getVatRegisteredInEuMemberState(2),
    postVatRegisteredInEuMemberState(2, "FR"),
    getEuVatNumber(2),
    postEuVatNumber(2),
    getHasFixedEstablishment(2),
    postHasFixedEstablishment(2, false),
    getCheckEuVatDetails(1),
    postCheckEuVatDetails(1),
    getAddEuVatDetails,
    postAddEuVatDetails(false),
    getCurrentlyRegisteredInEu,
    postCurrentlyRegisteredInEu(true),
    getCurrentCountryOfRegistration,
    postCurrentCountryOfRegistration("FR"),
    getStartDate,
    postStartDate,
    getBusinessAddress,
    postBusinessAddress,
    getWebsite(1),
    postWebsite(1, "www.example.com"),
    getAddWebsite,
    postAddWebsite(true),
    getWebsite(2),
    postWebsite(2, "www.anotherwebsite.com"),
    getAddWebsite,
    postAddWebsite(false),
    getBusinessContactDetails,
    postBusinessContactDetails,
    getCheckYourAnswers,
    postCheckYourAnswers,
    getApplicationComplete
  )

  runSimulation()
}
