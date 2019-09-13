package com.snowsnowgmail.snowman;

import java.util.*;

public final class Dictionary {
    //順に、文字列の長さ、スライスした文字列、スライス元のワードのインデックス。
    private Map<Integer, Map<String, Set<String>>> data = new HashMap<>();

    //インスタンス化外では禁止
    private Dictionary() {
    }

    public void setData(String baseString, String sliceString) {
        assert baseString.contains(sliceString);

        int lengthOfString = sliceString.length();

        data.computeIfAbsent(lengthOfString, k -> new HashMap<>());
        Map<String, Set<String>> sameLengthSliceWord = data.get(lengthOfString);

        //同じ長さの文字列に、今回スライスした文字列がない場合、
        if (!sameLengthSliceWord.containsKey(sliceString)) {
            sameLengthSliceWord.put(sliceString, new HashSet<>());
        }

        sameLengthSliceWord.get(sliceString).add(baseString);
    }

    public static Dictionary makeDictionary(List<String> wordList) {
        Dictionary dictionary = new Dictionary();
        for (String s : wordList) {
            for (int si = 0; si < s.length(); si++) {
                for (int ei = si + 1; ei < s.length(); ei++) {
                    String sliceString = s.substring(si, ei);
                    dictionary.setData(s, sliceString);
                }
            }
        }
        return dictionary;
    }

    public List<String> sliceAsLongAsPossible(String text) {
        int s = 0;
        List<String> words = new ArrayList<>();
        while (s < text.length()) {
            boolean flg = false;

            //可能な限り長めにとる
            for (int e = text.length(); e > s; e--) {
                String sliceText = text.substring(s, e);
                //対応する長さがなかったり、対応するスライスした文字列がないならスキップ
                if (!this.data.containsKey(sliceText.length())) {
                    continue;
                } else if (!this.data.get(sliceText.length()).containsKey(sliceText)) {
                    continue;
                }
                words.add(sliceText);
                s += sliceText.length();

                flg = true;

                break;
            }
            if (!flg) {
                System.err.println("「" + text.charAt(s) + "」が見つかりません");
                break;
            }
        }
        return words;
    }

    public List<String> search(String sliceText, int maxNumberOfWords) {
        List<String> serchedWords = new LinkedList<>();

        Set<String> baseWords = this.data.get(sliceText.length()).get(sliceText);
        Iterator<String> iterator = baseWords.iterator();

        int counter = 0;
        while (iterator.hasNext()) {
            String baseString = iterator.next();
            serchedWords.add(baseString);
            if (maxNumberOfWords == -1 || maxNumberOfWords > counter) {
                counter++;
            } else {
                break;
            }
        }
        return serchedWords;
    }
}
