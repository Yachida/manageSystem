package models

import play.api.db.slick.Config.driver.simple._

/**
 * DTO の定義
 */
case class Photo(ID: Long, file: String, left: Int, top: Int, right: Int, bottom: Int, imgwidth: Int, imgheight: Int, display: Boolean)

/**
 * テーブルの定義
 */
class PhotoTable(tag: Tag) extends Table[Photo](tag, "photos") {
  def ID = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def file = column[String]("file", O.NotNull)
  def left = column[Int]("left", O.NotNull)
  def top = column[Int]("top", O.NotNull)
  def right = column[Int]("right", O.NotNull)
  def bottom = column[Int]("bottom", O.NotNull)
  def imgwidth = column[Int]("imgwidth", O.NotNull)
  def imgheight = column[Int]("imgheight", O.NotNull)
  def display = column[Boolean]("display", O.NotNull)
  def * = (ID, file, left, top, right, bottom, imgwidth, imgheight, display) <> (Photo.tupled, Photo.unapply)
}

/**
 * DAO の定義
 */
object PhotoDAO {
  lazy val photoQuery = TableQuery[PhotoTable]
  
  /**
   * キーワード検索
   * @param word
   */
  def search(word: String)(implicit s: Session): List[Photo] = {
    photoQuery.filter(row => (row.file like "%"+word+"%")).list
  }
  
  /**
   * ID検索
   * @param ID
   */
  def searchByID(ID: Long)(implicit s: Session): Photo = {
    photoQuery.filter(_.ID === ID).first
  }
  
  /**
   * 作成
   * @param photo
   */
  def create(photo: Photo)(implicit s: Session) {
    photoQuery.insert(photo)
  }
  
  /**
   * 更新
   * @param photo
   */
  def update(photo: Photo)(implicit s: Session) {
    photoQuery.filter(_.ID === photo.ID).update(photo)
  }
  
  /**
   * 削除
   * @param photo
   */
  def remove(photo: Photo)(implicit s: Session) {
    photoQuery.filter(_.ID === photo.ID).delete
  }
}