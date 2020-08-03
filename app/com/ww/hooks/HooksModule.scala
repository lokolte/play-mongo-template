package com.ww.hooks

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.ww.services.GreetingService
import com.ww.services.impl.{EnglishGreetingServiceImpl, GermanGreetingServiceImpl}

class HooksModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApplicationHooks]).asEagerSingleton()

    bind(classOf[GreetingService])
      .annotatedWith(Names.named("en"))
      .to(classOf[EnglishGreetingServiceImpl])
      .asEagerSingleton()

    bind(classOf[GreetingService])
      .annotatedWith(Names.named("de"))
      .to(classOf[GermanGreetingServiceImpl])
      .asEagerSingleton()
  }
}
