package com.ww.services.impl

import com.ww.services.GreetingService

class EnglishGreetingServiceImpl extends GreetingService {
  override def greet(): String = "english greeting!"
}
