package webcrank.password

case class MCF(identifier: String, content: List[String]) {
  def mkString =
    "$" + (identifier :: content).mkString("$")
}

object MCFString {
  def unapply(crypted: String): Option[(String, List[String])] =
    crypted.split("\\$").toList match {
      case "" :: identifier :: content => Some((identifier, content))
      case _ => None
    }

  object AsInt {
    import scala.util.control.Exception._
    def unapply(s: String): Option[Int] =
      catching(classOf[NumberFormatException]) opt s.toInt
  }
}
