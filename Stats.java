/*745202 村上弘樹*/

import java.util.ArrayList;

public class Stats {
    Double max = 0.0;
    Double min = 100.0;  //テストは最高値が100だから
    Double sum = 0.0;
    Double count = 0.0;


    public void put(ArrayList<Double> gradeList) {
        for(Double grade : gradeList) {
            count++;
            max = (max < grade) ? grade : max;
            min = (min > grade) ? grade : min;
            sum += grade;
        }
    }


    Double max() {
        return max;
    }

    Double min() {
        return min;
    }

    Double average() {
        return (sum / count);
    }
}