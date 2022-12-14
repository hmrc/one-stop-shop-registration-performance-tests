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
    getIsTaxRegisteredInEu1,
    postIsTaxRegisteredInEu1,
    getPreviousOss ,
    postPreviousOss,
    getVatRegisteredInPreviousEuMemberState(1),
    postVatRegisteredInPreviousEuMemberState(1, "ES"),
    getPreviousScheme,
    postPreviousScheme,
    getPreviousOssSchemeNumber(1),
    postPreviousOssSchemeNumber(1),
    getPreviousSchemeAnswers,
    postPreviousSchemeAnswers,
    getAddDeRegistration,
    postAddDeRegistration(true),
    getPreviousCountry(2),
    postPreviousCountry(2, "FR"),
    getPreviousOss,
    postPreviousOss,
    getPreviousOssSchemeNumber(2),
    postPreviousOssSchemeNumber(2),
    getPreviousSchemeAnswers,
    postPreviousSchemeAnswers,
    getAddDeRegistration,
    postAddDeRegistration(true),
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
