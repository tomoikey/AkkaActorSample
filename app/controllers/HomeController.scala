package controllers

import model.ClientObject

import javax.inject._
import play.api._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the application's home page.
  */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be called when the application receives a `GET`
    * request with a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def show(number: Long) = Action {
    // Eitherはパターンマッチで以下のようにRightとLeftの場合で処理できる
    ClientObject.sendNumberToRemote(number) match {
      case Right(s) => Ok(views.html.show(s"Show => $number * 2 = $s")) // 成功時
      case Left(e)  => Ok(views.html.show(s"Error!!! : $e")) // 失敗時
    }
  }
}
