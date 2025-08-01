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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.registration.RegistrationRequests._

class RegistrationSimulation extends PerformanceTestRunner {

  val baseUrl: String = baseUrlFor("one-stop-shop-registration-frontend")

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
    postHasTradingName(1),
    getTradingName(1),
    postTradingName(1, "Company 1"),
    getAddTradingName,
    postAddTradingName(true, Some(2)),
    getTradingName(2),
    postTradingName(2, "Company 2"),
    getAddTradingName,
    postAddTradingName(false, None),
    getAlreadyMadeSales,
    postAlreadyMadeSales,
    getDateOfFirstSale,
    postDateOfFirstSale,
    getPreviousOss,
    postPreviousOss(1),
    getPreviousCountry(1),
    postPreviousCountry(1, 1, "BE"),
    getPreviousScheme(1, 1),
    postPreviousScheme(1, 1, "oss"),
    getPreviousOssSchemeNumber(1, 1),
    postPreviousOssSchemeNumber(1, 1, "BE0111222333"),
    getPreviousSchemeAnswers(1),
    postPreviousSchemeAnswers(1, false),
    getPreviousSchemesOverview,
    postPreviousSchemesOverview(true, Some(2)),
    getPreviousCountry(2),
    postPreviousCountry(2, 1, "FI"),
    getPreviousScheme(2, 1),
    postPreviousScheme(2, 1, "oss"),
    getPreviousOssSchemeNumber(2, 1),
    postPreviousOssSchemeNumber(2, 1, "FI11223344"),
    getPreviousSchemeAnswers(2),
    postPreviousSchemeAnswers(2, false),
    getPreviousSchemesOverview,
    postPreviousSchemesOverview(true, Some(3)),
    getPreviousCountry(3),
    postPreviousCountry(3, 1, "LU"),
    getPreviousScheme(3, 1),
    postPreviousScheme(3, 1, "ioss"),
    getPreviousIossScheme(3, 1),
    postPreviousIossScheme(3, 1, true),
    getPreviousIossNumber(3, 1),
    postPreviousIossNumber(3, 1, "IM4421234567"),
    getPreviousSchemeAnswers(3),
    postPreviousSchemeAnswers(3, false),
    getPreviousSchemesOverview,
    postPreviousSchemesOverview(false, None),
    getStartDate,
    postStartDate,
    getIsTaxRegisteredInEu,
    postIsTaxRegisteredInEu(1),
    getVatRegisteredInEuMemberState(1),
    postVatRegisteredInEuMemberState(1, "EL"),
    getSellGoodsToEuConsumers(1),
    postSellGoodsToEuConsumers(1),
    getSellGoodsToEuConsumersMethod(1),
    postSellGoodsToEuConsumersMethod(1, "fixedEstablishment"),
    getRegistrationType(1),
    postRegistrationType(1, "vatNumber"),
    getEuVatNumber(1),
    postEuVatNumber(1, "EL987654321"),
    getFixedEuTradingName(1),
    postFixedEuTradingName(1, "Grecian Goods"),
    getFixedEstablishmentAddress(1),
    postFixedEstablishmentAddress(1),
    getCheckTaxDetails(1),
    postCheckTaxDetails(1),
    getAddTaxDetails,
    postAddTaxDetails(true, Some(2)),
    getVatRegisteredInEuMemberState(2),
    postVatRegisteredInEuMemberState(2, "MT"),
    getSellGoodsToEuConsumers(2),
    postSellGoodsToEuConsumers(2),
    getSellGoodsToEuConsumersMethod(2),
    postSellGoodsToEuConsumersMethod(2, "dispatchWarehouse"),
    getRegistrationType(2),
    postRegistrationType(2, "taxId"),
    getEuTaxReference(2),
    postEuTaxReference(2, "MT12345678"),
    getSendGoodsTradingName(2),
    postSendGoodsTradingName(2, "Maltese Trading"),
    getSendGoodsAddress(2),
    postSendGoodsAddress(2),
    getCheckTaxDetails(2),
    postCheckTaxDetails(2),
    getAddTaxDetails,
    postAddTaxDetails(false, None),
    getIsOnlineMarketplace,
    postIsOnlineMarketplace,
    getHasWebsite,
    postHasWebsite(true, 1),
    getWebsite(1),
    postWebsite(1, "www.example.com"),
    getAddWebsite,
    postAddWebsite(true, Some(2)),
    getWebsite(2),
    postWebsite(2, "www.anotherwebsite.com"),
    getAddWebsite,
    postAddWebsite(false, None),
    getBusinessContactDetails,
    postBusinessContactDetails,
    getBankDetails,
    postBankDetails,
    getCheckYourAnswers,
    postCheckYourAnswers,
    getApplicationComplete
  )

  setup("amendRegistration", "Amend Registration Journey") withRequests
    (
      goToAuthLoginPage,
      upFrontAuthLoginWithOssEnrolment,
      getAmendJourney,
      getAmendAddTradingName,
      postAmendAddTradingName(true, Some(3)),
      getAmendTradingName(3),
      postAmendTradingName(3, "3rd trading name amend"),
      getAmendAddTradingName,
      postAmendAddTradingName(false, None),
      getAmendHasWebsite,
      postAmendHasWebsite,
      getAmendRemoveAllWebsites,
      postAmendRemoveAllWebsites,
      getChangeYourRegistration,
      postChangeYourRegistration,
      getSuccessfulAmend
    )

  setup("rejoinRegistration", "Rejoin Registration Journey") withRequests
    (
      goToAuthLoginPage,
      upFrontAuthLoginWithOssEnrolmentForRejoin,
      getRejoinJourney,
      getRejoinAlreadyMadeSales,
      postRejoinAlreadyMadeSales,
      getRejoinDateOfFirstSale,
      postRejoinDateOfFirstSale,
      getRejoinStartDate,
      postRejoinStartDate,
      getRejoinRegistration,
      getRejoinAddTradingName,
      postRejoinAddTradingName(true, Some(3)),
      getRejoinTradingName(3),
      postRejoinTradingName(3, "3rd rejoin trading name"),
      getRejoinAddTradingName,
      postRejoinAddTradingName(false, None),
      getRejoinRegistration,
      postRejoinRegistration,
      getSuccessfulRejoin
    )

  runSimulation()
}
