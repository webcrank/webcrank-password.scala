package webcrank.password

import java.security.NoSuchAlgorithmException
import javax.crypto.SecretKeyFactory

import scala.util.control.Exception._


object PasswordsSpec extends test.Spec {
  val bcrypt = BCrypt()
  val pbkdf2sha1 = PBKDF2withHMACSHA1()
  val pbkdf2sha256 = PBKDF2withHMACSHA256()

  import Passwords._

  def symmetric(s: String, alg: PasswordAlgorithm) =
    !s.isEmpty ==> verify(s, hash(s, alg), alg)

  def supported(alg: String) =
    (catching(classOf[NoSuchAlgorithmException]) opt {
      SecretKeyFactory.getInstance(alg)
    }).isDefined


  "password" should {
    if (supported("PBKDF2WithHMACSHA256")) {
      "symmetric pbkdf2 with hmac sha256" ! prop((s: String) => symmetric(s, pbkdf2sha256))
    }

    "symmetric pbkdf2 with hmac sha1" ! prop((s: String) => symmetric(s, pbkdf2sha1))

    "symmetric bcrypt" ! prop((s: String) => symmetric(s, bcrypt))
  }
}
