package controllers

import play.api.mvc._

import scala.collection.mutable.MutableList

/**
 * Encapsulates a video
 *
 * @param name      the name of the video
 * @param url       the URL of the video
 * @param duration  the duration of the video
 */
case class Video(name: String, url: String, duration: Long)

object Application extends Controller {

  /**
   * This is the known list of videos
   */
  val videos = MutableList.empty[Video]

  /**
   * API method for getting all videos
   *
   * @return  list of videos, one per line
   */
  def getVideos = Action {
    Ok(videos.map(v => s"${v.name}: ${v.url}\n").mkString)
  }

  /**
   * API method for creating a new video
   *
   * @return  200 and a message, if successful
   *          400 and a message, if error
   */
  def createVideo = Action { request =>
    withVideo(request) { video =>
      videos += video
      Ok("Video added.")
    }
  }

  /**
   * This method extracts a video, if possible, from the request
   *
   * @param request   the HTTP request to extract data from
   * @param body      the partial function to execute with the video
   * @return          the result of the partial function,
   *                  or error if a video could not be extracted
   */
  private[this] def withVideo(request: Request[AnyContent])
                             (body: Video => Result) = {
    (request.getQueryString("name"),
     request.getQueryString("url"),
     request.getQueryString("duration")) match {

      case (Some(name), Some(url), Some(durationStr))
      if name.trim.length > 0 && url.trim.length >= 10 && durationStr.length > 0 =>
        try {
          body(Video(name, url, durationStr.toInt))
        } catch {
          case e: NumberFormatException =>
            BadRequest("Value of 'duration' parameter must be a number.")
        }

      case _ => BadRequest("Missing ['name','duration','url'].")
    }
  }
}