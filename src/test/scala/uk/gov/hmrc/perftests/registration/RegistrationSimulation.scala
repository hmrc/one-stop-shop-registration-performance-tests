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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.registration.RegistrationRequests._
import utility.Client.clearAll

class RegistrationSimulation extends PerformanceTestRunner {

  val baseUrl: String = baseUrlFor("one-stop-shop-registration-frontend")

  before {
    println("Clearing the performance tests registrations from the database")
    clearAll(s"$baseUrl/pay-vat-on-goods-sold-to-eu/northern-ireland-register/test-only/delete-accounts")
  }

  setup("registration", "Registration Journey") withRequests (
    getAlreadyRegistered,
    postAlreadyRegistered,
    getSellsGoodsFromNi,
    postSellsGoodsFromNi,
    getBusinessBasedInNi,
    postBusinessBasedInNi,
    getBusinessPay,
    goToAuthLoginPage,
    upFrontAuthLogin,
    resumeJourney,
    getCheckVatDetails,
    postCheckVatDetails,
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
    getAlreadyMadeSales,
    postAlreadyMadeSales,
    getDateOfFirstSale,
    postDateOfFirstSale,
    getStartDate,
    postStartDate,
    getIsTaxRegisteredInEu,
    postIsTaxRegisteredInEu,
    getVatRegisteredInEuMemberState(1),
    postVatRegisteredInEuMemberState(1, "ES"),
    getVatRegistered(1),
    postVatRegistered(1, true),
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
    getVatRegistered(2),
    postVatRegistered(2, false),
    getEuTaxReference(2),
    postEuTaxReference(2),
    getHasFixedEstablishment(2),
    postHasFixedEstablishment(2, false),
    getCheckEuVatDetails(2),
    postCheckEuVatDetails(2),
    getAddEuVatDetails,
    postAddEuVatDetails(false),
    getPreviouslyRegistered,
    postPreviouslyRegistered(true),
    getPreviousEuCountry(1),
    postPreviousEuCountry(1, "BE"),
    getPreviousEuVatNumber(1),
    postPreviousEuVatNumber(1, "123"),
    getAddPreviousRegistration,
    postAddPreviousRegistration(false),
    getIsOnlineMarketplace,
    postIsOnlineMarketplace,
    getHasWebsite,
    postHasWebsite(true),
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
    getBankDetails,
    postBankDetails,
    getCheckYourAnswers,
    postCheckYourAnswers,
    getApplicationComplete
  )

  runSimulation()
}
