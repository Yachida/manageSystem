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

  // JSONをUserFormに変換するためのReadsを定義
  implicit val photoFormReads = Json.reads[Photo]
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val photoRowWrites = Json.writes[Photo]

  def search(word: String) = DBAction { implicit rs =>
    // IDの昇順にすべてのユーザ情報を取得
    val users = PhotoDAO.search(word)
    // ユーザの一覧をJSONで返す
    Ok(Json.obj("users" -> users))
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

  def update() = DBAction.transaction(parse.json) { implicit rs =>
    rs.body.validate[Photo].map { form =>
      // OKの場合はユーザを登録
      val photo = Photo(form.ID, form.file, form.left, form.top, form.right, form.bottom, form.imgwidth, form.imgheight, form.display)
      PhotoDAO.update(photo)
      Ok(Json.obj("result" -> "success"))
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
    }
  }

  def remove(ID: Long) = DBAction.transaction { implicit rs =>
    // ユーザを削除
    PhotoDAO.remove(PhotoDAO.searchByID(ID))
    Ok(Json.obj("result" -> "success"))
  }

  def showCreateForm() = Action { request =>
    Ok("views.html.photoCreateForm(photoForm)")
  }

  def showUpdateForm(ID: Long) = DBAction { implicit rs =>
    Ok("views.html.photoUpdateForm(ID, photoForm.fill(PhotoDAO.searchByID(ID)))")
  }
}
