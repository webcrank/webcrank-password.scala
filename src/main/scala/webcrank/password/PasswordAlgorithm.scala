package webcrank.password

import scalaz.effect.IO

// https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet

sealed trait PasswordAlgorithm {
  def gensalt: IO[String]
  def hash(password: String, salt: String): String
  def verify(password: String, hashed: String): Boolean
}

case class PBKDF2withHMACSHA1(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 160) extends PasswordAlgorithm {
  import internal.PBKDF2

  def gensalt =
    PBKDF2.gensalt(saltbytes)

  def hash(password: String, salt: String) =
    PBKDF2.hash(password, salt, "PBKDF2WithHMACSHA1", rounds, size)

  def verify(password: String, hashed: String) =
    PBKDF2.verify(password, hashed)
}

case class PBKDF2withHMACSHA256(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 256) extends PasswordAlgorithm {
  import internal.PBKDF2

  def gensalt =
    PBKDF2.gensalt(saltbytes)

  def hash(password: String, salt: String) =
    PBKDF2.hash(password, salt, "PBKDF2WithHMACSHA256", rounds, size)

  def verify(password: String, hashed: String) =
    PBKDF2.verify(password, hashed)
}

case class BCrypt(cost: Int = 14) extends PasswordAlgorithm {
  import org.mindrot.jbcrypt.{BCrypt => JBCrypt}

  def gensalt =
    IO { JBCrypt.gensalt(cost) }

  def hash(password: String, salt: String) =
    JBCrypt.hashpw(password, salt)

  def verify(password: String, hashed: String) =
    JBCrypt.checkpw(password, hashed)
}
