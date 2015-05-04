package com.mkyong;
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ilog.concert.*;
import ilog.cplex.*;
import java.util.*;
import java.lang.Math;

public class simReopt{
        public static void main(String[] args) {
        //double[] gap=new double[10]; //record gaps for different k.
        //double[] var=new double[10];
           try{

             int d=10; //every advertiser has 10 in budget.
             double alpha=1.2; //demand to supply ratio.
             Random r=new Random();
             double[] gap=new double[10]; //record gaps for different k.
             double[] var=new double[10];
             int sim_lp=10;
             int lp_num=10;
             for(int i=1;i<=10;i++){
                // sample 10 LPs.
                double[] gap100=new double[sim_lp];
                for(int simnum=0;simnum<sim_lp;simnum++){
                int k=100*i; //number of advertisers.
                
                double ds=(alpha-1)*k*d-Math.sqrt(k)*d; // budget for the sink advertiseri
                                
                // generate c matrix according to uniform distribution
                double[] c1=new double[k];
                double[] c2=new double[k];
                for(int j=0;j<k;j++){
                  r=new Random();
                  c1[j]=r.nextDouble();
                  r=new Random();
                  c2[j]=r.nextDouble();
                }
                //construct LP.
                IloCplex cplex = new IloCplex();
                int I=2;
                int J=k;
                double[] lb=new double[I*J];
                double[] ub=new double[I*J];

                for(int ii=0; ii<I*J; ii++){
                  lb[ii]=0;
                }

                for(int ii=0; ii<I*J; ii++){
                  ub[ii]=1;
                }
                
                IloNumVar[] x = cplex.numVarArray(I*J, lb, ub);
                double[] objvals = new double[I*J];
                for (int kk=0;kk<I*J;kk++){
                     if(kk<J){
                         objvals[kk]=k*d*alpha*0.25*c1[kk];
                     }
                     else{
                         objvals[kk]=k*d*alpha*0.75*c2[kk-J];
                     }
                }
                
                cplex.addMaximize(cplex.scalProd(x, objvals));
              
                for(int ii=0;ii<I;ii++){
                  IloLinearNumExpr expr = cplex.linearNumExpr();
                  for(int jj=0;jj<J;jj++){
                      expr.addTerm(1.0, x[J*ii+jj]);
                  }
                  cplex.addLe(expr, 1);
                }

                for(int jj=0;jj<J;jj++){
                  IloLinearNumExpr expr = cplex.linearNumExpr();
                  IloLinearNumExpr expr1 = cplex.linearNumExpr();
                  
                  expr.addTerm(-k*d*alpha*0.25, x[jj]);
                  expr.addTerm(-k*d*alpha*0.75, x[J+jj]);
                  expr1.addTerm(k*d*alpha*0.25, x[jj]);
                  expr1.addTerm(k*d*alpha*0.75, x[J+jj]);

                  cplex.addLe(expr,-d);
                  cplex.addLe(expr1,d);
                }

                //Solve the LP, and implement the policy in the simulation settings for multiple times (10 here).
                double[] val =new double[I*J];
                if (cplex.solve() ){
                    val = cplex.getValues(x);
                }
                double upperBound=cplex.getObjValue();
                cplex.end();
                //x1 and x2 denotes the fractions of assigned class 1 and 2 users. 
                double x1=0;
                double x2=0;
                  
                for (int s=0;s<val.length/2;s++){
                      x1+=val[s];
                      x2+=val[val.length/2+s];
                }
                //   System.out.println(x1);
                //   System.out.println(x2); 
                double t=0;
                double revenue=0;
                double lambda=(double)1/(double)(k*d*alpha);
                //number of simulation settings
                for (int s=0;s<sim_lp;s++){
                   t=0;
                   J=k;
                   //fulfilled set
                   HashSet<Integer> fulfill=new HashSet<Integer>();
                   int[] state=new int[J];
                   int sink=0;
                   for(int jj=0;jj<k;jj++){
                         state[jj]=0;
                   }
                   int arrive=0;
                   while(t<=1){
                      r=new Random();
                      double u=r.nextDouble();
                      t+=Math.log(1-u)*(-lambda);
                      arrive++;
                      r=new Random();
                      u=r.nextDouble();
                      int index=0; //which to assign to
                      if(u<0.5*t){
                       //class 1
                        if(sink<ds){
                          index=sampleIndices(val,0,fulfill,x1,false);
                        }
                        else{
                          index=sampleIndices(val,0,fulfill,x1,true);
                        }
                      }
                      else{
                        if(sink<ds){
                          index=sampleIndices(val,1,fulfill,x2,false);
                        }
                        else{
                          index=sampleIndices(val,1,fulfill,x2,true);
                        }
                      } 
                      if(index==-1){
                          break;
                      }
                      if(index==-2){
                          sink++;
                          continue;
                      }
                      state[index]++;
                      if(u<0.5*t){
                         revenue+=c1[index];
                      }
                      else{
                         revenue+=c2[index];
                      }
                      if(state[index]==10){
                            fulfill.add(new Integer(index));
                            ArrayList<Integer> indices=new ArrayList<Integer>();
                            for (int ii = 0; ii < k; ii++){
                                if(fulfill.contains(new Integer(ii))==false){
                                   indices.add(new Integer(ii));
                                }
                            }
                          
                            if(indices.size()==0){
                               break;
                            }
                            cplex = new IloCplex();
                            I=2;
                            J=J-1;
                            //System.out.println(J+" "+arrive);
                            for (int ii=0;ii<indices.size();ii++){
                               //  System.out.print(state[indices.get(ii).intValue()]+" ");
                            } 
                            lb=new double[I*J];
                            ub=new double[I*J];

                            for(int ii=0; ii<I*J; ii++){
                               lb[ii]=0;
                            }

                            for(int ii=0; ii<I*J; ii++){
                               ub[ii]=1;
                            }

                            x = cplex.numVarArray(I*J, lb, ub);
                            objvals = new double[I*J];
                            for (int kk=0;kk<I*J;kk++){
                                if(kk<J){
                                      objvals[kk]=k*d*alpha*(0.25*t+0.25)*(1-t)*c1[indices.get(kk).intValue()];
                                }
                                else{
                                      objvals[kk]=k*d*alpha*(1-t-0.25*(1-t*t))*c2[indices.get(kk-J).intValue()];
                                }
                            }

                            cplex.addMaximize(cplex.scalProd(x, objvals));

                            for(int ii=0;ii<I;ii++){
                               IloLinearNumExpr expr = cplex.linearNumExpr();
                               for(int jj=0;jj<J;jj++){
                                   expr.addTerm(1.0, x[J*ii+jj]);
                               }
                               cplex.addLe(expr, 1);
                            }

                            for(int jj=0;jj<J;jj++){
                               IloLinearNumExpr expr = cplex.linearNumExpr();
                               IloLinearNumExpr expr1 = cplex.linearNumExpr();

                               expr.addTerm(-k*d*alpha*(0.25*t+0.25)*(1-t), x[jj]);
                               expr.addTerm(-k*d*alpha*(1-t-0.25*(1-t*t)), x[J+jj]);
                               expr1.addTerm(k*d*alpha*(0.25*t+0.25)*(1-t), x[jj]);
                               expr1.addTerm(k*d*alpha*(1-t-0.25*(1-t*t)), x[J+jj]);

                               cplex.addLe(expr,-(d-state[indices.get(jj).intValue()]));
                               cplex.addLe(expr1,d-state[indices.get(jj).intValue()]);
                            }
                            if(cplex.solve()){
                                double[] newVal=new double[I*J];
                                newVal=cplex.getValues(x);
                                int size=0;
                                for(int ii=0;ii<k;ii++){
                                   if(fulfill.contains(ii)==true){
                                        val[ii]=0;
                                        val[ii+k]=0;
                                   }
                                   else{
                                        val[ii]=newVal[size];
                                        val[ii+k]=newVal[size+J];
                                        size++;
                                   }
                                }
                            }
                            cplex.end();
                            x1=0;
                            x2=0;
                            for (int ss=0;ss<val.length/2;ss++){
                               x1+=val[ss];
                               x2+=val[val.length/2+ss];
                            }
                      }
                  }
                  for(int jj=0;jj<k;jj++){
                         revenue+=(state[jj]-10);
                  }
                  }
                  revenue=revenue/sim_lp;
                  gap[i-1]+=Math.abs(revenue-upperBound)/k;
                  System.out.println("revenue:"+revenue+" "+"upperbound"+upperBound+" ");
                  gap100[simnum]=Math.abs(revenue-upperBound)/k;
               }
                 gap[i-1]=gap[i-1]/sim_lp;
                 for(int jj=0;jj<sim_lp;jj++){
                    var[i-1]+=Math.pow((gap100[jj]-gap[i-1]),2);
                 }
                 var[i-1]=Math.sqrt(var[i-1]/sim_lp);
             }
             /*for(int i=0;i<10;i++){
               System.out.println("gap"+i+" " gap[i]+",");
             }*/
             //System.out.println();
             /*for(int i=0;i<10;i++){
               System.out.println("var" +i+ " "+ var[i]+",");
             }*/
             File file = new File("output.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
                        for(int i=0;i<10;i++){
                        bw.write("gap"+i+" " + gap[i]+","+"\n");
                        bw.write("var" +i+ " "+ var[i]+",");
                        }
			//bw.write(content);
			bw.close();
           }
           catch (Exception e){
               System.err.println("Concert exception caught: " + e);
           }
           
   }

   // Get the index of the advertiser according to the probabilities.
   private static int sampleIndices(double[] probability, int index, HashSet<Integer> fulfill, double s, boolean flag){
                       ArrayList<Integer> indices=new ArrayList<Integer>();
                       int length=(int) probability.length/2;
                       for (int i = 0; i < length; i++){
                                if(fulfill.contains(new Integer(i))==false){
                                   indices.add(new Integer(i));
                                }
                       }
                       if(indices.size()>0){
                       double[] p=new double[indices.size()+1];
                       double sum=0;
                       for(int i=0;i<indices.size();i++){
                          p[i]=probability[indices.get(i).intValue()+index*length];
                          sum+=p[i];
                       }
                       if(sum==0){
                            for(int i=0;i<indices.size();i++){
                                p[i]=(double)1/(double)indices.size();
                            }
                            sum=1;
                       }
                       if(flag==true){
                          p[0]=p[0]/sum;
                          for(int i=1;i<indices.size();i++){
                             p[i]=(p[i]/sum)+p[i-1];
                          }
                          p[indices.size()]=1;
                       }
                       else{
                          p[0]=p[0]/sum*s;
                          for(int i=1;i<indices.size();i++){
                           p[i]=(p[i]/sum*s)+p[i-1];
                          }
                          p[indices.size()]=1;
                       }
                       Random r=new Random();
                       double n=r.nextDouble();
                       int low=-1;
                       int high=indices.size();
                       while(low<high-1){
                           int mid=(high+low)/2;
                           if(p[mid]>n){
                              high=mid;
                           }
                           else{
                              low=mid;
                           }
                       }
                       if(high<indices.size()){
                          return indices.get(high).intValue();
                       }
                       else{
                          return -2;
                       }
                      }
                      else{
                       return -1;
                      }
    }
}
