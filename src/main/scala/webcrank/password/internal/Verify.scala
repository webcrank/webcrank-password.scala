package webcrank.password
package internal

case class Verify(verify: String => Boolean)

object Verify {
  import Algorithms._

  def fromMCF(crypted: String): Verify = crypted match {
    case MCF("PBKDF2WithHMACSHA1", _) =>
      Verify(pbkdf2sha1.verify(_, crypted))
    case MCF("PBKDF2WithHMACSHA256", _) =>
      Verify(pbkdf2sha256.verify(_, crypted))
    case MCF("PBKDF2WithHMACSHA512", _) =>
      Verify(pbkdf2sha512.verify(_, crypted))
    case MCF("s0", _) =>
      Verify(scrypt.verify(_, crypted))
    case MCF(identifier, _) if identifier == "2" || identifier == "2a" =>
      Verify(bcrypt.verify(_, crypted))
  }
}
