package webcrank.password

import java.security.NoSuchAlgorithmException
import javax.crypto.SecretKeyFactory

import scala.util.control.Exception._

object PasswordsSpec extends test.Spec {
  // WARNING: These parameters are chosen for test performance not security.
  //                    -- DO NOT COPY --
  def scrypt = Passwords.scrypt(1024)
  def bcrypt = Passwords.bcrypt(8)
  def pbkdf2sha1 = Passwords.pbkdf2(rounds = 1024, digest = SHA1)
  def pbkdf2sha256 = Passwords.pbkdf2(rounds = 1024, digest = SHA256)
  def pbkdf2sha512 = Passwords.pbkdf2(rounds = 1024, digest = SHA512)

  "bcrypt" should satisfy(bcrypt, "2a")

  "acrypt" should satisfy(scrypt, "s0")

  "pbkdf2-hmac-sha1" should satisfy(pbkdf2sha1, "PBKDF2WithHMACSHA1")

  if (supported("PBKDF2WithHMACSHA256"))
    "pbkdf2-hmac-sha256" should satisfy(pbkdf2sha256, "PBKDF2WithHMACSHA256")

  if (supported("PBKDF2WithHMACSHA612"))
    "pbkdf2-hmac-sha512" should satisfy(pbkdf2sha512, "PBKDF2WithHMACSHA512")

  def satisfy(passwords: Passwords, identifier: String) = {
    "be symmetric" ! prop((s: String) =>
      !s.isEmpty ==> passwords.verify(s, passwords.crypt(s)))

    "be identified in mcf" ! prop((s: String) =>
      !s.isEmpty ==> passwords.crypt(s).startsWith("$" + identifier + "$"))

    "verify scrypt" ! prop((s: String) =>
      !s.isEmpty ==> passwords.verify(s, scrypt.crypt(s)))

    "verify bcrypt" ! prop((s: String) =>
      !s.isEmpty ==> passwords.verify(s, bcrypt.crypt(s)))

    "verify pbkdf2-hmac-sha1" ! prop((s: String) =>
      !s.isEmpty ==> passwords.verify(s, pbkdf2sha1.crypt(s)))

    if (supported("PBKDF2WithHMACSHA256"))
      "verify pbkdf2-hmac-sha256" ! prop((s: String) =>
        !s.isEmpty ==> passwords.verify(s, pbkdf2sha256.crypt(s)))

    if (supported("PBKDF2WithHMACSHA512"))
      "verify pbkdf2-hmac-sha512" ! prop((s: String) =>
        !s.isEmpty ==> passwords.verify(s, pbkdf2sha512.crypt(s)))

    "unique" ! prop((s: String) =>
      !s.isEmpty ==> (passwords.crypt(s) != passwords.crypt(s)))
  }

  def supported(alg: String) =
    (catching(classOf[NoSuchAlgorithmException]) opt {
      SecretKeyFactory.getInstance(alg)
    }).isDefined
}
