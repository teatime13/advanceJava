/*745202 村上弘樹*/

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

public class GradeChecker6 {
    /**
     *引数にcsv形式の成績を受け取りID，最終成績，試験の点数，課題の合計点，小テストの受験回数，評定を表示
     */
    String outputFile = null;
    HashMap<Integer,Student4> studentGradeMap = new HashMap<>();
    ArrayList<String> outputFileStrings = new ArrayList<>();

    void run(String[] args) throws IOException{
        this.makeGradeListToHashMap(args);
        this.mainPrintOutGrade();    //成績の算出
        this.mainPrintOutStats();
        if(outputFile != null)
            this.writeStringsToFile();
    }

//--------------それぞれのテストの点数を出す中心処理----------
    //argsからファイル名を探してHashMapに入れる
    void makeGradeListToHashMap(String[] fileNames) throws IOException{
        Integer maxStudentNum;
        HashMap<String,String> fileNameMap = new HashMap<>();
        for(Integer i=0; i < fileNames.length-1; i++) {
            if(Objects.equals(fileNames[i],"-exam"))                fileNameMap.put("exam", fileNames[i+1]);
            else if(Objects.equals(fileNames[i], "-assignments"))   fileNameMap.put("assignments", fileNames[i+1]);
            else if(Objects.equals(fileNames[i], "-miniexam"))      fileNameMap.put("miniexam", fileNames[i+1]);
            else if(Objects.equals(fileNames[i], "-output"))        outputFile = fileNames[i+1];
        }
        if(fileNameMap.size() < 1)  //ファイルが指定されなかった場合メッセージを出して終了
            this.printOutNoFileMessage();
        maxStudentNum = this.checkMaxStudentNum(fileNameMap);
        this.initStudentGradeMap(maxStudentNum);
        this.processingOfFiles(fileNameMap);
    }


//--------------それぞれのファイルの処理に分岐させる-------------
    void processingOfFiles(HashMap<String,String> fileNameMap) throws IOException{
        String fileName;
        fileName = fileNameMap.get("exam");
        if(fileName != null)
            this.addExamMap(fileName);
        fileName = fileNameMap.get("assignments");
        if(fileName != null)
            this.csvToListGradeList(fileName, 1);
        fileName = fileNameMap.get("miniexam");
        if(fileName != null)
            this.csvToListGradeList(fileName, 2);
    }




//--------------最大の学生番号を探す--------------------
    //最大の学生番号を探す
    Integer checkMaxStudentNum(HashMap<String,String> fileNameMap) throws IOException{
        String lastLine;
        Integer lastStudentNumber;
        Integer maxStudentNum = 0;
        for(String key : fileNameMap.keySet()) {
            lastLine = this.getLastLine(fileNameMap.get(key));
            lastStudentNumber = this.getLastStudentNumber(lastLine);
            if(lastStudentNumber > maxStudentNum)
                maxStudentNum = lastStudentNumber;
        }
        return maxStudentNum;
    }


    //最後の行を取ってくる
    String getLastLine(String fileName) throws IOException{
        String line, lastLine=null;
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        while((line = in.readLine()) != null)
            lastLine = line;
        return lastLine;
    }


