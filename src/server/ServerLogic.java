package server;

import com.sun.net.httpserver.HttpExchange;
import entities.Client;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import models.DataModel;
import server.cookies.Cookie;
import server.enums.ContentType;
import server.enums.ResponseCodes;
import utility.JsonUtil;
import utility.Utils;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class ServerLogic extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public ServerLogic(String host, int port, DataModel dataModel) throws IOException {
        super(host, port, dataModel);
        registerGet("/", this::calendarHandler);
        registerGet("/calendar", this::calendarHandler);

        registerGet("/day", this::dayHandler);
        registerGet("/new", this::newHandler);
        registerPost("/new", this::newPostHandler);
    }

    private void newPostHandler(HttpExchange exchange) {
        var formData = parsePostBody(exchange);

        String firstName = formData.get("firstName");
        String lastName = formData.get("lastName");
        String surName = formData.get("surName");
        String clientType = formData.get("clientType");
        String symptoms = formData.get("symptoms");
        int time = Integer.parseInt(formData.get("time"));
        String birthDate = formData.get("birthDate");

        LocalDate date = LocalDate.parse(formData.get("date"));

        if (date.isBefore(LocalDate.now())) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Дата приема не может быть в прошлом!");
            renderTemplate(exchange, "new.html", map);
            return;
        }

        if (dataModel.isBooked(time)) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Время " + time + " занято, выберите другое");
            renderTemplate(exchange, "new.html", map);
            return;
        }

        dataModel.getClients().add(new Client(
                firstName,
                lastName,
                surName,
                birthDate,
                clientType,
                symptoms,
                time
        ));

        dataModel.getClients().sort(Comparator.comparingInt(Client::getTime));
        redirect303(exchange, "/new");
    }

    private void newHandler(HttpExchange exchange) {
        Path path = makeFilePath("new.html");
        renderTemplate(exchange, "new.html", path);
    }


    private void dayHandler(HttpExchange exchange) {
        Map<String, Object> map = new HashMap<>();

        var query = Utils.parseUrlEncoded(getQueryParams(exchange), "&");
        String action = query.get("action");

        if ("delete".equals(action)) {
            dataModel.removeRandomClient();
            redirect303(exchange, "/day");
            return;
        }

        if ("add".equals(action)) {
            dataModel.addRandomClient();
            redirect303(exchange, "/day");
            return;
        }

        dataModel.generateClients();
        map.put("clients", dataModel.getClients());
        renderTemplate(exchange, "list.html", map);
    }


    private void calendarHandler(HttpExchange exchange) {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> query = Utils.parseUrlEncoded(getQueryParams(exchange), "&");


        String yearParam = query.get("year");
        String monthParam = query.get("month");

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        try {
            if (yearParam != null) year = Integer.parseInt(yearParam.replaceAll("\\s", ""));
            if (monthParam != null) month = Integer.parseInt(monthParam.replaceAll("\\s", ""));
        } catch (NumberFormatException e) {
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
        }

        List<LocalDate> days = makeCalendar(year, month);

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        int nextMonth = month + 1;
        int nextYear = year;
        if (nextMonth == 13) {
            nextMonth = 1;
            nextYear = year + 1;
        }

        map.put("year", year);
        map.put("month", month);
        map.put("days", days);
        map.put("today", LocalDate.now());
        map.put("prevMonth", prevMonth);
        map.put("prevYear", prevYear);
        map.put("nextMonth", nextMonth);
        map.put("nextYear", nextYear);

        renderTemplate(exchange, "index.html", map);
    }

    private List<LocalDate> makeCalendar(int year, int month) {
        List<LocalDate> list = new ArrayList<>();

        LocalDate first = LocalDate.of(year, month, 1);
        int size = first.lengthOfMonth();

        for (int i = 0; i < size; i++) {
            list.add(first.plusDays(i));
        }
        return list;
    }

    private Map<String, String> parsePostBody(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        return Utils.parseUrlEncoded(raw, "&");
    }

    private int getIdFromQuery(HttpExchange exchange) {
        String s = getQueryParams(exchange);

        if (s == null) {
            respond404(exchange);
        }

        var map = Utils.parseUrlEncoded(s, "&");
        String idParam = map.get("id");
        return Integer.parseInt(idParam);
    }

    protected void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();

                var data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
