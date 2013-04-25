package webcrank.password
package internal

object Algorithms {
  object bcrypt {
    import org.mindrot.jbcrypt.{BCrypt => B}

    def crypt(password: String, spec: BCrypt) =
      B.hashpw(password, B.gensalt(spec.cost))

    def verify(password: String, hashed: String) =
      B.checkpw(password, hashed)
  }

  object scrypt {
    import com.lambdaworks.crypto.{SCryptUtil => S}

    def crypt(password: String, spec: SCrypt) =
      S.scrypt(password, spec.n, spec.r, spec.p)

    def verify(password: String, hashed: String) =
      S.check(password, hashed)
  }

  /*
   * Generates a webcrank-password specific MCF string that is:
   *
   *   $algorithm$rounds$keysize$base64(salt)$base64(key)
   */
  object pbkdf2 {
    import com.owtelse.codec.Base64
    import MCFString._
    import JCE._

    def crypt(password: String, spec: PBKDF2) =
      pbkdf2(password, gensalt(spec.saltbytes), algorithm(spec.digest), spec.rounds, spec.size)

    def verify(password: String, crypted: String) = crypted match {
      case MCFString(alg, AsInt(rounds) :: AsInt(size) :: salt :: _ :: Nil) =>
        pbkdf2(password, Base64.decode(salt), alg, rounds, size) == crypted
      case _ => false
    }

    def pbkdf2(password: String, salt: Array[Byte], alg: String, rounds: Int, size: Int) =
      MCF(alg, List(
        rounds.toString,
        size.toString,
        Base64.encode(salt),
        Base64.encode(genkey(password, salt, rounds, size, alg))
      )).mkString

    def algorithm(digest: Digest) = digest match {
      case SHA1 => "PBKDF2WithHMACSHA1"
      case SHA256 => "PBKDF2WithHMACSHA256"
      case SHA512 => "PBKDF2WithHMACSHA512"
    }
  }
}
