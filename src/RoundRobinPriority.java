import java.util.ArrayList;
import java.util.List;
class Process{
    String name;
    int arrivalTime;
    int burstTime;
    int remainingBurstTime;
    int priority;
    int completionTime;
    int waitTime;
    Process(String name, int arrivalTime, int burstTime, int priority){
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        remainingBurstTime = burstTime;
        this.priority = priority;
    }
}
public class RoundRobinPriority {
    public static final int TIME_QUANTUM = 2;
    private List<Process> waitingQueue;
    private List<Process> readyQueue;
    private List<Process> finishQueue;
    private int currentTime = 0;

    RoundRobinPriority(){
        waitingQueue = new ArrayList<>();
        readyQueue = new ArrayList<>();
        finishQueue = new ArrayList<>();
    }
    public void addToWaitingQueue(Process process){
        waitingQueue.add(process);
    }
    public List<Process> runAlgorithm(){
        int highestPriority;
        int remainingBurstTime;

        System.out.println("Round Robin Priority Scheduling (Time Quantum = " + TIME_QUANTUM + ")\n");
        System.out.println("Gantt Chart:");

        //scheduling is complete when there is no longer any processes in the waiting queue or ready queue
        while (!waitingQueue.isEmpty() || !readyQueue.isEmpty()) {
            //the process is moved from waiting to ready if the current time matches the arrival time
            //I used <= rather than == because the time does not necessarily increment by 1
            for(int i = 0; i < waitingQueue.size(); i++){
                if(waitingQueue.get(i).arrivalTime <= currentTime){
                    readyQueue.add(waitingQueue.get(i));
                    finishQueue.add(waitingQueue.get(i));
                    waitingQueue.remove(i);
                    i--;
                }
            }

            //this loop is where scheduling takes place
            if(!readyQueue.isEmpty()) {
                //The lowest priority is initialized as the first element in the ready queue
                highestPriority = readyQueue.get(0).priority;

                //The highest priority of the available processes is selected
                for (Process p : readyQueue) {
                    if (p.priority < highestPriority) {
                        highestPriority = p.priority;
                    }
                }

                for(int i = 0; i < readyQueue.size(); i++) {
                    if (readyQueue.get(i).priority == highestPriority) {

                        //this statement is used since a process might finish during a time quantum
                        remainingBurstTime = Math.max(readyQueue.get(i).remainingBurstTime - TIME_QUANTUM, 0);

                        //these statements are called only if the process is finished during the time quantum
                        if(remainingBurstTime == 0){
                            //the time is incremented in this manor since the algorithm is not preemptive and only checks after the process or time quantum ends
                            if (readyQueue.get(i).remainingBurstTime < TIME_QUANTUM){
                                //this statement is used if the process finishes before the time available from the time quantum ends
                                currentTime += readyQueue.get(i).remainingBurstTime;
                            }
                            else{
                                //this statement is called if the process finishes exactly at the end of the time quantum
                                currentTime += TIME_QUANTUM;
                            }
                            System.out.print("[" + readyQueue.get(i).name + "]-" + currentTime + " ");

                            readyQueue.get(i).remainingBurstTime = 0;
                            readyQueue.get(i).completionTime = currentTime;
                            readyQueue.get(i).waitTime = readyQueue.get(i).completionTime - (readyQueue.get(i).arrivalTime + readyQueue.get(i).burstTime);
                            readyQueue.remove(i);
                            i--;
                        }
                        //These statements are called if the process does not finish during the time quantum
                        else{
                            currentTime += TIME_QUANTUM;
                            System.out.print("[" + readyQueue.get(i).name + "]-" + currentTime + " ");
                            readyQueue.get(i).remainingBurstTime = remainingBurstTime;

                            //these statements push the current process to the end of the queue
                            //because of that the algorithm uses FCFS when deciding between processes of the same priority
                            readyQueue.add(readyQueue.get(i));
                            readyQueue.remove(i);
                        }
                    }
                }
            }
            //The time is incremented by 1 if the waiting queue still has processes but the ready queue is empty
            else{
                currentTime++;
            }
        }
        System.out.println("\n");
        //A copy of the processes in their initial arrival order is returned
        return finishQueue;
    }

    public static void main(String[] args) {
        RoundRobinPriority roundRobinPriority = new RoundRobinPriority();
        List<Process> finishQueue;
        int totalWaitTime = 0;
        double averageWaitTime;

        roundRobinPriority.addToWaitingQueue(new Process("p1",0,2,2));
        roundRobinPriority.addToWaitingQueue(new Process("p2",1,1,1));
        roundRobinPriority.addToWaitingQueue(new Process("p3",2,8,4));
        roundRobinPriority.addToWaitingQueue(new Process("p4",3,4,2));
        roundRobinPriority.addToWaitingQueue(new Process("p5",4,5,3));

        finishQueue = roundRobinPriority.runAlgorithm();

        for (Process p : finishQueue) {
            totalWaitTime += p.waitTime;
            System.out.println(p.name + " wait time: " + p.waitTime + " = " +
                    p.completionTime + " - (" + p.arrivalTime + " + " + p.burstTime + ")");
        }

        averageWaitTime = (double) totalWaitTime / finishQueue.size();
        System.out.println("\nAverage Wait Time: " + averageWaitTime);

    }
}