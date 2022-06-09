package huffman

import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class HuffmanSpec extends AnyFlatSpec:
  import HuffmanError.*
  import Node.*

  "The frequencies of 'abc'" should "be a map, containing a, b and c, all with 1 as their frequency" in
    assert(getFrequency(List("a", "b", "c")) == Map("a" -> 1, "b" -> 1, "c" -> 1))

  "The frequencies of an empty list" should "be empty" in
    assert(getFrequency(List()) == Map())

  "createTree" should "yield an error if the frequencies are empty" in
    assert(createTree(Map()) == Left(NoFrequencies))

  it should """yield the right tree for Map("a" -> 2, "b" -> 1)""" in
    assert(createTree(Map("a" -> 2, "b" -> 1)) == Right(Inner(Leaf("b", 1), Leaf("a", 2), 3)))

  val ab = Inner(Leaf("a", 1), Leaf("b", 2), 3)
  val abc = Inner(ab, Leaf("c", 4), 7)
  val abcd = Inner(Leaf("d", 5), abc, 12)
  val ef = Inner(Leaf("e", 8), Leaf("f", 10), 18)
  val all = Inner(abcd, ef, 30)

  it should """yield the right tree for "efbefffbdfdeedcffdeecdceffeacf" """ in
    assert(
      createTree("efbefffbdfdeedcffdeecdceffeacf".split("").toList.groupBy(x => x).view.mapValues(_.size).toMap)
        == Right(all))

  "encode" should """yield the right encoding for the tree of "efbefffbdfdeedcffdeecdceffeacf" """ +
  """and the string "abcdef" """ in
    assert(encode("abcdef".split("").toList, all) == Right(
      List(
        true, false, true, true,
        true, false, true, false,
        true, false, false,
        true, true,
        false, true,
        false, false)))

  it should """yield the ValueNotFound error for the tree Leaf("a", 1) and the input List("b")""" in
    assert(encode(List("b"), Leaf("a", 1)) === Left(ValueNotFound))

  "decode" should """yield the right decoding for the tree of "efbefffbdfdeedcffdeecdceffeacf" """ +
  """and the input 10111010100110100""" in
    assert(decode(List(true, false, true, true, true, false, true, false, true, false, false, true, true, false, true, false, false), all) === Right("abcdef".split("").toList))

  it should """yield the MissingBits error if it gets stuck with bits left to decode """ +
  """on the input 10111 and the tree for "efbefffbdfdeedcffdeecdceffeacf"""" in
    assert(decode(List(true, false, true, true, true), all) === Left(MissingBits))