    //最初の一文字目にある学生番号を数値に変換する
    Integer getLastStudentNumber(String line) {
        String[] splitedLine = line.split(",");
        return Integer.valueOf(splitedLine[0]);
    }

    




//------------------有効なファイルが存在しなかった----------
    void printOutNoFileMessage() throws IOException{
        String errorMessage = String.format("ERROR: 何もファイルが指定されていません．%n"
                                            +"java GradeChecker5 [OPTIONS]%n"
                                            +"OPTIONS%n"
                                            +"-exam        <EXAM.CSV>%n"
                                            +"-assignments <ASSIGNMENTS.CSV>%n"
                                            +"-miniexam    <MINIEXAM.CSV>%n"
                                            +"-output      <RESULT_FILE>%n");
        if(outputFile == null)
            System.out.printf(errorMessage);
        else {
            outputFileStrings.add(errorMessage);
            writeStringsToFile();
        }
        System.exit(0);
    }


//-------------------HashMapを初期化------------
    //3つのArrayList型の情報をHashMap型の<Integer,Student>にまとめる
    void initStudentGradeMap(Integer maxStudentNum) {
        for(Integer studentNum=1; studentNum <= maxStudentNum; studentNum++) {
            Student4 tempStudent = new Student4();
            tempStudent.id = studentNum;
            tempStudent.examScore = "0";
            tempStudent.assignments = 0;
            tempStudent.miniexams = 0;
            studentGradeMap.put(studentNum, tempStudent); //学生番号を鍵として情報を収集
        }
    }



//--------------------試験成績のhashMapに入れる-----------
    //ファイルから一行ずつとって渡す
    void addExamMap(String file) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        Integer nowStudentNum = 0;  //上から何行目を処理しているのかカウント
        while((line = in.readLine()) != null)
            nowStudentNum = this.splitListAndSentNumberToNext(line, nowStudentNum);
    }

    //文字列を,で区切って次に
    Integer splitListAndSentNumberToNext(String line, Integer nowStudentNum) {
        String[] items = line.split(",");
        Integer numberOfList = Integer.valueOf(items[0]);
        nowStudentNum = this.checkNumberEqualNowListSize(numberOfList, nowStudentNum);
        this.addExamGrade(Integer.valueOf(items[0]), items[1]);
        return nowStudentNum;
    }

    //今のファイル内のデータ数と連番の学生番号が同じ数か、同じなら戻って、なければ次に
    Integer checkNumberEqualNowListSize(Integer numberOfList, Integer nowStudentNum) {
        for(nowStudentNum += 1;;nowStudentNum++) {   //現在の生徒の番号をカウント。二重に処理しないよう+1
            if(Objects.equals(numberOfList, nowStudentNum)){
                return nowStudentNum;
            }
            else{
                this.addAbsentStudent(nowStudentNum);
            }
        }
    }


    //欠席した生徒の成績をnullとして配列に入れる
    void addAbsentStudent(Integer numberOfAbsent) {
        this.addExamGrade(numberOfAbsent, null);
    }

    //受け取った成績を配列に入れる
    void addExamGrade(Integer studentNum, String gradeData) {
        Student4 tempStudent = studentGradeMap.get(studentNum);
        if(gradeData == null)
            tempStudent.examScore = null;
        else
            tempStudent.examScore = gradeData;
        studentGradeMap.put(studentNum, tempStudent);
    }



//------------------CSVファイルを扱いやすいように変換-------------------
    void csvToListGradeList(String file, Integer flag) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while((line = in.readLine()) != null)
            this.splitListBySemicolon(line, flag);
    }


    //文字列を,で区切って次に
    void splitListBySemicolon(String line, Integer flag) {
        String[] items = line.split(",");
        this.checkProgressTaskOrMiniExam(items, flag);
    }


    void checkProgressTaskOrMiniExam(String[] items, Integer flag) {
        if(Objects.equals(flag, 1))
            this.convertBlankToZero(items);
        else
            this.makeListForCount(items);
    }



//-----------課題の合計値をHashMapに格納------------
    //課題の空白を0にしたリストを作成
    void convertBlankToZero(String[] items) {
        ArrayList<Integer> convertedBlankToZeroList =  new ArrayList<>();
        Integer tempItem_i;
        for(String item : items) {
            if(Objects.equals(item, ""))
                tempItem_i = 0;
            else
                tempItem_i = Integer.valueOf(item);
            convertedBlankToZeroList.add(tempItem_i);
        }
        this.sumTaskGradeList(convertedBlankToZeroList);
    }

    
    //リスト列を足して合計値を求める
    void sumTaskGradeList(ArrayList<Integer> blankToZeroGradeList) {
        Integer sum = 0;
        Integer i = 0;
        for(Integer taskGrade : blankToZeroGradeList) {
            if(!Objects.equals(i, 0))   //リストの一つ目、つまり学生証番号は足さない
                sum += taskGrade;
            i++;
        }
        this.addSumTaskGradeList(sum, blankToZeroGradeList.get(0));
    }

    
    //合計値をtaskGradeListに追加
    void addSumTaskGradeList(Integer sum, Integer studentNum) {
        Student4 tempStudent;
        tempStudent = studentGradeMap.get(studentNum);
        tempStudent.assignments = sum;
        studentGradeMap.put(studentNum, tempStudent);
    }


