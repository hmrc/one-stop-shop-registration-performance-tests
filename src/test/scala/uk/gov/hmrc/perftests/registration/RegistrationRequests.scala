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
  val route: String   = "/registeredCompanyName"
  val registeredCompanyNameUrl = baseUrl + route

  val loginUrl = baseUrlFor("auth-login-stub")

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  def goToAuthLoginPage = {
    http("Go to Auth login page")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .check(status.in(200, 303))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
  }

  def upFrontAuthLogin = {
    http("Enter Auth login credentials ")
      .get(loginUrl + s"/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("redirectionUrl", registeredCompanyNameUrl)
      .check(status.in(200, 303))
      .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))
  }

  def enterRegisteredCompanyName = {
    println("URL is" + registeredCompanyNameUrl)
    http("Enter registered company name")
      .get(registeredCompanyNameUrl)
      .formParam("csrfToken", "${csrfToken}")
//      .formParam("value", "Foo Ltd")
//      .check(status.in(200,303))
      .check(status.in(200))
  }
}
