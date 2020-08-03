package com.ww.services.impl

import com.ww.services.GreetingService

class GermanGreetingServiceImpl extends GreetingService {
  override def greet(): String = "german greeting!"
}
