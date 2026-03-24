  import java.util.*;
import java.io.*;


public class Main {
    public static void main(String[] args) {
        LibrarySystem app = new LibrarySystem();
        app.start();
    }
}

// --- Book Model ---
class Book {
    private int id;
    private String title;
    private String author;
    private int year;

    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    public String toFileString() {
        return id + ";;" + title + ";;" + author + ";;" + year;
    }

    public static Book fromFileString(String line) {
        String[] parts = line.split(";;");
        if (parts.length == 4) {
            
            return new Book(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                Integer.parseInt(parts[3])
            );
        }
        return null;
    }
}

// --- Library Logic ---
class LibrarySystem {
    private static final String DATA_FILE = "library_data.txt";
    private List<Book> books;
    private Scanner scanner;

    public LibrarySystem() {
        books = new ArrayList<>();
        scanner = new Scanner(System.in);
        loadData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Book b = Book.fromFileString(line);
                if (b != null) books.add(b);
            }
        } catch (Exception e) {
            // Online compilers might restrict file access, so we handle silently
        }
    }

    private void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Book b : books) {
                writer.write(b.toFileString());
                writer.newLine();
            }
        } catch (Exception e) {
            // Silent catch for online environments
        }
    }

    private int generateId() {
        return books.stream().mapToInt(Book::getId).max().orElse(0) + 1;
    }

    public void start() {
        while (true) {
            System.out.println("\n--- LIBRARY SYSTEM ---");
            System.out.println("1. View Books\n2. Add Book\n3. Remove Book\n4. Search\n5. Exit");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                display(books);
            } else if (choice.equals("2")) {
                System.out.print("Title: "); String t = scanner.nextLine();
                System.out.print("Author: "); String a = scanner.nextLine();
                System.out.print("Year: "); 
                try {
                    int y = Integer.parseInt(scanner.nextLine());
                    books.add(new Book(generateId(), t, a, y));
                    saveData();
                    System.out.println("✅ Added.");
                } catch (Exception e) { System.out.println("❌ Invalid Year."); }
            } else if (choice.equals("3")) {
                System.out.print("ID to remove: ");
                try {
                    int id = Integer.parseInt(scanner.nextLine());
                    books.removeIf(b -> b.getId() == id);
                    saveData();
                    System.out.println("✅ Removed.");
                } catch (Exception e) { System.out.println("❌ Invalid ID."); }
            } else if (choice.equals("4")) {
                System.out.print("Search: ");
                String q = scanner.nextLine().toLowerCase();
                List<Book> res = new ArrayList<>();
                for(Book b : books) if(b.getTitle().toLowerCase().contains(q)) res.add(b);
                display(res);
            } else if (choice.equals("5")) {
                break;
            }
        }
    }

    private void display(List<Book> list) {
        if (list.isEmpty()) { System.out.println("Empty."); return; }
        System.out.printf("%-5s | %-20s | %-15s | %-5s%n", "ID", "Title", "Author", "Year");
        for (Book b : list) {
            System.out.printf("%-5d | %-20s | %-15s | %-5d%n", b.getId(), b.getTitle(), b.getAuthor(), b.getYear());
        }
    }
}
