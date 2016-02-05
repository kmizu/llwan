package com
package github
package kmizu
package llwan

import scala.util.parsing.combinator._
import scala.util.parsing.input.{CharSequenceReader, StreamReader}
import scala.util.parsing.input.Position
import java.io._
import Ast._
/**
  * This object provides a parser that parses strings in HOPEG and translates
  * them into ASTs of HOPEG (which is like PEGs).
  * @author Kota Mizushima
  *
  */
object Parser {

  /**
    * This exception is thrown in the case of a parsing failure
    * @param pos the position where the parsing failed
    * @param msg error message
    */
  case class ParseException(pos: Pos, msg: String) extends Exception(pos.line + ", " + pos.column + ":" + msg)

  private[this] object ParserCore extends Parsers {
    type Elem = Char
    private val any: Parser[Char] = elem(".", c => c != CharSequenceReader.EofCh)
    private def chr(c: Char): Parser[Char] = c
    private def crange(f: Char, t: Char): Parser[Char] = elem("[]", c => f <= c && c <= t)
    private def cset(cs: Char*): Parser[Char] = elem("[]", c => cs.indexWhere(_ == c) >= 0)
    private val escape: Map[Char, Char] = Map(
      'n' -> '\n', 'r' -> '\r', 't' -> '\t', 'f' -> '\f'
    )
    private def not[T](p: => Parser[T], msg: String): Parser[Unit] = {
      not(p) | failure(msg)
    }
    lazy val GRAMMAR: Parser[Grammar] = (loc <~ Spacing) ~ Definition.* <~ EndOfFile ^^ {
      case pos ~ rules => Grammar(Pos(pos.line, pos.column), rules)
    }
    lazy val Definition: Parser[Rule] = (Identifier  <~ EQ) ~ (Expression <~ SEMI_COLON) ^^ {
      case name ~ body =>
        Rule(name.pos, name.name, body)
    }
    lazy val Expression: Parser[Exp] = rep1sep(Sequence, BAR) ^^ {ns => Alt(ns.head.head.pos, ns) }
    lazy val Sequence: Parser[List[Exp]] = Primary.+
    lazy val Primary: Parser[Exp] = (
      Identifier
    | loc <~ chr('_') ^^ { case pos => Str(Pos(pos.line, pos.column), "") }
    | loc <~ EPS ^^ { case pos => Emp(Pos(pos.line, pos.column)) }
    | Literal
    )
    lazy val loc: Parser[Position] = Parser{reader => Success(reader.pos, reader)}
    lazy val Identifier: Parser[Ident] = loc ~ IdentStart ~ IdentCont.* <~Spacing ^^ {
      case pos ~ s ~ c => Ident(Pos(pos.line, pos.column), Symbol("" + s + c.foldLeft("")(_ + _)))
    }
    lazy val IdentStart: Parser[Char] = crange('a','z') | crange('A','Z') | '_'
    lazy val IdentCont: Parser[Char] = IdentStart | crange('0','9')
    lazy val Literal: Parser[Str] = loc ~ (chr('\"') ~> CHAR.* <~ chr('\"')) <~ Spacing ^^ {
      case pos ~ cs => Str(Pos(pos.line, pos.column), cs.mkString)
    }
    private val META_CHARS = List('/','&','!','?','*','+','(',')',';','=','\'','"','\\')
    lazy val META: Parser[Char] = cset(META_CHARS:_*)
    lazy val HEX: Parser[Char] = crange('0','9') | crange('a', 'f')
    lazy val CHAR: Parser[Char] = (
      chr('\\') ~> cset('n','r','t','f') ^^ { case c => escape(c) }
        | chr('\\') ~> chr('u') ~> (HEX ~ HEX ~ HEX ~ HEX) ^^ {
        case u1 ~ u2 ~ u3 ~ u4 => Integer.parseInt("" + u1 + u2 + u3 + u4, 16).toChar
      }
        | chr('\\') ~ META ^^ { case _ ~ c => c }
        | chr('\\') ~ crange('0','2') ~ crange('0','7') ~ crange('0','7') ^^ {
        case _ ~ a ~ b ~ c => Integer.parseInt("" + a + b + c, 8).toChar
      }
        | chr('\\') ~ crange('0','7') ~ opt(crange('0','7')) ^^ {
        case _ ~ a ~ Some(b) => Integer.parseInt("" + a + b, 8).toChar
        case _ ~ a ~ _ => Integer.parseInt("" + a, 8).toChar
      }
        | not(META, " meta character " + META_CHARS.mkString("[",",","]") + " is not expected") ~>  any ^^ { case c => c}
      )
    lazy val LPAREN = chr('(') <~ Spacing
    lazy val RPAREN = chr(')') <~ Spacing
    lazy val COMMA = chr(',') <~ Spacing
    lazy val LT = chr('<') <~ Spacing
    lazy val GT = chr('>') <~ Spacing
    lazy val COLON = chr(':') <~ Spacing
    lazy val SEMI_COLON = chr(';') <~ Spacing
    lazy val EQ = chr('=') <~ Spacing
    lazy val BAR = chr('|') <~ Spacing
    lazy val AND = chr('&') <~ Spacing
    lazy val NOT = chr('!') <~ Spacing
    lazy val QUESTION = chr('?') <~ Spacing
    lazy val STAR = chr('*') <~ Spacing
    lazy val PLUS = chr('+') <~ Spacing
    lazy val OPEN = chr('(') <~ Spacing
    lazy val CLOSE = chr(')') <~ Spacing
    lazy val DOT = chr('.') <~ Spacing
    lazy val ARROW = chr('-') <~ chr('>') <~ Spacing
    lazy val Spacing = (Space | Comment).*
    lazy val EPS = chr('e') <~ chr('p') <~ chr('s') <~ Spacing
    lazy val Comment = (
      chr('/') ~ chr('/') ~ (not(EndOfLine) ~ any).* ~ EndOfLine
      )
    lazy val Space = chr(' ') | chr('\t') | EndOfLine
    lazy val EndOfLine = chr('\r') ~ chr('\n') | chr('\n') | chr('\r')
    lazy val EndOfFile = not(any)
  }

  /**
    * Parses a pattern from `content` and returns the `Grammar` instance, which is the parse result.
    * @param fileName
    * @param content
    * @return `Grammar` instance
    */
  def parse(fileName: String, content: java.io.Reader): Ast.Grammar = {
    ParserCore.GRAMMAR(StreamReader(content)) match {
      case ParserCore.Success(node, _) => node
      case ParserCore.Failure(msg, rest) =>
        val pos = rest.pos
        println(pos)
        throw new ParseException(Pos(pos.line, pos.column), msg)
      case ParserCore.Error(msg, rest) =>
        val pos = rest.pos
        throw new ParseException(Pos(pos.line, pos.column), msg)
    }
  }

  /**
    * Parses a `pattern` and returns the `Grammar` instance, which is the parse result.
    * @param pattern input string
    * @return `Grammar` instance
    */
  def parse(pattern: String): Grammar = {
    parse("", new StringReader(pattern))
  }

  def main(args: Array[String]) {
    val g = parse(args(0), new FileReader(args(0)))
    println(g)
  }
}