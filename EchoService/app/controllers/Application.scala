package controllers

import play.api.mvc._

object Application extends Controller {

  def echo = Action { request =>
    request.getQueryString("msg") match {
      case Some(message) => Ok(s"Echo: $message")
      case None => BadRequest("Missing 'msg' query parameter.")
    }
  }
}