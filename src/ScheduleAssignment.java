/**
 * This program is Intended for Combinatorial Optimization Assignment
 * 
 * The program calculate the optimal Exam Schedule:
 * # Students 2823
 * # Exam 80
 * # Time slot 20 
 * 
 * The method that being used in this program is Greedy Algorithm and then improved by 
 * Descent Direction and Simulated Annealing
 * 
 * @author Dessy Amirudin
 * s1147078
 * The University of Edinburgh
 * 
 */


import java.io.File;

import java.io.IOException;
import jxl.read.biff.BiffException;
import java.util.*;

//import java.util.Date;
import jxl.*;

public class ScheduleAssignment {

	/**
	 * Initialize each cell in 2 dimensional boolean array to be false
	 * @param y the 2 dimensional array
	 */
	public static void initial2array(boolean[][] y){
		for(int i=0;i<y.length;i++){
			for(int j=0;j<y[i].length;j++){
				y[i][j]=false;
			}
		}
	}

	/**
	 * Initialize each cell in 1 dimensional boolean array to be false
	 * @param z the 1 dimensional array
	 */
	public static void initial1array(boolean[] z){
		for(int i=0;i<z.length;i++){
			z[i]=false;
		}
	}

	/**
	 * Calculating the cost of solutions
	 * 
	 * Observed exam i and j. If exam i happen at time t and exam j happen at time t+1
	 * and student k taking both exam, increase the cost by 1
	 * 
	 * @param id is maximum number of student(id)
	 * @param exam is maximum number of exam(id)
	 * @param slot is maximum number of slot
	 * @param schec_exam is the schedule of the exam
	 * @param student_exam is the exam schedule of each student
	 * @return cost
	 */

	public static int CalculatingCost(int id,int exam,int slot,boolean[][] schec_exam,boolean[][] student_exam){
		int cost=0;
		//looping on student
		for(int h=1;h<id;h++){
			//first looping on exam
			for(int i=1;i<exam;i++){
				//2nd looping on exam
				for(int j=1;j<exam;j++){
					//looping on slot
					for(int k=0;k<slot-1;k++){
						if(i!=j && schec_exam[k][i] && schec_exam[k+1][j]&&student_exam[h][i]&&student_exam[h][j])
							cost++;
					}
				}
			}
		}
		return cost;
	}

	/**
	 * Move a schedule to new designated time slot
	 * 
	 * @param exammoved the exam that will be moved
	 * @param prev_slot the exam time slot before moved
	 * @param newslot the exam new time slot
	 * @param exam the maximum number of exam (id)
	 * @param schec_exam the exam schedule
	 * @param clashed to check if the new slot is clashed
	 */

	public static void MoveSchedule(int exammoved,int prev_slot,int newslot,int exam, boolean[][] schec_exam,boolean[][] clashed){
		boolean clash=false;

		for(int k=1;k<exam;k++){
			if(exammoved!=k && schec_exam[newslot][k] && clashed[exammoved][k]) clash=true;
		}

		if(!clash){
			schec_exam[newslot][exammoved]=true;
			schec_exam[prev_slot][exammoved]=false;
		}		
	}

	/**
	 * Reset the schedule to previous feasible schedule
	 * 
	 * @param exammoved the exam that already moved
	 * @param prev_slot previous time slot of exammoved
	 * @param newslot the current exammoved timeslot
	 * @param schec_exam the schedule of the exam
	 */

	public static void ResetSchedule(int exammoved,int prev_slot,int newslot,boolean[][] schec_exam){
		schec_exam[newslot][exammoved]=false;
		schec_exam[prev_slot][exammoved]=true;
	}
	
	/**
	 * Reset the exam schedule to the initial result from greedy algorithm
	 * 
	 * @param init_schec initial result
	 * @param schec_exam schedule already change for parameter x in Simulated Annealing
	 * @param slot maximum number of slot(id)
	 * @param exam maximum number of exam(id)
	 */

	public static void ResetToInitial(boolean[][] init_schec,boolean[][] schec_exam, int slot,int exam){
		for(int i=1;i<slot;i++){
			for(int j=1;j<exam;j++){
				if(init_schec[i][j]){
					schec_exam[i][j]=true;
				}else{
					schec_exam[i][j]=false;
				}
			}
		}
	}


