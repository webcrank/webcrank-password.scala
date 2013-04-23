package webcrank.password
package internal

import com.owtelse.codec.Base64

import java.security._
import javax.crypto._, javax.crypto.spec._

import scala.util.control.Exception._
import scalaz.effect.IO


private[webcrank] object PBKDF2 {
  lazy val random = new SecureRandom

  def gensalt(size: Int) = IO {
    val salt = new Array[Byte](size)
    random.nextBytes(salt)
    Base64.encode(salt)
  }

  def hash(password: String, salt: String, algorithm: String, rounds: Int, size: Int) = {
    val spec = new PBEKeySpec(password.toCharArray, salt.getBytes("UTF-8"), rounds, size)
    val keys = SecretKeyFactory.getInstance(algorithm)
    val key = keys.generateSecret(spec)
    val bytes = key.getEncoded
    List(
      algorithm,
      rounds.toString,
      size.toString,
      salt,
      Base64.encode(bytes)
    ).mkString("$")
  }

  def verify(password: String, hashed: String) = {
    def toInt(s: String): Option[Int] =
      catching(classOf[NumberFormatException]) opt s.toInt

    def compute(salt: String, algorithm: String, rounds: String, size: String) =
      for {
        r <- toInt(rounds)
        s <- toInt(size)
      } yield hash(password, salt, algorithm, r, s)

    hashed.split("[$]").toList match {
      case alg :: rounds :: size :: salt :: _ :: Nil =>
        compute(salt, alg, rounds, size) map (_ == hashed) getOrElse false
      case _ => false
    }
  }
}
