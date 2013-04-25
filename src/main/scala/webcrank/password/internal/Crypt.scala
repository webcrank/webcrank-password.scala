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
    case p @ PBKDF2(_, _, _, _) =>
      Crypt(pbkdf2.crypt(_, p))
  }
}
