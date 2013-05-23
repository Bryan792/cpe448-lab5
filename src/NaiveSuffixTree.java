/*
 * Team 6
 * Andrew Nguyen
 * Bryan Ching
 * Matt Crussell
 * CPE 448 Bioinformatics
 * NaiveSuffixTree
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class NaiveSuffixTree
{
  public class Node
  {
    int count = 1;
    ArrayList<Node> children = null;
    int stringStart = 0;
    int stringEnd = 0;
    int leaf = -1;
    int stringNum = -1;
    Node parent = null;
    ArrayList<Integer> leaves = null;
    HashMap<Integer, Integer> stringLeaf;

    public Node()
    {
      children = new ArrayList<Node>();
      leaves = new ArrayList<Integer>();
      stringLeaf = new HashMap<Integer, Integer>();
    }

    public Node(int start, int end, int stringNum, Node parent)
    {
      this.stringStart = start;
      this.stringEnd = end;
      this.children = new ArrayList<Node>();
      this.leaves = new ArrayList<Integer>();
      this.stringNum = stringNum;
      this.parent = parent;
    }

    public Node(int start, int end, int leaf, int stringNum, Node parent)
    {
      this.stringStart = start;
      this.stringEnd = end;
      this.children = new ArrayList<Node>();
      this.leaves = new ArrayList<Integer>();
      this.leaves.add(leaf);
      this.leaf = leaf;
      this.stringNum = stringNum;
      this.parent = parent;
    }

    public int length()
    {
      return stringEnd - stringStart;
    }

    public void addChild(Node node)
    {
      children.add(node);
    }
  }

  private Node root;
  // public String input;
  public ArrayList<String> input;
  private ArrayList<Node> nodes;
  private int stringNum = -2;
  private HashMap<Integer, ArrayList<Node>> hMap;

  public NaiveSuffixTree()
  {
    root = new Node();
    root.count--;
    nodes = new ArrayList<Node>();
    input = new ArrayList<String>();
    hMap = new HashMap<Integer, ArrayList<Node>>();
  }

  int leaf = 1;

  public Node createTree(String input, int stringNum)
  {
    this.leaf = 1;
    this.stringNum = stringNum;
    this.input.add(input);
    // this.input = input.concat("$");
    // this.input = " " + input;
    for (int i = 0; i < input.length(); i++)
    {
      process(i, input.length(), root);
      // printTree(root);
      // System.out.println(hMap);
//      System.out.println("TREE-----------------" + i);
//      printTree(root);
    }
    return root;
  }

  // I'm at a node
  public void process(int start, int end, Node node)
  {
    //System.out.println(input.get(this.stringNum).substring(start, end));
    node.leaves.add(leaf);
    node.count++;
    for (int i = 0; i < node.children.size(); i++)
    {

      if (input
          .get(this.stringNum)
          .substring(start, end)
          .equals(
              input.get(node.children.get(i).stringNum).substring(
                  node.children.get(i).stringStart,
                  node.children.get(i).stringEnd)))
      {
        addLeafToMap(node.children.get(i));
        return;
      }
      if (input
          .get(this.stringNum)
          .substring(start, end)
          .startsWith(
              input.get(node.children.get(i).stringNum).substring(
                  node.children.get(i).stringStart,
                  node.children.get(i).stringEnd)))
      {
        process(
            start
                + (node.children.get(i).stringEnd - node.children.get(i).stringStart),
            end, node.children.get(i));
        return;
      }

      int match = stringMatch(start, end, node.children.get(i).stringStart,
          node.children.get(i).stringNum);
      if (match > 0)
      {
        splitTree(match, start, end, node, node.children.get(i));
        return;
      }
    }
    // no match
    Node newNode = new Node(start, end, leaf++, stringNum, node);
    node.addChild(newNode);
    nodes.add(newNode);
    addLeafToMap(newNode);
  }

  public int stringMatch(int start, int end, int ostart, int ostringNum)
  {
    for (int i = 0; i < end - start; i++)
    {
      if (input.get(this.stringNum).charAt(start + i) != input.get(ostringNum)
          .charAt(ostart + i))
      {
        return i;
      }
    }
    return -1;
  }

  public void splitTree(int match, int start, int end, Node parent, Node child)
  {
    Node newParent = new Node(start, start + match, stringNum, parent);
    Node newNode = new Node(start + match, end, leaf, stringNum, newParent);
    newParent.leaves.addAll(child.leaves);
    newParent.leaves.add(leaf++);
    child.stringStart += match;
    child.parent = newParent;
    parent.children.remove(child);
    parent.children.add(newParent);
    newParent.children.add(newNode);
    newParent.children.add(child);
    newParent.count = child.count + 1;
    nodes.add(newNode);
    nodes.add(newParent);
    addLeafToMap(newNode);
  }

  public void addLeafToMap(Node node)
  {
    if (!hMap.containsKey(this.stringNum))
    {
      hMap.put(this.stringNum, new ArrayList<Node>());
    }
    hMap.get(this.stringNum).add(node);
  }

  public void printTree(Node node)
  {
    System.out.println();
    System.out.println(node);
    System.out.println(node.count);
    System.out.println(node.leaves);
    // System.out.println(node.leaf);
    if (node.stringNum >= 0)
      System.out.println("String: "
          + input.get(node.stringNum).substring(node.stringStart,
              node.stringEnd));
    System.out.println(node.stringStart + " " + (node.stringEnd - 1));
    for (int i = 0; i < node.children.size(); i++)
    {
      System.out.println(node.children.get(i));
    }
    for (int i = 0; i < node.children.size(); i++)
    {
      printTree(node.children.get(i));
    }
  }

  public boolean isLeftDiverse(Node node)
  {
    if (node.leaves.size() == 0 || node.leaves.get(0) == 1)
    {
      return true;
    }
    char temp = input.get(this.stringNum).charAt(node.leaves.get(0) - 1 - 1);
    for (int i = 1; i < node.leaves.size(); i++)
    {
      if (temp != input.get(this.stringNum).charAt(node.leaves.get(0) - 1))
      {
        return true;
      }
    }
    return false;
  }

  public HashMap<String, Integer> findMaxRepeats(Node node, int length,
      int repeats, String acc)
  {
    HashMap<String, Integer> returnArr = new HashMap<String, Integer>();
    acc += input.get(this.stringNum)
        .substring(node.stringStart, node.stringEnd);
    if (node.length() >= length && node.count >= repeats
        && isLeftDiverse(node) == true)
    {
      // System.out.println(node);
      // System.out.println(node.stringStart + " " + node.stringEnd);
      // System.out.println(acc);
      returnArr.put(acc, node.count);
    }
    for (Node child : node.children)
    {
      returnArr.putAll(findMaxRepeats(child, length, repeats, acc));
    }
    return returnArr;
  }

  public ArrayList<Integer> findPosition(Node node)
  {
    // ArrayList<Integer> returnArr = new ArrayList<Integer>();
    // if (node.count == 1)
    // returnArr.add(node.leaf);
    // TODO
    return node.leaves;
    /*
     * for (int i = 0; i < node.children.size(); i++) { if
     * (node.children.get(i).count > 1) {
     * returnArr.addAll(findPosition(node.children.get(i))); } else {
     * returnArr.add(node.children.get(i).leaf); } } return returnArr;
     */
  }

  public ArrayList<Integer> findSequence(String sequence, Node node)
  {
    if (sequence.length() == 0)
    {
      return findPosition(node);
    }

    for (int i = 0; i < node.children.size(); i++)
    {
      if (sequence.startsWith(input.get(this.stringNum).substring(
          node.children.get(i).stringStart, node.children.get(i).stringEnd)))
      {
        return findSequence(
            sequence.substring(node.children.get(i).stringEnd
                - node.children.get(i).stringStart), node.children.get(i));
      }
      else if (input
          .get(this.stringNum)
          .substring(node.children.get(i).stringStart,
              node.children.get(i).stringEnd).startsWith(sequence))
      {
        // System.out.println(input.substring(node.children.get(i).stringStart,
        // node.children.get(i).stringEnd));
        return findPosition(node.children.get(i));
      }
    }
    return null;
  }

  private Node LCA(Node node1, Node node2)
  {
    ArrayList<Node> traceBack = new ArrayList<Node>();
    Node tempPtr = node1;
    do
    {
      traceBack.add(tempPtr);
      tempPtr = tempPtr.parent;
    } while (tempPtr != null);

    tempPtr = node2;
    do
    {
      if (traceBack.contains(tempPtr))
      {
        return tempPtr;
      }
      tempPtr = tempPtr.parent;
    } while (tempPtr != null);
    return root;
  }

  private ArrayList<Results> findPalindrome(Node node, int minRadius,
      int maxRadius, int minLoop, int maxLoop, int length)
  {
    ArrayList<Results> returnArr = new ArrayList<Results>();
    for (int loop = minLoop; loop <= maxLoop; loop++)
    {
      for (int q = minRadius + loop; q < length - minRadius; q++)
      {
        Node internal;
        if ((internal = LCA(hMap.get(0).get(q),
            hMap.get(1).get(length - q + loop))) != root)
        {
          int radius = 0;
          while (internal != root)
          {
            radius += internal.length();
            internal = internal.parent;
          }
          if (radius >= minRadius && radius <= maxRadius)
          {
//            System.out.println(input.get(0).substring(q - radius - loop,
//                q + radius)
//                + " " + q + " " + radius + " " + loop);
            Results temp = new Results(input.get(0).substring(
                q - radius - loop, q + radius), " ", radius);
            if (returnArr.contains(temp))
            {
              if (returnArr.get(returnArr.indexOf(temp)).radius < radius)
              {
                returnArr.set(returnArr.indexOf(temp), temp);
              }
            }
            else
            {
              returnArr.add(temp);
            }
          }
        }
      }
    }
    return returnArr;
  }

  public class Results
  {
    String palindrome;
    String metadata;
    Integer radius;

    public Results(String p, String m, Integer r)
    {
      this.palindrome = p;
      this.metadata = m;
      this.radius = r;
    }

    @Override
    //TODO
    public boolean equals(Object r)
    {
      return palindrome.equals(((Results) r).palindrome);
    }

    public String toString()
    {
      return palindrome + " " + radius + " " + metadata;
    }

  }

  public static String run(String input, int length, int filter)
  {
    StringBuilder sb = new StringBuilder();
    NaiveSuffixTree myTree = new NaiveSuffixTree();
    input += "$";
    final BaseCalculator myBaseCalculator = new BaseCalculator(input);
    Node root = myTree.createTree(input, 0);

    ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
        myTree.findMaxRepeats(root, length, 2, new String()).entrySet());

    Collections.sort(list, new Comparator<Entry<String, Integer>>()
    {

      @Override
      public int compare(Entry<String, Integer> arg1,
          Entry<String, Integer> arg0)
      {
        return (int) (arg0.getValue()
            / myBaseCalculator.expectedOccurences(arg0.getKey()) - arg1
            .getValue() / myBaseCalculator.expectedOccurences(arg1.getKey()));
      }
    });

    for (Entry<String, Integer> entry : list)
    {
      // System.out.println(myBaseCalculator.expectedOccurences(entry.getKey()));
      // System.out.println(entry.getValue());
      // System.out.println(entry.getKey());
      // System.out.println(entry.getValue()
      // / myBaseCalculator.expectedOccurences(entry.getKey()));
      //
      if (entry.getValue()
          / myBaseCalculator.expectedOccurences(entry.getKey()) >= filter)
      {
        sb.append(entry.getKey()
            + "\n"
            + entry.getValue()
            + "\n"
            + round(myBaseCalculator.expectedOccurences(entry.getKey()), 2)
            + "\n"
            + round(
                entry.getValue()
                    / myBaseCalculator.expectedOccurences(entry.getKey()), 2)
            + "\n@\n");
        sb.append(myTree.findSequence(entry.getKey(), root) + "\n\n");
      }

    }

    return sb.toString();
  }

  public static double round(double number, int places)
  {
    return ((int) (number * Math.pow(10, places))) / Math.pow(10, places);
  }

  public static String find(String input, String findMe)
  {
    StringBuilder sb = new StringBuilder();
    NaiveSuffixTree myTree = new NaiveSuffixTree();
    input += "$";
    Node root = myTree.createTree(input, 0);
    for (int i : myTree.findSequence(findMe, root))
    {
      sb.append(i + "\n");
    }

    return sb.toString();
  }

  public static String findPalindromes(String input, int minRadius,
      int maxRadius, int minLoop, int maxLoop)
  {
    StringBuilder sb = new StringBuilder();
    NaiveSuffixTree myTree = new NaiveSuffixTree();

    Node root = myTree.createTree(input + "$", 0);

    root = myTree.createTree(
        new StringBuilder(NucTranslator.reverseComplement(input)).reverse()
            .toString() + "$", 1);
    input += "$";

    for(Results r : myTree.findPalindrome(root, minRadius, maxRadius,
        minLoop, maxLoop, input.length() - 1))
    {
      sb.append(r.toString() + "\n");
    }
    
    return sb.toString();
  }

  public static void main(String[] args)
  {
    findPalindromes("AACGATTTTTATCCA", 2, 20, 0, 1000);
    //findPalindromes("CATTGATCAACGA", 3, 20, 0, 1000);
  }

}
