/*
 * Team 6
 * Andrew Nguyen
 * Bryan Ching
 * Matt Crussell
 * CPE 448 Bioinformatics
 * NaiveSuffixTree
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Math;

public class MRNAFinder {
 //Return -1 for no mRNA, -2 for no file, else size
 public static int findSmallestMRNA(String path) {
  int smallest = Integer.MAX_VALUE;

  try
  {
    FileInputStream fstream = new FileInputStream(path);
    Scanner fScanner = new Scanner(fstream);
    String currLine = new String();
    while (fScanner.hasNextLine()) {
      currLine = fScanner.nextLine();
      
      String[] splitline = currLine.split("\\s+");
      if (splitline.length > 10 && splitline[2].equals("mRNA"))
      {
        smallest = Math.min(Integer.valueOf(splitline[3]), smallest);
        smallest = Math.min(Integer.valueOf(splitline[4]), smallest);
      }
    }
    fScanner.close();
  } catch (FileNotFoundException e) {
    return -2;
  }
  if(smallest == Integer.MAX_VALUE)
    return -1;
  return smallest;
 }
}
/*class driver {
  public static void main(String[] args)
  {
    System.out.println("Smallest = " +  MRNAFinder.findSmallestMRNA(args[0]));
  }
}*/

