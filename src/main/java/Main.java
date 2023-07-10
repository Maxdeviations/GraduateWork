import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        try (ServerSocket server = new ServerSocket(ServerConfig.PORT)) {
            System.out.println("Сервер запущен");
            while (true) {
                try (
                        Socket socket = server.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    System.out.println("Подключен клиент " + socket.getPort());
                    writer.println("Введите слово для поиска: например 'бизнес'");
                    List<PageEntry> searchResult = engine.search(reader.readLine());
                    writer.println(listToJson(searchResult));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }

    public static String listToJson(List<PageEntry> list) {
        Type listType = new TypeToken<List<PageEntry>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }
}