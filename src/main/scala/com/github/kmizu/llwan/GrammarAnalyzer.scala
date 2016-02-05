package com.github.kmizu.llwan

/**
  * Created by Mizushima on 2016/02/03.
  */
object GrammarAnalyzer {
  type FirstSetTable = Map[Ast.Exp, Set[String]]
  def calculateFirstSet(grammar: Ast.Grammar, mapping: Map[Ast.Ident, Ast.Exp]): FirstSetTable = {
    def first(e: Ast.Exp, visit: Set[Symbol]): Set[String] = e match {
      case Ast.Seq(_, l, r) =>
        val firstOfL = first(l, visit)
        firstOfL ++ (if(firstOfL.contains("")) first(r, visit) else Set())
      case Ast.Alt(_, l, r) =>
        first(l, visit) ++ first(r, visit)
      case ident@Ast.Ident(_, name) =>
        first(mapping(ident), visit + name)
      case Ast.Str(_, c) =>
        Set(c)
    }
    (mapping.map { case (ident, exp) => (ident) -> first(mapping(ident), Set(ident.name)) }: FirstSetTable)
  }
  def calculateFollowSet(grammar: Ast.Grammar) = ???
  def calculateDirectorSet(grammar: Ast.Grammar) = ???
}
