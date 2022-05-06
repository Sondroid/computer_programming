package cpta;

import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.ExamSpec;
import cpta.exam.Student;
import cpta.exam.Problem;
import cpta.exam.TestCase;
import cpta.exceptions.CompileErrorException;
import cpta.exceptions.FileSystemRelatedException;
import cpta.exceptions.InvalidFileTypeException;
import cpta.exceptions.RunTimeErrorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Grader {
    Compiler compiler;
    Executer executer;

    public Grader(Compiler compiler, Executer executer) {
        this.compiler = compiler;
        this.executer = executer;
    }


    public Map<String,Map<String, List<Double>>> gradeSimple(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-1
        Map<String,Map<String,List<Double>>> result = new HashMap<>();
        try{
            for(Student student: examSpec.students){
                String studentSubmissionDirPath = submissionDirPath + student.id + "/";
                result.put(student.id, gradeProblemSimple(studentSubmissionDirPath, examSpec.problems));
            }
        }
        catch(Exception e){
            ;
        }

        return result;
    }

    private Map<String,List<Double>> gradeProblemSimple(String studentSubmissionDirPath, List<Problem> problems) throws Exception{

        Map<String, List<Double>> problemScoreMap = new HashMap<>();

        for(Problem problem: problems){
            problemScoreMap.put(problem.id, gradeTestCasesSimple(studentSubmissionDirPath, problem));
        }

        return problemScoreMap;
    }

    private List<Double> gradeTestCasesSimple(String studentSubmissionDirPath, Problem problem) throws Exception{

        List<Double> problemScore = new ArrayList<>();

        sortTestCases(problem.testCases);

        String problemSubmissionDirPath = studentSubmissionDirPath + problem.id + "/";
        String targetYOFileName = problem.targetFileName.replace(".sugo", ".yo");

        String targetSUGOFilePath = problemSubmissionDirPath + problem.targetFileName;

        compiler.compile(targetSUGOFilePath);

        for(TestCase testcase: problem.testCases){
            problemScore.add(gradeTestCaseSimple(
                    testcase,
                    problemSubmissionDirPath,
                    targetYOFileName,
                    problem.testCasesDirPath));
        }

        return problemScore;
    }

    private Double gradeTestCaseSimple (TestCase testcase,
                                 String problemSubmissionDirPath,
                                 String targetYOFileName,
                                 String testCasesDirPath) throws Exception{

        String inputFilePath = testCasesDirPath + testcase.inputFileName;
        String ExpectedOutputFilePath = testCasesDirPath + testcase.outputFileName;
        String studentOutputFilePath = problemSubmissionDirPath + testcase.outputFileName;
        String targetYOFilePath = problemSubmissionDirPath + targetYOFileName;

        Double score = 0.0;

        executer.execute(targetYOFilePath, inputFilePath, studentOutputFilePath);
        if(compareFileSimple(studentOutputFilePath, ExpectedOutputFilePath)){
            score = testcase.score;
        }

        return score;
    }

    private boolean compareFileSimple(String studentOutputFilePath, String ExpectedOutputFilePath) throws FileNotFoundException {
        String studentOutputContent = new Scanner(new File(studentOutputFilePath)).useDelimiter("\\Z").next();
        String ExpectedOutputContent = new Scanner(new File(ExpectedOutputFilePath)).useDelimiter("\\Z").next();
        return studentOutputContent.equals(ExpectedOutputContent);
    }


    public Map<String,Map<String, List<Double>>> gradeRobust(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-2
        Map<String,Map<String,List<Double>>> result = new HashMap<>();
        try{
            for(Student student: examSpec.students){
                String studentSubmissionDirPath = submissionDirPath + student.id + "/";

                // check student submission dir exists
                // if not, check it is just wrong name or indeed missing
                if(isValidPath(studentSubmissionDirPath) || isWrongDirName(submissionDirPath, student.id)){
                    if(!isValidPath(studentSubmissionDirPath)){
                        studentSubmissionDirPath = handleWrongDirName(submissionDirPath, student.id);
                    }
                    result.put(student.id, gradeProblemRobust(studentSubmissionDirPath, examSpec.problems));
                } else {
                    result.put(student.id, gradeAllProblemsZero(examSpec.problems));
                }
            }
        }
        catch(Exception e) {
            ;
        }

        return result;
    }

    private boolean isValidPath(String studentSubmissionDirPath){
        File studentSubmissionDir = new File(studentSubmissionDirPath);
        return studentSubmissionDir.exists() || studentSubmissionDir.isDirectory();
    }

    private boolean isWrongDirName(String submissionDirPath, String studentId){
        File submissionDir = new File(submissionDirPath);

        for(String studentSubmissionDirName: submissionDir.list()){
            if(studentSubmissionDirName.equals(studentId)){
                return false;
            }
        }

        for(String studentSubmissionDirName: submissionDir.list()){
            if(studentSubmissionDirName.contains(studentId)){
                File studentSubmissionDir = new File(submissionDirPath + studentSubmissionDirName + "/");
                if(studentSubmissionDir.isDirectory()){

                    return true;
                }
            }
        }
        return false;
    }

// only execute when directory name is wrong but can be modified
    private String handleWrongDirName(String submissionDirPath, String studentId){
        File submissionDir = new File(submissionDirPath);
        // assume submissionDir not null
        for(String studentSubmissionDirName: submissionDir.list()){
            if(studentSubmissionDirName.contains(studentId)){
                File studentSubmissionDir = new File(submissionDirPath + studentSubmissionDirName + "/");
                if(studentSubmissionDir.isDirectory()){
                    return submissionDirPath + studentSubmissionDirName + "/";
                }
            }
        }

        // should not reach since already check wrongDirName
        return "";
    }

    private Map<String, List<Double>> gradeAllProblemsZero(List<Problem> problems){
        Map<String, List<Double>> problemScoreMap = new HashMap<>();

        for(Problem problem: problems){

            List<Double> allZero = new ArrayList<Double>();
            for(int i=0; i<problem.testCases.size(); i++){
                allZero.add(0.0);
            }
            problemScoreMap.put(problem.id, allZero);
        }

        return problemScoreMap;
    }


    private Map<String,List<Double>> gradeProblemRobust(String studentSubmissionDirPath, List<Problem> problems) throws Exception {

        Map<String, List<Double>> problemScoreMap = new HashMap<>();

        for(Problem problem: problems){
            problemScoreMap.put(problem.id, gradeTestCasesRobust(studentSubmissionDirPath, problem));
        }
        return problemScoreMap;
    }

    private List<Double> gradeTestCasesRobust(String studentSubmissionDirPath, Problem problem)
            throws Exception {

        List<Double> problemScore = new ArrayList<>();

        sortTestCases(problem.testCases);

        String problemSubmissionDirPath = studentSubmissionDirPath + problem.id + "/";
        String targetYOFileName = problem.targetFileName.replace(".sugo", ".yo");
        String targetSUGOFilePath = problemSubmissionDirPath + problem.targetFileName;

        if(!isValidPath(problemSubmissionDirPath)){
            for(int i=0; i<problem.testCases.size(); i++){
                problemScore.add(0.0);
            }
            return problemScore;
        }

        if(problem.wrappersDirPath != null){
            copyWrappers(problem.wrappersDirPath, problemSubmissionDirPath);
        }

        if(isWrongDirStructure(problemSubmissionDirPath)){
            handleWrongDirStructure(problemSubmissionDirPath);
        }

        try {
            File problemSubmissionDir = new File(problemSubmissionDirPath);
            if(problemSubmissionDir.listFiles() != null){
                for(File submittedFile: problemSubmissionDir.listFiles()){
                    if(submittedFile.getName().contains(".sugo")){
                        compiler.compile(submittedFile.getPath());
                    }
                }
            }
        } catch (Exception e) {
            for(int i=0; i<problem.testCases.size(); i++){
                problemScore.add(0.0);
            }
            return problemScore;
        }

        // half penalty when only yo file submitted
        // else 1
        double penalty = submittedOnlyYO(problemSubmissionDirPath);

        for(TestCase testcase: problem.testCases){
            problemScore.add(gradeTestCaseRobust(
                    testcase,
                    problemSubmissionDirPath,
                    targetYOFileName,
                    problem.testCasesDirPath,
                    problem.judgingTypes) * penalty);
        }

        return problemScore;
    }

    private double submittedOnlyYO(String problemSubmissionDirPath){
        File problemSubmissionDir = new File(problemSubmissionDirPath);
        // not empty

        List<String> YOList = getYOList(problemSubmissionDir.list());
        List<String> SUGOList = getSUGOList(problemSubmissionDir.list());

        for(String YOFilePath: YOList){
            if(!SUGOList.contains(YOFilePath.replace(".yo", ".sugo"))){
                return 0.5;
            }
        }

        return 1.0;
    }

    private List<String> getYOList(String[] totalList){
        List<String> YOList = new ArrayList<>();

        for(String filePath: totalList){
            if(filePath.contains(".yo")){
                YOList.add(filePath);
            }
        }
        // not empty
        return YOList;
    }

    private List<String> getSUGOList(String[] totalList){
        List<String> SUGOList = new ArrayList<>();

        for(String filePath: totalList){
            if(filePath.contains(".sugo")){
                SUGOList.add(filePath);
            }
        }
        // not empty
        return SUGOList;
    }


    private void handleWrongDirStructure(String problemSubmissionDirPath) throws IOException, NullPointerException {
        File problemSubmissionDir = new File(problemSubmissionDirPath);
        // assume not null
        for(File submission: problemSubmissionDir.listFiles()){
            if(submission.isDirectory() && submission.list() != null && submission.list().length > 0){
                for(File nestedFile: submission.listFiles()){
                    Files.move(nestedFile.toPath(), Paths.get(problemSubmissionDirPath + nestedFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
                submission.delete();
            }
        }
    }

    private boolean isWrongDirStructure(String problemSubmissionDirPath){
        File problemSubmissionDir = new File(problemSubmissionDirPath);
        // assume not null
        for(File submission: problemSubmissionDir.listFiles()){
            if(submission.isDirectory()) return true;
        }
        return false;
    }

    private void sortTestCases(List<TestCase> testCases){
        Collections.sort(testCases, new Comparator<TestCase>() {
            @Override
            public int compare(TestCase o1, TestCase o2) {
                return o1.id.compareTo(o2.id);
            }
        });
    }

    private Double gradeTestCaseRobust(TestCase testcase,
                                       String problemSubmissionDirPath,
                                       String targetYOFileName,
                                       String testCasesDirPath,
                                       Set<String> judgingTypes) throws Exception{

        String inputFilePath = testCasesDirPath + testcase.inputFileName;
        String expectedOutputFilePath = testCasesDirPath + testcase.outputFileName;
        String studentOutputFilePath = problemSubmissionDirPath + testcase.outputFileName;
        String targetYOFilePath = problemSubmissionDirPath + targetYOFileName;

        Double score = 0.0;

        try{
            executer.execute(targetYOFilePath, inputFilePath, studentOutputFilePath);
            if(compareFileRobust(studentOutputFilePath, expectedOutputFilePath, judgingTypes)){
                score = testcase.score;
            }
        }
        catch(Exception e) {
            return score;
        }

        return score;
    }

    private boolean compareFileRobust(String studentOutputFilePath,
                                      String expectedOutputFilePath,
                                      Set<String> judgingTypes) throws Exception {

        String studentOutputContent = new Scanner(new File(studentOutputFilePath)).useDelimiter("\\Z").next();
        String expectedOutputContent = new Scanner(new File(expectedOutputFilePath)).useDelimiter("\\Z").next();


        if(judgingTypes != null && !judgingTypes.isEmpty()){
            if(judgingTypes.contains(Problem.LEADING_WHITESPACES)){
                studentOutputContent = studentOutputContent.replaceAll("^\\s+","");
                expectedOutputContent = expectedOutputContent.replaceAll("^\\s+","");
            }
            if(judgingTypes.contains(Problem.IGNORE_WHITESPACES)){
                studentOutputContent = studentOutputContent.replaceAll("\\s","");
                expectedOutputContent = expectedOutputContent.replaceAll("\\s","");
            }
            if(judgingTypes.contains(Problem.CASE_INSENSITIVE)){
                studentOutputContent = studentOutputContent.toLowerCase();
                expectedOutputContent = expectedOutputContent.toLowerCase();
            }
            if(judgingTypes.contains(Problem.IGNORE_SPECIAL_CHARACTERS)){
                studentOutputContent = studentOutputContent.replaceAll("[^A-Za-z0-9\\s]","");
                expectedOutputContent = expectedOutputContent.replaceAll("[^A-Za-z0-9\\s]","");
            }
        }

        return studentOutputContent.equals(expectedOutputContent);
    }



    private void copyWrappers(String wrappersDirPath, String problemSubmissionDirPath) throws Exception {
        File wrappersDir = new File(wrappersDirPath);
        // valid by assumption
        if(wrappersDir.listFiles() != null){
            for(File wrapperFile: wrappersDir.listFiles()){
                Files.copy(wrapperFile.toPath(), Paths.get(problemSubmissionDirPath + wrapperFile.getName()));
            }
        }
    }
}

