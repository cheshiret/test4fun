package com.active.qa.automation.web.testapi.util;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;

import com.active.qa.automation.web.testapi.ItemNotFoundException;
import com.active.qa.automation.web.testapi.NotSupportedException;

/**
 * wrap the convenient methods for manipulating Files, input/output etc.
 * Created by tchen on 1/11/2016.
 */
public class FileUtil {

    /**
     * Unzip all files in the given zip file
     *
     * @param zipFile
     *            - the absolute path of zip file
     * @param toPath
     *            - the absolute path for saving unzipped files
     * @return - array of file names been extracted
     * @throws IOException
     */
    public static String[] unzip(String zipFile, String toPath)
            throws IOException {
        File path = new File(toPath);
        if (!path.exists()) {
            path.mkdirs();
        }

        ZipFile file = new ZipFile(zipFile);
        Enumeration<?> entries = file.entries();
        List<String> files = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            String entry = entries.nextElement().toString();
            files.add(entry);
            InputStream in = file.getInputStream(file.getEntry(entry));
            write(in, toPath + "/" + entry);
        }
        file.close();
        return files.toArray(new String[0]);
    }

    /**
     * Write the InputStream into a file
     *
     * @param in
     * @param file
     * @throws IOException
     */
    public static void write(InputStream in, String file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int count;
        while ((count = in.read(buf)) != -1) {
            out.write(buf, 0, count);
        }
        out.flush();
        out.close();
    }

    /**
     * Read a text file into a String
     *
     * @param file
     * @return
     */
    public static String read(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer fileData = new StringBuffer(1000);

            char[] buf = new char[1024];
            int numRead = 0;

            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
            reader.close();

            return fileData.toString();

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file, e);
        }
    }

    public static String[] readLines(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> list = new ArrayList<String>();
            String aLine;
            while ((aLine = reader.readLine()) != null) {
                list.add(aLine);
            }
            reader.close();

            return list.toArray(new String[0]);

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + file, e);
        }
    }

    public static List<String> listFiles(String path, String format) {
        return listFiles(path,format,false);
    }
    /**
     * Recursively list all files of the given format for the given path. ".svn"
     * folder will be ignored
     *
     * @param path
     * @param format
     * @param exclude - flag for excluding the files with given format
     * @return
     */
    public static List<String> listFiles(String path, String format, boolean exclude) {
        List<String> files = new ArrayList<String>();

        File file = new File(path);

        if (!file.exists()) {
            throw new ItemNotFoundException("Path \"" + path + "\" not found");
        }

        if (file.isFile()) {
            boolean qualify = false;
            if (!StringUtil.isEmpty(format)) {
                if (path.endsWith(format)) {

                    qualify = !exclude;
                } else {
                    qualify = exclude;
                }
            } else {
                qualify = true;
            }
            if (qualify) {
//				String fileName = path.split("\\.")[0];
//				fileName = fileName.replaceAll("/|\\\\", "\\.").replaceAll(
//						"\\.\\.", "\\.");
                files.add(path);
            }
        } else if (file.isDirectory() && !file.getName().equals(".svn")) {
            // skip '.svn' directories
            String[] dirContents = file.list();

            // recurse through files and sub-directories
            for (int i = 0; i < dirContents.length; i++) {
                files.addAll(listFiles(path + "/" + dirContents[i], format, exclude));
            }
        }
        return files;
    }

    public static void modifyFile(String filename, String pattern, String replacement) {
        try {
            File tempFile = File.createTempFile("temp"+DateFunctions.getTimeStamp(), ".java");
            PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(tempFile, true)));
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String aLine;
            while ((aLine = reader.readLine()) != null) {
                if(RegularExpression.contains(aLine, pattern, false)) {
                    String newLine=aLine.replaceAll(pattern, replacement);
                    System.out.println(filename+":: "+aLine+" -> "+newLine);
                    writer.println(newLine);
                } else {
                    writer.println(aLine);
                }
            }
            reader.close();
            writer.close();
            File file=new File(filename);
            file.delete();
            tempFile.renameTo(file);

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + filename, e);
        }
    }

    public static void modifyFileReplaceString(String filename, String pattern, String replacement) {
        try {
            File tempFile = File.createTempFile("temp"+DateFunctions.getTimeStamp(), ".java");
            PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(tempFile, true)));
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String aLine;
            while ((aLine = reader.readLine()) != null) {
                if(RegularExpression.contains(aLine, pattern, false)) {
                    String newLine=aLine.replaceAll(pattern, replacement);
                    System.out.println(filename+":: "+aLine+" -> "+newLine);
                    writer.println(newLine);
                } else {
                    writer.println(aLine);
                }
            }
            reader.close();
            writer.close();
            File file=new File(filename);
            file.delete();
            tempFile.renameTo(file);

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + filename, e);
        }
    }

    public static List<String> scanFile(String filename, String pattern) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            List<String> list = new ArrayList<String>();
            String aLine;
            RegularExpression reg=new RegularExpression(pattern,true);
            while ((aLine = reader.readLine()) != null) {
                if(reg.match(aLine)) {
                    list.add(aLine);
                }
            }
            reader.close();

            return list;

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + filename, e);
        }
    }

    public static List<String> scanFile(String fileName, String startLinePattern,String endLinePatern){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<String> list = new ArrayList<String>();
            String aLine;
            RegularExpression startReg=new RegularExpression(startLinePattern,true);
            RegularExpression endReg=new RegularExpression(endLinePatern,true);
            boolean record = false;
            while ((aLine = reader.readLine()) != null) {
                if(startReg.match(aLine)) {
                    record = true;
                }

                if(endReg.match(aLine)){
                    record = false;
                }
                if(record){
                    list.add(aLine);
                }
                if(list.size()>0&&!record){
                    break;
                }
            }
            reader.close();

            return list;

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + fileName, e);
        }
    }

    public void scanAndModifyFile(String path, String format, String scanPattern, String replacePattern, String replacement) {
        List<String> f=FileUtil.listFiles(path, format,false);
        int count=0;
        System.out.println("Found "+f.size()+" files.");
        for(String filename:f) {

            List<String> r=FileUtil.scanFile(filename, scanPattern);
            if(r.size()>0) {
                FileUtil.modifyFile(filename, replacePattern, replacement);

//				System.out.println(s+"::"+r.toString());
                count++;

            }
        }

        System.out.println("Total changed "+count);
    }

    public static void copyFile(String sourceFile, String destFile) {
        FileInputStream fis=null;
        BufferedInputStream bin=null;
        FileOutputStream fos=null;
        BufferedOutputStream bout=null;
        try{
            File file = new File(sourceFile);
            File dFile = new File(destFile);
            if(!dFile.exists()){
                dFile.getParentFile().mkdirs();
                dFile.createNewFile();
            }
            fis = new FileInputStream(file);
            bin = new BufferedInputStream(fis);

            fos = new FileOutputStream(destFile);
            bout = new BufferedOutputStream(fos);
            int c;
            while((c=bin.read())!=-1){
                bout.write(c);
            }
            bout.flush();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
                fis.close();
                bout.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finish copy file "+sourceFile);
    }

    /**
     * The method used to copy given format file from one source folder to expect folder
     * @param sourceFolder
     * @param destFolder
     * @param fileFormat
     */
    public static void copyFiles(String sourceFolder,String destFolder,String fileFormat){
        List<String> names = FileUtil.listFiles(sourceFolder, fileFormat,false);
        File sourceFile = null;
        File destFile = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        if(!fileFormat.startsWith(".")){
            fileFormat = "."+fileFormat;
        }
        for(String name:names){
            if(!name.endsWith(fileFormat)) {
                name=name+fileFormat;
            }
            System.out.println("Start Copy file "+name);
            try {
                sourceFile = new File(name);
                destFile = new File(destFolder+"\\"+sourceFile.getName());
                if(!destFile.exists()){
                    destFile.getParentFile().mkdirs();
                    destFile.createNewFile();
                }
                fis = new FileInputStream(sourceFile);
                bin = new BufferedInputStream(fis);

                fos = new FileOutputStream(destFile);
                bout = new BufferedOutputStream(fos);
                int c;
                while((c=bin.read())!=-1){
                    bout.write(c);
                }
                bout.flush();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    bin.close();
                    fis.close();
                    bout.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Finish copy file "+name);
        }
    }

    public static void copyFile(InputStream in,OutputStream out){
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            bin = new BufferedInputStream(in);

            bout = new BufferedOutputStream(out);
            int c;
            while((c=bin.read())!=-1){
                bout.write(c);
            }
            bout.flush();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
                bout.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * This method used to compare two text/csv file
     *
     * @param existFile
     * @param newFile
     * @return
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static List<String> compareFile(String existFile, String newFile)
            throws IOException {
        BufferedReader br1 = new BufferedReader(new FileReader(existFile));
        BufferedReader br2 = new BufferedReader(new FileReader(newFile));

        List<String> msgs = new ArrayList<String>();
        String temp = null;
        int i = 1;
        while ((temp = br1.readLine()) != null) {
            String temp2 = br2.readLine();
            if (temp2 == null) {
                String msg = "Row " + i + " Not Found in New File.";
                AutomationLogger.getInstance().error(msg);
                msgs.add(msg);
                return msgs;
            }
            String msg = compareTwoLine(temp, temp2);
            if (msg.length() > 0) {
                AutomationLogger.getInstance().error(msg + " on Row " + i);
                msgs.add(msg);
            }
            i++;
        }
        if (br2.readLine() != null) {
            String msg = "New file have redundant data on Row " + i;
            AutomationLogger.getInstance().error(msg);
            msgs.add(msg);
        }
        br1.close();
        br2.close();
        return msgs;
    }

    /**
     * This method used to compare two row data is equals
     *
     * @param line1
     * @param line2
     * @return difference between the two row
     */
    private static String compareTwoLine(String line1, String line2) {
        String msg = "";
        if (!line1.equals(line2)) {
            String delimit = line1.contains(",") ? "," : " ";
            String[] a1 = line1.split(delimit);
            String[] a2 = line2.split(delimit);
            if (a1.length != a2.length) {
                msg = "Row Length is Different " + a1.length + " VS "
                        + a2.length;
                return msg;
            }
            for (int i = 0; i < a1.length; i++) {
                if (a1[i] != null) {
                    if (a2[i] == null || !(a1[i].equals(a2[i]))) {
                        if (a2[i] != null
                                && a2[i].contains("Run Date and Time:")) {
                            continue;
                        }
                        msg = "Column " + i + " should be " + a1[i] + " VS "
                                + a2[i];
                        return msg;
                    }
                } else {
                    if (a2[i] != null) {
                        msg = a2[i] + " is not exists in template";
                        return msg;
                    }
                }
            }
        }
        return msg;
    }

    /**
     * Append log info into given file
     *
     * @param fileName
     * @param log
     * @throws IOException
     */
    public static void writeLog(String fileName, String log) throws IOException {
        try {
            // Create parent dir
            File dir = new File(fileName).getParentFile();
            mkdir(dir);

            // Write file
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName, true)));
            out.println(log);
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("the file not found: " + e.toString());
            throw e;
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.toString());
            throw e;
        }
    }

    private static void mkdir(File dir) {
        while (!dir.exists()) {
            mkdir(dir.getParentFile());
            dir.mkdir();
        }
    }

    /**
     * Append log info into given file without changing lines
     *
     * @param fileName
     * @param log
     * @throws IOException
     */
    public static void writeSameLineLog(String fileName, String log)
            throws IOException {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName, true)));
            out.print(log);
            out.close();
        } catch (FileNotFoundException e) {
            // throw exception up to testMain()
            System.err.println("the file not found: " + e.toString());
            throw e;
        } catch (IOException e) {
            // throw exception up to testMain()
            System.err.println("Error loading file: " + e.toString());
            throw e;
        }
    }

    public static void generateSpiraImportFile(String startPath, String prefix,
                                               String fileName) throws IOException {
        File file = new File(startPath);

        int index = startPath.lastIndexOf("\\");
        if (prefix == null) {
            prefix = "";
        }

        String name = startPath.substring(index + 1);

        if (file.exists()) {
            String nextPrefix = prefix + ",";
            if (file.isDirectory()) {
                if (!name.equalsIgnoreCase(".svn")) {
                    writeLog(fileName, prefix + name);
                    String[] files = file.list();

                    for (String aFile : files) {
                        generateSpiraImportFile(startPath + "\\" + aFile,
                                nextPrefix, fileName);
                    }
                }

            } else {
                index = name.lastIndexOf(".");
                name = name.substring(0, index);
                writeLog(fileName, prefix + name);
            }
        } else {
            throw new RuntimeException("file " + startPath + " doesn't exist");
        }
    }

    public static void generateAndWriteFile(String fullPath, String content) {
        File file = new File(fullPath);

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fullPath) {
        File file = new File(fullPath);
        if(file.exists()) {
            file.delete();
        }
    }

    /**
     * This method will get all screen snapshots with format as .png under the given path in mapped X:driver.
     * the screen snapshot file will be transfered to a URL link to http://raon-toolsvm.qa.reserveamerica.com
     * @param path
     * @return
     */
    public static List<String> getScreenShots(String path) {
        if(!path.toLowerCase().startsWith("x")) {
            throw new NotSupportedException("Path has to be in X: driver which is mapped to TestFarm_shared folder");
        }
        List<String> shots=FileUtil.listFiles(path, ".png");
        System.out.println("Found "+shots.size()+" screen snapshots.");
        List<String> newList=new ArrayList<String>();
        for(String s:shots) {
            String filename=s.replaceAll("\\.", "/")+".png";
            //transfer to be displayed via http://raon-toolsvm.qa.reserveamerica.com
            newList.add(filename.replaceAll("X:/", "http://raon-toolsvm.qa.reserveamerica.com/TestFarm/"));
        }

        return newList;
    }

    /**
     * Get all screen snapshots with format as .png from the given path and save them to the given file name.
     * @param path
     * @param file
     * @throws IOException
     */
    public static void getScreenShotsToFile(String path, String file) throws IOException {
        List<String> shots=getScreenShots(path);

        StringBuffer sb=new StringBuffer();

        for(String s: shots) {
            sb.append(s);
            sb.append("\n");
        }

        generateAndWriteFile(file,sb.toString());
    }

    /**
     * Slide show via IE browser for all the snapshots provided in the list
     * @param shots
     */
    public static void slideShowScreenShots(List<String> shots) {
        slideShowScreenShots(shots,0);
    }

    /**
     * Slide show via IE browser for all the snapshots provided in the list, starting from the given index
     * @param shots
     * @param start
     */
    public static void slideShowScreenShots(List<String> files, int index) {
        IBrowser browser=Browser.getInstance();
        browser.open();

        String[] fileTypes=new String[]{"png","log"};
        int defaultType=0;
        String[] options=new String[]{"<<Previous","Next>>","Switch to "+fileTypes[(defaultType+1)%2],"Stop"};
        int current=index;
        int size=files.size();
        boolean done=false;


        while(!done) {
            String file=files.get(current);
            if(!file.toLowerCase().matches(".+\\.(png|log)$")) {
                throw new NotSupportedException("Format is not supported for file: "+file);
            }
            if(!file.toLowerCase().endsWith(fileTypes[defaultType].toLowerCase())) {
                int i=file.lastIndexOf(".");
                file=file.substring(0, i+1)+fileTypes[defaultType];
            }

            try {
                browser.load(file);
            } catch(Exception e) {
                if(e.getMessage().contains("Unable to get browser")) {
                    browser.open();
                    browser.load(file);
                }
            }
            int selection=JOptionPane.showOptionDialog(null, "Displaying "+(current+1)+" of total "+size+"\n"+file, "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
            switch(selection) {
                case 0:
                    current=current-1;
                    if(current<0) {
                        current=0;
                    }
                    break;
                case 1:
                    current++;
                    if(current>=size) {
                        current=size-1;
                    }
                    break;
                case 2:
                    defaultType=(defaultType+1)%2;
                    options[2]="Switch to "+fileTypes[(defaultType+1)%2];
                    break;
                case 3:
                default:
                    done=true;

            }


        }
    }

    /**
     * Slide show all screen snapshots from the given path via IE Browser and http://raon-toolsvm.qa.reserveamerica.com.
     * the path has to be in X: driver, which is mapped to TestFarm_Shared network folder.
     * @param path
     */
    public static void slideShowScreenShots(String path) {
        slideShowScreenShots(getScreenShots(path));
    }

    /**
     * Slide show screen snapshots from the given index under the given path via IE Browser and http://raon-toolsvm.qa.reserveamerica.com.
     * the path has to be in X: driver, which is mapped to TestFarm_Shared network folder.
     * @param path
     * @param index
     */
    public static void slideShowScreenShots(String path, int index) {
        slideShowScreenShots(getScreenShots(path), index);
    }

    /**
     * Cleanup the given folder and return the total number of file/directories cleaned
     * @param folder
     * @return the total number of files/folders cleaned.
     */
    public static int cleanupFolder(String folder) {
        File dir=new File(folder);
        int deleted=0;
        if(dir.exists() && dir.isDirectory()) {
            File[] files=dir.listFiles();
            for(File aFile:files) {
                deleted+=forceDelete(aFile);
            }
        } else {
            AutomationLogger.getInstance().warn("The given folder \""+folder+"\" doesn't exist");
        }

        return deleted;
    }

    /**
     * Delete the given file/folder. If it is a folder, it will recursively delete the content of the folder and then the folder itself
     * @param file
     * @return - the total number of files/folders deleted
     */
    public static int forceDelete(File file) {
        int deleted=0;
        if(file.exists() ) {
            if(file.isDirectory()) {
                File[] files=file.listFiles();
                for(File aFile:files) {
                    deleted+=forceDelete(aFile);
                }

            }

            if(file.delete()) {
                deleted++;
            }

        } else {
            AutomationLogger.getInstance().warn("The given file \""+file+"\" doesn't exist or no permission to delete");
        }

        return deleted;
    }

    public static boolean isDirectory(String path){
        File dir=new File(path);
        if(dir.exists() && dir.isDirectory()) {
            return true;
        }else{
            return false;
        }
    }

    public static String searchFile(File startFolder, final String targetFile, boolean up) {
        String[] list=startFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.equals(targetFile);
            }});

        if(list!=null && list.length>0) {
            return startFolder.getAbsolutePath();
        } else if(up) {
            return searchFile(startFolder.getParentFile(),targetFile,up);
        } else {
            String[] dirs=startFolder.list(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return new File(dir,name).isDirectory();
                }});
            String file=null;
            if(dirs!=null && dirs.length>0) {

                for(String dir:dirs) {
                    file=searchFile(new File(startFolder,dir),targetFile,up);
                    if(!StringUtil.isEmpty(file)) {
                        break;
                    }
                }


            }
            return file;
        }

    }

    public static String getEscapeFileSeperator() {
        if(File.separator.equals("\\"))
            return "\\\\\\\\";
        else
            return File.separator;
    }
}

