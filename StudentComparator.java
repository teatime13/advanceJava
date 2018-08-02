/*745202 村上弘樹*/

import java.util.Comparator;

public class StudentComparator implements Comparator<Student4>{
    public int compare(Student4 student1, Student4 student2){
        Double score1 = student1.finalResult;
        Double score2 = student2.finalResult;
        if(score1 < score2)      return 1;
        else if(score1 > score2) return -1;
        return 0;
    }
}