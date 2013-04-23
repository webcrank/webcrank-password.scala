package webcrank.password

import scalaz.effect.IO

trait PasswordOps {
  def hash(password: String, algorithm: PasswordAlgorithm): String =
    iohash(password, algorithm).unsafePerformIO

  def iohash(password: String, algorithm: PasswordAlgorithm): IO[String] =
    algorithm.gensalt map (salt => algorithm.hash(password, salt))

  def verify(password: String, hashed: String, algorithm: PasswordAlgorithm): Boolean =
    algorithm.verify(password, hashed)

  // FIX implement
  def migrate(password: String, hashed: String, algorithm: PasswordAlgorithm) =
    sys.error("???")
}

object Passwords extends PasswordOps
