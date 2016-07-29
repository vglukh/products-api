package controllers

import javax.inject.Inject
import play.api._
import play.api.mvc._
import akka.util.ByteString
import play.api.http.HttpEntity
import scala.collection.mutable.ArrayBuffer
import model.Product
import play.api.libs.json.Json
import play.api.libs.json.Writes
import model.ModelDAO
import play.api.db.Database
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json.JsValue

class Application @Inject()(db: Database) extends Controller {
  
  def getProductList = Action.async {
    val dao = new ModelDAO(db)
    val productsAsFuture = scala.concurrent.Future{ dao.getProducts }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 10.second)
    
    scala.concurrent.Future.firstCompletedOf(Seq(productsAsFuture, timeoutFuture)).map {
      case productsAsFuture: JsValue => Ok("Got result: " + productsAsFuture)
      case t: String => InternalServerError(t)
    }

  }
  
  def getProduct(productid: String) = Action {
    Result(
        header = ResponseHeader(200, Map.empty),
        body = HttpEntity.Strict(ByteString("Information about product, id = " + productid), Some("text/plain"))
    )
  }

}