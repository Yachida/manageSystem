package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick._
import models._

import play.api.libs.json._

object PhotoController extends Controller {

  val photoForm = Form(
    mapping(
      "ID" -> longNumber,
      "file" -> nonEmptyText(maxLength = 140),
      "left" -> number,
      "top" -> number,
      "right" -> number,
      "bottom" -> number,
      "imgwidth" -> number,
      "imgheight" -> number,
      "display" -> boolean
    )(Photo.apply)(Photo.unapply)
  )

//  // ユーザ情報を受け取るためのケースクラス
//  case class PhotoForm(ID: Long, file: String, left: Int, top: Int, right: Int, bottom: Int, imgwidth: Int, imgheight: Int, display: Boolean)
  // JSONをUserFormに変換するためのReadsを定義
  implicit val photoFormReads = Json.reads[Photo]
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val photoRowWrites = Json.writes[Photo]

  def showCreateForm() = Action { request =>
    Ok("views.html.photoCreateForm(photoForm)")
  }

  def create() = DBAction.transaction(parse.json) { implicit rs =>
    rs.body.validate[Photo].map { form =>
      // OKの場合はユーザを登録
      val photo = Photo(0, form.file, form.left, form.top, form.right, form.bottom, form.imgwidth, form.imgheight, form.display)
      PhotoDAO.create(photo)
      Ok(Json.obj("result" -> "success"))
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
    }
  }

//    DBAction { implicit rs =>
//    photoForm.bindFromRequest.fold(
//      errors => BadRequest("views.html.photoCreateForm(errors)"),
//      photo => {
//        PhotoDAO.create(photo)
//        Redirect(routes.PhotoController.search())
//      }
//    )
//  }

  def search(word: String) = DBAction { implicit rs =>
    // IDの昇順にすべてのユーザ情報を取得
    val users = PhotoDAO.search(word)

    // ユーザの一覧をJSONで返す
    Ok(Json.obj("users" -> users))
  }

  def showUpdateForm(ID: Long) = DBAction { implicit rs =>
    Ok("views.html.photoUpdateForm(ID, photoForm.fill(PhotoDAO.searchByID(ID)))")
  }

  def update(ID: Long) = DBAction { implicit rs =>
    photoForm.bindFromRequest.fold(
      errors => BadRequest("views.html.photoUpdateForm(ID, errors)"),
      photo => {
        PhotoDAO.update(photo)
        Redirect(routes.PhotoController.search())
      }
    )
  }

  def remove(ID: Long) = DBAction { implicit rs =>
    PhotoDAO.remove(PhotoDAO.searchByID(ID))
    Redirect(routes.PhotoController.search())
  }
}
