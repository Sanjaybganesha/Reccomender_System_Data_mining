import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RecommenderSystem {
	
	private final static int userCount = 943;/**number of user in train set*/
	private final static int itemCount = 1682;/**number of item in train set*/
	private final static String sSplitValue = " +";	
	private static int[][] arrMatrix = new int[userCount+ 1][itemCount  + 1];
	private static int[][] arrOutputMatrix = new int[userCount + 1][itemCount + 1];
	private int[] arrSimilarUser = new int[userCount + 1];
	Map<Integer, HashMap<Integer, Double>> mapCoeffient = new HashMap<Integer, HashMap<Integer, Double>>();

	public void initializeSimilarUserArray() {
		/**array initialize to find simmiler users*/ 
		for (int i = 1; i <= userCount; i++) {
			arrSimilarUser[i] = 0;	
		}
	}
	
	/** getting co-rrelation between users(pearsons algoritm)*/
	public double Correlation(int[] xUser, int[] yUser) {
		double dMeanX = 0.0, dMeanY = 0.0, sumY=0.0; 
		double sumX=0.0,dSumXY = 0.0, dSumX2 = 0.0, dSumY2 = 0.0;
		double temp1=0.0, temp2=0.0, temp3=0.0;
		
	    for(int i = 1; i < xUser.length; i++)
		{	
	        sumX = sumX + xUser[i];
	        sumY = sumY+ yUser[i];
	    }

	    dMeanX = sumX/(xUser.length - 1);
	    dMeanY = sumY/(yUser.length - 1);
	    for(int j = 1; j < xUser.length; j++)
	    {
			dSumXY =dSumXY + ((xUser[j] - dMeanX) * (yUser[j] - dMeanY));
			temp1=xUser[j] - dMeanX;
			temp2=yUser[j] - dMeanY;
	    	dSumX2 = dSumX2 + Math.pow(temp1, 2.0);
	    	dSumY2 = dSumY2 + Math.pow(temp2, 2.0);
	    }
	    temp3=(dSumXY / (Math.sqrt(dSumX2 * dSumY2)));
	return temp3;
}
	/**
	 * This function gives prediction to user's with rating 0 based on algorithm. It
	 * computes weighted sum algorithm on similar user's tastes to compute the
	 * predicted value.
	 */
	public void recommender(int iUserId, List<Integer> lst) {
		for (int i = 1; i <= itemCount; i++) {
			if (arrMatrix[iUserId][i] == 0) {
				double dblValue = 0.0;
				double dblDemo = 0.0, mapCoeffientuserid=0.0;
				for (int j : lst) {
					if (arrMatrix[j][i] == 0){
						//arrOutputMatrix[iUserId][i]=2;
						continue;

					}
						
					mapCoeffientuserid=mapCoeffient.get(iUserId).get(j);
					dblValue = dblValue + arrMatrix[j][i] * mapCoeffientuserid;
					dblDemo  = dblDemo+ Math.abs(mapCoeffientuserid);

			}
				double dbl = dblValue / dblDemo;
				int dblSimilar =(int) dbl;
				if (dblSimilar < 1)
					dblSimilar = 1;

				else if (dblSimilar > 5)
					dblSimilar = 5;
				arrOutputMatrix[iUserId][i] = (int) dblSimilar;
			} else {
				arrOutputMatrix[iUserId][i] = arrMatrix[iUserId][i];
			}
		}
	}
	/**
	 * compute Pearson's Correlation Coeffient.
	 *implements user similarity algorithm	 
	 */
	public void UserSimilarities(NearestNNeighbor objNearestNNeighbor) {
		for (int i = 1; i <= userCount; i++) {
			List<Integer> lstSimilarNearestNeighbor = new ArrayList<Integer>();
			HashMap<Integer, Double> mapUser = new HashMap<Integer, Double>();
			for (int j = 1; j <= userCount; j++) {
				if (i == j)
					continue;
				double dUserSimilarCoefficient = 0.0;
				dUserSimilarCoefficient = Correlation(arrMatrix[i], arrMatrix[j]);
				if(dUserSimilarCoefficient > 0.1) {
				lstSimilarNearestNeighbor.add(j);
				}
				mapUser.put(j, dUserSimilarCoefficient);
			}
			mapCoeffient.put(i, mapUser);
			objNearestNNeighbor.arrNearestUser.put(i, lstSimilarNearestNeighbor);
		}
	}


	public static void main(String args[]) throws IOException {

		if (args.length <= 0&& args.length>1)
		{
			throw new IllegalArgumentException("wrong argument");
		}
		
		File fInputDataSet = new File(args[0]);
		File fOutputDataSet = new File("Output.txt");
		/**
		 * reading value from input file and saving to mattrix
		 */
		RecommenderSystem objRecommenderSystem = new RecommenderSystem();
		try (BufferedReader br = new BufferedReader(new FileReader(fInputDataSet))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] arrSplit = new String[3];
				arrSplit = line.split(sSplitValue);
				//splitting and storing to matrix
				arrMatrix[Integer.parseInt(arrSplit[0])][Integer.parseInt(arrSplit[1])] = Integer.parseInt(arrSplit[2]);
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			System.out.println("The file cannot be read.");
		 }
		 catch (NumberFormatException e) {
			System.out.println("The file contains non numeric data.");
		 }catch (Exception e1) {
			e1.printStackTrace();
		}
		NearestNNeighbor objNearestNNeighbor = new NearestNNeighbor();
		objRecommenderSystem.UserSimilarities(objNearestNNeighbor);
		for (int i = 1; i <= RecommenderSystem.userCount; i++) {
			objRecommenderSystem.recommender(i, objNearestNNeighbor.arrNearestUser.get(i));
		}
		/**
		 * writing data to output file
		 */
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fOutputDataSet));
			for (int i = 1; i <= userCount; i++) {
				for (int j = 1; j <= itemCount; j++) {
					writer.write(i + " " + j + " " + arrOutputMatrix[i][j] + "\n");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
				writer.close();

		}
		System.out.println("check output file");
	}
}