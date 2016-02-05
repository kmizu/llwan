package com.github.kmizu.llwan

/**
  * Created by Mizushima on 2016/02/03.
  */
object GrammarAnalyzer {
  type FirstSetTable = Map[Ast.Exp, Set[String]]
  def calculateFirstSet(grammar: Ast.Grammar, mapping: Map[Ast.Ident, Ast.Exp]): FirstSetTable = {
    def first(e: Ast.Exp, visit: Set[Symbol]): Set[String] = e match {
      case Ast.Alt(_, choices) =>
        ???
      case ident@Ast.Ident(_, name) =>
        if(visit.contains(name)) Set() else first(mapping(ident), visit + name)
      case Ast.Str(_, c) =>
        Set(c)
      case Ast.Emp(_) =>
        Set("")
    }
    (mapping.map { case (ident, exp) => (ident) -> first(mapping(ident), Set(ident.name)) }: FirstSetTable)
  }
  def calculateFollowSet(grammar: Ast.Grammar) = ???
  def calculateDirectorSet(grammar: Ast.Grammar) = ???
}
