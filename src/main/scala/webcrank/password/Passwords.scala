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
 *  - availability of algorithm
 *  - trust of underlying implementation
 *  - work factors of algorithm
 *  - validation requirements
 *
 * '''Availability of algorithm'''
 *
 * Of the provided algorithms scrypt, bcrypt and PBKDF2 with HMAC-SHA1 are
 * always available. PBKDF2 with HMAC-SHA256 or HMAC-SHA512 are only available
 * on JDK8 or with a custom JCE provider such as IAIK or BSAFE.
 *
 * The fact that PBKDF2 is stuck with HMAC-SHA1 by default potentially makes
 * it less desirable.
 *
 *
 * '''Trust of underlying implementaion'''
 *
 *
 * Trust of underlying implementations is difficult, but all implementations
 * are open source and can be audited:
 *  - [[http://www.mindrot.org/projects/jBCrypt/ bcrypt implementation]]
 *  - [[https://github.com/wg/scrypt scrypt implementation]]
 *  - [[http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/f4c62eecf7fa/src/share/classes/com/sun/crypto/provider/ openjdk pbkdf2 implementations]]
 *
 * There is some ongoing effort to complete a review of the scrypt and bcrypt
 * implementations.
 *
 * '''Work factors'''
 *
 * Work factors of underlying algorithm is also difficult to classify because
 * of the number of variables invloved (CPU, Memory, etc...).
 *
 * Reading is possibly the only solution, but it is worth noting that scrypt
 * was specifically designed to be more difficult to run with modern computing
 * constraints (or lack there of) on FGPAs and alike. Scrypt is build on top
 * of PBKDF2 with HMAC-SHA256. The [scrypt paper](http://www.tarsnap.com/scrypt/scrypt.pdf)
 * has some insight into this.
 *
 * In terms of choosing appropriate factors, measurement is often the best
 * approach. As a general rule you would want to tune the algorithm so password generation takes ~100ms.
 * There are some factors listed in the
 * [[https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet OWASP password cheatsheet]]
 * which is a pretty good source of information
 *
 * '''Validation requirements'''
 *
 * Validation requirements are easier. You will know if you have them.
 * PBKDF2 is the only NIST approved algorithm.
 */
object Passwords {
  /**
   * Use [[http://www.tarsnap.com/scrypt/scrypt.pdf SCrypt]] for derivation.
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
   * Use [[http://static.usenix.org/events/usenix99/provos/provos_html/node1.html BCrypt]] for derivation.
   *
   * Default parameters: cost = 12.
   *
   * Note that work factor for bcrypt increases at 2^{cost}.
   */
  def bcrypt(cost: Int = 12) =
    Passwords(BCrypt(cost))

  /**
   * Use [[http://www.ietf.org/rfc/rfc2898.txt PBKDF2]] for derivation.
   *
   * Default parameters: rounds = 65536 (2 ^ 16), salt length: 16 bytes, key size: 256 bits, digest = SHA1.
   *
   * Note: That SHA256 and SHA512 can only be used with JDK8+ or with a custom JCE
   * provider that supports this algorithm such as BSAFE or IAIK. Excepting this
   * limitations SHA2 algorithms would normally be preferred.
   */
  def pbkdf2(rounds: Int = 65536, saltbytes: Int = 16, size: Int = 256, digest: Digest = SHA1) =
    Passwords(PBKDF2(rounds, saltbytes, size, digest))
}
