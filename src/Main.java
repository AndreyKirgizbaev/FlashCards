import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, String> flashcards = new LinkedHashMap<>();
        SortedMap<String, Integer> mistakeCards = new TreeMap<>();
        String input = "";
        ArrayList<String> log = new ArrayList<>();
        String exportInEnd = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import"))
                importCard(flashcards, log, mistakeCards, args[i + 1]);

            if (args[i].equals("-export"))
                exportInEnd = args[i + 1];
        }

        while (!input.equals("exit")) {
            systemOutLine("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):", log);
            input = scanner.nextLine();
            log.add(input);

            switch (input) {
                case "add":
                    addCard(flashcards, scanner, log);
                    break;

                case "remove":
                    removeCard(flashcards, scanner, log, mistakeCards);
                    break;

                case "import":
                    importCard(flashcards, scanner, log, mistakeCards);
                    break;

                case "export":
                    exportCard(flashcards, scanner, log, mistakeCards);
                    break;

                case "ask":
                    ask(flashcards, scanner, log, mistakeCards);
                    break;

                case "exit":
                    exit(flashcards, log, mistakeCards, exportInEnd);
                    return;

                case "log":
                    exportLog(log, scanner);
                    break;

                case "hardest card":
                    hardestCard(log, mistakeCards);
                    break;

                case "reset stats":
                    resetStats(log, mistakeCards);
                    break;
            }
        }
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public static void addCard(Map<String, String> flashcards, Scanner scanner, List<String> log) {
        systemOutLine("The card:", log);
        String s1 = scanner.nextLine();
        log.add(s1);
        if (flashcards.containsKey(s1)) {
            systemOutLine("The card \"" + s1 + "\" already exists. Try again:", log);
            return;
        }

        systemOutLine("The definition of the card:", log);
        String s2 = scanner.nextLine();
        log.add(s2);
        if (flashcards.containsValue(s2)) {
            systemOutLine("The definition \"" + s2 + "\" already exists. Try again:", log);
            return;
        }

        flashcards.put(s1, s2);
        systemOutLine("The pair (\"" + s1 + "\":\"" + s2 + "\") has been added.", log);
    }

    public static void removeCard(Map<String, String> flashcards, Scanner scanner, List<String> log, Map<String, Integer> mistakeCards) {
        systemOutLine("The card:", log);
        String s1 = scanner.nextLine();
        log.add(s1);
        if (flashcards.containsKey(s1)) {
            flashcards.remove(s1);
            systemOutLine("The card has been removed.", log);
            mistakeCards.remove(s1);
        } else {
            systemOutLine("Can't remove \"" + s1 + "\": there is no such card.", log);
        }
    }

    public static void importCard(Map<String, String> flashcards, Scanner scanner, List<String> log, Map<String, Integer> mistakeCards) {
        systemOutLine("File name:", log);
        String filename = scanner.nextLine();
        log.add(filename);
        try (Scanner scanner1 = new Scanner(new File(filename))) {
            int count = 0;
            while (true) {
                if (scanner1.hasNextLine()) {
                    String flash = scanner1.nextLine();
                    flashcards.put(flash, scanner1.nextLine());
                    int err = Integer.parseInt(scanner1.nextLine());
                    mistakeCards.put(flash, err);
                    count++;
                    if (!mistakeCards.containsKey(flash)) {
                        mistakeCards.put(flash, 0);
                    }
                } else
                    break;

            }
            systemOutLine(count + " cards have been loaded.", log);
        } catch (FileNotFoundException e) {
            systemOutLine("File not found.", log);
        }
    }

    public static void importCard(Map<String, String> flashCards, List<String> log, Map<String, Integer> mistakeCards, String filename) {
        try (Scanner scanner1 = new Scanner(new File(filename))) {
            int count = 0;
            while (true) {
                if (scanner1.hasNextLine()) {
                    String flash = scanner1.nextLine();
                    flashCards.put(flash, scanner1.nextLine());
                    int err = Integer.parseInt(scanner1.nextLine());
                    mistakeCards.put(flash, err);
                    count++;
                    if (!mistakeCards.containsKey(flash)) {
                        mistakeCards.put(flash, 0);
                    }
                } else
                    break;
            }
            systemOutLine(count + " cards have been loaded.", log);
        } catch (FileNotFoundException e) {
            systemOutLine("File not found.", log);
        }
    }

    public static void exportCard(Map<String, String> flashCards, Scanner scanner, List<String> log, Map<String, Integer> mistakeCards) {
        systemOutLine("File name:", log);
        int count = 0;
        String filename = scanner.nextLine();
        log.add(filename);
        try (FileWriter print = new FileWriter(new File(filename))) {
            for (String str : flashCards.keySet()) {
                count++;
                print.write(str + "\n");
                print.write(flashCards.get(str) + "\n");
                print.write(mistakeCards.getOrDefault(str, 0) + "\n");
            }
            systemOutLine(count + " cards have been saved.", log);
        } catch (IOException e) {
            systemOutLine("0 cards have been saved.", log);
        }
    }

    public static void exportCard(Map<String, String> flashCards, List<String> log, Map<String, Integer> mistakeCards, String filename) {
        int count = 0;
        try (FileWriter print = new FileWriter(new File(filename))) {
            for (String str : flashCards.keySet()) {
                count++;
                print.write(str + "\n");
                print.write(flashCards.get(str) + "\n");
                print.write(mistakeCards.getOrDefault(str, 0) + "\n");
            }
            systemOutLine(count + " cards have been saved.", log);
        } catch (IOException e) {
            systemOutLine("0 cards have been saved.", log);
        }
    }

    public static void ask(Map<String, String> flashCards, Scanner scanner, List<String> log, Map<String, Integer> mistakeCards) {
        systemOutLine("How many times to ask?", log);

        int s = Integer.parseInt(scanner.nextLine());
        log.add(String.valueOf(s));

        String[] arr = flashCards.values().toArray(new String[0]);

        Random r = new Random();

        for (int i = 0; i < s; i++) {
            int randomNumber = r.nextInt(arr.length);

            String s1 = (String) getKeyFromValue(flashCards, arr[randomNumber]);

            systemOutLine("Print the definition of \"" + s1 + "\":", log);

            String s2 = scanner.nextLine();
            if (flashCards.get(s1).equals(s2)) {
                systemOutLine("Correct answer.", log);
                mistakeCards.remove(s1);
            } else if (flashCards.containsValue(s2)) {
                systemOutLine("Wrong answer. The correct one is \"" + flashCards.get(s1) + "\"," + "" +
                        "you've just written the definition of \"" + getKeyFromValue(flashCards, s2) + "\".", log);

                if (mistakeCards.containsKey(s1)) {
                    int k = mistakeCards.get(s1) + 1;
                    mistakeCards.put(s1, k);
                } else
                    mistakeCards.put(s1, 1);

            } else {
                systemOutLine("Wrong answer. The correct one is \"" + flashCards.get(s1) + "\".", log);

                if (mistakeCards.containsKey(s1)) {
                    int k = mistakeCards.get(s1) + 1;
                    mistakeCards.put(s1, k);
                } else
                    mistakeCards.put(s1, 1);
            }
        }
    }

    public static void exit(Map<String, String> flashCards, List<String> log, Map<String, Integer> mistakeCards, String filename) {
        systemOutLine("Bye bye!", log);

        if (filename != null)
            exportCard(flashCards, log, mistakeCards, filename);
    }

    public static void exportLog(List<String> log, Scanner scanner) {
        systemOutLine("File name:", log);
        String s = scanner.nextLine();
        log.add(s);
        File file = new File(s);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String value : log) {
                printWriter.println(value);
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
        systemOutLine("The log has been saved.", log);
    }

    public static void hardestCard(List<String> log, Map<String, Integer> mistakeCards) {
        int max = 0;
        String hard = " ";
        StringBuilder hardCards1 = new StringBuilder();
        int count = 0;
        for (String a : mistakeCards.keySet()) {
            if (mistakeCards.get(a) > max) {
                max = mistakeCards.get(a);
                hard = a;

            }
        }
        for (String a : mistakeCards.keySet()) {
            if (mistakeCards.get(a) == max) {
                hardCards1.append("\"" + a + "\"" + ", ");
                count++;
            }
        }

        if (max == 0) {
            systemOutLine("There are no cards with errors.", log);
        } else if (count == 1) {
            systemOutLine("The hardest card is \"" + hard + "\". You have " + max + " errors answering it.", log);
        } else {
            systemOutLine("The hardest card is " + hardCards1.substring(0, hardCards1.length() - 2) + ". You have " + max + " errors answering it.", log);
        }
    }

    public static void resetStats(List<String> log, Map<String, Integer> mistakeCards) {
        mistakeCards.clear();
        systemOutLine("Card statistics has been reset.", log);
    }

    public static void systemOutLine(String message, List<String> log) {
        System.out.println(message);
        log.add(message);
    }
}
