package webcrank.password

case class MCF(identifier: String, content: List[String])

object MCFString {
  def unapply(crypted: String): Option[(String, List[String])] =
    crypted.split("\\$").toList match {
      case "" :: identifier :: content => Some((identifier, content))
      case _ => None
    }
}
