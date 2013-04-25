package webcrank.password

import java.security.NoSuchAlgorithmException
import javax.crypto.SecretKeyFactory

import scala.util.control.Exception._

object PasswordsSpec extends test.Spec {
  // WARNING: This parameters are chosen for test performance not security.
  //                    -- DO NOT COPY --
  def scrypt = Passwords.scrypt(1024)
  def bcrypt = Passwords.bcrypt(8)
  def pbkdf2sha1 = Passwords.pbkdf2(rounds = 1024, digest = SHA1)
  def pbkdf2sha256 = Passwords.pbkdf2(rounds = 1024, digest = SHA256)
  def pbkdf2sha512 = Passwords.pbkdf2(rounds = 1024, digest = SHA512)

  def symmetric(passwords: Passwords, s: String) =
    !s.isEmpty ==> passwords.verify(s, passwords.crypt(s))

  def supported(alg: String) =
    (catching(classOf[NoSuchAlgorithmException]) opt {
      SecretKeyFactory.getInstance(alg)
    }).isDefined


  "password" should {
    if (supported("PBKDF2WithHMACSHA256"))
      "symmetric pbkdf2 with hmac sha256" ! prop((s: String) => symmetric(pbkdf2sha256, s))

    if (supported("PBKDF2WithHMACSHA512"))
      "symmetric pbkdf2 with hmac sha512" ! prop((s: String) => symmetric(pbkdf2sha512, s))

    "symmetric pbkdf2 with hmac sha1" ! prop((s: String) => symmetric(pbkdf2sha1, s))

    "symmetric bcrypt" ! prop((s: String) => symmetric(bcrypt, s))

    "symmetric scrypt" ! prop((s: String) => symmetric(scrypt, s))
  }
}