	/**
	 * Calculate the slot that being used in the end
	 * 
	 * @param slot maximum number of slot(id)
	 * @param slotused the slot that being used
	 * @return count
	 */

	public static int CalculatingSlotUsed(int slot,int exam,boolean[][] schec_exam){
		int count=0;
		for(int i=0;i<slot;i++){
			for(int j=0;j<exam;j++){
				if(schec_exam[i][j]) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	/**
	 * Printing the schedule of the exam. I will print out the time slot and the exam associated
	 * with the time slot
	 * 
	 * @param slot
	 * @param exam
	 * @param schec_exam
	 */

	public static void printSchedule(int slot,int exam,boolean[][] schec_exam){
		for(int i=0;i<slot;i++){
			System.out.println("Time Slot "+(i+1));
			System.out.print("Exam :");
			for(int j=1;j<exam;j++){
				if(schec_exam[i][j]){
					System.out.print(" "+j);
				}
			}
			System.out.println();
			System.out.println("========================");
		}
	}

	//###################################### Main progam #####################################//

	public static void main (String[] args) throws IOException{
		File input=new File("hecdata.xls");
		Workbook workbook;

		int slot_id=20;
		int student_id=2824;
		int exam_id=81;

		boolean[][] StudentExam=new boolean[student_id][exam_id];
		boolean[][] ExamClashed=new boolean[exam_id][exam_id];
		boolean[][] ScheduleExam=new boolean[slot_id][exam_id];
		boolean[][] InitialScheduleExam=new boolean[slot_id][exam_id];
		boolean[] exam_used=new boolean[exam_id+1];
		boolean[] slot_used=new boolean[slot_id];
		int[] examcount=new int[exam_id];
		int[] examclashcount=new int[exam_id];
		int[] rank=new int[exam_id+1];

		int initial_cost;
		int number_slot_used;
		int best_cost;
		int cost;

		//initialize random;
		Random r=new Random();

		//all cell in student test is false
		initial2array(StudentExam);
		initial2array(ExamClashed);
		initial2array(ScheduleExam);
		initial2array(InitialScheduleExam);
		initial1array(exam_used);
		initial1array(slot_used);

		//filling test data for each student. if true, student take the test
		try {
			workbook = Workbook.getWorkbook(input);
			Sheet sheet = workbook.getSheet(0);

			//loop on row
			for (int j = 3; j <2826; j++) {
				//loop on column
				for (int i = 1; i <8; i++) {
					Cell cell = sheet.getCell(i, j);
					if(cell.getContents()!=""){
						StudentExam[j-2][Integer.parseInt(cell.getContents())]=true;
					}
				}
			}

			workbook.close();

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//counting how many student took an exam_id
		for(int i=1;i<exam_id;i++){
			for(int j=1;j<student_id;j++){
				if(StudentExam[j][i]){
					examcount[i]=examcount[i]+1;
				}
			}
		}

		//checking if 2 exams is clashed
		for(int i=1;i<student_id;i++){
			for(int j=1;j<exam_id;j++){
				for(int k=1;k<exam_id;k++){
					if(j!=k && StudentExam[i][j]&&StudentExam[i][k]){
						ExamClashed[j][k]=true;
						ExamClashed[k][j]=true;
					}
				}
			}
		}

		//counting how many clashes each exam have
		for(int i=1;i<exam_id;i++){
			for(int j=1;j<exam_id;j++){
				if(ExamClashed[i][j]) examclashcount[i]=examclashcount[i]+1;
			}
		}

		//###################################### Greedy Algorithm #####################################//
		System.out.println("------------------Greedy Algorithm----------------");

		//order exam based on how many exam clashed
		exam_used[0]=true;
		int count_rank=1;
		int num=1;
		while(num!=81){

			for(int j=num;j<exam_id;j++){
				if(!exam_used[j]){
					if(examclashcount[num]<examclashcount[j]){
						num=j;
					}
				}
			}

			rank[count_rank]=num;
			exam_used[num]=true;
			count_rank++;

			for(int i=1;i<rank.length;i++){
				if(!exam_used[i]){
					num=i;
					break;
				}
			}
		}

		//check the rank
		//for(int i=1;i<exam_id;i++){
		//	System.out.println("Rank ["+i+"] is exam "+rank[i]+" with as many as "+examclashcount[rank[i]]+" clashes");
		//}

		//placing the the exam in time slot
		/*
		 * if the exam not clashed with any exam already scheduled in slot j,
		 * or slot j is empty
		 * schedule that exam at j
		 */

		//placed by the rank
		boolean clashed=false;

		for(int i=1;i<rank.length;i++){
			for(int j=0;j<slot_id;j++){
				if(!slot_used[j]){
					ScheduleExam[j][rank[i]]=true;
					InitialScheduleExam[j][rank[i]]=true;
					slot_used[j]=true;
					break;
				}else{
					for(int k=1;k<exam_id;k++){
						if(rank[i]!=k && ScheduleExam[j][k] && ExamClashed[rank[i]][k]) clashed=true;
					}

					if(!clashed){
						ScheduleExam[j][rank[i]]=true;
						InitialScheduleExam[j][rank[i]]=true;
						break;
					}
				}
				clashed=false;
			}
		}

		System.out.println("------------------Greedy Algorithm Result----------------");
		//-----------------------------counting the cost-----------------------------------------
		cost=CalculatingCost(student_id,exam_id,slot_id,ScheduleExam,StudentExam);
		number_slot_used=CalculatingSlotUsed(slot_id,exam_id,ScheduleExam);
		initial_cost=cost;
		best_cost=initial_cost;
		System.out.println("Cost :"+initial_cost);
		System.out.println("Number of slot :"+number_slot_used);
		System.out.println();

		//--------------------------See result of greedy algorithm-------------------------------
		printSchedule(slot_id,exam_id,ScheduleExam);

		//check if all exam is scheduled
		/*
		int exam_count=0;
		for(int i=1;i<exam_id;i++){
			for(int j=0;j<slot_id;j++){
				if(ScheduleExam[j][i]){
					System.out.println("Exam "+rank[i]+" is scheduled");
					exam_count++;
				}
			}
		}

		System.out.println("Number exam scheduled "+exam_count);
		 */

		//############################# Descent Algorithm /Simulated Annealing ###########################//

		//try to move one schedule (from rank 1) and see the new cost

		System.out.println();
		System.out.println("==================Simulated Annealing/Descent Algorithm======================");

		double[] var_temp={20.0,20.0,10.0,10.0};
		double[] var_cp={0.9,0.1,0.9,0.1};
		int itr=1;

		for (int a=0;a<var_temp.length;a++){
			double temp=var_temp[a];	//temperature
			double cp=var_cp[a];	//cooling parameter
			double prob;
			int previous_slot=0;
			int new_slot;
			int exam_moved;
			int DescentLoop=0;
			int diff;
			
			System.out.println("==================Simulated Annealing Result ["+itr+"]=======================");
			System.out.println("t	: "+temp+",r	: "+cp);
			
			while(DescentLoop<10){
				exam_moved=r.nextInt(80)+1;
				new_slot=r.nextInt(20);
				prob=r.nextDouble();

				for(int i=1;i<slot_id;i++){
					if(ScheduleExam[i][exam_moved]) previous_slot=i;
				}

				MoveSchedule(exam_moved,previous_slot,new_slot,exam_id,ScheduleExam,ExamClashed);
				cost=CalculatingCost(student_id,exam_id,slot_id,ScheduleExam,StudentExam);
				if(cost<best_cost){
					best_cost=cost;
					DescentLoop=0;
					//System.out.println("Descent diff(-)  "+DescentLoop);
				}else{
					diff=cost-best_cost;
					if(prob<Math.exp(-1*diff/temp)){
						best_cost=cost;
						DescentLoop=0;
						//System.out.println("Descent diff(+) "+DescentLoop);
					}else{
						ResetSchedule(exam_moved,previous_slot,new_slot,ScheduleExam);
						DescentLoop++;
						//System.out.println("Descent "+DescentLoop);
					}
				}
				temp=cp*temp;
			}
			
			number_slot_used=CalculatingSlotUsed(slot_id,exam_id,ScheduleExam);
			System.out.println("Cost :"+best_cost);
			System.out.println("Number of slot :"+number_slot_used);
			printSchedule(slot_id,exam_id,ScheduleExam);
			System.out.println();
			ResetToInitial(InitialScheduleExam,ScheduleExam, slot_id,exam_id);
			itr++;
			best_cost=initial_cost;
		}
	}
}
