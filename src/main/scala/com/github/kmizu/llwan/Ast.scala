package com
package github
package kmizu
package llwan

/** This object provides types representing ASTs of extended PEG.
  * It is used as namespace.
  * @author Kota Mizushima
  */
object Ast {
  type ==>[-A, +B] = PartialFunction[A, B]

  val DUMMY_POS = Pos(-1, -1)

  /** A trait for types that has position. */
  trait HasPosition { def pos: Pos }

  /** This class represents position in a source file.
    * @param line line number (0-origin)
    * @param column column number (0-origin) */
  case class Pos (line: Int, column: Int)

  /** This class represents an AST of PEG grammar.
    * @param pos position in source file
    * @param rules the list of rules constituting PEG grammar */
  case class Grammar(pos: Pos, rules: List[Rule]) extends HasPosition {
    def +(newRule: Rule): Grammar = Grammar(pos, rules = newRule::rules)
    override def hashCode: Int = super.hashCode
  }

  /** This class represents an AST of rule in PEG grammar.
    * @param pos position in source file
    * @param name the name of this rule.  It is referred in body
    * @param body the parsing expression which this rule represents */
  case class Rule(pos: Pos, name: Symbol, body: Exp) extends HasPosition {
    override def hashCode: Int = super.hashCode
  }

  /** This trait represents common super-type of parsing expression AST. */
  sealed trait Exp extends HasPosition

  /** This class represents an AST of sequence (e1 e2).
    * @param pos position in source file
    * @param lhs e1
    * @param rhs e2 */
  case class Seq(pos: Pos, lhs: Exp, rhs: Exp) extends Exp {
    override def hashCode: Int = super.hashCode
  }

  /** This class represents an AST of ordered choice (e1 / e2).
    * @param pos position in source file
    * @param lhs e1
    * @param rhs e2 */
  case class Alt(pos: Pos, lhs: Exp, rhs: Exp) extends Exp {
    override def hashCode: Int = super.hashCode
  }

  /** This class represents an AST of string literal "...".
    * @param pos position in source file
    * @param target literal */
  case class Str(pos: Pos, target: String) extends Exp {
    override def hashCode: Int = super.hashCode
  }

  /** This class represents an AST of epsilon value
    * @param pos position in source file */
  case class Emp(pos: Pos) extends Exp {
    override def hashCode: Int = super.hashCode
  }

  /** This class represents an AST of identifier.
    * An identifier is used as reference of nonterminal.
    * @param pos position in source file
    * @param name the name of identifier */
  case class Ident(pos: Pos, name: Symbol) extends Exp {
    override def hashCode: Int = super.hashCode
  }
}