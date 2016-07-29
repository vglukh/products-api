package model

import scala.collection.mutable.ArrayBuffer
import play.api.db.Database
import javax.inject.Inject
import play.api.libs.json.Writes
import play.api.libs.json.Json


class ModelDAO (db: Database) {
  
  implicit val productWrites = new Writes[model.Product] {
      def writes(product: model.Product) = Json.obj(
        "productID" -> product.id,
        "productCode" -> product.productCode,
        "name" -> product.name,
        "quantity" -> product.quantity,
        "price" -> product.price
      )
   }
  
  def getProducts = {
    val products = ArrayBuffer[Product]()
    val conn = db.getConnection()
    
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(ModelDAO.allProductsQuery)
      
      while (rs.next()) {
        val product = new Product(rs.getString(ModelDAO.productIDColumn), rs.getString(ModelDAO.productCodeColumn), rs.getString(ModelDAO.nameColumn), 
              rs.getInt(ModelDAO.quantityColumn), rs.getDouble(ModelDAO.priceColumn))
        products += product
      }
    
    } finally {
      conn.close()
    }
    val productsJson = Json.toJson(products.toList)
    productsJson
  }
}

object ModelDAO {
  val allProductsQuery = "select productID, productCode, name, quantity,price from products"
  val productIDColumn = 1
  val productCodeColumn = 2
  val nameColumn = 3
  val quantityColumn = 4
  val priceColumn = 5
}