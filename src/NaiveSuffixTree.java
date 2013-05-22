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

    public Node()
    {
      children = new ArrayList<Node>();
      leaves = new ArrayList<Integer>();
    }

    public Node(int start, int end, Node parent)
    {
      this.stringStart = start;
      this.stringEnd = end;
      this.children = new ArrayList<Node>();
      this.leaves = new ArrayList<Integer>();
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
  public String input;
  private ArrayList<Node> nodes;
  private int stringNum;

  public NaiveSuffixTree()
  {
    root = new Node();
    root.count--;
    nodes = new ArrayList<Node>();
  }

  int leaf = 1;

  public Node createTree(String input, int stringNum)
  {
    this.stringNum = stringNum;
    this.input = input;
    // this.input = input.concat("$");
    // this.input = " " + input;
    for (int i = 0; i < this.input.length(); i++)
    {
      process(i, this.input.length(), root);
      // System.out.println("TREE-----------------" + i);
      // printTree(root);
    }
    return root;
  }

  // I'm at a node
  public void process(int start, int end, Node node)
  {
    node.leaves.add(leaf);
    node.count++;
    for (int i = 0; i < node.children.size(); i++)
    {
      if (input.substring(start, end).startsWith(
          input.substring(node.children.get(i).stringStart,
              node.children.get(i).stringEnd)))
      {
        process(
            start
                + (node.children.get(i).stringEnd - node.children.get(i).stringStart),
            end, node.children.get(i));
        return;
      }
      int match = stringMatch(start, end, node.children.get(i).stringStart);
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
  }

  public int stringMatch(int start, int end, int ostart)
  {
    for (int i = 0; i < end - start; i++)
    {
      if (input.charAt(start + i) != input.charAt(ostart + i))
      {
        return i;
      }
    }
    return -1;
  }

  public void splitTree(int match, int start, int end, Node parent, Node child)
  {
    Node newParent = new Node(start, start + match, parent);
    Node newNode = new Node(start + match, end, leaf, stringNum, newParent);
    newParent.leaves.addAll(child.leaves);
    newParent.leaves.add(leaf++);
    child.stringStart += match;
    parent.children.remove(child);
    parent.children.add(newParent);
    newParent.children.add(newNode);
    newParent.children.add(child);
    newParent.count = child.count + 1;
    nodes.add(newNode);
    nodes.add(newParent);
  }

  public void printTree(Node node)
  {
    System.out.println();
    System.out.println(node);
    System.out.println(node.count);
    System.out.println(node.leaves);
    System.out.println(node.leaf);
    System.out.println("String: "
        + input.substring(node.stringStart, node.stringEnd));
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
    char temp = input.charAt(node.leaves.get(0) - 1 - 1);
    for (int i = 1; i < node.leaves.size(); i++)
    {
      if (temp != input.charAt(node.leaves.get(0) - 1))
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
    acc += input.substring(node.stringStart, node.stringEnd);
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
    ArrayList<Integer> returnArr = new ArrayList<Integer>();
    if (node.count == 1)
      returnArr.add(node.leaf);
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
      if (sequence.startsWith(input.substring(node.children.get(i).stringStart,
          node.children.get(i).stringEnd)))
      {
        return findSequence(
            sequence.substring(node.children.get(i).stringEnd
                - node.children.get(i).stringStart), node.children.get(i));
      }
      else if (input.substring(node.children.get(i).stringStart,
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
      tempPtr = node1.parent;
    }while(tempPtr!=root);
    tempPtr = node2;
    do
    {
      if(traceBack.contains(tempPtr))
      {
        return tempPtr;
      }
      tempPtr = node1.parent;
    }while(tempPtr!=root);
    return null;
  }
  
  private ArrayList<String> findPalindrome(Node node, int loop, int minRadius, int maxRadius)
  {
    for(int i = 0; i<1; i++)
    {
    }
    return null;
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

  public static String findPalindromes(String input, int minRadius, int maxRadius, int minLoop, int maxLoop)
  {
    StringBuilder sb = new StringBuilder();
    NaiveSuffixTree myTree = new NaiveSuffixTree();
    
    Node root = myTree.createTree(input + "$", 0);
    root = myTree.createTree(NucTranslator.reverseComplement(input) + "$", 1);
    input += "$";
    
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

}
