package server;

import course.*;
import jdk.swing.interop.SwingInterOpUtils;
import utils.Config;
import utils.ErrorCode;
import utils.Pair;

import java.io.*;
import java.util.*;

public class Server {

    private final String COURSES_DIR_PATH = "data/Courses/";
    private final String USERS_DIR_PATH = "data/Users/";
    List<Course> courses;
    Set<Integer> courseIds;
    Map<String, List<Bidding>> biddings;
    Set<String> users;
    {
        try {
            courses = loadCourses(COURSES_DIR_PATH);
            courseIds = new HashSet<>();
            for(Course c: courses){
                courseIds.add(c.courseId);
            }
        } catch (Exception e) {
            System.out.println("IOException occur during load courses!");
        }
    }
    {
        try {
            biddings = loadBiddings(USERS_DIR_PATH);
            users = biddings.keySet();
        } catch (Exception e) {
            System.out.println("IOException occur during load biddings!");
        }
    }

    private boolean noBidFile = false;

    public List<Course> search(Map<String,Object> searchConditions, String sortCriteria){
        // TODO Problem 2-1

        List<Course> retrieved = new ArrayList<>();

        try {
            if(searchConditions == null) return retrieved;
            if(searchConditions.containsKey("name") && searchConditions.get("name").equals("")) return new ArrayList<>();
            if(searchConditions.containsKey("dept") && searchConditions.get("dept").equals("")) return new ArrayList<>();
            if(searchConditions.containsKey("ay") && searchConditions.get("ay") == null) return new ArrayList<>();

            for(Course course: courses){
                if(filter(course, searchConditions)){
                    retrieved.add(course);
                }
            }

            sortCourses(retrieved, sortCriteria);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retrieved;
    }

    private boolean filter(Course course, Map<String,Object> searchConditions){
        if(searchConditions.containsKey("name")){
            String[] querySplit = ((String)searchConditions.get("name")).split(" ");
            String[] courseSplit = course.courseName.split(" ");
            Set<String> courseSplitSet = new HashSet<>(Arrays.asList(courseSplit));

            for(String query: querySplit){
                if(!courseSplitSet.contains(query)) return false;
            }
        }

        if(searchConditions.containsKey("ay")){
            if(course.academicYear > (int)searchConditions.get("ay")) return false;
        }

        if(searchConditions.containsKey("dept")){
            if(!course.department.equals(searchConditions.get("dept"))) return false;
        }

        return true;
    }

    private List<Course> loadCourses(String coursesDirPath) throws Exception{
        List<Course> courses = new ArrayList<>();

        File coursesDir = new File(coursesDirPath);

        if(!isEmptyDir(coursesDir)){
            for(File ayDir: coursesDir.listFiles()){
                if(ayDir.isDirectory() && !isEmptyDir(ayDir)){
                    for(File collegeDir: ayDir.listFiles()){
                        if(collegeDir.isDirectory() && !isEmptyDir(collegeDir)){
                            for(File course: collegeDir.listFiles()){
                                Scanner scanner = new Scanner(course);
                                String[] courseInfo = scanner.nextLine().split("\\|");

                                courses.add(new Course(
                                        Integer.parseInt(course.getName().replace(".txt", "")),
                                        collegeDir.getName(),
                                        courseInfo[0],
                                        courseInfo[1],
                                        Integer.parseInt(courseInfo[2]),
                                        courseInfo[3],
                                        Integer.parseInt(courseInfo[4]),
                                        courseInfo[5],
                                        courseInfo[6],
                                        Integer.parseInt(courseInfo[7])
                                        ));
                                scanner.close();
                            }
                        }
                    }
                }
            }
        }
        return courses;
    }

    private void sortCourses(List<Course> course, String sortCriteria){
        if(sortCriteria == null || sortCriteria.equals("") || sortCriteria.equals("id")){
            course.sort(new IdComparator());
        } else if(sortCriteria.equals("name")){
            course.sort(new IdComparator());
            course.sort(new NameComparator());
        } else if(sortCriteria.equals("dept")){
            course.sort(new IdComparator());
            course.sort(new DeptComparator());
        } else if(sortCriteria.equals("ay")){
            course.sort(new IdComparator());
            course.sort(new AyComparator());
        }
    }

    class IdComparator implements Comparator<Course> {
        @Override
        public int compare(Course c1, Course c2) {
            return c1.courseId - c2.courseId;
        }
    }
    class NameComparator implements Comparator<Course> {
        @Override
        public int compare(Course c1, Course c2) {
            return c1.courseName.compareTo(c2.courseName);
        }
    }
    class DeptComparator implements Comparator<Course> {
        @Override
        public int compare(Course c1, Course c2) {
            return c1.department.compareTo(c2.department);
        }
    }
    class AyComparator implements Comparator<Course> {
        @Override
        public int compare(Course c1, Course c2) {
            return c1.academicYear - c2.academicYear;
        }
    }

    private boolean isEmptyDir(File dir){

        if(dir.list() == null || dir.list().length == 0){
            return true;
        }

        return false;
    }

    public int bid(int courseId, int mileage, String userId){
        // TODO Problem 2-2

        try{
            if(userId == null || !users.contains(userId)) return ErrorCode.USERID_NOT_FOUND;
            if(!courseIds.contains(courseId)) return ErrorCode.NO_COURSE_ID;
            if(mileage < 0) return ErrorCode.NEGATIVE_MILEAGE;
            if(biddings.get(userId).size() >= Config.MAX_COURSE_NUMBER) return ErrorCode.OVER_MAX_COURSE_NUMBER;
            if(mileage > Config.MAX_MILEAGE_PER_COURSE) return ErrorCode.OVER_MAX_COURSE_MILEAGE;

            int mileageSum = 0;
            for(Bidding b: biddings.get(userId)){
                if(b.courseId != courseId) mileageSum += b.mileage;
            }
            mileageSum += mileage;
            if(mileageSum > Config.MAX_MILEAGE) return ErrorCode.OVER_MAX_MILEAGE;

            if(noBidFile) throw new IOException();
            if(alreadyBid(userId, courseId)){
                if(mileage == 0){
                    removeFromMemory(courseId, userId);
                    removeFromDisk(courseId, userId);
                } else {
                    modifyMemory(courseId, mileage, userId);
                    modifyDisk(courseId, mileage, userId);
                }
            } else {
                if(mileage != 0){
                    saveToDisk(courseId, mileage, userId);
                    saveToMemory(courseId, mileage, userId);
                }
            }

        }
        catch(IOException | NullPointerException e) {
            return ErrorCode.IO_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    private void modifyDisk(int courseId, int mileage, String userId) throws IOException {

        File bidFile = new File(USERS_DIR_PATH + userId + "/bid.txt");
        Scanner scanner = new Scanner(bidFile);
        String dummy = "";
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if(!line.startsWith("" + courseId)) {
                dummy += line + "\n";
            } else {
                dummy += courseId + "|" + mileage + "\n";
            }
        }
        scanner.close();
        FileWriter bidFileWriter = new FileWriter(bidFile);
        bidFileWriter.write(dummy.replaceAll("[\n\r]$", ""));
        bidFileWriter.close();
    }

    private void modifyMemory(int courseId, int mileage, String userId) {
        List<Bidding> biddingList = biddings.get(userId);
        for(Bidding b: biddingList){
            if(b.courseId == courseId){
                b.mileage = mileage;
            }
        }
    }

    private void removeFromMemory(int courseId, String userId) {
        List<Bidding> biddingList = biddings.get(userId);
        biddingList.removeIf(b -> b.courseId == courseId);
    }

    private void removeFromDisk(int courseId, String userId) throws IOException {
        File bidFile = new File(USERS_DIR_PATH + userId + "/" + "bid.txt");
        Scanner scanner = new Scanner(bidFile);
        String dummy = "";
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if (!line.startsWith("" + courseId)) {
                dummy += line + "\n";
            }
        }
        scanner.close();
        FileWriter bidFileWriter = new FileWriter(bidFile);
        bidFileWriter.write(dummy.replaceAll("[\n\r]$", ""));
        bidFileWriter.close();
    }

    private void saveToDisk(int courseId, int mileage, String userId) throws IOException {
        File bidFile = new File(USERS_DIR_PATH + userId + "/" + "bid.txt");
        FileWriter bidFileWriter = new FileWriter(bidFile, true);
        if(bidFile.length() == 0) {
            bidFileWriter.write(courseId + "|" + mileage);
        } else{
            bidFileWriter.write("\n" + courseId + "|" + mileage);
        }
        bidFileWriter.close();
    }

    private void saveToMemory(int courseId, int mileage, String userId) {
        biddings.get(userId).add(new Bidding(courseId, mileage));
    }

    private boolean alreadyBid(String userId, int courseId) {
        for(Bidding b: biddings.get(userId)){
            if(b.courseId == courseId) return true;
        }
        return false;
    }

    public Pair<Integer,List<Bidding>> retrieveBids(String userId){
        // TODO Problem 2-2
        if(userId == null || users == null || !users.contains(userId)) return new Pair<>(ErrorCode.USERID_NOT_FOUND, new ArrayList<>());
        return new Pair<>(ErrorCode.SUCCESS, biddings.get(userId));
    }

    private Map<String, List<Bidding>> loadBiddings(String usersDirPath) throws Exception{
        Map<String, List<Bidding>> biddings = new HashMap<>();

        File usersDir = new File(usersDirPath);

        if(!isEmptyDir(usersDir)){
            for(File userIdDir: usersDir.listFiles()){
                if(userIdDir.isDirectory() && !isEmptyDir(userIdDir)){
                    File bidFile = new File(userIdDir.getPath() + "/bid.txt");
                    List<Bidding> bidding = new ArrayList<>();
                    if(bidFile.exists()){
                        Scanner scanner = new Scanner(bidFile);
                        while(scanner.hasNext()){
                            String[] bidInfo = scanner.nextLine().split("\\|");
                            bidding.add(new Bidding(Integer.parseInt(bidInfo[0]), Integer.parseInt(bidInfo[1])));
                        }
                        scanner.close();
                        biddings.put(userIdDir.getName(), bidding);
                    } else {
                        noBidFile = true;
                        break;
                    }
                }
            }
        }

        return biddings;
    }

    Map<String, List<Course>> confirmedCourses;

    public boolean confirmBids(){
        // TODO Problem 2-3

        Set<User> usersForConfirm = new HashSet<User>();

        try{
//            biddings = loadBiddings(USERS_DIR_PATH);
            for(String userId: users){
                usersForConfirm.add(new User(userId, biddings.get(userId)));
            }

            Map<Course, List<User>> bidUsers = new HashMap<>();

            for(Course c: courses){
                List<User> bidders = new ArrayList<>();
                for(User user: usersForConfirm){
                    if(user.bidTo(c)) bidders.add(user);
                }
                bidUsers.put(c, bidders);
            }


            Map<Course, List<User>> confirmedUsers = confirm(bidUsers);


            confirmedCourses = new HashMap<>();
            for(String userId: users){
                confirmedCourses.put(userId, new ArrayList<>());
            }


            for(Course c: confirmedUsers.keySet()){
                for(User u: confirmedUsers.get(c)){
                    confirmedCourses.get(u.userId).add(c);
                }
            }

            saveConfirmation(confirmedCourses, USERS_DIR_PATH);
            removeBidData(USERS_DIR_PATH);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void saveConfirmation(Map<String, List<Course>> confirmedCourses, String usersDirPath) throws IOException {
        File usersDir = new File(usersDirPath);
        if(!isEmptyDir(usersDir)){
            for(File userIdDir: usersDir.listFiles()){
                if(userIdDir.isDirectory() && !isEmptyDir(userIdDir)){
                    FileWriter fw = new FileWriter(userIdDir.getPath() + "/confirmed.txt");
                    String content = "";
                    for(Course c: confirmedCourses.get(userIdDir.getName())){
                        content += c.courseId + " ";
                    }
                    fw.write(content);
                    fw.close();
                }
            }
        }
    }


    private void removeBidData(String usersDirPath) throws IOException {
        File usersDir = new File(usersDirPath);
        if(!isEmptyDir(usersDir)){
            for(File userIdDir: usersDir.listFiles()){
                if(userIdDir.isDirectory() && !isEmptyDir(userIdDir)){
                    File bidFile = new File(userIdDir.getPath() + "/bid.txt");
                    if(bidFile.exists() && bidFile.isFile()){
                        FileWriter fw = new FileWriter(bidFile);
                        fw.write("");
                        fw.close();
                    }
                }
            }
        }
    }

    private Map<Course, List<User>> confirm(Map<Course, List<User>> bidUsers) {
        Map<Course, List<User>> result = new HashMap<>();

        for(Course c: bidUsers.keySet()){
            List<User> userList = bidUsers.get(c);
            if(userList.size() > c.quota){
                userList.sort(new UserIdComparator());
                userList.sort(new TotalMileageComparator());
                userList.sort(new MileageComparator(c));

                List<User> cutList = new ArrayList<>();
                for(int i=0; i<c.quota; i++){
                    cutList.add(userList.get(i));
                }
                result.put(c, cutList);

            } else {
                result.put(c, userList);
            }
        }

        return result;
    }

    class MileageComparator implements Comparator<User> {
        Course course;
        MileageComparator(Course course){
            this.course = course;
        }
        @Override
        public int compare(User u1, User u2) {
            return u2.mileageOnCourse(course) - u1.mileageOnCourse(course);
        }
    }
    class TotalMileageComparator implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.totalBidMileage - u2.totalBidMileage;
        }
    }
    class UserIdComparator implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.userId.compareTo(u2.userId);
        }
    }

    public Pair<Integer,List<Course>> retrieveRegisteredCourse(String userId){
        // TODO Problem 2-3
        if(userId == null || users == null || !users.contains(userId)) {
            return new Pair<>(ErrorCode.USERID_NOT_FOUND, new ArrayList<>());
        }

//        if(confirmedCourses == null) {
//            System.out.println("confirmedCourses null");
//            return new Pair<>(ErrorCode.IO_ERROR, new ArrayList<>());
//        }

        return new Pair<>(ErrorCode.SUCCESS, confirmedCourses.get(userId));
    }
}

class User {
    public String userId;
    public int totalBidMileage;
    public List<Bidding> bidInfo;

    User(String userId, List<Bidding> bidInfo){
        this.userId = userId;
        int bidMileageSum = 0;
        for(Bidding b: bidInfo){
            bidMileageSum += b.mileage;
        }
        this.totalBidMileage = bidMileageSum;
        this.bidInfo = bidInfo;
    }

    public boolean bidTo(Course c){
        for(Bidding b: bidInfo){
            if(b.courseId == c.courseId) return true;
        }
        return false;
    }

    public int mileageOnCourse(Course c){
        for(Bidding b: bidInfo){
            if(b.courseId == c.courseId) return b.mileage;
        }
        return 0;
    }
}