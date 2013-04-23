package webcrank.password
package internal

case class Crypt(crypt: String => String)

object Crypt {
  import Algorithms._

  def fromSpec(spec: PasswordSpec): Crypt = spec match {
    case p @ SCrypt(_, _, _) =>
      Crypt(scrypt.crypt(_, p))
    case p @ BCrypt(_) =>
      Crypt(bcrypt.crypt(_, p))
    case p @ PBKDF2withHMACSHA1(_, _, _) =>
      Crypt(pbkdf2sha1.crypt(_, p))
    case p @ PBKDF2withHMACSHA256(_, _, _) =>
      Crypt(pbkdf2sha256.crypt(_, p))
    case p @ PBKDF2withHMACSHA512(_, _, _) =>
      Crypt(pbkdf2sha512.crypt(_, p))
  }
}