//------------受験回数をHashMapに格納---------------

    //カウント用のリストを作成
    void makeListForCount(String[] items) {
        ArrayList<Integer> forCountList = new ArrayList<>();
        for(String item : items) {
            if(Objects.equals(item, ""))
                forCountList.add(-1);
            else
                forCountList.add(Integer.valueOf(item));
        }
        this.countMiniExamList(forCountList, Integer.valueOf(items[0]));
    }

    //受験回数を求める
    void countMiniExamList(ArrayList<Integer> forCountList, Integer studentNum) {
        Integer count = 0;
        for(Integer grade : forCountList) {
            if(0 <= grade) {    //0以上10未満ならば正しい値と判断
                count++;
            }
        }
        this.addCountMiniExamList(count, studentNum);
    }


    //受験回数をリストに加える
    void addCountMiniExamList(Integer count, Integer studentNum) {
        Student4 tempStudent;
        tempStudent = studentGradeMap.get(studentNum);
        tempStudent.miniexams = count - 1;
        studentGradeMap.put(studentNum, tempStudent);
    }


//---------------最終出力------------------

    // 一つずつHashMapから値をとってくる
    void mainPrintOutGrade() {
        Student4 tempStudent = studentGradeMap.get(0);
        Double finalResult;
        for(Integer key : studentGradeMap.keySet()) {
            tempStudent = studentGradeMap.get(key);
            finalResult = this.exceptNullAndChoiceResult(tempStudent.id, tempStudent.examScore,
                                                tempStudent.assignments, tempStudent.miniexams);
            tempStudent.finalResult = finalResult;
            studentGradeMap.put(key, tempStudent);
        }
        this.mainAddGradeRankForStudent();  //ランクをつける
        for(Integer key : studentGradeMap.keySet()) {
            tempStudent = studentGradeMap.get(key);
            choicePrintOutFinalResult(tempStudent.id, tempStudent.examScore,
                    tempStudent.assignments, tempStudent.miniexams,tempStudent.finalResult, tempStudent.rank);
        }
    }


    //-------------計算---------------
    
    //試験が欠席の場合と場合分け
    Double exceptNullAndChoiceResult(Integer id, String exam, Integer assign, Integer miniexams) {
        if(exam == null)
            return this.calculationAndGetResultAbsence(id, assign, miniexams);
        else
            return this.calculationAndGetResult(id, exam, assign, miniexams);
    }


    //欠席した場合の出力
    Double calculationAndGetResultAbsence(Integer id, Integer assign, Integer miniexams) {
        Double assignScore = (double)assign * 25 / 60;
        Double miniExamScore = ((double)miniexams / 14) * 5;
        Double finalResult = Math.ceil(assignScore + miniExamScore);
        return finalResult;
    }


    //欠席していない場合の出力
    Double calculationAndGetResult(Integer id, String exam, Integer assign, Integer miniexams) {
        Double examScore = Double.valueOf(exam) * 70 / 100;
        Double assignScore = (double)assign * 25 / 60;
        Double miniExamScore = ((double)miniexams / 14) * 5;
        Double finalResult = Math.ceil(examScore + assignScore + miniExamScore);
        //特別ルール
        if(Double.valueOf(exam) > finalResult && Math.ceil(Double.valueOf(exam)) >= 80)
            finalResult = Math.ceil(Double.valueOf(exam));
        return finalResult;
    }


    //-----------出力---------------
    //試験が欠席の場合と場合分け
    Double choicePrintOutFinalResult(Integer id, String exam, Integer assign, Integer miniexams, Double finalResult, Integer rank) {
        if(exam == null)
            return this.calculationAndPrintOutAbsence(id, assign, miniexams,finalResult, rank);
        else
            return this.calculationAndPrintOut(id, exam, assign, miniexams,finalResult, rank);
    }


    //欠席した場合の出力
    Double calculationAndPrintOutAbsence(Integer id, Integer assign, Integer miniexams, Double finalResult, Integer rank) {
        Double assignScore = (double)assign * 25 / 60;
        Double miniExamScore = ((double)miniexams / 14) * 5;
        String absent_s = String.format("%d,%.1f,,%.4f,%f,K,%d%n",id, finalResult, assign.doubleValue(), miniexams.doubleValue() / 14, rank);
        checkPrintOrWrite(absent_s);
        return finalResult;
    }


    //欠席していない場合の出力
    Double calculationAndPrintOut(Integer id, String exam, Integer assign, Integer miniexams, Double finalResult, Integer rank) {
        Double examScore = Double.valueOf(exam) * 70 / 100;
        Double assignScore = (double)assign * 25 / 60;
        Double miniExamScore = ((double)miniexams / 14) * 5;
        String noAbsent_s = String.format("%d,%.1f,%s,%.4f,%f,%s,%d%n",
                id, finalResult, exam, assign.doubleValue(), miniexams.doubleValue() / 14, this.checkGradeWhichLevel(finalResult, miniexams), rank);
        this.checkPrintOrWrite(noAbsent_s);
        return finalResult;
    }



