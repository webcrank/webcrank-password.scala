package webcrank.password
package internal

import com.owtelse.codec.Base64

import java.security._
import javax.crypto._, javax.crypto.spec._

import scala.util.control.Exception._

/**
 * Implement PBKDF2 in terms of JCE.
 *
 * This implementation generates and verifies an MCF string of
 * the form:
 *
 *     `$algorithm$rounds$keysize$salt$key`
 *
 * This implementation requires that specified `algorithm` be
 * provided by a registered JCE provider.
 *
 * @param algorithm Underlying PBKDF algorithm name.
 */
case class PBKDF2(algorithm: String) {
  import PBKDF2._

  def crypt(password: String, rounds: Int, saltbytes: Int, size: Int) =
    pbkdf2(password, gensalt(saltbytes), algorithm, rounds, size)

  def verify(password: String, crypted: String) = crypted match {
    case MCFString(alg, AsInt(rounds) :: AsInt(size) :: salt :: _ :: Nil) =>
      pbkdf2(password, Base64.decode(salt), alg, rounds, size) == crypted
    case _ => false
  }

  def pbkdf2(password: String, salt: Array[Byte], algorithm: String, rounds: Int, size: Int) = {
    val spec = new PBEKeySpec(password.toCharArray, salt, rounds, size)
    val keys = SecretKeyFactory.getInstance(algorithm)
    val key = keys.generateSecret(spec)
    val bytes = key.getEncoded
    "$" + List(
      algorithm,
      rounds.toString,
      size.toString,
      Base64.encode(salt),
      Base64.encode(bytes)
    ).mkString("$")
  }
}

object PBKDF2 {
  def gensalt(size: Int) = {
    val random =  SecureRandom.getInstance("SHA1PRNG")
    val salt = new Array[Byte](size)
    random.nextBytes(salt)
    salt
  }

  object AsInt {
    def unapply(s: String): Option[Int] =
      catching(classOf[NumberFormatException]) opt s.toInt
  }
}
