package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick._
import models._

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
  
  def showCreateForm() = Action { request =>
    Ok("views.html.photoCreateForm(photoForm)")
  }
  
  def create() = DBAction { implicit rs =>
    photoForm.bindFromRequest.fold(
      errors => BadRequest("views.html.photoCreateForm(errors)"),
      photo => {
        PhotoDAO.create(photo)
        Redirect(routes.PhotoController.search())
      }
    )
  }
  
  def search(word: String) = DBAction { implicit rs =>
    Ok("views.html.photoSearch(word, PhotoDAO.search(word))")
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
