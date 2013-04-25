package webcrank.password
package internal

case class Verify(verify: String => Boolean)

object Verify {
  import Algorithms._

  def fromMCF(crypted: String): Verify = crypted match {
    case MCFString("PBKDF2WithHMACSHA1", _) |
         MCFString("PBKDF2WithHMACSHA256", _) |
         MCFString("PBKDF2WithHMACSHA512", _)  =>
      Verify(pbkdf2.verify(_, crypted))
    case MCFString("s0", _) =>
      Verify(scrypt.verify(_, crypted))
    case MCFString(identifier, _) if identifier == "2" || identifier == "2a" =>
      Verify(bcrypt.verify(_, crypted))
  }
}
