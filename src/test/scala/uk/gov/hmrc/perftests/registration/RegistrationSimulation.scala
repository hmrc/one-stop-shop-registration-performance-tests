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
    goToAuthLoginPage,
    upFrontAuthLogin,
    getRegisteredCompanyName,
    postRegisteredCompanyName,
    getHasTradingName,
    postHasTradingName,
    getTradingName,
    postTradingName,
    getPartOfVatGroup,
    postPartOfVatGroup,
    getUkVatNumber,
    postUkVatNumber,
    getUkVatEffectiveDate,
    postUkVatEffectiveDate,
    getUkVatRegisteredPostcode,
    postUkVatRegisteredPostcode,
    getIsVatRegisteredInEu,
    postIsVatRegisteredInEu,
    getVatRegisteredInEuMemberState,
    postVatRegisteredInEuMemberState,
    getEuVatNumber,
    postEuVatNumber,
    getAddAdditionalEuVatDetails,
    postAddAdditionalEuVatDetails,
    getBusinessAddress,
    postBusinessAddress,
    getBusinessContactDetails,
    postBusinessContactDetails,
    getCheckYourAnswers,
    postCheckYourAnswers,
    getApplicationComplete)

  runSimulation()
}
