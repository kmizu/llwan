package com.github.kmizu.llwan

import com.github.kmizu.llwan.Ast.Choices

class GrammarAnalyzer(grammar: Ast.Grammar) {
  type FirstSetTable = Map[Symbol, Set[String]]
  private[this] val mapping: Map[Symbol, Choices] = grammar.rules.map{ rule => rule.name -> rule.body}.toMap

  def calculateFirstSet: FirstSetTable = {
    def first(e: Ast.Exp, visit: Set[Symbol]): Set[String] = e match {
      case Ast.Choices(_, choices) =>
        def firsts(seq: List[Ast.Prm]): Set[String] = seq match {
          case hd::tl =>
            val result = first(hd, visit)
            result ++ (if(result.contains("")) firsts(tl) else Set[String]())
          case Nil => Set[String]()
        }
        choices.foldLeft(Set[String]()){(set, choice) => set ++ firsts(choice)}
      case ident@Ast.Ident(_, name) =>
        if(visit.contains(name)) Set() else first(mapping(name), visit + name)
      case Ast.Str(_, c) =>
        Set(c)
      case Ast.Emp(_) =>
        Set("")
    }
    (mapping.map { case (ident, exp) => (ident) -> first(mapping(ident), Set(ident)) }: FirstSetTable)
  }
  def calculateFollowSet(grammar: Ast.Grammar) = ???
  def calculateDirectorSet(grammar: Ast.Grammar) = ???
}