//------------------受け取った成績の評価をする----------------
    String checkGradeWhichLevel(Double grade_d, Integer miniexams) {
        Integer[] gradeBoundaryList = {90, 80, 70, 60};
        if(grade_d < 60 && miniexams <= 7)       return "※";
        else if(grade_d >= gradeBoundaryList[0]) return "秀";
        else if(grade_d >= gradeBoundaryList[1]) return "優";
        else if(grade_d >= gradeBoundaryList[2]) return "良";
        else if(grade_d >= gradeBoundaryList[3]) return "可";
        else                                     return "不可";
    }


    //ランク用の成績の評価
    String checkGradeWhichLevelForRank(Double grade_d, String examScore, Integer miniexams) {
        Integer[] gradeBoundaryList = {90, 80, 70, 60};
        if(examScore == null)                    return "K";
        else if(grade_d < 60 && miniexams <= 7)  return "※";
        else if(grade_d >= gradeBoundaryList[0]) return "秀";
        else if(grade_d >= gradeBoundaryList[1]) return "優";
        else if(grade_d >= gradeBoundaryList[2]) return "良";
        else if(grade_d >= gradeBoundaryList[3]) return "可";
        else                                     return "不可";
    }


//------------------成績の順位を入れる---------------
    //成績のランクのリストを作成する
    void mainAddGradeRankForStudent() {
        ArrayList<Student4> list = new ArrayList<>();
        list.addAll(studentGradeMap.values());
        Comparator<Student4> comp = new StudentComparator();
        Collections.sort(list, comp);
        list = updateDetailRankList(list);
        addGradeRankForStudent(list);
    }


    //不可、K、※の特殊な事例に対応
    ArrayList<Student4> updateDetailRankList(ArrayList<Student4> list) {
        ArrayList<Student4> noGradeList = new ArrayList<>();
        ArrayList<Student4> kList = new ArrayList<>();
        ArrayList<Student4> astaList = new ArrayList<>();
        ArrayList<Student4> normalList = new ArrayList<>();;
        String gradeRank;
        for(Student4 student : list) {
            gradeRank = checkGradeWhichLevelForRank(student.finalResult,student.examScore, student.miniexams);
            if(Objects.equals(gradeRank, "不可"))
                noGradeList.add(student);
            else if(Objects.equals(gradeRank, "K"))
                kList.add(student);
            else if(Objects.equals(gradeRank, "※"))
                astaList.add(student);
            else
                normalList.add(student);
        }
        return unitList(noGradeList, kList, astaList, normalList);
    }


    //受け取ったリストを結合して一つに
    ArrayList<Student4> unitList(ArrayList<Student4> noGradeList, ArrayList<Student4> kList,
                                ArrayList<Student4> astaList, ArrayList<Student4> normalList) {
        ArrayList<Student4> unitedList = new ArrayList<>();
        for(Student4 student : normalList)
            unitedList.add(student);
        for(Student4 student : noGradeList)
            unitedList.add(student);
        for(Student4 student : kList)
            unitedList.add(student);
        for(Student4 student : astaList)
            unitedList.add(student);
        return unitedList;
    }


    //Studentにランクを入れていく
    void addGradeRankForStudent(ArrayList<Student4> rankList) {
        Student4 tempStudent;
        Integer countRank = 1;
        Integer key = null;
        for(Student4 rankStudent : rankList) {
            key = rankStudent.id();
            tempStudent = studentGradeMap.get(key);
            tempStudent.rank = countRank;
            countRank++;
        }
    }




