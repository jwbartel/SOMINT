package kelli.mergeAttempts;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

public class cliqueCPPexample {
	BufferedReader inputStream;// = new BufferedReader(new FileReader("xanadu.txt"));
	FileOutputStream out; 
	PrintStream outputStream;
	FileInputStream in;
	DataInputStream infile;

   int cliqueCPPmain()
   {
	   try {
		   out = new FileOutputStream("cliquesCPP.txt");
		   outputStream = new PrintStream(out);
		   in = new FileInputStream("adjacencyMatrix.txt");
		   infile = new DataInputStream(in);
		   //Read Graph (note we work with the complement of the input graph)
		   System.out.println("Clique Algorithm.");
		   int n, i, j, k, K, p, q, r, s, min, edge, counter=0;
		   n = infile.read();
		   Vector< Vector<Integer> > graph = new Vector<Vector<Integer>>();
		   for(i=0; i<n; i++)
		   {
			  Vector<Integer> row = new Vector<Integer>();
			  for(j=0; j<n; j++)
			  {
				 edge = infile.read();
				 if(edge==0)
					 row.add(1);
				 else row.add(0);
			  }
			  graph.add(row);
		   }
		   //Find Neighbors
		   Vector<Vector<Integer> > neighbors = new Vector<Vector<Integer>>();
		   for(i=0; i<graph.size(); i++)
		   {
			  Vector<Integer> neighbor = new Vector<Integer>();
			  for(j=0; j<graph.elementAt(i).size(); j++)
				 if(graph.elementAt(i).elementAt(j)==1) neighbor.add(j);
			  neighbors.add(neighbor);
		   }
		   System.out.println("Graph has n = "+n+" vertices.");
		   //Read maximum size of Clique wanted
		   System.out.println("Find a Clique of size at least k = 5");
		   K = 5;
		   k = n - K;
		   //Find Cliques
		   boolean found=false;
		   System.out.println("Finding Cliques...");
		   min=n+1;
		   Vector<Vector<Integer> > covers = new Vector<Vector<Integer>>();
		   Vector<Integer> allcover = new Vector<Integer>();
		   for(i=0; i<graph.size(); i++)
			  allcover.add(1);
		   for(i=0; i<allcover.size(); i++)
		   {
			  if(found) break;
			  counter++; System.out.print(counter+". ");  outputStream.print(counter+". ");
			  Vector<Integer> cover=allcover;
			  cover.setElementAt(0,i);
			  cover=procedure_1(neighbors,cover);
			  s=cover_size(cover);
			  if(s<min) min=s;
			  if(s<=k)
			  {
				 outputStream.print("Clique ("+(n-s)+"): ");
				 for(j=0; j<cover.size(); j++) if(cover.elementAt(j)==0) outputStream.print((j+1)+" ");
				 outputStream.print("\n");
				 System.out.println("Clique Size: "+(n-s));
				 covers.add(cover);
				 found=true;
				 break;
			  }
			  for(j=0; j<n-k; j++)
				  cover=procedure_2(neighbors,cover,j);
			  s=cover_size(cover);
			  if(s<min) min=s;
			  outputStream.print("Clique ("+(n-s)+"): ");
			  for(j=0; j<cover.size(); j++) if(cover.elementAt(j)==0) outputStream.print((j+1)+" ");
			  outputStream.print("\n");
			  System.out.println("Clique Size: "+(n-s));
			  covers.add(cover);
			  if(s<=k){ found=true; break; }
		   }
		//	Pairwise Intersections
		   for(p=0; p<covers.size(); p++)
		   {
			   if(found) break;
			   for(q=p+1; q<covers.size(); q++)
			   {
				   if(found) break;
				   counter++; System.out.print(counter+". ");  outputStream.print(counter+". ");
				   Vector<Integer> cover=allcover;
				   for(r=0; r<cover.size(); r++)
					   if(covers.elementAt(p).elementAt(r)==0 && covers.elementAt(q).elementAt(r)==0) cover.setElementAt(0,r);
				   cover=procedure_1(neighbors,cover);
				   s=cover_size(cover);
				   if(s<min) min=s;
				   if(s<=k)
				   {
					   outputStream.print("Clique ("+(n-s)+"): ");
					   for(j=0; j<cover.size(); j++) if(cover.elementAt(j)==0) outputStream.print((j+1)+" ");
					   outputStream.print("\n");
					   System.out.println("Clique Size: "+(n-s));
					   found=true;
					   break;
				   }
				   for(j=0; j<k; j++)
					   cover=procedure_2(neighbors,cover,j);
				   s=cover_size(cover);
				   if(s<min) min=s;
				   outputStream.print("Clique ("+(n-s)+"): ");
				   for(j=0;	 j<cover.size(); j++) if(cover.elementAt(j)==0) outputStream.print((j+1)+" ");
				   outputStream.print("\n");
				   System.out.println("Clique Size: "+(n-s));
				   if(s<=k){ found=true; break; }
			   }
		   }
		   if(found) System.out.println("Found Clique of size at least "+K+".");
		   else System.out.println("Could not find Clique of size at least "+"."+"\nMaximum Clique size found is "+(n-min)+".");
		   System.out.println("See cliques.txt for results.");
	   } catch (Exception e){
		   System.out.println(e.getMessage());
	   }
	   //system("PAUSE");
	   return 0;
   }	

