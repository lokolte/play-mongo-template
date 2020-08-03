package com.ww.hooks

import javax.inject._
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future

@Singleton
class ApplicationHooks @Inject()(lifecycle: ApplicationLifecycle) {
  println("Hello!")
  // Shut-down hook
  lifecycle.addStopHook { () =>
    println("Goodbye!")
    Future.successful(())
  }
}