//--------------------統計情報を計算、出力------------------

    //順にマップから値を取ってリストに入れるくる
    void mainPrintOutStats() {
        Integer limitScore = 60;
        Student4 tempStudent = null;
        ArrayList<Double> studentGrade = new ArrayList<>();
        ArrayList<Double> goodStudentGrade = new ArrayList<>();
        HashMap<String,Integer> countGradeMap = new HashMap<>();
        for(Integer key : studentGradeMap.keySet()) {
            tempStudent = studentGradeMap.get(key);
            studentGrade.add(tempStudent.finalResult);
            if(Double.valueOf(tempStudent.finalResult) >= limitScore)
                goodStudentGrade.add(tempStudent.finalResult);
            countGradeMap =
                    this.countGradeLevel(countGradeMap, tempStudent.finalResult, tempStudent.examScore, tempStudent.miniexams);
        }
        this.initStats(studentGrade, goodStudentGrade);
        this.printOutGradeLevel(countGradeMap);
    }


    //nullなら0を返す
    Integer checkAndNullToZero(Integer count) {
        if(count == null)
            return 0;
        else
            return count;
    }



    //統計情報の初期化を行う
    void initStats(ArrayList<Double> studentGrade, ArrayList<Double> goodStudentGrade) {
        Stats studentStats = new Stats();
        Stats goodStudentStats = new Stats();
        studentStats.put(studentGrade);
        goodStudentStats.put(goodStudentGrade);
        printOutStats(studentStats, goodStudentStats);
    }


    //統計情報の表示を行う
    void printOutStats(Stats studentStats, Stats goodStudentStats) {
        String avg_s = String.format("Average: %.4f (%.4f)%n", studentStats.average(), goodStudentStats.average());
        String max_s = String.format("Max: %.4f (%.4f)%n", studentStats.max(), goodStudentStats.max());
        String min_s = String.format("Min: %.4f (%.4f)%n", studentStats.min(), goodStudentStats.min());
        checkPrintOrWrite(avg_s);
        checkPrintOrWrite(max_s);
        checkPrintOrWrite(min_s);
    }


    //成績の評価をカウントする
    HashMap<String,Integer> countGradeLevel(HashMap<String,Integer> countGradeMap,
            Double finalResult, String examScore, Integer miniexams) {
        String grade = this.checkGradeWhichLevel(finalResult, miniexams);
        //定期試験欠席の場合は強制的にKになるので先に処理する
        if(examScore == null && countGradeMap.get("K") == null)
            countGradeMap.put("K", 1);
        else if(examScore == null)
            countGradeMap.put("K", countGradeMap.get("K") + 1);
        else if(countGradeMap.get(grade) == null)
            countGradeMap.put(grade, 1);
        else
            countGradeMap.put(grade, countGradeMap.get(grade) + 1);
        return countGradeMap;
    }


    //成績の評価の表示
    void printOutGradeLevel(HashMap<String, Integer> countGradeMap){
        String outputString;
        ArrayList<String> gradeLevelList = new ArrayList<String>(Arrays.asList("秀", "優", "良", "可", "不可", "K", "※"));
        ArrayList<String> getGradeList = new ArrayList<String>(Arrays.asList("秀", "優", "良", "可"));
        Double numberOfStudent, numberOfGetGrade;
        Integer gradeCount = null;
        numberOfStudent = countStudentNumber(countGradeMap);
        numberOfGetGrade = countGradeNumber(countGradeMap);
        checkPrintOrWrite(String.format("単位取得率: %.1f (%.0f/%.0f)%n", numberOfGetGrade/numberOfStudent*100, numberOfGetGrade, numberOfStudent));
        for(String gradeLevel : gradeLevelList) {
            gradeCount = checkAndNullToZero(countGradeMap.get(gradeLevel));
            outputString = String.format("%2s:%4d (%.4f％ (%d/%.0f))",
                    gradeLevel, gradeCount, gradeCount/numberOfStudent*100, gradeCount, numberOfStudent);
            if(getGradeList.contains(gradeLevel))
                outputString += String.format(", %.4f％ (%d/%.0f))%n", gradeCount/numberOfGetGrade*100, gradeCount, numberOfGetGrade);
            else
                outputString += String.format(")%n");
            checkPrintOrWrite(outputString);
        }
    }


    //生徒の数を数える
    Double countStudentNumber(HashMap<String, Integer> countGradeMap) {
        Double sum = 0.0;
        for(String key : countGradeMap.keySet())
            sum += countGradeMap.get(key);
        return sum;
    }


    //成績取得者を数える
    Double countGradeNumber(HashMap<String, Integer> countGradeMap) {
        Double sum = 0.0;
        ArrayList<String> keySet = new ArrayList<String>(Arrays.asList("秀", "優", "良", "可"));
        for(String key : keySet)
            sum += countGradeMap.get(key);
        return sum;
    }


//-----------------受け取った文字列を出力するかファイルに入れるのか確認する----------
    void checkPrintOrWrite(String message){
        if(outputFile == null)
            System.out.printf(message);
        else
            outputFileStrings.add(message);
    }



//----------------受け取ったデータをファイルに入れる------------
    void writeStringsToFile() throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter(outputFile));
        for(String message : outputFileStrings)
            out.print(message);
        out.close();
    }




    public static void main(String[] args) throws IOException{
        GradeChecker6 gChecker6 = new GradeChecker6();
        gChecker6.run(args);
    }
}