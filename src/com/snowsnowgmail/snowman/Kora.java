package com.snowsnowgmail.snowman;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

public class Kora {
    public static URI cardListDir;

    static {
        try {
            cardListDir = Kora.class.getResource("./CardList.txt").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static boolean recommendDisable = false;
    public static String outputFile = null;

    public final static String optionHelp =
            "使用方法\n" +
                    "java com.snowsnowgmail.snowman.Kora [options...] <CollageString>\n\n" +
                    "オプションは次の通りです\n\n" +
                    "-cldir <relative path of file>\n" +
                    "-cardlistdirectory <relative path of file>\n" +
                    "\tカードの情報が書かれているファイルの場所を、相対パスで指定します。\n" +
                    "-rdis\n" +
                    "-recommenddisable\n" +
                    "\tおすすめを非表示にします。\n" +
                    "-o <relative path of file>\n" +
                    "-output <relative path of file>\n" +
                    "\t検索したカードを、相対パスで指定されたファイルに出力します。\n" +
                    "-h\n" +
                    "-help\n" +
                    "-?\n" +
                    "\tヘルプを出力します。";

    /**
     * @param args 起動引数
     * @return コラージュする文字列
     */
    public static String loadCommandLine(String[] args) {
        String collageString = null;

        //起動引数が０の時
        if (args.length == 0) {
            System.out.println(optionHelp);
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            try {
                if (args[i].startsWith("-")) {
                    args[i] = args[i].toLowerCase();
                    switch (args[i]) {
                        case "-cldir"://Card List Directory
                        case "-cardlistdirectory"://Card List Directory
                            i++;
                            cardListDir = URI.create(args[i]);
                            break;
                        case "-o"://Card List Directory
                        case "-output"://Card List Directory
                            i++;
                            outputFile = args[i];
                            break;
                        case "-rdis": //Recommend Disable
                        case "-recommenddisable": //Recommend Disable
                            recommendDisable = true;
                            break;
                        case "-h":
                        case "-help":
                        case "-?":
                            System.out.println(optionHelp);
                            System.exit(0);
                        default:
                            System.err.printf("unknown option \"%s\" found\n", args[i]);
                            System.err.println(optionHelp);
                            System.exit(-1);
                    }
                } else {
                    //オプションの様でないなら、
                    collageString = args[i];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.printf("\"%s\" needs some data", args[i - 1]);
                System.exit(-1);
            }
        }
        if (collageString == null) {
            System.err.println("collage string is not put");
            System.err.println(optionHelp);
            System.exit(-1);
        }
        return collageString;
    }

    public static void main(String[] args) {
        String word = loadCommandLine(args);
        List<String> wordList = loadFile();
<<<<<<< Updated upstream
        Map<Integer, Map<String, Set<Integer>>> l = createWordDic(wordList);
        List<String> la = searchAndPrint(word, wordList, l);
=======
        Dictionary dictionary = Dictionary.makeDictionary(wordList);
        List<String> sliceWords = dictionary.sliceAsLongAsPossible(word);
        List<String> recommends = new ArrayList<>();
        List<String> outPut = new LinkedList<>();
        for (String sliceWord : sliceWords) {
            List<String> searchedWords = dictionary.search(sliceWord, -1);
            if (searchedWords.size() > 0) {
                String recommend = searchedWords.get(0);
                recommend = recommend.replace(sliceWord, "「" + sliceWord + "」");
                recommends.add(recommend);

                System.out.println("【" + sliceWord + "】:");
                outPut.add("【" + sliceWord + "】: " + searchedWords.size() + "\n");

                int counter = 0;
                for (String searchedWord : searchedWords) {
                    if (counter < 5) {
                        System.out.println(searchedWord);
                        counter++;
                    }
                    outPut.add(searchedWord + "\n");
                }
            } else {
                break;
            }
        }

        if (outputFile != null) {
            writeFile(outPut);
        }

>>>>>>> Stashed changes
        if (!recommendDisable) {
            System.out.print("\n\nおすすめ！\n\n");
            int count = 0;
            for (int i = 0; i < la.size(); i++) {
                for (int j = 0; j < count - la.get(i).indexOf("「"); j++) {
                    System.out.print("　");
                }
                System.out.println(la.get(i));
                count += la.get(i).indexOf("」") - (i == 0 ? 0 : la.get(i).indexOf("「"));
            }
        }
    }

    public static List<String> loadFile() {
        try {
            Path p = Paths.get(cardListDir);
            return Files.readAllLines(p);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Map<Integer, Map<String, Set<Integer>>> createWordDic(java.util.List<String> wordlist) {
        Map<Integer, Map<String, Set<Integer>>> l = new HashMap<>();

        for (int i = 0; i < wordlist.size(); i++) {
            String s = wordlist.get(i);
            for (int si = 0; si < s.length(); si++) {
                for (int ei = si + 1; ei < s.length(); ei++) {
                    String sliceString = s.substring(si, ei);
                    l.computeIfAbsent(sliceString.length(), k -> new HashMap<>());
                    if (!l.get(sliceString.length()).containsKey(sliceString)) {
                        l.get(sliceString.length()).put(sliceString, new HashSet<>());
                    }
                    l.get(sliceString.length()).get(sliceString).add(i);
                }
            }
        }
        return l;
    }

    public static List<String> searchAndPrint(String text, List<String> wordList, Map<Integer, Map<String, Set<Integer>>> dic) {
        int s = 0;
        List<String> l = new ArrayList<>();
        while (s < text.length()) {
            boolean flg = false;

            for (int e = text.length(); e > s; e--) {
                String sliceText = text.substring(s, e);
                if (!dic.containsKey(sliceText.length())) {
                    continue;
                }
                if (!dic.get(sliceText.length()).containsKey(sliceText)) {
                    continue;
                }
                System.out.println("【" + sliceText + "】：");

                Object[] d = dic.get(sliceText.length()).get(sliceText).toArray();
                for (int i = 0; i < d.length && i < 5; i++) {
                    System.out.println(wordList.get((Integer) d[i]));
                }
                if (d.length != 0) {
                    String rs = wordList.get((Integer) d[0]);
                    l.add(rs.replace(sliceText, "「" + sliceText + "」"));
                }
                s += sliceText.length();

                flg = true;

                break;
            }
            if (!flg) {
                System.out.println("「" + text.charAt(s) + "」が見つかりません");
                break;
            }
        }
        return l;
    }

    public static void writeFile(List<String> data) {
        assert outputFile != null;

        StringBuilder writeString = new StringBuilder();
        for (String s : data) {
            writeString.append(s);
        }

        try {
            Path p = Paths.get(outputFile);
            if (!Files.exists(p)) {
                Files.createFile(p);
            }
            Files.writeString(p, writeString.toString());
        } catch (Exception e) {
            System.err.println("データが書き込めませんでした。");
            System.exit(-1);
        }
    }
}