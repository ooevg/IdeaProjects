package ru.ooevg;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

public class RawConvert {

    public static void main(String[] args) throws IOException {
        Path basePath;
        try {
            basePath = Paths.get(args[0]);
        }catch (Exception e){
            System.out.println("Нет входной параметра. Будет обследован путь по умолчанию (/oldsystem/photo)");
            basePath= Paths.get("/oldsystem/photo");
        }

        ArrayList<Path> foundsFiles;
        SearchFileVisitor sfv = new SearchFileVisitor();

        Files.walkFileTree(basePath,sfv);
        foundsFiles = sfv.getFondFiles();
        if (foundsFiles.size()==0) System.out.println("Все папки сконвертированы");
        for(Path path : foundsFiles){
            convertRaw(path);
        }

	// write your code here
    }
    public static void convertRaw(Path p){
        try {
            String jpgFileName =p.getParent().getParent().toString() +"/"+ p.getFileName().toString().replaceAll("CR2","")+"jpg";
        Process process = Runtime.getRuntime().exec("darktable-cli "+ p.toString() + " " + jpgFileName);
        process.waitFor();
        System.out.println("Convert file "+p.getFileName().toString());
        }

        catch (Exception e){
            System.out.println(e);
        }
    }

    public static class SearchFileVisitor extends SimpleFileVisitor <Path>{
        ArrayList<Path> fondFiles = new ArrayList<Path>();

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            if(path.toString().endsWith(".CR2")) {
               fondFiles.add(path);
            }
            return super.visitFile(path, basicFileAttributes);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            if(Arrays.stream(path.toFile().list()).anyMatch("raw"::equals) && path.toFile().listFiles().length>1) {
                return FileVisitResult.SKIP_SUBTREE;
            }else if(path.toString().contains("raw") && path.toFile().getParentFile().listFiles().length==1){
                System.out.println("Not converting Dir "+ path.toString());
            }
            return super.preVisitDirectory(path, basicFileAttributes);
        }

        public ArrayList<Path> getFondFiles() {
            return fondFiles;
        }
    }
}
