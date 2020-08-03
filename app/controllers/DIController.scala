package controllers

import com.ww.services.GreetingService
import javax.inject.{Inject, Named}
import play.api.mvc._

class DIController @Inject() (cc: ControllerComponents,
                              @Named("en") englishGreeting: GreetingService,
                              @Named("de") germanGreeting: GreetingService)
    extends AbstractController(cc) {

  def greet(lang: String) = Action {
    val greeting =
      if (lang == "en")
        englishGreeting.greet()
      else if (lang == "de")
        germanGreeting.greet()
      else
        "international greeting"

    Ok(greeting)
  }
}
