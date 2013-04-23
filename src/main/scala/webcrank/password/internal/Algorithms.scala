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

  object pbkdf2sha1 {
    val pbkdf = PBKDF2("PBKDF2WithHMACSHA1")

    def crypt(password: String, spec: PBKDF2withHMACSHA1) =
      pbkdf.crypt(password, spec.rounds, spec.saltbytes, spec.size)

    def verify(password: String, hashed: String) =
      pbkdf.verify(password, hashed)
  }

  object pbkdf2sha256 {
    val pbkdf = PBKDF2("PBKDF2WithHMACSHA256")

    def crypt(password: String, spec: PBKDF2withHMACSHA256) =
      pbkdf.crypt(password, spec.rounds, spec.saltbytes, spec.size)

    def verify(password: String, hashed: String) =
      pbkdf.verify(password, hashed)
  }


  object pbkdf2sha512 {
    val pbkdf = PBKDF2("PBKDF2WithHMACSHA512")

    def crypt(password: String, spec: PBKDF2withHMACSHA512) =
      pbkdf.crypt(password, spec.rounds, spec.saltbytes, spec.size)

    def verify(password: String, hashed: String) =
      pbkdf.verify(password, hashed)
  }
}
