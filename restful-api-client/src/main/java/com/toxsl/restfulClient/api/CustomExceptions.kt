package com.toxsl.restfulClient.api

class AppInMaintenance(message: String) : Exception(message)
class AppExpiredError(date: String) : Exception("The app has expired on $date")