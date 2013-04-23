package webcrank.password
/**
 * Define password algorithm and parameters.
 *
 * This library shall provide the tools required to switch
 * between algorithms and change parameters on existing
 * password databases.
 *
 * Unless a serious security issue occurs with one of the
 * follow _all_ algorithms will remain available.
 *
 * There is the potential that algorithms may be _added_
 * in the future as required.
 *
 * For guidance consider using
 * [[https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet OWASP password cheat sheet.]]
 */
sealed trait PasswordSpec

/**
 * BCrypt key derivation algorithm defined by
 * [[http://static.usenix.org/events/usenix99/provos/provos_html/node1.html Provos and Mazieres].
 */
case class BCrypt(cost: Int = 14)
  extends PasswordSpec

/**
 * SCrypt key derivation algorithm defined by Percival in
 * [[http://tools.ietf.org/html/draft-josefsson-scrypt-kdf-01 draft-josefsson-scrypt-kdf-01]].
 */
case class SCrypt(n: Int = 32768, r: Int = 8, p: Int = 1)
  extends PasswordSpec

/**
 * PBKDF #2 algorithm defined in [[http://www.ietf.org/rfc/rfc2898.txt rfc2892]]
 * utilizing an HMAC SHA1 as the underlying auth.
 */
case class PBKDF2withHMACSHA1(rounds: Int, saltbytes: Int, size: Int)
  extends PasswordSpec

/**
 * PBKDF #2 algorithm defined in [[http://www.ietf.org/rfc/rfc2898.txt rfc2892]]
 * utilizing an HMAC SHA256 as the underlying auth.
 *
 * Note: This can only be used with JDK8+ or with another JCE provider that
 * supports this algorithm such as BSAFE or IAIK.
 */
case class PBKDF2withHMACSHA256(rounds: Int, saltbytes: Int, size: Int)
  extends PasswordSpec

/**
 * PBKDF #2 algorithm defined in [[http://www.ietf.org/rfc/rfc2898.txt rfc2892]]
 * utilizing an HMAC SHA256 as the underlying auth.
 *
 * Note: This can only be used with JDK8+ or with another JCE provider that
 * supports this algorithm such as BSAFE or IAIK.
 */
case class PBKDF2withHMACSHA512(rounds: Int, saltbytes: Int, size: Int)
  extends PasswordSpec
