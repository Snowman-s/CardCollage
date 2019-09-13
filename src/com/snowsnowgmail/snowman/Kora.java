package com.snowsnowgmail.snowman;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Kora {
    public static String cardListDir = "./CardList.txt";
    public static boolean recommendDisable = false;

    public final static String optionHelp =
            "使用方法\n" +
                    "java com.snowsnowgmail.snowman.Kora [options...] <CollageString>\n\n" +
                    "オプションは次の通りです\n\n" +
                    "-cldir <relative path of file>\n" +
                    "-cardlistdirectory <relative path of file>\n" +
                    "\tカードの情報が書かれているファイルの場所を相対パスで指定します。(デフォルトでは./CardList.txt)\n" +
                    "-rdis\n" +
                    "-recommenddisable\n" +
                    "\tおすすめを非表示にします。\n" +
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
                            cardListDir = args[i];
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
        Dictionary dictionary = Dictionary.makeDictionary(wordList);
        List<String> sliceWords = dictionary.sliceAsLongAsPossible(word);
        List<String> recommends = new ArrayList<>();
        for (String sliceWord : sliceWords) {
            List<String> searchedWords = dictionary.search(sliceWord, 5);
            if (searchedWords.size() > 0) {
                String recommend = searchedWords.get(0);
                recommend = recommend.replace(sliceWord, "「" + sliceWord + "」");
                recommends.add(recommend);
                System.out.println("【" + sliceWord + "】:");
                for (String searchedWord : searchedWords) {
                    System.out.println(searchedWord);
                }
            } else {
                break;
            }
        }
        if (!recommendDisable) {
            System.out.print("\n\nおすすめ！\n\n");
            int count = 0;
            for (int i = 0; i < recommends.size(); i++) {
                for (int j = 0; j < count - recommends.get(i).indexOf("「"); j++) {
                    System.out.print(' ');
                }
                System.out.println(recommends.get(i));
                count += recommends.get(i).indexOf("」") - (i == 0 ? 0 : recommends.get(i).indexOf("「"));
            }
        }
    }

    public static List<String> loadFile() {
        try {
            URL now = Kora.class.getResource(cardListDir);
            Path p = Paths.get(now.toURI());
            return Files.readAllLines(p);
        } catch (Exception e) {
            System.err.println("カード定義ファイルが読み込めませんでした");
            System.exit(-1);
        }
        return null;
    }
}