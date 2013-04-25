package webcrank.password

/**
 * Standard interface for dealing with self-contained key-deriviation functions.
 *
 * Passwords will crypt based upon the provided `spec`, but verify based on
 * arguments included in MCF style string. This means it is safe to simply
 * change the `spec` and new passwords will meet the new security requirements
 * whilst old passwords can still be verified.
 */
case class Passwords(spec: PasswordSpec) {
  import internal._

  /**
   * Derive key from password. Implementations will use any cost parameters
   * a salt generated from a cryptographically secure PRNG, and the
   * password to derive the key. The String returned will be an MCF style
   * string specifying the algorithm, any parameters, the salt and the
   * derived key.
   */
  def crypt(password: String): String =
    Crypt.fromSpec(spec).crypt(password)

  /**
   * Check a password against an MCF style crypted value. This function
   * will use the parameters and salt from the hashed string to compute
   * the hash for the password and check it against the password.
   *
   * Note: The crypted string does not have to match the parameters
   * of this `Password#spec`. It may be any supported algorithm. This
   * allows for password parameters to be modified over time.
   */
  def verify(password: String, crypted: String): Boolean =
    Verify.fromMCF(crypted).verify(password)
}

/**
 * Build passwords structure using default (overridable) algorithm parameters.
 *
 * Each algorithm has sensible defaults, but consider careful research
 * and potentailly doing computation tests on your hardware to make
 * the most appropriate choice (read as: set as high as you can before
 * the performance becomes crippling).
 *
 * In terms of algorithms selection there are a few factors that should be
 * considered:
 *  - trust of underlying implementation
 *  - work factors of algorithm
 *  - validation requirements
 *
 * Trust of underlying implementations is difficult, but all implementations
 * are open source and can be audited:
 *  - [[http://www.mindrot.org/projects/jBCrypt/ bcrypt implementation]]
 *  - [[https://github.com/wg/scrypt scrypt implementation]]
 *  - [[http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/f4c62eecf7fa/src/share/classes/com/sun/crypto/provider/ openjdk pbkdf2 implementations]]
 *
 * Work factors of underlying algorithm is also difficult to classify because
 * of the number of variables invloved (CPU, Memory, etc...), but a generalized
 * assesment would be:
 *  1) scrypt
 *  2) bcrypt
 *  3) pbkdf2
 *
 * Validation requirements are easier. You will know if you have them.
 * PBKDF2 is the only NIST approved algorithm.
 */
object Passwords {
  /**
   * Use SCrypt for derivation.
   *
   * Default parameters: N = 16384 (2 ^ 14). r = 8, p = 1
   *
   * Default parameters taken from Colin Pervival's original
   * recommendations for interactive password storage. Even
   * though this recommendation is old (2009), these defaults
   * are reasonable on modern hardware.
   */
  def scrypt(n: Int = 16384, r: Int = 8, p: Int = 1) =
    Passwords(SCrypt(n, r, p))

  /**
   * Use BCrypt for derivation.
   *
   * Default parameters: cost = 14.
   *
   * Note that work factor increases at 2^cost.
   */
  def bcrypt(cost: Int = 14) =
    Passwords(BCrypt(cost))

  /**
   * Use PBKDF2 with HMAC-SHA1 for derivation.
   *
   * Default parameters: rounds = 65536 (2 ^ 16), salt length: 16 bytes, key size: 256 bits.
   *
   * Note: SHA2 based implementations would normally be preferred, but they
   * were not included in the standard library before JDK8.
   */
  def pbkdf2sha1(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 256) =
    Passwords(PBKDF2withHMACSHA1(rounds, saltbytes, size))

  /**
   * Use PBKDF2 with HMAC-SHA256 for derivation.
   *
   * Default parameters: rounds = 65536 (2 ^ 16), salt length: 16 bytes, key size: 256 bits.
   *
   * Note: This can only be used with JDK8+ or with another JCE provider that
   * supports this algorithm such as BSAFE or IAIK.
   */
  def pbkdf2sha256(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 256) =
    Passwords(PBKDF2withHMACSHA256(rounds, saltbytes, size))

  /**
   * Use PBKDF2 with HMAC-SHA512 for derivation.
   *
   * Default parameters: rounds = 65536 (2 ^ 16), salt length: 16 bytes, key size: 512 bits.
   *
   * Note: This can only be used with JDK8+ or with another JCE provider that
   * supports this algorithm such as BSAFE or IAIK.
   */
  def pbkdf2sha512(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 512) =
    Passwords(PBKDF2withHMACSHA512(rounds, saltbytes, size))
}