   boolean removable(Vector<Integer> neighbor, Vector<Integer> cover)
   {
      boolean check=true;
      for(int i=0; i<neighbor.size(); i++)
    	 if(cover.elementAt(neighbor.elementAt(i))==0){
    		check=false;
    		break;
    	 }
      return check;
   }

   int max_removable(Vector<Vector<Integer> > neighbors, Vector<Integer> cover)
   {
	  int r=-1, max=-1;
	  for(int i=0; i<cover.size(); i++){
		 if(cover.elementAt(i)==1 && removable(neighbors.elementAt(i),cover)==true)
		 {
			Vector<Integer> temp_cover=cover;
			temp_cover.setElementAt(0, i);
			int sum=0;
			for(int j=0; j<temp_cover.size(); j++)
			   if(temp_cover.elementAt(j)==1 && removable(neighbors.elementAt(j), temp_cover)==true)
				  sum++;
			if(sum>max)
			{
			   max=sum;
			   r=i;
			}
		 }
	  }
	  return r;
   }

   Vector<Integer> procedure_1(Vector<Vector<Integer> > neighbors, Vector<Integer> cover)
   {
	  Vector<Integer> temp_cover=cover;
	  int r=0;
	  while(r!=-1){
		 r= max_removable(neighbors,temp_cover);
		 if(r!=-1) temp_cover.setElementAt(0, r);
	  }
	  return temp_cover;
   }

   Vector<Integer> procedure_2(Vector<Vector<Integer> > neighbors, Vector<Integer> cover, int k)
   {
	  int count=0;
	  Vector<Integer> temp_cover=cover;
	  //int i=0;
	  for(int i=0; i<temp_cover.size(); i++)
	  {
		 if(temp_cover.elementAt(i)==1)
		 {
			int sum=0; 
			int index=0;
			for(int j=0; j<neighbors.elementAt(i).size(); j++)
				if(temp_cover.elementAt(neighbors.elementAt(i).elementAt(j))==0) {index=j; sum++;}
			if(sum==1 && cover.elementAt(neighbors.elementAt(i).elementAt(index))==0)
			{
			   temp_cover.set(1, neighbors.elementAt(i).elementAt(index));
			   temp_cover.set(0, i);
			   temp_cover=procedure_1(neighbors,temp_cover);
			   count++;
			}
			if(count>k) break;
		 }
	  }
	  return temp_cover;
   }

   int cover_size(Vector<Integer> cover)
   {
	  int count=0;
	  for(int i=0; i<cover.size(); i++)
		  if(cover.elementAt(i)==1) count++;
	  return count;
   }
}
